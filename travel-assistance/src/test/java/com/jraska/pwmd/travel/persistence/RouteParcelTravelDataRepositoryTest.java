package com.jraska.pwmd.travel.persistence;

import android.database.sqlite.SQLiteOpenHelper;

public class RouteParcelTravelDataRepositoryTest extends BaseRouteDataRepositoryTest {
  //region BaseRouteDataRepositoryTest impl

  @Override protected TravelDataRepository createRepository(SQLiteOpenHelper openHelper) {
    return new RouteParcelTravelDataRepository(openHelper);
  }

  //endregion
}