package com.jraska.core.database;

import android.database.sqlite.SQLiteDatabase;
import com.jraska.common.IDisposable;
import com.jraska.core.JRApplication;
import com.jraska.core.services.IAppService;

public interface IDatabaseService extends IAppService, IDisposable
{
	//region Methods

	SQLiteDatabase getReadableDatabase();

	SQLiteDatabase getWritableDatabase();

	//endregion

	//region Nested classes

	static class Stub
	{
		public static IDatabaseService asInterface()
		{
			return JRApplication.getService(IDatabaseService.class);
		}
	}

	//endregion
}
