package com.jraska.core.services;

import com.jraska.common.ArgumentCheck;

import java.io.File;

public class SimpleAppEnvironmentService implements AppEnvironmentService {
  //region Fields

  private final File _rootAppDir;

  //endregion

  //region Constructors

  public SimpleAppEnvironmentService(File rootAppDir) {
    ArgumentCheck.notNull(rootAppDir, "rootAppDir");

    _rootAppDir = rootAppDir;

    ensureDirExists(_rootAppDir);
  }

  //endregion

  //region IAppEnvironmentService impl

  @Override
  public File getAppDataRootDirectory() {
    return _rootAppDir;
  }

  //endregion

  //region methods

  protected void ensureDirExists(File rootAppDir) {
    if (!rootAppDir.exists()) {
      boolean created = rootAppDir.mkdirs();
      if (!created) {
        throw new RuntimeException(String.format("Error creating RootAppDir: %s", rootAppDir.getAbsolutePath()));
      }
    }
  }

  //endregion
}
