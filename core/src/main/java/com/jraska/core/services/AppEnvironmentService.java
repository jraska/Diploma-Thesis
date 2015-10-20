package com.jraska.core.services;

import com.jraska.core.BaseApp;

import java.io.File;

public interface AppEnvironmentService extends AppService {
  //region Methods

  File getAppDataRootDirectory();

  //endregion

  //region Nested class

  class Stub {
    public static AppEnvironmentService asInterface() {
      return BaseApp.getService(AppEnvironmentService.class);
    }
  }

  //endregion
}
