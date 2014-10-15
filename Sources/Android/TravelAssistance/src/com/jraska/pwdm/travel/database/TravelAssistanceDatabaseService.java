package com.jraska.pwdm.travel.database;

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

	private final String mDbName;

	//endregion

	//region Constructors

	public TravelAssistanceDatabaseService(String dbName)
	{
		ArgumentCheck.notNull(dbName);

		mDbName = dbName;
	}

	//endregion

	//region Properties

	public String getDbName()
	{
		return mDbName;
	}

	//endregion

	//region DatabaseServiceBase implementation

	@Override
	protected SQLiteOpenHelper createOpenHelper()
	{
		return new TravelAssistanceDatabaseOpenHelper(mDbName);
	}

	//endregion

	//region Nested classes

	@dagger.Module(injects = IDatabaseService.class, complete = false)
	public static class Module
	{
		@Provides
		@Singleton
		public IDatabaseService provideSvc(@Named("dbName") String dbName)
		{
			return new TravelAssistanceDatabaseService(dbName);
		}
	}

	//endregion
}
