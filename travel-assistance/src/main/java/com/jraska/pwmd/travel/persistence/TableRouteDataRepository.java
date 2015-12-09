package com.jraska.pwmd.travel.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.data.TransportChangeSpec;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class TableRouteDataRepository extends RouteRepositoryBase {
  //region Constructors

  public TableRouteDataRepository(SQLiteOpenHelper openHelper) {
    super(openHelper);
  }

  //endregion

  //region ITravelDataPersistenceService impl

  @Override
  public RouteData selectRouteData(UUID id) {
    return getRouteDataFromDatabase(id);
  }

  @Override
  public long deleteRoute(UUID id) {
    return deleteRouteFromDatabase(id);
  }

  @Override
  public long insertRoute(RouteData routeData) {
    long id = insertRouteToDatabase(routeData);

    onNewRoute(routeData);

    return id;
  }

  //endregion

  //region Methods

  protected RouteData getRouteDataFromDatabase(UUID id) {
    String[] args = {idToDbValue(id)};
    Cursor cursor = getReadableDatabase().query(DbModel.RoutesTable.TABLE_NAME, DbModel.RoutesTable.ALL_COLUMNS, "Id = ?", args, null, null, null);

    RouteDescription routeDescription;
    try {
      if (cursor.getCount() != 1) {
        return null;
      }

      cursor.moveToFirst();
      routeDescription = readRouteDescription(cursor);
    }
    finally {
      cursor.close();
    }

    cursor = getReadableDatabase().query(DbModel.PointsTable.TABLE_NAME, null, "RouteId = ?", args, null, null, null);

    Path path;
    try {
      path = readPath(cursor);
    }
    finally {
      cursor.close();
    }

    cursor = getReadableDatabase().query(DbModel.TransportChangesTable.TABLE_NAME, null, "RouteId = ?", args, null, null, null);
    try {
      List<TransportChangeSpec> specs = readChanges(cursor);

      return new RouteData(routeDescription, path, specs);
    }
    finally {
      cursor.close();
    }
  }

  protected List<TransportChangeSpec> readChanges(Cursor cursor) {
    if (cursor.getCount() == 0) {
      return Collections.emptyList();
    }

    List<TransportChangeSpec> specs = new ArrayList<>();

    while (cursor.moveToNext()) {
      // Data are rad as Strings to remove conversion errors
      String latitude = cursor.getString(cursor.getColumnIndex(DbModel.TransportChangesTable.COLUMN_LATITUDE));
      String longitude = cursor.getString(cursor.getColumnIndex(DbModel.TransportChangesTable.COLUMN_LONGITUDE));
      double lat = Double.parseDouble(latitude);
      double lon = Double.parseDouble(longitude);
      LatLng latLng = new LatLng(lat, lon);

      int type = cursor.getInt(cursor.getColumnIndex(DbModel.TransportChangesTable.COLUMN_TRANSPORTATION_TYPE));
      String title = cursor.getString(cursor.getColumnIndex(DbModel.TransportChangesTable.COLUMN_TITLE));
      String description = cursor.getString(cursor.getColumnIndex(DbModel.TransportChangesTable.COLUMN_DESCRIPTION));

      TransportChangeSpec spec = new TransportChangeSpec(latLng, type, title, description);
      specs.add(spec);
    }

    return specs;
  }

  protected Path readPath(Cursor cursor) {
    List<Position> positions = new ArrayList<>(cursor.getCount());

    while (cursor.moveToNext()) {
      String latitude = cursor.getString(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_LATITUDE));
      String longitude = cursor.getString(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_LONGITUDE));
      String accuracy = cursor.getString(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_ACCURACY));
      long time = cursor.getLong(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_TIME));
      String provider = cursor.getString(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_PROVIDER));

      double lat = Double.parseDouble(latitude);
      double lon = Double.parseDouble(longitude);
      float acc = Float.parseFloat(accuracy);

      Position position = new Position(lat, lon, time, acc, provider);
      positions.add(position);
    }

    return new Path(positions);
  }

  protected long insertRouteToDatabase(RouteData routeData) {
    SQLiteDatabase database = getWritableDatabase();

    ContentValues contentValues = prepareRouteContentValues(routeData);

    database.beginTransaction();
    long insert = database.insertWithOnConflict(DbModel.RoutesTable.TABLE_NAME, null,
        contentValues, SQLiteDatabase.CONFLICT_REPLACE);

    writePath(routeData.getId(), routeData.getPath());
    writeTransportChanges(routeData.getId(), routeData.getTransportChangeSpecs());

    database.setTransactionSuccessful();
    database.endTransaction();
    return insert;
  }

  protected void writeTransportChanges(UUID routeDataId, List<TransportChangeSpec> specs) {
    SQLiteDatabase database = getWritableDatabase();

    for (TransportChangeSpec spec : specs) {
      ContentValues values = prepareChangeValues(spec, routeDataId);

      database.insert(DbModel.TransportChangesTable.TABLE_NAME, null, values);
    }
  }

  private ContentValues prepareChangeValues(TransportChangeSpec spec, UUID routeDataId) {

    ContentValues contentValues = new ContentValues();
    contentValues.put(DbModel.TransportChangesTable.COLUMN_ID, idToDbValue(UUID.randomUUID()));
    contentValues.put(DbModel.TransportChangesTable.COLUMN_ROUTE_ID, idToDbValue(routeDataId));
    contentValues.put(DbModel.TransportChangesTable.COLUMN_LATITUDE, spec.latLng._latitude);
    contentValues.put(DbModel.TransportChangesTable.COLUMN_LONGITUDE, spec.latLng._longitude);
    contentValues.put(DbModel.TransportChangesTable.COLUMN_TRANSPORTATION_TYPE, spec.transportType);
    contentValues.put(DbModel.TransportChangesTable.COLUMN_TITLE, spec.title);
    contentValues.put(DbModel.TransportChangesTable.COLUMN_DESCRIPTION, spec.description);

    return contentValues;
  }

  protected void writePath(UUID routeId, Path path) {
    SQLiteDatabase database = getWritableDatabase();
    for (Position position : path.getPoints()) {
      ContentValues values = preparePointValues(position, routeId);

      database.insert(DbModel.PointsTable.TABLE_NAME, null, values);
    }
  }

  protected ContentValues preparePointValues(Position p, UUID routeId) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(DbModel.PointsTable.COLUMN_ID, idToDbValue(UUID.randomUUID()));
    contentValues.put(DbModel.PointsTable.COLUMN_ROUTE_ID, idToDbValue(routeId));
    contentValues.put(DbModel.PointsTable.COLUMN_LATITUDE, p.latLng._latitude);
    contentValues.put(DbModel.PointsTable.COLUMN_LONGITUDE, p.latLng._longitude);
    contentValues.put(DbModel.PointsTable.COLUMN_ACCURACY, p.accuracy);
    contentValues.put(DbModel.PointsTable.COLUMN_TIME, p.time);
    contentValues.put(DbModel.PointsTable.COLUMN_PROVIDER, p.provider);

    return contentValues;
  }

  protected long deleteRouteFromDatabase(UUID id) {
    SQLiteDatabase database = getWritableDatabase();
    database.beginTransaction();

    String[] args = {idToDbValue(id)};
    deletePoints(id);

    int deleted = database.delete(DbModel.RoutesTable.TABLE_NAME, DbModel.PointsTable.COLUMN_ID + " = ?", args);

    database.setTransactionSuccessful();
    database.endTransaction();

    return deleted;
  }

  protected void deletePoints(UUID id) {
    String[] args = {idToDbValue(id)};
    getWritableDatabase().delete(DbModel.PointsTable.TABLE_NAME, DbModel.PointsTable.COLUMN_ROUTE_ID + " = ?", args);
  }

  protected ContentValues prepareRouteContentValues(RouteData routeData) {
    ContentValues contentValues = new ContentValues();

    contentValues.put(DbModel.RoutesTable.COLUMN_ID, routeData.getId().toString());
    contentValues.put(DbModel.RoutesTable.COLUMN_TITLE, routeData.getTitle());
    contentValues.put(DbModel.RoutesTable.COLUMN_START, formatDbDate(routeData.getStart()));
    contentValues.put(DbModel.RoutesTable.COLUMN_END, formatDbDate(routeData.getEnd()));

    return contentValues;
  }

  //endregion
}
