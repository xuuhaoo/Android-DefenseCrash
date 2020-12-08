package com.android.tony.defenselib.hook;

import java.lang.reflect.InvocationTargetException;

import android.app.Instrumentation;
import android.content.Context;
import android.os.Looper;

import com.android.cglib.proxy.Enhancer;
import com.android.cglib.proxy.MethodInterceptor;
import com.android.cglib.proxy.MethodProxy;
import com.android.tony.defenselib.handler.ExceptionDispatcher;


public class InstrumentationProxy implements MethodInterceptor {
  private Context mContext;

  private Instrumentation mInstrumentation;

  private ExceptionDispatcher mExceptionDispatcher;

  public InstrumentationProxy(Context context, Instrumentation instrumentation, ExceptionDispatcher exceptionDispatcher) {
    this.mContext = context;
    this.mInstrumentation = instrumentation;
    this.mExceptionDispatcher = exceptionDispatcher;
  }

  public Instrumentation getProxy(Class<? extends Instrumentation> clz) {
    Enhancer enhancer = new Enhancer(mContext);
    enhancer.setSuperclass(clz);
    enhancer.setInterceptor(this);
    return (Instrumentation) enhancer.create();
  }

  @Override
  public Object intercept(Object object, Object[] args, MethodProxy methodProxy) throws Exception {
    if ("onException".equals(methodProxy.getMethodName()) && !(ExceptionDispatcher.hasExemptException((Throwable) args[1]))) {
      mExceptionDispatcher.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), (Throwable) args[1]);
      return true;
    }
    try {
      return methodProxy.invokeSuper(mInstrumentation, args);
    } catch (Throwable e) {
      if (e instanceof InvocationTargetException) {
        Throwable temp = ((InvocationTargetException) e).getTargetException();
        if (temp != null) {
          e = temp;
        }
      }
      if (!(ExceptionDispatcher.hasExemptException(e))) {
        mExceptionDispatcher.uncaughtExceptionHappened(Looper.getMainLooper().getThread(), e);
      }
    }
    return null;
  }

}
