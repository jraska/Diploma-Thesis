package com.jraska.core.database;

import android.database.sqlite.SQLiteDatabase;
import com.jraska.common.Disposable;
import com.jraska.core.BaseApp;
import com.jraska.core.services.AppService;

public interface DatabaseService extends AppService, Disposable {
  //region Methods

  SQLiteDatabase getReadableDatabase();

  SQLiteDatabase getWritableDatabase();

  //endregion

  //region Nested classes

  class Stub {
    public static DatabaseService asInterface() {
      return BaseApp.getService(DatabaseService.class);
    }
  }

  //endregion
}
