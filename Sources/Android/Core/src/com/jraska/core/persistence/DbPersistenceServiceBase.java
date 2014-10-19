package com.jraska.core.persistence;

import android.database.sqlite.SQLiteDatabase;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.IDisposable;
import com.jraska.common.exceptions.JRRuntimeException;
import com.jraska.core.database.IDatabaseService;
import com.jraska.core.utils.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public abstract class DbPersistenceServiceBase implements IDisposable
{
	//region Fields

	private final IDatabaseService mDatabaseService;

	private DateFormat mDbDateFormat;

	//endregion

	//region Constructors

	public DbPersistenceServiceBase(IDatabaseService databaseService)
	{
		ArgumentCheck.notNull(databaseService);

		mDatabaseService = databaseService;
	}

	//endregion

	//region Properties

	protected DateFormat getDbDateFormat()
	{
		if (mDbDateFormat == null)
		{
			mDbDateFormat = new SimpleDateFormat(DateHelper.APP_DATE_PATTERN);
		}

		return mDbDateFormat;
	}

	protected SQLiteDatabase getReadableDatabase()
	{
		return mDatabaseService.getReadableDatabase();
	}

	protected SQLiteDatabase getWritableDatabase()
	{
		return mDatabaseService.getWritableDatabase();
	}

	//endregion

	//region IDisposable impl

	@Override
	public void dispose()
	{
		mDatabaseService.dispose();
	}

	//endregion

	//region Methods

	protected String idToDbValue(UUID id)
	{
		return id.toString();
	}

	protected UUID idFromDbValue(String value)
	{
		return UUID.fromString(value);
	}

	public String formatDbDate(Date date)
	{
		return getDbDateFormat().format(date);
	}

	public Date parseDbDate(String string)
	{
		try
		{
			return getDbDateFormat().parse(string);
		}
		catch (ParseException e)
		{
			throw new JRRuntimeException(e);
		}
	}

	//endregion
}
