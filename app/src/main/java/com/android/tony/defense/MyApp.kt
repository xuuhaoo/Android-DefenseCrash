package com.android.tony.defense

import android.app.Application
import android.content.Context
import android.util.Log
import com.android.tony.defenselib.DefenseCrash
import com.android.tony.defenselib.handler.IExceptionHandler

class MyApp : Application(), IExceptionHandler {
  override fun attachBaseContext(base: Context) {
    super.attachBaseContext(base)
    DefenseCrash.initialize(this)
    DefenseCrash.install (this)
  }

  override fun onCreate() {
    super.onCreate()
    throw NullPointerException("测试崩溃 Application onCreate")
  }

  @Throws(Throwable::class)
  override fun onCaughtException(thread: Thread, throwable: Throwable, isSafeMode: Boolean, isCrashInChoreographer: Boolean) {
    Log.i("Exceptionhandler",
      "thread:${thread.name} " +
          "exception:${throwable.message} " +
          "isCrashInChoreographer:$isCrashInChoreographer " +
          "isSafeMode:$isSafeMode")
    throwable.printStackTrace()
    //    throw throwable;
    //
    //    if(isCrashInChoreographer){
    //                throw throwable;
    //            }
  }
}