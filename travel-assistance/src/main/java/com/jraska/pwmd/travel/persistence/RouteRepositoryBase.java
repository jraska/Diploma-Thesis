package com.jraska.pwmd.travel.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.common.events.Observable;
import com.jraska.common.events.ObservableImpl;
import com.jraska.core.persistence.DbRepositoryBase;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class RouteRepositoryBase extends DbRepositoryBase implements TravelDataPersistenceService {
  //region Fields

  private ObservableImpl<RouteDescription> _newRouteEvent;

  //endregion

  //region Constructors

  protected RouteRepositoryBase(SQLiteOpenHelper sqliteOpenHelper) {
    super(sqliteOpenHelper);
  }

  //endregion

  //region ITravelDataPersistenceService impl

  @Override
  public Observable<RouteDescription> getOnNewRoute() {
    if (_newRouteEvent == null) {
      _newRouteEvent = new ObservableImpl<>();
    }

    return _newRouteEvent;
  }

  @Override
  public List<RouteDescription> selectAllRouteDescriptions() {
    return getRouteDescriptionsFromDatabase();
  }

  @Override
  public long updateRoute(RouteData routeData) {
    SQLiteDatabase database = getWritableDatabase();

    database.beginTransaction();
    try {
      deleteRoute(routeData.getId());
      long route = insertRoute(routeData);

      database.setTransactionSuccessful();
      return route;
    }
    finally {
      database.endTransaction();
    }
  }

  //endregion

  //region Methods

  protected List<RouteDescription> getRouteDescriptionsFromDatabase() {
    Cursor cursor = getReadableDatabase().query(DbModel.RoutesTable.TABLE_NAME, DbModel.RoutesTable.DESCRIPTION_COLUMNS, null, null, null, null, null);

    List<RouteDescription> descriptions = new ArrayList<>();

    try {
      while (cursor.moveToNext()) {
        RouteDescription routeDescription = readRouteDescription(cursor);
        descriptions.add(routeDescription);
      }
    }
    finally {
      cursor.close();
    }

    return descriptions;
  }

  protected RouteDescription readRouteDescription(Cursor c) {
    String idValue = c.getString(c.getColumnIndex(DbModel.RoutesTable.COLUMN_ID));
    UUID id = idFromDbValue(idValue);

    String title = c.getString(c.getColumnIndex(DbModel.RoutesTable.COLUMN_TITLE));

    String startValue = c.getString(c.getColumnIndex(DbModel.RoutesTable.COLUMN_START));
    Date start = parseDbDate(startValue);

    String endValue = c.getString(c.getColumnIndex(DbModel.RoutesTable.COLUMN_END));
    Date end = parseDbDate(endValue);

    RouteDescription routeDescription = new RouteDescription(id, start, end, title);
    return routeDescription;
  }

  protected void onNewRoute(RouteData routeData) {
    if (_newRouteEvent != null) {
      _newRouteEvent.notify(this, routeData.getDescription());
    }
  }

  //endregion
}
