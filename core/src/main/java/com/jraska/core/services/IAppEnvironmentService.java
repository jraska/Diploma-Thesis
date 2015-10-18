package com.jraska.core.services;

import com.jraska.core.BaseApplication;

import java.io.File;

public interface IAppEnvironmentService extends IAppService {
  //region Methods

  File getAppDataRootDirectory();

  //endregion

  //region Nested class

  class Stub {
    public static IAppEnvironmentService asInterface() {
      return BaseApplication.getService(IAppEnvironmentService.class);
    }
  }

  //endregion
}
