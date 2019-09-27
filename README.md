# OkRouter
Android路由框架，支持模块间的路由、通信、解耦、支持多进程等


#### 最新版本

模块|okrouter|okrouter-compiler|okrouter-create
---|---|---|---
最新版本|[![Download](https://api.bintray.com/packages/albert-jiao/maven/okrouter/images/download.svg?version=0.0.1)](https://bintray.com/albert-jiao/maven/okrouter/0.0.1/link)|[![Download](https://api.bintray.com/packages/albert-jiao/maven/okrouter-compiler/images/download.svg?version=0.0.1)](https://bintray.com/albert-jiao/maven/okrouter-compiler/0.0.1/link)|[![Download](https://api.bintray.com/packages/albert-jiao/maven/okrouter-create/images/download.svg?version=0.0.1)](https://bintray.com/albert-jiao/maven/okrouter-create/0.0.1/link)


#### 一、功能介绍
1. **支持必须以“/”开头的string地址进行跳转**
2. **支持直接解析标准URL进行跳转，也支持url编码，并自动注入参数到目标页面中**
3. **支持添加多全局个拦截器，自定义拦截顺序；支持添加对单个地址进行拦截**
4. **支持在初始化时添加全局拦截器**
5. **支持多种方式配置转场动画**
6. **支持获取Fragment**
7. **支持binder方式进程间通讯并且支持线程间切换**
8. **支持多种方式配置转场动画**


#### 三、使用配置
1. 添加依赖和配置
 ``` 
 1.1、module-gradle：
 
    android {
        defaultConfig {
            ...
            javaCompileOptions {
                annotationProcessorOptions {
                    arguments = [OKROUTER_MODULE_NAME: project.getName()]
                }
            }
        }
    }

    dependencies {
        // 替换成最新版本, 需要注意的是api
        // 要与compiler匹配使用，均使用最新版可以保证兼容
        implementation 'com.albert.android:okrouter:0.0.1'
		annotationProcessor 'com.albert.android:okrouter-compiler:0.0.1'
        ...
    }
    
    
  	1.2、插件实现路由表的自动加载
  		project-gradle：
  		repositories {
    		jcenter()
		}
		
		dependencies {
    		classpath 'com.albert.plugin:okrouter-create:0.0.1'
		}

		main-gradle：
		apply plugin: 'com.okrouter.plugin'

 ```
2. Appliaction初始化
```
  OkRouterConfig.getInstance()
        .init(this, false)
        // 添加全局拦截器，默认第一个
        .addInterceptor(new RouterInterceptor() {
            @Override
            public void intercept(RouteEntity routeEntity, InterceptorCallback callback) {
                routeEntity.putString("age", "1");
                callback.onContinue(routeEntity);
            }
        });
        
  	// isOpenLog 打开日志, isAuto是否要自动获取当前activity,如果不设置，当不传入当前Activity时，会使用Application进行跳转
  	init(Application context, boolean isOpenLog, boolean isAuto) 
  
```

3. 添加页面跳转注解
```
    // 在支持路由的页面上添加注解(必选)
    // 这里的路径必须以‘/’开始，/xx/xx
    @Route(adress = "/test/activity")
    public class MainActivity extend Activity {
        ...
    }
  
    // 支持标准URL,也可以添加参数：okrouter://test/Main2Activity?a=1
    @Route(adress = "okrouter://test/Main2Activity")
    public class Main2Activity extend Activity {
        ...
    } 
```

4. 声明拦截器
```
  // 添加全局拦截器会在跳转之间执行，多个拦截器会按优先级顺序依次执行
  @Interceptor(priority = 1)
  public class InterceptorTest implements RouterInterceptor {
    @Override
    public void intercept(RouteEntity routeEntity, InterceptorCallback callback) {
        routeEntity.putString("weight", "my weight = 70kg ");
        callback.onContinue(routeEntity);
        // 异常返回callback.onInterrupt();中断路由
    	}
  }
  
  // 添加单个地址拦截器会，adress要拦截的地址
  @InterceptPoint(adress = "/app/Main3Activity")
  public class InterceptorPointTest implements 	RouterInterceptor {
    	@Override
    	public void intercept(RouteEntity routeEntity, 	InterceptorCallback callback) {
        	routeEntity.putBoolean("point", false);
        	callback.onContinue(routeEntity);
    	}
  }
```

5. 跳转
```
OkRouter.getInstance().build("router://module1/Model1TestActivity?name=albert&age=18").navigation();
	
OkRouter.getInstance().build("router://module1/Model1TestActivity?name=albert&age=18").navigation(this);
	
// 可以调用putxxx进程设置参数，通过bundle进行传递
OkRouter.getInstance().build("/WebViewTestActivity").putString("1","2").navigation();

```
6. 转场动画
```
	// 常规方式
	ARouter.getInstance()
	    .build("/test/MainActivity")
	    .withTransition(R.anim.slide_in_bottom, R.anim.slide_out_bottom)
	    .navigation(this);
	
	// 转场动画(API16+)
	ActivityOptionsCompat compat = ActivityOptionsCompat.
	    makeScaleUpAnimation(v, v.getWidth() / 2, v.getHeight() / 2, 0, 0);
	
	// ps. makeSceneTransitionAnimation 使用共享元素的时候，需要在navigation方法中传入当前Activity
	ARouter.getInstance()
	    .build("/test/MainActivity")
	    .withOptionsCompat(compat)
	    .navigation();

```
7. 进程间通讯
```
  	1.创建服务
  	// 必须创建Service，必须继承ProviderService
  	@Provider(processName = "com.albert.okrouter.demo")
  	public class AppServiceTest extends ProviderService {
  		...
  	}
  	// processName进程名与AndroidManifest.xml android:process配置对应
  	<service
   			android:name=".AppServiceTest"
    		android:process="com.albert.okrouter.demo" />

	2.创建action接口
	// 必须继承IBaseAction，用来进程间传递数据，也可以不依赖模块名进行数据传递
	// processName：进程名，adress：action的地址，如果不填写processName，默认主进程
	@Action(processName = "com.albert.okrouter.demo", adress = "AppTestAction")
	public class AppTestAction implements IBaseAction {
	
		@Override
		public ActionResult invoke(Context context, Bundle bundle) {
			// Bundle里携带传过来的参数
			// ActionResult 接口返回的结果
	    	ActionResult result = new ActionResult();
	    	result.setStringData("jiaoya+AppTestAction");
	    	return result;
   	 	}
	}

	3.使用
	OkRouter.getInstance()
	    	.bind("com.albert.okrouter.demo","AppTestAction")
	    	.callbackOn(RouterScheduler.MAIN)
	    	.getAction(new ActionCallback() {
	        	@Override
	        	public void result(ActionResult result) {
	            	Log.e(TAG, result.getStringData());
	            	tvShow.setText("结果：" + 		result.getStringData());
	        	}
	
				@Override
	        	public void error(Exception e) {
	            	Log.e(TAG, e.toString() + "");
	        	}
	    	});

     // putxxx 出入参数
     .putString("test", "我是测试2")

	 // 回调线程切换，RouterScheduler.MAIN：主线程 RouterScheduler.IO：子线程  RouterScheduler.NORMAL：默认发起线程
	 .callbackOn(RouterScheduler.MAIN)
	    		
	// 直接获取接口，可以跨模块，不需要相互依赖
	AppTestAction1 appTestAction1 = (AppTestAction1)OkRouter.getInstance().bind("AppTestAction1").getAction();
	ActitvShow.setText("获取接口结果：" + result.getStringData());
	Log.e(TAG, result.getStringData());

```

8. 其他
  1. **谢谢使用和支持**
  2. **如有问题请github上反馈给我**
  3. **第一版功能不是很全，如有更好的想法，请反馈给我**
  4. **[简书地址](https://www.jianshu.com/u/24fcedd34db7)**
  5. **参考rxjava/arouter**


