package com.jraska.pwmd.travel.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TravelAssistanceDbHelper extends SQLiteOpenHelper {
  //region Constructors

  public TravelAssistanceDbHelper(Context context, String name) {
    super(context, name, null, 1);
  }

  //endregion

  //region SQLiteOpenHelper impl

  @Override
  public void onCreate(SQLiteDatabase db) {
    db.execSQL(DbModel.RoutesTable.CREATE_STATEMENT);
    db.execSQL(DbModel.PointsTable.CREATE_STATEMENT);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    throw new UnsupportedOperationException("Not implemented!");
  }

  //endregion
}
