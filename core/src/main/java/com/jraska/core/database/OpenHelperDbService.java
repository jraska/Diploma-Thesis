package com.jraska.core.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.IDisposable;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Base class for sqlite database based on open helper
 */
public class OpenHelperDbService implements IDatabaseService, IDisposable
{
	//region Fields

	private final SQLiteOpenHelper mHelper;

	//endregion

	//region Constructors

	public OpenHelperDbService(SQLiteOpenHelper helper)
	{
		ArgumentCheck.notNull(helper);

		mHelper = helper;
	}

	//endregion

	//region Properties

	public SQLiteOpenHelper getOpenHelper()
	{
		return mHelper;
	}

	//endregion

	//region IDatabaseService implementation

	@Override
	public SQLiteDatabase getReadableDatabase()
	{
		return mHelper.getWritableDatabase();
	}

	@Override
	public SQLiteDatabase getWritableDatabase()
	{
		return mHelper.getReadableDatabase();
	}

	//endregion

	//region IDisposable impl

	@Override
	public void dispose()
	{
		mHelper.close();
	}

	//endregion

	//region Nested classes

	@dagger.Module(injects = IDatabaseService.class, complete = false)
	public static class Module
	{
		@Provides
		@Singleton
		public IDatabaseService provideSvc(SQLiteOpenHelper openHelper)
		{
			return new OpenHelperDbService(openHelper);
		}
	}

	//endregion
}
