package com.android.tony.defenselib.handler;

public interface IExceptionHandler {
  /**
   * when we caught the exception,this method will be called.
   * you should print the stack of the throwable,let developer know this crash.
   * when you release this apk,you can upload this throwable to you bug collection sdk.
   *
   * @param thread which thread crashed.
   * @param throwable crash throwable.
   * @param isSafeMode it is already in safe mode,if it is true,will mean the previous crash led to this crash
   * @param isCrashInChoreographer Whether a crash occurred in the draw process.
   */
  void onCaughtException(Thread thread, Throwable throwable, boolean isSafeMode, boolean isCrashInChoreographer) throws Throwable;

}
