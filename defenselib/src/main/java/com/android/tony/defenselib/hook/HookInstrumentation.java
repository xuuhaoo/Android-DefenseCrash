package com.android.tony.defenselib.hook;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.app.Instrumentation;
import android.content.Context;

import com.android.tony.defenselib.handler.ExceptionDispatcher;

public class HookInstrumentation implements IHook {

  private volatile boolean isHooked;

  private Context mContext;

  private ExceptionDispatcher mExceptionDispatcher;

  private Instrumentation mEvilInstrumentation;

  private Instrumentation mOriginInstrumentation;

  public HookInstrumentation(ExceptionDispatcher exceptionDispatcher, Context context) {
    mExceptionDispatcher = exceptionDispatcher;
    mContext = context;
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

      Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
      mInstrumentationField.setAccessible(true);
      mOriginInstrumentation = (Instrumentation) mInstrumentationField.get(currentActivityThread);
      InstrumentationProxy proxy = new InstrumentationProxy(mContext, mOriginInstrumentation, mExceptionDispatcher);
      mEvilInstrumentation = proxy.getProxy(Instrumentation.class);

      // 创建代理对象
      Instrumentation evilInstrumentation = mEvilInstrumentation;

      mInstrumentationField.set(currentActivityThread, evilInstrumentation);
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

      Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
      mInstrumentationField.setAccessible(true);

      mInstrumentationField.set(currentActivityThread, mOriginInstrumentation);
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
