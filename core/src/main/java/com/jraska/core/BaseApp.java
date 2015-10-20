package com.jraska.core;

import android.app.Application;

/**
 * Base class for all applications
 */
public abstract class BaseApp extends Application {
  //region Static

  private static BaseApp _current;

  public static BaseApp getCurrent() {
    return _current;
  }

  public static <T> T getService(Class<T> serviceType) {
    // FIXME: remove method with exception
    throw new UnsupportedOperationException("Delete this method");
  }

  //endregion

  //region Fields


  //endregion

  //region Constructors

  public BaseApp() {
    _current = this;
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
