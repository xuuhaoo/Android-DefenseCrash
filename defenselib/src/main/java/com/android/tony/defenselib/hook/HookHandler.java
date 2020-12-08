package com.android.tony.defenselib.hook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.os.Handler;
import android.os.Looper;

import com.android.tony.defenselib.handler.ExceptionDispatcher;

public class HookHandler implements IHook {

  private volatile boolean isHooked;

  private ExceptionDispatcher mExceptionDispatcher;

  private Handler.Callback mOriginCallback;

  public HookHandler(ExceptionDispatcher exceptionDispatcher) {
    mExceptionDispatcher = exceptionDispatcher;
  }

  @Override
  public void hook() {
    if (isHooked()) {
      return;
    }
    try {
      Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
      Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
      currentActivityThreadMethod.setAccessible(true);
      Object currentActivityThread = currentActivityThreadMethod.invoke(null);

      Field mHField = activityThreadClass.getDeclaredField("mH");
      mHField.setAccessible(true);
      Handler handler = (Handler) mHField.get(currentActivityThread);
      Field callbackField = Handler.class.getDeclaredField("mCallback");
      callbackField.setAccessible(true);
      mOriginCallback = (Handler.Callback) callbackField.get(handler);
      Handler.Callback fakeCallback = msg -> {
        try {
          if (mOriginCallback != null) {
            if (!mOriginCallback.handleMessage(msg)) {
              handler.handleMessage(msg);
            } else {
              //do nothing
            }
          } else {
            handler.handleMessage(msg);
          }
        } catch (Throwable e) {
          mExceptionDispatcher.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), e);
        }
        return true;
      };
      callbackField.set(handler, fakeCallback);

      synchronized (this) {
        isHooked = true;
      }
    } catch (Throwable ignored) {
      ignored.printStackTrace();
      synchronized (this) {
        isHooked = false;
      }
    }
  }

  @Override
  public void unHook() {
    if (!isHooked()) {
      return;
    }
    try {
      Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
      Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
      currentActivityThreadMethod.setAccessible(true);
      Object currentActivityThread = currentActivityThreadMethod.invoke(null);

      Field mHandlerField = activityThreadClass.getDeclaredField("mH");
      mHandlerField.setAccessible(true);
      Handler handler = (Handler) mHandlerField.get(currentActivityThread);
      Field callbackField = Handler.class.getDeclaredField("mCallback");
      callbackField.setAccessible(true);
      callbackField.set(handler, mOriginCallback);
      synchronized (this) {
        isHooked = false;
      }
    } catch (Throwable ignored) {
      ignored.printStackTrace();
      synchronized (this) {
        isHooked = true;
      }
    }
  }

  @Override
  public boolean isHooked() {
    return isHooked;
  }

}
