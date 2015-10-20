package com.jraska.core.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.Disposable;


/**
 * Base class for sqlite database based on open helper
 */
public class OpenHelperDbService implements DatabaseService, Disposable {
  //region Fields

  private final SQLiteOpenHelper _helper;

  //endregion

  //region Constructors

  public OpenHelperDbService(SQLiteOpenHelper helper) {
    ArgumentCheck.notNull(helper);

    _helper = helper;
  }

  //endregion

  //region Properties

  public SQLiteOpenHelper getOpenHelper() {
    return _helper;
  }

  //endregion

  //region IDatabaseService implementation

  @Override
  public SQLiteDatabase getReadableDatabase() {
    return _helper.getWritableDatabase();
  }

  @Override
  public SQLiteDatabase getWritableDatabase() {
    return _helper.getReadableDatabase();
  }

  //endregion

  //region IDisposable impl

  @Override
  public void dispose() {
    _helper.close();
  }

  //endregion

  //region Nested classes


  //endregion
}
