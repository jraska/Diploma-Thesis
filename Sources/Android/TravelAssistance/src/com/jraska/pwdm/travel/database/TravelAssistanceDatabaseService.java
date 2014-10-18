package com.jraska.pwdm.travel.database;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.common.ArgumentCheck;
import com.jraska.core.database.DatabaseServiceBase;
import com.jraska.core.database.IDatabaseService;
import com.jraska.pwdm.travel.TravelAssistanceApplication;
import dagger.Module;
import dagger.Provides;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

public class TravelAssistanceDatabaseService extends DatabaseServiceBase
{
	//region Fields

	private final Context mContext;
	private final String mDbName;

	//endregion

	//region Constructors

	public TravelAssistanceDatabaseService(Context context, String dbName)
	{
		ArgumentCheck.notNull(context);
		ArgumentCheck.notNull(dbName);

		mContext = context;
		mDbName = dbName;
	}

	//endregion

	//region Properties

	public String getDbName()
	{
		return mDbName;
	}

	public Context getContext()
	{
		return mContext;
	}

	//endregion

	//region DatabaseServiceBase implementation

	@Override
	protected SQLiteOpenHelper createOpenHelper()
	{
		return new TravelAssistanceDatabaseOpenHelper(mContext, mDbName);
	}

	//endregion

	//region Nested classes

	@dagger.Module(injects = IDatabaseService.class, complete = false)
	public static class Module
	{
		@Provides
		@Singleton
		public IDatabaseService provideSvc(Context context, @Named("dbName") String dbName)
		{
			return new TravelAssistanceDatabaseService(context, dbName);
		}
	}

	//endregion
}
