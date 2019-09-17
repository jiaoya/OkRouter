package com.albert.okrouter.compiler;

import com.albert.okrouter.annotation.Action;
import com.albert.okrouter.annotation.InterceptPoint;
import com.albert.okrouter.annotation.Interceptor;
import com.albert.okrouter.annotation.Provider;
import com.albert.okrouter.annotation.Route;
import com.albert.okrouter.annotation.model.UniqueKeyTreeMap;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-06-03.
 *      Desc         : 通过javapoet自动生成代码 HashMap<String, Class> 形式
 * </pre>
 */
@AutoService(Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)//java版本支持
//@SupportedAnnotationTypes({"com.albert.jrouter.annotion.Route"})//标注注解处理器支持的注解类型
public class RouteProcessor extends AbstractProcessor {
    private static String JROUTER_MODULE_NAME = "JROUTER_MODULE_NAME";
    private Filer mFiler;
    private Messager printMsg;
    /**
     * 元素上进行操作的某些实用工具方法的实现
     */
    private Elements elementUtils;
    // 创建的类的后置名==模块名称
    private String classNameSuffix = "";


    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        printMsg = processingEnvironment.getMessager();
        elementUtils = processingEnvironment.getElementUtils();
        mFiler = processingEnvironment.getFiler();

        Map<String, String> options = processingEnvironment.getOptions();
        for (String key : options.keySet()) {
            if (key.equals(JROUTER_MODULE_NAME) && "".equals(classNameSuffix)) {
                classNameSuffix = options.get(key);
                //break;
            }
        }
        printMsg.printMessage(Diagnostic.Kind.NOTE, "初识化--");
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        // 指定这个注解处理器是注册给哪个注解的
        HashSet<String> supportTypes = new LinkedHashSet<>();
        supportTypes.add(Route.class.getCanonicalName());
        supportTypes.add(Interceptor.class.getCanonicalName());
        supportTypes.add(InterceptPoint.class.getCanonicalName());
        return supportTypes;
    }

    Set<String> paths = new HashSet<>();
    HashMap<String, String> adressClassHashMap = new HashMap<>();
    HashMap<Integer, String> interceptorsHashMap = new HashMap<>();
    HashMap<String, String> interceptorPointHashMap = new HashMap<>();
    HashMap<String, String> providerHashMap = new HashMap<>();
    HashMap<String, String> actionHashMap = new HashMap<>();

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        printLog("processing...S.size=" + set.size() + "--" + set.toString());
        if (set.isEmpty()) {
            return false;
        }

        // 得到所有的Route注解
        Set<? extends Element> adressElements = roundEnvironment.getElementsAnnotatedWith(Route.class);
        printLog("elements.size=" + adressElements.size());
        if (adressElements.size() > 0) {

            for (Element tElement : adressElements) {
                // 得到注解上的类名
                TypeElement classElement = (TypeElement) tElement;

                String fullClassName = classElement.getQualifiedName().toString();
                // 获取Annotation,然后获取方法的值
                Route routeAnnotation = tElement.getAnnotation(Route.class);
                String key = routeAnnotation.adress();

                // 检查路径头
                URI uri = URI.create(key);
                if (uri.getHost() == null || "".equals(uri.getHost())) {
                    if (!key.startsWith("/") || key.substring(0, 2).equals("//")) {
                        throw new RuntimeException("the path（" + key + "）must be start with '/' and not more than 2 '/'! or url");
                    }
                }
                printLog("全名：" + fullClassName);
                paths.add(key);
                adressClassHashMap.put(key, fullClassName + ".class");
            }
        }

        // 全局拦截器注解
        Set<? extends Element> interceptorElements = roundEnvironment.getElementsAnnotatedWith(Interceptor.class);
        if (interceptorElements.size() > 0) {
            for (Element tElement : interceptorElements) {
                // 得到注解上的类名
                TypeElement classElement = (TypeElement) tElement;
                String fullClassName = classElement.getQualifiedName().toString();
                printLog("interceptorElements-" + fullClassName);
                boolean isInterceptor = false;
                for (TypeMirror typeMirror : classElement.getInterfaces()) {
                    if (typeMirror.toString().equals("com.albert.okrouter.interceptor.RouterInterceptor")) {
                        isInterceptor = true;
                        break;
                    }
                }
                if (isInterceptor) {
                    Interceptor interceptor = tElement.getAnnotation(Interceptor.class);
                    Integer priority = interceptor.priority();
                    if (interceptorsHashMap.containsKey(priority)) {
                        throw new RuntimeException(String.format("More than one interceptors use same priority [%s]", priority));
                    }
                    interceptorsHashMap.put(priority, fullClassName + ".class");
                    printLog("interceptorElements-" + interceptorsHashMap.toString());
                }
            }
        }

        // 局部/点 截器注解
        Set<? extends Element> interceptorPointElements = roundEnvironment.getElementsAnnotatedWith(InterceptPoint.class);
        if (interceptorPointElements.size() > 0) {
            for (Element tElement : interceptorPointElements) {
                // 得到注解上的类名
                TypeElement classElement = (TypeElement) tElement;
                String fullClassName = classElement.getQualifiedName().toString();
                boolean isInterceptor = false;
                for (TypeMirror typeMirror : classElement.getInterfaces()) {
                    if (typeMirror.toString().equals("com.albert.okrouter.interceptor.RouterInterceptor")) {
                        isInterceptor = true;
                        break;
                    }
                }
                if (isInterceptor) {
                    InterceptPoint interceptPoint = tElement.getAnnotation(InterceptPoint.class);
                    String adress = interceptPoint.adress();
                    interceptorPointHashMap.put(adress, fullClassName + ".class");
                    printLog("interceptorPointElements-" + interceptorPointHashMap.toString());
                }
            }
        }


        // 服务端 binder
        Set<? extends Element> providerElements = roundEnvironment.getElementsAnnotatedWith(Provider.class);
        if (providerElements.size() > 0) {
            for (Element tElement : providerElements) {
                // 得到注解上的类名
                TypeElement classElement = (TypeElement) tElement;
                String fullClassName = classElement.getQualifiedName().toString();
                boolean isProviderService = false;
                if (classElement.getSuperclass().toString().equals("com.albert.okrouter.provide.ProviderService")) {
                    isProviderService = true;
                }
                if (isProviderService) {
                    Provider providerService = tElement.getAnnotation(Provider.class);
                    String processName = providerService.processName();
                    providerHashMap.put(processName, fullClassName + ".class");
                    printLog("providerHashMap-" + providerHashMap.toString());
                }
            }
        }

        //  action接口
        Set<? extends Element> actionElements = roundEnvironment.getElementsAnnotatedWith(Action.class);
        if (actionElements.size() > 0) {
            for (Element tElement : actionElements) {
                // 得到注解上的类名
                TypeElement classElement = (TypeElement) tElement;
                String fullClassName = classElement.getQualifiedName().toString();
                boolean isAction = false;
                for (TypeMirror typeMirror : classElement.getInterfaces()) {
                    if (typeMirror.toString().equals("com.albert.okrouter.provide.IBaseAction")) {
                        isAction = true;
                        break;
                    }
                }
                if (isAction) {
                    Action action = tElement.getAnnotation(Action.class);
                    String processName = action.processName();
                    String adress = action.adress();
                    String key = "okrouter://provider/action?processName=" + processName + "&adress=" + adress;
                    actionHashMap.put(key, fullClassName + ".class");
                    printLog(" actionElements-" + actionHashMap.toString());
                }
            }
        }

        // 生成类
        saveFile();
        return true;
    }

    /**
     * 打印
     *
     * @param value
     */
    private void printLog(String value) {
        if (printMsg != null) {
            printMsg.printMessage(Diagnostic.Kind.NOTE, value);
        }
    }

    // 生成类的包名okrouter
    final String packageName = "com.ablert.okrouter.cache";


    // 创建的成员变量
    final String classPaths_VariableName = "MOLDE_CLASS_PATHS";
    // 获取成员变量get方法
    final String getPath_MethodName = "getMoldeClassPaths";

    // 创建的成员变量-全局拦截
    final String interceptors_VariableName = "MOLDE_INTERCEPTORS_MAP";
    // 获取成员变量get方法
    final String getInterceptors_MethodName = "getModelInterceptors";

    // 创建的成员变量-单个路径拦截
    final String interceptorsPoint_VariableName = "MOLDE_INTERCEPTORS_POINT_MAP";
    // 获取成员变量get方法
    final String getInterceptorsPoint_MethodName = "getModelInterceptorsPoint";


    // 创建的成员变量-provide
    final String provideVariableName = "MOLDE_PROVIDE_MAP";
    // 获取成员变量get方法
    final String getProvideMethodName = "getModelProvides";

    // 创建的成员变量-action
    final String actionVariableName = "MOLDE_ACTION_MAP";
    // 获取成员变量get方法
    final String getActionMethodName = "getModelActions";

    /**
     * 保存 生成类
     */
    private void saveFile() {
        if ("".equals(classNameSuffix)) {
            return;
        }
        // 创建的类名
        final String className = "RouterModelClassPaths_" + classNameSuffix;
        printLog(className);
        try {
            // 创建类名
            TypeSpec.Builder routerClassBuilder = TypeSpec.classBuilder(className)
                    .addModifiers(Modifier.PUBLIC);

            // 创建静态变量 HashMap类型
            FieldSpec pathsfieldSpec = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, String.class, Class.class), classPaths_VariableName)
                    .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                    .initializer("new HashMap<>()")
                    .build();

            // 创建获取地址方法
            MethodSpec.Builder getPathsMethodbuilder = MethodSpec.methodBuilder(getPath_MethodName);
            getPathsMethodbuilder.addModifiers(Modifier.PUBLIC);
            getPathsMethodbuilder.addModifiers(Modifier.STATIC);
            getPathsMethodbuilder.returns(ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
            getPathsMethodbuilder.addStatement("if (" + classPaths_VariableName + ".size() == 0) { ");
            // 添加方法里的代码
            Iterator<String> iterableKey = paths.iterator();
            while (iterableKey.hasNext()) {
                String key = iterableKey.next();
                if (adressClassHashMap.get(key) != null && !"".equals(adressClassHashMap.get(key))) {
                    getPathsMethodbuilder.addStatement(classPaths_VariableName + ".put($S," + adressClassHashMap.get(key) + ")", key);
                }
            }
            getPathsMethodbuilder.addStatement(" }");
            getPathsMethodbuilder.addStatement("return " + classPaths_VariableName, ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
            // 添加到类里
            routerClassBuilder
                    .addMethod(getPathsMethodbuilder.build())
                    .addField(pathsfieldSpec);

            // 生成模块内 获取全局拦截器代码
            if (interceptorsHashMap.size() > 0) {
                // 创建静态变量-全局拦截 UniqueKeyTreeMap类型
                FieldSpec interceptorsField = FieldSpec.builder(ParameterizedTypeName.get(UniqueKeyTreeMap.class, Integer.class, Class.class), interceptors_VariableName)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new UniqueKeyTreeMap<>()")
                        .build();
                // 创建获取模块内拦截器方法
                MethodSpec.Builder getInterceptsCacheMethodbuilder = MethodSpec.methodBuilder(getInterceptors_MethodName);
                getInterceptsCacheMethodbuilder.addModifiers(Modifier.PUBLIC);
                getInterceptsCacheMethodbuilder.addModifiers(Modifier.STATIC);
                getInterceptsCacheMethodbuilder.returns(ParameterizedTypeName.get(UniqueKeyTreeMap.class, Integer.class, Class.class));
                getInterceptsCacheMethodbuilder.addStatement("if (" + interceptors_VariableName + ".size() == 0) { ");

                Iterator<Integer> interceptorKey = interceptorsHashMap.keySet().iterator();
                while (interceptorKey.hasNext()) {
                    Integer key = interceptorKey.next();
                    if (interceptorsHashMap.get(key) != null) {
                        getInterceptsCacheMethodbuilder.addStatement(interceptors_VariableName + ".put($L," + interceptorsHashMap.get(key) + ")", key);
                    }
                }
                getInterceptsCacheMethodbuilder.addStatement("}");
                getInterceptsCacheMethodbuilder.addStatement("return " + interceptors_VariableName, ParameterizedTypeName.get(UniqueKeyTreeMap.class, Integer.class, Class.class));
                // 添加到类里
                routerClassBuilder.
                        addField(interceptorsField).
                        addMethod(getInterceptsCacheMethodbuilder.build());
            }

            // 生成模块内 获取 点/局部拦截器代码
            if (interceptorPointHashMap.size() > 0) {
                // 创建静态变量-全局拦截 HashMap类型
                FieldSpec interceptorsPointField = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, String.class, Class.class), interceptorsPoint_VariableName)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new HashMap<>()")
                        .build();
                // 创建获取模块内 点拦截器 方法
                MethodSpec.Builder getInterceptsPointCacheMethodbuilder = MethodSpec.methodBuilder(getInterceptorsPoint_MethodName);
                getInterceptsPointCacheMethodbuilder.addModifiers(Modifier.PUBLIC);
                getInterceptsPointCacheMethodbuilder.addModifiers(Modifier.STATIC);
                getInterceptsPointCacheMethodbuilder.returns(ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
                getInterceptsPointCacheMethodbuilder.addStatement("if (" + interceptorsPoint_VariableName + ".size() == 0) { ");

                Iterator<String> interceptorPointKey = interceptorPointHashMap.keySet().iterator();
                while (interceptorPointKey.hasNext()) {
                    String key = interceptorPointKey.next();
                    if (interceptorPointHashMap.get(key) != null) {
                        getInterceptsPointCacheMethodbuilder.addStatement(interceptorsPoint_VariableName + ".put($S," + interceptorPointHashMap.get(key) + ")", key);
                    }
                }
                getInterceptsPointCacheMethodbuilder.addStatement("}");
                getInterceptsPointCacheMethodbuilder.addStatement("return " + interceptorsPoint_VariableName, ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
                // 添加到类里
                routerClassBuilder.
                        addField(interceptorsPointField).
                        addMethod(getInterceptsPointCacheMethodbuilder.build());
            }

            // 生成binder服务
            if (providerHashMap.size() > 0) {
                // 创建静态变量-全局拦截 HashMap类型
                FieldSpec provideField = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, String.class, Class.class), provideVariableName)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new HashMap<>()")
                        .build();

                // 创建获取模块内  方法
                MethodSpec.Builder getProvideMethodbuilder = MethodSpec.methodBuilder(getProvideMethodName);
                getProvideMethodbuilder.addModifiers(Modifier.PUBLIC);
                getProvideMethodbuilder.addModifiers(Modifier.STATIC);
                getProvideMethodbuilder.returns(ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
                getProvideMethodbuilder.addStatement("if (" + provideVariableName + ".size() == 0) { ");
                Iterator<String> actionKey = providerHashMap.keySet().iterator();
                while (actionKey.hasNext()) {
                    String key = actionKey.next();
                    if (providerHashMap.get(key) != null) {
                        getProvideMethodbuilder.addStatement(provideVariableName + ".put($S," + providerHashMap.get(key) + ")", key);
                    }
                }
                getProvideMethodbuilder.addStatement("}");
                getProvideMethodbuilder.addStatement("return " + provideVariableName, ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
                // 添加到类里
                routerClassBuilder.
                        addField(provideField).
                        addMethod(getProvideMethodbuilder.build());
            }

            // 生成action，使用uri处理，一个map搞定
            if (actionHashMap.size() > 0) {
                // 创建静态变量-全局拦截 HashMap类型
                FieldSpec actionField = FieldSpec.builder(ParameterizedTypeName.get(HashMap.class, String.class, Class.class), actionVariableName)
                        .addModifiers(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL)
                        .initializer("new HashMap<>()")
                        .build();

                // 创建获取模块内  方法
                MethodSpec.Builder getActionMethodbuilder = MethodSpec.methodBuilder(getActionMethodName);
                getActionMethodbuilder.addModifiers(Modifier.PUBLIC);
                getActionMethodbuilder.addModifiers(Modifier.STATIC);
                getActionMethodbuilder.returns(ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
                getActionMethodbuilder.addStatement("if (" + actionVariableName + ".size() == 0) { ");
                Iterator<String> actionKey = actionHashMap.keySet().iterator();
                while (actionKey.hasNext()) {
                    String key = actionKey.next();
                    if (actionHashMap.get(key) != null) {
                        getActionMethodbuilder.addStatement(actionVariableName + ".put($S," + actionHashMap.get(key) + ")", key);
                    }
                }
                getActionMethodbuilder.addStatement("}");
                getActionMethodbuilder.addStatement("return " + actionVariableName, ParameterizedTypeName.get(HashMap.class, String.class, Class.class));
                // 添加到类里
                routerClassBuilder.
                        addField(actionField).
                        addMethod(getActionMethodbuilder.build());
            }

            // 生成文件
            TypeSpec typeSpec = routerClassBuilder.build();
            JavaFile file = JavaFile.builder(packageName, typeSpec).build();
            file.writeTo(mFiler);
            printLog("------create");

        } catch (Exception e) {
            e.printStackTrace();
            printLog("------" + e.getLocalizedMessage());
        }
    }

    public static boolean check(String fstrData) {
        char c = fstrData.charAt(0);
        if (((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'))) {
            return true;
        } else {
            return false;
        }
    }

}
