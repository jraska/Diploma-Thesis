package com.jraska.pwmd.travel.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.common.utils.ParcelableUtil;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.persistence.DbModel.RoutesTable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RouteParcelTravelDataRepository extends RouteRepositoryBase implements TravelDataPersistenceService {
  //region Constructors

  public RouteParcelTravelDataRepository(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  //endregion

  //region ITravelDataPersistenceService impl

  @Override
  public List<RouteDescription> selectAllRouteDescriptions() {
    return getRouteDescriptionsFromDatabase();
  }

  @Override
  public RouteData selectRouteData(UUID id) {
    return getRouteDataFromDatabase(id);
  }

  @Override
  public long deleteRoute(UUID id) {
    return deleteRouteFromDatabase(id);
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

  @Override
  public long insertRoute(RouteData routeData) {
    long id = insertRouteToDatabase(routeData);

    onNewRoute(routeData);

    return id;
  }

  //endregion

  //region Methods

  protected List<RouteDescription> getRouteDescriptionsFromDatabase() {
    Cursor cursor = getReadableDatabase().query(RoutesTable.TABLE_NAME, RoutesTable.DESCRIPTION_COLUMNS, null, null, null, null, null);

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

  protected RouteData getRouteDataFromDatabase(UUID id) {
    String[] args = {idToDbValue(id)};
    Cursor cursor = getReadableDatabase().query(RoutesTable.TABLE_NAME, RoutesTable.ALL_COLUMNS, "Id = ?", args, null, null, null);

    try {
      if (cursor.getCount() != 1) {
        return null;
      }

      cursor.moveToFirst();
      return readRouteData(cursor);
    }
    finally {
      cursor.close();
    }
  }

  protected RouteData readRouteData(Cursor c) {
    RouteDescription routeDescription = readRouteDescription(c);

    byte[] bytes = c.getBlob(c.getColumnIndex(RoutesTable.COLUMN_PATH));
    Path path = unParcelPath(bytes);

    RouteData routeData = new RouteData(routeDescription, path);
    return routeData;
  }

  protected RouteDescription readRouteDescription(Cursor c) {
    String idValue = c.getString(c.getColumnIndex(RoutesTable.COLUMN_ID));
    UUID id = idFromDbValue(idValue);

    String title = c.getString(c.getColumnIndex(RoutesTable.COLUMN_TITLE));

    String startValue = c.getString(c.getColumnIndex(RoutesTable.COLUMN_START));
    Date start = parseDbDate(startValue);

    String endValue = c.getString(c.getColumnIndex(RoutesTable.COLUMN_END));
    Date end = parseDbDate(endValue);

    RouteDescription routeDescription = new RouteDescription(id, start, end, title);
    return routeDescription;
  }

  protected long insertRouteToDatabase(RouteData routeData) {
    SQLiteDatabase database = getWritableDatabase();

    ContentValues contentValues = prepareRouteContentValues(routeData);

    return database.insertWithOnConflict(RoutesTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
  }

  protected long deleteRouteFromDatabase(UUID id) {
    String[] args = {idToDbValue(id)};

    return getWritableDatabase().delete(RoutesTable.TABLE_NAME, "Id = ?", args);
  }

  protected ContentValues prepareRouteContentValues(RouteData routeData) {
    ContentValues contentValues = new ContentValues();

    contentValues.put(RoutesTable.COLUMN_ID, routeData.getId().toString());
    contentValues.put(RoutesTable.COLUMN_TITLE, routeData.getTitle());
    contentValues.put(RoutesTable.COLUMN_START, formatDbDate(routeData.getStart()));
    contentValues.put(RoutesTable.COLUMN_END, formatDbDate(routeData.getEnd()));
    contentValues.put(RoutesTable.COLUMN_PATH, parcelPath(routeData.getPath()));

    return contentValues;
  }

  protected byte[] parcelPath(Path path) {
    return ParcelableUtil.marshall(path);
  }

  protected Path unParcelPath(byte[] data) {
    return ParcelableUtil.unMarshall(data, Path.CREATOR);
  }

  //endregion

  //region Nested classes


  //endregion
}
