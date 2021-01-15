# Android Defense Crash

[ ![Download](https://api.bintray.com/packages/xuuhaoo/maven/DefenseCrash/images/download.svg) ](https://bintray.com/xuuhaoo/maven/DefenseCrash/_latestVersion)
[![TonyStark](https://img.shields.io/badge/TonyStark-IronMan-red.svg)]()

### 这是啥
这是一个可以`防止程序运行时Crash`的库,可以帮助你捕获Android运行时的`JavaException`并且保证程序依然高可用.


### 我要集成
* 第一步: 找到项目目录下的`build.gradle`文件,并且加入如下代码

```groovy
allprojects {
	repositories {
        //其他的仓库..
        maven(){
            url "https://dl.bintray.com/xuuhaoo/maven"
        }
    }
}
```

* 第二步: 找到你需要集成的模块中的`build.gradle`文件,并且添加依赖

```groovy
dependencies {
    implementation 'com.tonystark.android:defense-crash:最新版本’
}
```
> 注意: `最新版本`是一个代位词, 真正的版本号请参照 [ ![Download](https://api.bintray.com/packages/xuuhaoo/maven/DefenseCrash/images/download.svg) ](https://bintray.com/xuuhaoo/maven/DefenseCrash/_latestVersion)

### 怎么用
* 尽量早的初始化这个库, 我们建议你将如下代码放到`Application` 中的`attachBaseContext(base:Context)`方法中.
	* 示例

	```kotlin
		  override fun attachBaseContext(base: Context) {
				super.attachBaseContext(base)
				//初始化库
				DefenseCrash.initialize(this)
				...
 		 }
	```
* 安装`防止崩溃`库在初始化之后
	* 示例

	```kotlin
		  override fun attachBaseContext(base: Context) {
				super.attachBaseContext(base)
				DefenseCrash.initialize(this)
				DefenseCrash.install { thread, throwable, isSafeMode, isCrashInChoreographer ->
      				//thread: 异常崩溃在的线程对象
					//throwable: 具体的异常对象
					//isSafeMode: 如果应用程序崩溃过,但是被我们捕获,那么这个值将会是true来告知开发人员,
					//具体来讲就是当你的主线程(Main Looper)已经被错误破坏不能够正常loop的时候,我们将使用魔法保证他运行.这称之为安全模式
					//isCrashInChoreographer: 如果崩溃发生在 OnMeasure/OnLayout/OnDraw 方法中,这将会导致程序白屏或黑屏亦或是一些View显示不成功					
					//当你收到这个值为True的时候,我们建议你关闭或者重启当前的Activity

					//你当然可以在本方法中抛出异常,但是你的抛出将会被虚拟机(VM)捕获并且你的进程将被它关闭
					//我们建议你在这里进行如下操作:
					Log.i("Exceptionhandler",
							"thread:${thread.name} " +
							"exception:${throwable.message} " +
         					“isCrashInChoreographer:$isCrashInChoreographer " +
							"isSafeMode:$isSafeMode")
    				throwable.printStackTrace()
					//上报异常到你的统计平台
					FirebaseCrashlytics.getInstance().recordException(throwable);
   				}
 		 }
	```

* 卸载本库,如果你不再需要
	* 示例

	```kotlin
		DefenseCrash.unInstall()
	```
