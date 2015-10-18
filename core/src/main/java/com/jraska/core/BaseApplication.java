package com.jraska.core;

import android.app.Application;

/**
 * Base class for all applications
 */
public abstract class BaseApplication extends Application {
  //region Static

  private static BaseApplication sCurrent;

  public static BaseApplication getCurrent() {
    return sCurrent;
  }

  public static <T> T getService(Class<T> serviceType) {
    // FIXME: remove method with exception
    throw new UnsupportedOperationException("Delete this method");
  }

  //endregion

  //region Fields


  //endregion

  //region Constructors

  public BaseApplication() {
    sCurrent = this;
  }

  //endregion

  //region Application overrides

  @Override
  public void onCreate() {
    super.onCreate();
  }

  //endregion

  //region Methods

  //endregion
}
