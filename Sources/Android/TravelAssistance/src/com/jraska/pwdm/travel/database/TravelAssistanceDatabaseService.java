package com.jraska.pwdm.travel.database;

import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.common.ArgumentCheck;
import com.jraska.core.database.DatabaseServiceBase;

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
}
