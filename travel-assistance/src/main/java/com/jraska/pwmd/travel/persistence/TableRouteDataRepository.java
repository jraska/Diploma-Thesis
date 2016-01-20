package com.jraska.pwmd.travel.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.core.persistence.DbRepositoryBase;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.*;
import de.greenrobot.event.EventBus;

import java.util.*;

public class TableRouteDataRepository extends DbRepositoryBase implements TravelDataRepository {
  //region Fields

  private EventBus _dataBus;

  //endregion

  //region Constructors

  public TableRouteDataRepository(@NonNull SQLiteOpenHelper openHelper, @NonNull EventBus dataBus) {
    super(openHelper);
    ArgumentCheck.notNull(dataBus);

    _dataBus = dataBus;
  }

  //endregion

  //region TravelDataRepository impl

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
    _dataBus.post(new NewRouteEvent(routeData.getDescription()));
  }

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

    List<TransportChangeSpec> changeSpecs;
    try {
      changeSpecs = readChanges(cursor);
    }
    finally {
      cursor.close();
    }

    cursor = getReadableDatabase().query(DbModel.NotesTable.TABLE_NAME, null, "RouteId = ?", args, null, null, null);
    try {
      List<NoteSpec> noteSpecs = readNotes(cursor);

      return new RouteData(routeDescription, path, changeSpecs, noteSpecs);
    }
    finally {
      cursor.close();
    }
  }

  protected List<NoteSpec> readNotes(Cursor cursor) {
    if (cursor.getCount() == 0) {
      return Collections.emptyList();
    }

    List<NoteSpec> specs = new ArrayList<>();

    while (cursor.moveToNext()) {
      // Data are rad as Strings to remove conversion errors
      LatLng latLng = readLatLng(cursor);

      String caption = cursor.getString(cursor.getColumnIndex(DbModel.NotesTable.COLUMN_CAPTION));
      String pictureIdValue = cursor.getString(cursor.getColumnIndex(DbModel.NotesTable.COLUMN_PICTURE_ID));
      UUID pictureId = idFromDbValue(pictureIdValue);

      String soundIdValue = cursor.getString(cursor.getColumnIndex(DbModel.NotesTable.COLUMN_SOUND_ID));
      UUID soundId = idFromDbValue(soundIdValue);

      NoteSpec spec = new NoteSpec(latLng, pictureId, caption, soundId);
      specs.add(spec);
    }

    return specs;
  }

  protected List<TransportChangeSpec> readChanges(Cursor cursor) {
    if (cursor.getCount() == 0) {
      return Collections.emptyList();
    }

    List<TransportChangeSpec> specs = new ArrayList<>();

    while (cursor.moveToNext()) {
      // Data are rad as Strings to remove conversion errors
      LatLng latLng = readLatLng(cursor);

      int type = cursor.getInt(cursor.getColumnIndex(DbModel.TransportChangesTable.COLUMN_TRANSPORTATION_TYPE));
      String title = cursor.getString(cursor.getColumnIndex(DbModel.TransportChangesTable.COLUMN_TITLE));

      TransportChangeSpec spec = new TransportChangeSpec(latLng, type, title);
      specs.add(spec);
    }

    return specs;
  }

  @NonNull
  protected LatLng readLatLng(Cursor cursor) {
    String latitude = cursor.getString(cursor.getColumnIndex(DbModel.PositionTable.COLUMN_LATITUDE));
    String longitude = cursor.getString(cursor.getColumnIndex(DbModel.PositionTable.COLUMN_LONGITUDE));
    double lat = Double.parseDouble(latitude);
    double lon = Double.parseDouble(longitude);
    return new LatLng(lat, lon);
  }

  protected Path readPath(Cursor cursor) {
    List<Position> positions = new ArrayList<>(cursor.getCount());

    while (cursor.moveToNext()) {

      LatLng latLng = readLatLng(cursor);

      String accuracy = cursor.getString(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_ACCURACY));
      long time = cursor.getLong(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_TIME));
      String provider = cursor.getString(cursor.getColumnIndex(DbModel.PointsTable.COLUMN_PROVIDER));
      float acc = Float.parseFloat(accuracy);

      Position position = new Position(latLng, time, acc, provider);
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
    writePictures(routeData.getId(), routeData.getNoteSpecs());

    database.setTransactionSuccessful();
    database.endTransaction();
    return insert;
  }

  protected void writePictures(UUID id, List<NoteSpec> noteSpecs) {
    SQLiteDatabase database = getWritableDatabase();

    for (NoteSpec spec : noteSpecs) {
      ContentValues noteValues = prepareNoteValues(spec, id);

      database.insert(DbModel.NotesTable.TABLE_NAME, null, noteValues);
    }
  }

  protected ContentValues prepareNoteValues(NoteSpec spec, UUID id) {
    ContentValues contentValues = new ContentValues();
    contentValues.put(DbModel.NotesTable.COLUMN_ID, idToDbValue(UUID.randomUUID()));
    contentValues.put(DbModel.NotesTable.COLUMN_ROUTE_ID, idToDbValue(id));
    contentValues.put(DbModel.NotesTable.COLUMN_LATITUDE, spec.latLng._latitude);
    contentValues.put(DbModel.NotesTable.COLUMN_LONGITUDE, spec.latLng._longitude);
    contentValues.put(DbModel.NotesTable.COLUMN_PICTURE_ID, idToDbValue(spec.imageId));
    contentValues.put(DbModel.NotesTable.COLUMN_CAPTION, spec.caption);
    contentValues.put(DbModel.NotesTable.COLUMN_SOUND_ID, idToDbValue(spec.soundId));

    return contentValues;
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
