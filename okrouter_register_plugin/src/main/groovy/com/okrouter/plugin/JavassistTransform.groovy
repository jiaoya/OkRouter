package com.okrouter.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

class JavassistTransform extends Transform {

    Project mProject

    JavassistTransform(Project project) {
        mProject = project
    }

    /**
     * 用于指明本Transform的名字，也是代表该Transform的task的名字
     * @return
     */
    @Override
    String getName() {
        // 设置我们自定义的Transform对应的Task名称
        return "okrouterPlugin"
    }

    /**
     * 用于指明Transform的输入类型，可以作为输入过滤的手段
     * CLASSES和RESOURCES：CLASSES代表处理的java的class文件，RESOURCES代表要处理java的资源
     * @return
     */
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * 用于指明Transform的作用域，在TransformManager集成了多种类型
     *
     * EXTERNAL_LIBRARIES        只有外部库
     * PROJECT                   只有项目内容
     * PROJECT_LOCAL_DEPS        只有项目的本地依赖(本地jar)
     * PROVIDED_ONLY             只提供本地或远程依赖项
     * SUB_PROJECTS              只有子项目。
     * SUB_PROJECTS_LOCAL_DEPS   只有子项目的本地依赖项(本地jar)。
     * TESTED_CODE               由当前变量(包括依赖项)测试的代码
     */
    @Override
    Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs, Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider, boolean isIncremental) throws IOException, TransformException, InterruptedException {
        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)

        LogUtil.error("ransform-start")

        // 每次开始前先删除之前的缓存文件
        File file = new File(PluginSetting.CREATE_FILE_ROUTER_CALSS_PATHS)
        FileUtils.deleteDirectory(file)

        // 最后一个输出的路径
        def lastDest = null
        inputs.each { TransformInput input ->
            //对 jar包 类型的inputs 进行遍历（主要扫码主模块app之外的模块，子模块一般以jar形式存在）
            input.jarInputs.each { JarInput jarInput ->
                //  LogUtil.error("jarInput name = " + jarInput.name + ", path = " + jarInput.file.absolutePath + "\n")
                // 扫描过滤，自定义的逻辑
                JavassistInject.scanFilterPath(jarInput.file.getAbsolutePath(), PluginSetting.MOLDE_PAHTS_CACHE_PACKAGE_NAME, mProject)

                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = jarInput.file.hashCode()
                if (jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0, jarName.length() - 4)
                }
                //生成输出路径
                def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }

            // 对类型为“文件夹”的input进行遍历（主要处理app模块）
            for (DirectoryInput directoryInput in input.directoryInputs) {
                //   LogUtil.error("directoryInput name = " + directoryInput.name + ", path = " + directoryInput.file.absolutePath + "\n")
                // 扫描过滤，自定义的逻辑
                JavassistInject.scanFilterPath(directoryInput.file.absolutePath, PluginSetting.MOLDE_PAHTS_CACHE_PACKAGE_NAME, mProject)
                
                // 获取output目录
                def dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                lastDest = dest
                //将 input 的目录复制到 output 指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }

        JavassistInject.injectCreate(lastDest)
        LogUtil.error("ransform-end")
    }


}
