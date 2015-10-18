package com.jraska.core.database;

import android.database.sqlite.SQLiteDatabase;
import com.jraska.common.IDisposable;
import com.jraska.core.BaseApplication;
import com.jraska.core.services.IAppService;

public interface IDatabaseService extends IAppService, IDisposable {
  //region Methods

  SQLiteDatabase getReadableDatabase();

  SQLiteDatabase getWritableDatabase();

  //endregion

  //region Nested classes

  class Stub {
    public static IDatabaseService asInterface() {
      return BaseApplication.getService(IDatabaseService.class);
    }
  }

  //endregion
}
