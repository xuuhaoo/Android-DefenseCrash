package com.android.tony.defenselib.hook;

import android.os.Looper;

import com.android.tony.defenselib.handler.ExceptionDispatcher;

public class HookThreadLoop implements IHook {

  private volatile boolean isHooked;

  private ExceptionDispatcher mExceptionDispatcher;

  private Thread.UncaughtExceptionHandler mOriginHandler;

  public HookThreadLoop(ExceptionDispatcher exceptionDispatcher) {
    mExceptionDispatcher = exceptionDispatcher;
  }

  @Override
  public void hook() {
    if (isHooked()) {
      return;
    }
    mOriginHandler = Thread.getDefaultUncaughtExceptionHandler();
    Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
      if (t == Looper.getMainLooper().getThread()) {
        SafeMode.setIsSafeMode(true);
      }
      if (mExceptionDispatcher != null) {
        if (!(ExceptionDispatcher.hasExemptException(e))) {
          try {
            mExceptionDispatcher.uncaughtExceptionHappened(t, e);
          } catch (Exception ex) {
            mOriginHandler.uncaughtException(t, e);
          }
        }
      }
      if (t == Looper.getMainLooper().getThread()) {
        if (!ExceptionDispatcher.hasExemptException(e)) {
          enterSafeModeKeepLoop(mExceptionDispatcher);
        }
      }
      if (ExceptionDispatcher.hasExemptException(e)) {
        mOriginHandler.uncaughtException(t, e);
      }
    });
    synchronized (this) {
      isHooked = true;
    }
  }

  @Override
  public void unHook() {
    if (!isHooked()) {
      return;
    }
    Thread.setDefaultUncaughtExceptionHandler(mOriginHandler);
    synchronized (this) {
      isHooked = false;
    }
  }

  /**
   * Keep the Looper loop when crashed
   *
   * @param dispatcher
   */
  public void enterSafeModeKeepLoop(ExceptionDispatcher dispatcher) {
    if (!SafeMode.isIsSafeMode()) {
      SafeMode.setIsSafeMode(true);
    }
    while (true) {
      try {
        Looper.loop();
      } catch (Throwable e) {
        if (dispatcher != null) {
          if (!(ExceptionDispatcher.hasExemptException(e))) {
            try {
              dispatcher.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), e);
            } catch (Exception ex) {
              mOriginHandler.uncaughtException(Looper.getMainLooper().getThread(), e);
            }
          } else {
            mOriginHandler.uncaughtException(Looper.getMainLooper().getThread(), e);
            break;
          }
        }
      }
      if (!isHooked()) {
        break;
      }
    }
  }

  @Override
  public boolean isHooked() {
    return isHooked;
  }
}
