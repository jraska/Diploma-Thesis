package com.jraska.core.services;

import com.jraska.core.BaseApp;

public class DefaultExternalStorageAppEnvironmentService extends SimpleAppEnvironmentService {
  //region Constructors

  public DefaultExternalStorageAppEnvironmentService() {
    super(BaseApp.getCurrent().getExternalFilesDir(null)); //null is for default dir
  }

  //endregion
}
