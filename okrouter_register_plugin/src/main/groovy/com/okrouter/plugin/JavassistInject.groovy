package com.okrouter.plugin

import javassist.*
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.jar.JarEntry
import java.util.jar.JarFile

class JavassistInject {

    private static ArrayList classPathList
    private static ClassPool pool
    private static HashSet<String> scanAdressClassSet

    /**
     * 扫描过滤路径
     * @param path
     * @param packageName
     * @param project
     */
    public static void scanFilterPath(String path, String packageName, Project project) {

        // 初始化
        if (classPathList == null && scanAdressClassSet == null) {
            classPathList = new ArrayList<String>()
            scanAdressClassSet = new HashSet<>()
            pool = ClassPool.getDefault()
        }

        // 开始扫描
        File dir = new File(path)
        // 如果path表示的是一个目录则返回true
        if (!dir.isDirectory()) {
            scanFilterJar(path, packageName, project)
        } else {
            scanFilterDir(dir, path, packageName, project)
        }
    }

    /**
     * 扫描jar包
     * @param path
     * @param packageName == com.ablert.jrouter.cache
     * @param project
     */
    public static void scanFilterJar(String path, String packageName, Project project) {
        try {
            JarFile jarFile = new JarFile(path);
            Enumeration<JarEntry> entrys = jarFile.entries()
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement()
                // LogUtil.error("jarEntry.name:--" + jarEntry)
                String className = jarEntry.getName().replaceAll('/', '.').replace('\\', '.')
                if (!className.startsWith("com.android")
                        && !className.startsWith("android.")
                        && !className.startsWith("androidx.")
                        && !className.startsWith("com.squareup.")
                        && !className.startsWith("com.google.")
                        && !className.startsWith("com.facebook.")
                        && !className.startsWith("org.apache.")
                        && !className.startsWith("org.apache.")
                        && !className.startsWith("google.")) {

                    if (className.endsWith(".class")) {
                        // LogUtil.error("injectJar:--" + className)
                        className = className.substring(0, className.length() - 6)
                        if (className.contains(packageName) || className.equals(PluginSetting.IMPORT_ROUTER_CLASS_UniqueKeyTreeMap)) {
                            // 路由
                            pool.appendClassPath(path)
                            // 记录扫描到的路径
                            classPathList.add(path)
                            if (!className.equals(PluginSetting.IMPORT_ROUTER_CLASS_UniqueKeyTreeMap)) {
                                // 记录扫码的类
                                scanAdressClassSet.add(className)
                            }
                            LogUtil.error("injectJar:--" + className)
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描文件夹路径
     * @param dir
     * @param path
     * @param packageName == com.ablert.jrouter.cache
     * @param project
     */
    public static void scanFilterDir(File dir, String path, String packageName, Project project) {
        try {
            dir.eachFileRecurse { File file ->
                String filePath = file.absolutePath
                if (filePath.endsWith(".class")
                        && !filePath.contains('R$')
                        && !filePath.contains('$')//代理类
                        && !filePath.contains('R.class')
                        && !filePath.contains("BuildConfig.class")) {

                    // 扫描的路径是：/app/build/intermediates/javac,这种形式要转成 app.build.intermediates.javac
                    String classFile = filePath.replace('\\', '.').replace('/', '.')
                    // LogUtil.error("injectDir:--" + classFile + "\n")
                    // 如果是包名packageName
                    if (classFile.contains(packageName) || classFile.contains(PluginSetting.IMPORT_ROUTER_CLASS_UniqueKeyTreeMap)) {

                        classPathList.add(path)
                        pool.appendClassPath(path)

                        int index = classFile.indexOf(packageName)
                        // .class = 6
                        int end = classFile.length() - 6
                        String className = classFile.substring(index, end)
                        if (!className.equals(PluginSetting.IMPORT_ROUTER_CLASS_UniqueKeyTreeMap)) {
                            // 记录扫码的类
                            scanAdressClassSet.add(className)
                        }
                        // LogUtil.error("injectDir:--" + className)
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 注入并创建文件，不支持泛型
     */
    public static void injectCreate(File targetDir) {
        LogUtil.error("injectCreate:--" + scanAdressClassSet.size())
        if (scanAdressClassSet == null) {
            return
        }
        try {
            // 先转换获取真实类
            HashSet<CtClass> adressClassSet = new HashSet()
            for (String className : scanAdressClassSet) {
                CtClass ctClass = pool.getCtClass(className)
                adressClassSet.add(ctClass)
            }
            if (adressClassSet.size() == 0) {
                return
            }

            // 导入包
            pool.importPackage("java.util.HashMap")
            // 导入包com.ablert.jrouter.cache.*
            pool.importPackage(PluginSetting.MOLDE_PAHTS_CACHE_PACKAGE_NAME + ".*")
            pool.importPackage(PluginSetting.IMPORT_ROUTER_CLASS_UniqueKeyTreeMap)

            // 创建新类:com.ablert.jrouter.cache.RouterInfo
            CtClass routerClass = pool.makeClass(PluginSetting.ROUTER_CALSS_PATHS);

            // 创建变量，Javassist不支持要创建或注入的类中存在泛型参数
            StringBuilder variableBuilder = new StringBuilder("private static final HashMap " + PluginSetting.ALL_CLASS_PATHS + " = new HashMap();")
            CtField pathField = CtField.make(variableBuilder.toString(), routerClass)
            routerClass.addField(pathField)

            // 创建变量，Javassist不支持要创建或注入的类中存在泛型参数
            StringBuilder variableInterceptorsBuilder = new StringBuilder("private static final UniqueKeyTreeMap " + PluginSetting.ALL_INTERCEPTORS + " = new UniqueKeyTreeMap();")
            CtField interceptorsField = CtField.make(variableInterceptorsBuilder.toString(), routerClass)
            routerClass.addField(interceptorsField)

            // 创建变量，Javassist不支持要创建或注入的类中存在泛型参数
            StringBuilder variableInterceptorsPointBuilder = new StringBuilder("private static final HashMap " + PluginSetting.ALL_INTERCEPTORS_POINT + "= new HashMap();")
            CtField interceptorsPointField = CtField.make(variableInterceptorsPointBuilder.toString(), routerClass)
            routerClass.addField(interceptorsPointField)

            // 创建变量，ALL_PROVIDERS
            StringBuilder variableProvideBuilder = new StringBuilder("private static final HashMap " + PluginSetting.ALL_PROVIDERS + "= new HashMap();")
            CtField provideField = CtField.make(variableProvideBuilder.toString(), routerClass)
            routerClass.addField(provideField)

            // 创建变量，ALL_ACTIONS
            StringBuilder variableActiobBuilder = new StringBuilder("private static final HashMap " + PluginSetting.ALL_ACTIONS + "= new HashMap();")
            CtField actionField = CtField.make(variableActiobBuilder.toString(), routerClass)
            routerClass.addField(actionField)

            // 创建方法:getAllClassPaths()，但是不支持泛型，所以创建HashMap无法指定确定类型
            StringBuilder methodBuilder = new StringBuilder("public static HashMap " + PluginSetting.GET_ALL_CLASS_PATH_METHOD + "() {\n");
            methodBuilder.append("if (" + PluginSetting.ALL_CLASS_PATHS + ".size() == 0) {\n")

            // 创建方法:getAllInterceptors()，但是不支持泛型，所以创建HashMap无法指定确定类型
            StringBuilder interceptorsMethodBuilder = new StringBuilder("public static UniqueKeyTreeMap " + PluginSetting.GET_ALL_INTERCEPTOS_METHOD + "() {\n");
            interceptorsMethodBuilder.append("if (" + PluginSetting.ALL_INTERCEPTORS + ".size() == 0) {\n")

            // 创建方法:getAllInterceptorsPoint()，但是不支持泛型，所以创建HashMap无法指定确定类型
            StringBuilder interceptorsPointMethodBuilder = new StringBuilder("public static HashMap " + PluginSetting.GET_ALL_INTERCEPTOS_POINT_METHOD + "() {\n");
            interceptorsPointMethodBuilder.append("if (" + PluginSetting.ALL_INTERCEPTORS_POINT + ".size() == 0) {\n")

            // 创建方法：getAllProvidersPoint
            StringBuilder provideMethodBuilder = new StringBuilder("public static HashMap " + PluginSetting.GET_ALL_PROVIDERS_METHOD + "() {\n");
            provideMethodBuilder.append("if (" + PluginSetting.ALL_PROVIDERS + ".size() == 0) {\n")

            // 创建方法：getAllActions
            StringBuilder actionMethodBuilder = new StringBuilder("public static HashMap " + PluginSetting.GET_ALL_ACTIONS_METHOD + "() {\n");
            actionMethodBuilder.append("if (" + PluginSetting.ALL_ACTIONS + ".size() == 0) {\n")

            // 循环向总集合里注入各个模块里的路径集合
            for (CtClass tCtclass : adressClassSet) {
                try {
                    String a = tCtclass.name + "." + tCtclass.getDeclaredMethod(PluginSetting.MODEL_GET_PATHS_METHOD_NAME).name + "()"
                    methodBuilder.append(PluginSetting.ALL_CLASS_PATHS + ".putAll(")
                    methodBuilder.append(a)
                    methodBuilder.append(");")
                } catch (NotFoundException e) {
                    //e.printStackTrace()
                    LogUtil.error(e.getMessage());
                }

                try {
                    String a = tCtclass.name + "." + tCtclass.getDeclaredMethod(PluginSetting.MODEL_GET_INTERCEPTORS_METHOD_NAME).name + "()"
                    interceptorsMethodBuilder.append(PluginSetting.ALL_INTERCEPTORS + ".putAll(")
                    interceptorsMethodBuilder.append(a)
                    interceptorsMethodBuilder.append(");")
                } catch (NotFoundException e) {
                    //e.printStackTrace()
                    LogUtil.error(e.getMessage());
                }

                try {
                    String a = tCtclass.name + "." + tCtclass.getDeclaredMethod(PluginSetting.MODEL_GET_INTERCEPTORS_POINT_METHOD_NAME).name + "()"
                    interceptorsPointMethodBuilder.append(PluginSetting.ALL_INTERCEPTORS_POINT + ".putAll(")
                    interceptorsPointMethodBuilder.append(a)
                    interceptorsPointMethodBuilder.append(");")
                } catch (NotFoundException e) {
                    //e.printStackTrace()
                    LogUtil.error(e.getMessage());
                }

                // provides
                try {
                    String a = tCtclass.name + "." + tCtclass.getDeclaredMethod(PluginSetting.MODEL_GET_PROVIDES).name + "()"
                    LogUtil.error("injectDir:--" + a)
                    provideMethodBuilder.append(PluginSetting.ALL_PROVIDERS + ".putAll(")
                    provideMethodBuilder.append(a)
                    provideMethodBuilder.append(");")
                } catch (NotFoundException e) {
                    //e.printStackTrace()
                    LogUtil.error(e.getMessage());
                }

                // action
                try {
                    String a = tCtclass.name + "." + tCtclass.getDeclaredMethod(PluginSetting.MODEL_GET_ACTIONS).name + "()"
                    actionMethodBuilder.append(PluginSetting.ALL_ACTIONS + ".putAll(")
                    actionMethodBuilder.append(a)
                    actionMethodBuilder.append(");")
                } catch (NotFoundException e) {
                    //e.printStackTrace()
                    LogUtil.error(e.getMessage());
                }
            }

            // 添加方法
            // activity
            methodBuilder.append("}")
            methodBuilder.append("return " + PluginSetting.ALL_CLASS_PATHS + ";\n}")
            CtMethod ctMethod = CtNewMethod.make(methodBuilder.toString(), routerClass)
            routerClass.addMethod(ctMethod)

            // interceptors
            interceptorsMethodBuilder.append("}")
            interceptorsMethodBuilder.append("return " + PluginSetting.ALL_INTERCEPTORS + ";\n}")
            CtMethod interceptorsMethod = CtNewMethod.make(interceptorsMethodBuilder.toString(), routerClass)
            routerClass.addMethod(interceptorsMethod)

            // interceptorsPoint
            interceptorsPointMethodBuilder.append("}")
            interceptorsPointMethodBuilder.append("return " + PluginSetting.ALL_INTERCEPTORS_POINT + ";\n}")
            CtMethod interceptorsPointMethod = CtNewMethod.make(interceptorsPointMethodBuilder.toString(), routerClass)
            routerClass.addMethod(interceptorsPointMethod)

            // providers
            provideMethodBuilder.append("}")
            provideMethodBuilder.append("return " + PluginSetting.ALL_PROVIDERS + ";\n}")
            CtMethod provideMethod = CtNewMethod.make(provideMethodBuilder.toString(), routerClass)
            routerClass.addMethod(provideMethod)

            // actions
            actionMethodBuilder.append("}")
            actionMethodBuilder.append("return " + PluginSetting.ALL_ACTIONS + ";\n}")
            CtMethod actionMethod = CtNewMethod.make(actionMethodBuilder.toString(), routerClass)
            routerClass.addMethod(actionMethod)


            // 生成文件
            routerClass.writeFile(PluginSetting.CREATE_FILE_ROUTER_CALSS_PATHS)
            File file = new File(PluginSetting.CREATE_FILE_ROUTER_CALSS_PATHS)
            // 拷贝到targetDir里，才能被打包到apk里
            FileUtils.copyDirectory(file, targetDir)
            //LogUtil.error("\n\n\n生成AddressList, 位置:" + file.getAbsolutePath())
            // 关闭
            routerClass.detach()
        } catch (Exception e) {
            e.printStackTrace();
        }
        removeClassPath()
    }

    /**
     * 移除pool里的数据
     */
    private static void removeClassPath() {
        if (classPathList != null) {
            for (String a : classPathList) {
                try {
                    ClassPath classPath = ClassPoolTail.makePathObject(a)
                    pool.removeClassPath(classPath)
                } catch (Exception e) {

                }
            }
            classPathList.clear()
        }
    }


}