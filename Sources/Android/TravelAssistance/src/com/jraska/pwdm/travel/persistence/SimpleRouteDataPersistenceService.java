package com.jraska.pwdm.travel.persistence;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.core.database.IDatabaseService;
import com.jraska.pwdm.core.gps.Position;
import com.jraska.pwdm.travel.data.Path;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.data.RouteDescription;
import com.jraska.pwdm.travel.database.DbModel;
import com.jraska.pwdm.travel.database.TravelAssistanceDbHelper;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class SimpleRouteDataPersistenceService extends RoutePersistenceServiceBase
{
	//region Constructors

	public SimpleRouteDataPersistenceService(IDatabaseService databaseService)
	{
		super(databaseService);
	}

	//endregion

	//region ITravelDataPersistenceService impl

	@Override
	public RouteData selectRouteData(UUID id)
	{
		return getRouteDataFromDatabase(id);
	}

	@Override
	public long deleteRoute(UUID id)
	{
		return deleteRouteFromDatabase(id);
	}

	@Override
	public long insertRoute(RouteData routeData)
	{
		long id = insertRouteToDatabase(routeData);

		onNewRoute(routeData);

		return id;
	}

	//endregion

	//region Methods

	protected RouteData getRouteDataFromDatabase(UUID id)
	{
		String[] args = {idToDbValue(id)};
		Cursor cursor = getReadableDatabase().query(DbModel.RoutesTable.TABLE_NAME, DbModel.RoutesTable.ALL_COLUMNS, "Id = ?", args, null, null, null);

		RouteDescription routeDescription;
		try
		{
			if (cursor.getCount() != 1)
			{
				return null;
			}

			cursor.moveToFirst();
			routeDescription = readRouteDescription(cursor);
		}
		finally
		{
			cursor.close();
		}

		cursor = getReadableDatabase().query(DbModel.PointsTable.TABLE_NAME, null, "RouteId = ?", args, null, null, null);

		try
		{
			Path path = readPath(cursor);
			return new RouteData(routeDescription, path);
		}
		finally
		{
			cursor.close();
		}
	}

	protected Path readPath(Cursor cursor)
	{
		List<Position> positions = new ArrayList<Position>(cursor.getCount());

		while (cursor.moveToNext())
		{
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

	protected long insertRouteToDatabase(RouteData routeData)
	{
		SQLiteDatabase database = getWritableDatabase();

		ContentValues contentValues = prepareRouteContentValues(routeData);

		database.beginTransaction();
		long insert = database.insertWithOnConflict(DbModel.RoutesTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

		writePath(routeData.getId(), routeData.getPath());

		database.setTransactionSuccessful();
		database.endTransaction();
		return insert;
	}

	protected void writePath(UUID routeId, Path path)
	{
		SQLiteDatabase database = getWritableDatabase();
		for (Position position : path.getPoints())
		{
			ContentValues values = preparePointValues(position, routeId);

			database.insert(DbModel.PointsTable.TABLE_NAME, null, values);
		}
	}

	protected ContentValues preparePointValues(Position p, UUID routeId)
	{
		ContentValues contentValues = new ContentValues();
		contentValues.put(DbModel.PointsTable.COLUMN_ID, idToDbValue(UUID.randomUUID()));
		contentValues.put(DbModel.PointsTable.COLUMN_ROUTE_ID, idToDbValue(routeId));
		contentValues.put(DbModel.PointsTable.COLUMN_LATITUDE, p.latitude);
		contentValues.put(DbModel.PointsTable.COLUMN_LONGITUDE, p.longitude);
		contentValues.put(DbModel.PointsTable.COLUMN_ACCURACY, p.accuracy);
		contentValues.put(DbModel.PointsTable.COLUMN_TIME, p.time);
		contentValues.put(DbModel.PointsTable.COLUMN_PROVIDER, p.provider);

		return contentValues;
	}

	protected long deleteRouteFromDatabase(UUID id)
	{
		SQLiteDatabase database = getWritableDatabase();
		database.beginTransaction();

		String[] args = {idToDbValue(id)};
		deletePoints(id);

		int deleted = database.delete(DbModel.RoutesTable.TABLE_NAME, DbModel.PointsTable.COLUMN_ID + " = ?", args);

		database.setTransactionSuccessful();
		database.endTransaction();

		return deleted;
	}

	protected void deletePoints(UUID id)
	{
		String[] args = {idToDbValue(id)};
		getWritableDatabase().delete(DbModel.PointsTable.TABLE_NAME, DbModel.PointsTable.COLUMN_ROUTE_ID + " = ?", args);
	}

	protected ContentValues prepareRouteContentValues(RouteData routeData)
	{
		ContentValues contentValues = new ContentValues();

		contentValues.put(DbModel.RoutesTable.COLUMN_ID, routeData.getId().toString());
		contentValues.put(DbModel.RoutesTable.COLUMN_TITLE, routeData.getTitle());
		contentValues.put(DbModel.RoutesTable.COLUMN_START, formatDbDate(routeData.getStart()));
		contentValues.put(DbModel.RoutesTable.COLUMN_END, formatDbDate(routeData.getEnd()));
//		contentValues.put(DatabaseModel.RoutesTable.COLUMN_PATH, parcelPath(routeData.getPath()));

		return contentValues;
	}

	//endregion

	//region Nested classes

	@dagger.Module(injects = {ITravelDataPersistenceService.class, IDatabaseService.class}, complete = false, library = true)
	public static class Module
	{
		@Provides
		@Singleton
		ITravelDataPersistenceService providePersistenceSvc(IDatabaseService databaseService)
		{
			return new SimpleRouteDataPersistenceService(databaseService);
		}

		@Provides
		@Singleton
		public SQLiteOpenHelper getDbHelper(Context context, @Named("dbName") String dbName)
		{
			return new TravelAssistanceDbHelper(context, dbName);
		}
	}

	//endregion
}