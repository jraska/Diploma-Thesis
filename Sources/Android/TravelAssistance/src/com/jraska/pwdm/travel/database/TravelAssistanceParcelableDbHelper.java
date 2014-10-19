package com.jraska.pwdm.travel.database;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TravelAssistanceParcelableDbHelper extends SQLiteOpenHelper
{
	//region SQLiteOpenHelper impl

	public TravelAssistanceParcelableDbHelper(Context context, String name)
	{
		this(context, name, null, 1, null);
	}

	public TravelAssistanceParcelableDbHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler)
	{
		super(context, name, factory, version, errorHandler);
	}

	//endregion

	//region SQLiteOpenHelper impl

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DbModel.RoutesTable.CREATE_STATEMENT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		throw new UnsupportedOperationException("Should be no upgrade now!"); //TODO: solve upgrades
	}

	//endregion
}
