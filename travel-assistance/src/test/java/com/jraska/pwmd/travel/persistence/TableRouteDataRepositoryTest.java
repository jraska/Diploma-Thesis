package com.jraska.pwmd.travel.persistence;

import android.database.sqlite.SQLiteOpenHelper;


public class TableRouteDataRepositoryTest extends BaseRouteDataRepositoryTest {
  //region BaseRouteDataRepositoryTest impl

  @Override
  protected TravelDataRepository createRepository(SQLiteOpenHelper openHelper) {
    return new TableRouteDataRepository(openHelper);
  }

  //endregion
}
