package com.jraska.pwdm.travel.persistence;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.jraska.common.events.IObservable;
import com.jraska.common.events.Observable;
import com.jraska.core.database.IDatabaseService;
import com.jraska.core.persistence.DbPersistenceServiceBase;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.data.RouteDescription;
import com.jraska.pwdm.travel.database.DbModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public abstract class RoutePersistenceServiceBase extends DbPersistenceServiceBase implements ITravelDataPersistenceService
{
	//region Fields

	private Observable<RouteDescription> mNewRouteEvent;

	//endregion

	//region Constructors

	protected RoutePersistenceServiceBase(IDatabaseService databaseService)
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
	public List<RouteDescription> selectAllRouteDescriptions()
	{
		return getRouteDescriptionsFromDatabase();
	}

	@Override
	public long updateRoute(RouteData routeData)
	{
		SQLiteDatabase database = getWritableDatabase();

		database.beginTransaction();
		try
		{
			deleteRoute(routeData.getId());
			long route = insertRoute(routeData);

			database.setTransactionSuccessful();
			return route;
		}
		finally
		{
			database.endTransaction();
		}
	}

	//endregion

	//region Methods

	protected List<RouteDescription> getRouteDescriptionsFromDatabase()
	{
		Cursor cursor = getReadableDatabase().query(DbModel.RoutesTable.TABLE_NAME, DbModel.RoutesTable.DESCRIPTION_COLUMNS, null, null, null, null, null);

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

	protected RouteDescription readRouteDescription(Cursor c)
	{
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

	protected void onNewRoute(RouteData routeData)
	{
		if (mNewRouteEvent != null)
		{
			mNewRouteEvent.notify(this, routeData.getDescription());
		}
	}

	//endregion
}
