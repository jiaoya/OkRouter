package com.okrouter.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-07-21.
 *      Desc         :
 * </pre>
 */
public class RouterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        LogUtil.setProject(project)
        LogUtil.error("=====================OkRouterPlugin Transform插件已注册 ======================")
        def classTransform = new JavassistTransform(project)
        project.android.registerTransform(classTransform)

//        project.task('routerPluginTest') {
//            println 'hello, world!'
//        }
    }

}
