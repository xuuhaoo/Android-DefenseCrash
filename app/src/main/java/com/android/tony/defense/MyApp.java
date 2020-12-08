package com.android.tony.defense;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.android.tony.defenselib.DefenseCrash;
import com.android.tony.defenselib.handler.IExceptionHandler;


public class MyApp extends Application implements IExceptionHandler {
  @Override
  protected void attachBaseContext(Context base) {
    super.attachBaseContext(base);
    DefenseCrash.initialize(this);
    DefenseCrash.install(this, true, true, true);
  }

  @Override
  public void onCreate() {
    super.onCreate();

    throw new NullPointerException("测试崩溃 Application onCreate");
  }

  @Override
  public void onCaughtException(Thread thread, Throwable throwable, boolean isSafeMode, boolean isCrashInChoreographer) throws Throwable {
    Log.i("Exceptionhandler",
        "thread:" + thread.getName() + " exception:" + throwable.getMessage() + " isCrashInChoreographer:" + isCrashInChoreographer + " isSafeMode:" +
            isSafeMode);
    throwable.printStackTrace();
//    throw throwable;
//
    //    if(isCrashInChoreographer){
    //                throw throwable;
    //            }
  }
}
