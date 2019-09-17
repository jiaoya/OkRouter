package com.okrouter.plugin

public class PluginSetting {

    /**
     * 扫描过滤的包名，apt在此包名下生成各个模块的路由集合，对应：模块名/bulid/generted/source/apt/debug/...
     */
    public static final String MOLDE_PAHTS_CACHE_PACKAGE_NAME = "com.ablert.okrouter.cache"
    /**
     * 模块内获取类的路径集合的方法名
     */
    public static final String MODEL_GET_PATHS_METHOD_NAME = "getMoldeClassPaths"

    /**
     * 模块内获取全局拦截器集合的方法名
     */
    public static final String MODEL_GET_INTERCEPTORS_METHOD_NAME = "getModelInterceptors"

    /**
     * 模块内获取局部/点烂机器集合的方法名
     */
    public static final String MODEL_GET_INTERCEPTORS_POINT_METHOD_NAME = "getModelInterceptorsPoint"

    /**
     * 模块内获取局部/点烂机器集合的方法名
     */
    public static final String MODEL_GET_PROVIDES = "getModelProvides"

    /**
     * 模块内获取局部/点烂机器集合的方法名
     */
    public static final String MODEL_GET_ACTIONS = "getModelActions"


    /**
     * 生成文件的路径，Javassist生成新类的路径，.gradle文件是不被打包到apk里的，只是用来临时显示
     */
    public static final String CREATE_FILE_ROUTER_CALSS_PATHS = "./.gradle/router/"

    /**
     * 导入RouterInfo中的包名/类
     */
    public static final String IMPORT_ROUTER_CLASS_UniqueKeyTreeMap = "com.albert.okrouter.annotation.model.UniqueKeyTreeMap"
    /**
     * 最终生成类的路径集合全量类名:RouterClassPaths
     */
    public static final String ROUTER_CALSS_PATHS = "com.ablert.okrouter.cache.RouterInfo"
    // 静态变量名
    public static final String ALL_CLASS_PATHS = "ALL_CLASS_PATHS"
    public static final String ALL_INTERCEPTORS = "ALL_INTERCEPTORS"
    public static final String ALL_INTERCEPTORS_POINT = "ALL_INTERCEPTORS_POINT"
    public static final String ALL_PROVIDERS = "ALL_PROVIDES"
    public static final String ALL_ACTIONS = "ALL_ACTIONS"


    // 静态方法名
    public static final String GET_ALL_CLASS_PATH_METHOD = "getAllClassPaths"
    public static final String GET_ALL_INTERCEPTOS_METHOD = "getAllInterceptors"
    public static final String GET_ALL_INTERCEPTOS_POINT_METHOD = "getAllInterceptorsPoint"
    public static final String GET_ALL_PROVIDERS_METHOD = "getAllProviders"
    public static final String GET_ALL_ACTIONS_METHOD = "getAllActions"


}