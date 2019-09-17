package com.okrouter.plugin;

import org.gradle.api.Project;

/**
 * <pre>
 *      Copyright    : Copyright (c) 2019.
 *      Author       : jiaoya.
 *      Created Time : 2019-07-21.
 *      Desc         :
 * </pre>
 */
public class LogUtil {

    private static Project sProject;

    static setProject(Project project) {
        sProject = project;
    }

    static void error(String content) {
        sProject.logger.error(content);
    }

    static void info(String content) {
        sProject.logger.info(content);
    }

    static void warn(String content) {
        sProject.logger.warn(content);
    }

    static void debug(String content) {
        sProject.logger.debug(content);
    }
}
