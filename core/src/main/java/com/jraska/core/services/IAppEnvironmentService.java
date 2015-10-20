package com.jraska.core.services;

import com.jraska.core.BaseApp;

import java.io.File;

public interface IAppEnvironmentService extends IAppService {
  //region Methods

  File getAppDataRootDirectory();

  //endregion

  //region Nested class

  class Stub {
    public static IAppEnvironmentService asInterface() {
      return BaseApp.getService(IAppEnvironmentService.class);
    }
  }

  //endregion
}
