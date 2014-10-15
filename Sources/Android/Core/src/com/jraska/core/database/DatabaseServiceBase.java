package com.jraska.core.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Base class for sqlite database based on open helper
 */
public abstract class DatabaseServiceBase implements IDatabaseService
{
	//region Fields

	private final Object mLock = new Object();
	private volatile SQLiteOpenHelper mHelper;

	//endregion

	//region Properties

	public SQLiteOpenHelper getOpenHelper()
	{
		if (mHelper == null)
		{
			synchronized (mLock)
			{
				if (mHelper == null)
				{
					mHelper = createOpenHelper();
				}
			}
		}

		return mHelper;
	}

	//endregion

	//region IDatabaseService implementation

	@Override
	public SQLiteDatabase getReadableDatabase()
	{
		return getOpenHelper().getWritableDatabase();
	}

	@Override
	public SQLiteDatabase getWritableDatabase()
	{
		return getOpenHelper().getReadableDatabase();
	}

	//endregion

	//region Methods

	protected abstract SQLiteOpenHelper createOpenHelper();

	//endregion
}
