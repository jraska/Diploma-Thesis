package com.jraska.pwdm.travel.persistence;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jraska.common.events.IObservable;
import com.jraska.common.events.Observable;
import com.jraska.common.utils.ParcelableUtil;
import com.jraska.core.database.IDatabaseService;
import com.jraska.core.persistence.DbPersistenceServiceBase;
import com.jraska.pwdm.travel.data.Path;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.data.RouteDescription;
import com.jraska.pwdm.travel.database.DatabaseModel.RoutesTable;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class RouteParcelTravelDataPersistenceService extends DbPersistenceServiceBase implements ITravelDataPersistenceService
{
	//region Fields

	private Observable<RouteDescription> mNewRouteEvent;

	//endregion

	//region Constructors

	public RouteParcelTravelDataPersistenceService(IDatabaseService databaseService)
	{
		super(databaseService);
	}

	//endregion

	//region ITravelDataPersistenceService impl

	@Override
	public IObservable<RouteDescription> getOnNewRoute()
	{
		if (mNewRouteEvent == null)
		{
			mNewRouteEvent = new Observable<RouteDescription>();
		}

		return mNewRouteEvent;
	}

	@Override
	public List<RouteDescription> getRouteDescriptions()
	{
		return getRouteDescriptionsFromDatabase();
	}

	@Override
	public RouteData getRouteData(UUID id)
	{
		return getRouteDataFromDatabase(id);
	}

	@Override
	public long deleteRoute(RouteDescription routeData)
	{
		return deleteRouteFromDatabase(routeData);
	}

	@Override
	public long updateRoute(RouteData routeData)
	{
		SQLiteDatabase database = getWritableDatabase();

		database.beginTransaction();
		try
		{
			deleteRoute(routeData.getDescription());
			long route = insertRoute(routeData);

			database.setTransactionSuccessful();
			return route;
		}
		finally
		{
			database.endTransaction();
		}
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

	protected void onNewRoute(RouteData routeData)
	{
		if (mNewRouteEvent != null)
		{
			mNewRouteEvent.notify(this, routeData.getDescription());
		}
	}

	protected List<RouteDescription> getRouteDescriptionsFromDatabase()
	{
		Cursor cursor = getReadableDatabase().query(RoutesTable.TABLE_NAME, RoutesTable.DESCRIPTION_COLUMNS, null, null, null, null, null);

		List<RouteDescription> descriptions = new ArrayList<RouteDescription>();

		try
		{
			while (cursor.moveToNext())
			{
				RouteDescription routeDescription = readRouteDescription(cursor);
				descriptions.add(routeDescription);
			}
		}
		finally
		{
			cursor.close();
		}

		return descriptions;
	}

	protected RouteData getRouteDataFromDatabase(UUID id)
	{
		String[] args = {idToDbValue(id)};
		Cursor cursor = getReadableDatabase().query(RoutesTable.TABLE_NAME, RoutesTable.ALL_COLUMNS, "Id = ?", args, null, null, null);

		try
		{
			if (cursor.getCount() != 1)
			{
				return null;
			}

			cursor.moveToFirst();
			return readRouteData(cursor);
		}
		finally
		{
			cursor.close();
		}
	}

	protected RouteData readRouteData(Cursor c)
	{
		RouteDescription routeDescription = readRouteDescription(c);

		byte[] bytes = c.getBlob(c.getColumnIndex(RoutesTable.COLUMN_PATH));
		Path path = unParcelPath(bytes);

		RouteData routeData = new RouteData(routeDescription, path);
		return routeData;
	}

	protected RouteDescription readRouteDescription(Cursor c)
	{
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

	protected long insertRouteToDatabase(RouteData routeData)
	{
		SQLiteDatabase database = getWritableDatabase();

		ContentValues contentValues = prepareRouteContentValues(routeData);

		return database.insertWithOnConflict(RoutesTable.TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
	}

	protected long deleteRouteFromDatabase(RouteDescription routeData)
	{
		String[] args = {idToDbValue(routeData.getId())};

		return getWritableDatabase().delete(RoutesTable.TABLE_NAME, "Id = ?", args);
	}

	protected ContentValues prepareRouteContentValues(RouteData routeData)
	{
		ContentValues contentValues = new ContentValues();

		contentValues.put(RoutesTable.COLUMN_ID, routeData.getId().toString());
		contentValues.put(RoutesTable.COLUMN_TITLE, routeData.getTitle());
		contentValues.put(RoutesTable.COLUMN_START, formatDbDate(routeData.getStart()));
		contentValues.put(RoutesTable.COLUMN_END, formatDbDate(routeData.getEnd()));
		contentValues.put(RoutesTable.COLUMN_PATH, parcelPath(routeData.getPath()));

		return contentValues;
	}

	protected byte[] parcelPath(Path path)
	{
		return ParcelableUtil.marshall(path);
	}

	protected Path unParcelPath(byte[] data)
	{
		return ParcelableUtil.unMarshall(data, Path.CREATOR);
	}

	//endregion

	//region Nested classes

	@dagger.Module(injects = ITravelDataPersistenceService.class, complete = false)
	public static class Module
	{
		@Provides
		@Singleton
		ITravelDataPersistenceService providePersistenceSvc(IDatabaseService databaseService)
		{
			return new RouteParcelTravelDataPersistenceService(databaseService);
		}
	}

	//endregion
}
