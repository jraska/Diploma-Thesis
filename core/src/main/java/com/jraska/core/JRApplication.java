package com.jraska.core;

import android.app.Application;

/**
 * Base class for all applications
 */
public abstract class JRApplication extends Application {
  //region Static

  private static JRApplication sCurrent;

  public static JRApplication getCurrent() {
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

  public JRApplication() {
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
