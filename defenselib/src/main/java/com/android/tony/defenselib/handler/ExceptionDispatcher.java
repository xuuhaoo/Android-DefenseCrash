package com.android.tony.defenselib.handler;


import com.android.tony.defenselib.exception.ExemptCaughtException;
import com.android.tony.defenselib.hook.SafeMode;

public class ExceptionDispatcher {

  private IExceptionHandler mIExceptionHandler;

  public ExceptionDispatcher() {
  }

  public final void uncaughtExceptionHappened(Thread thread, Throwable throwable) throws ExemptCaughtException {
    if (mIExceptionHandler == null) {
      return;
    }
    try {
      mIExceptionHandler.onCaughtException(thread, throwable, SafeMode.isIsSafeMode(), maybeChoreographerException(throwable));
    } catch (Throwable e) {
      throw new ExemptCaughtException(e);
    }
  }

  public void setIExceptionHandler(IExceptionHandler IExceptionHandler) {
    mIExceptionHandler = IExceptionHandler;
  }

  /**
   * 判断是否是测量,布局,绘制过程中导致的异常
   *
   * @param e
   */
  private boolean maybeChoreographerException(Throwable e) {
    if (e == null) {
      return false;
    }
    StackTraceElement[] elements = e.getStackTrace();
    if (elements == null) {
      return false;
    }

    for (int i = elements.length - 1; i > -1; i--) {
      if (elements.length - i > 20) {
        return false;
      }
      StackTraceElement element = elements[i];
      if ("android.view.Choreographer".equals(element.getClassName())
          && "Choreographer.java".equals(element.getFileName())
          && "doFrame".equals(element.getMethodName())) {
        return true;
      }
    }
    return false;
  }

  public static boolean hasExemptException(Throwable throwable) {
    if (throwable == null) {
      return false;
    }
    if (throwable instanceof ExemptCaughtException) {
      return true;
    }
    if (throwable.equals(throwable.getCause())) {
      return false;
    }
    return hasExemptException(throwable.getCause());
  }
}
