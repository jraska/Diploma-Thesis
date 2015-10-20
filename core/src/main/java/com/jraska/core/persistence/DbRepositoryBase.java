package com.jraska.core.persistence;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.common.ArgumentCheck;
import com.jraska.common.Disposable;
import com.jraska.core.utils.DateHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public abstract class DbRepositoryBase implements Disposable {
  //region Fields

  private final SQLiteOpenHelper _sqLiteOpenHelper;

  private DateFormat _dbDateFormat;

  //endregion

  //region Constructors

  public DbRepositoryBase(SQLiteOpenHelper sqLiteOpenHelper) {
    ArgumentCheck.notNull(sqLiteOpenHelper);

    _sqLiteOpenHelper = sqLiteOpenHelper;
  }

  //endregion

  //region Properties

  protected DateFormat getDbDateFormat() {
    if (_dbDateFormat == null) {
      _dbDateFormat = new SimpleDateFormat(DateHelper.APP_DATE_PATTERN, Locale.US);
    }

    return _dbDateFormat;
  }

  protected SQLiteDatabase getReadableDatabase() {
    return _sqLiteOpenHelper.getReadableDatabase();
  }

  protected SQLiteDatabase getWritableDatabase() {
    return _sqLiteOpenHelper.getWritableDatabase();
  }

  //endregion

  //region IDisposable impl

  @Override
  public void dispose() {
    _sqLiteOpenHelper.close();
  }

  //endregion

  //region Methods

  protected String idToDbValue(UUID id) {
    return id.toString();
  }

  protected UUID idFromDbValue(String value) {
    return UUID.fromString(value);
  }

  public String formatDbDate(Date date) {
    return getDbDateFormat().format(date);
  }

  public Date parseDbDate(String string) {
    try {
      return getDbDateFormat().parse(string);
    }
    catch (ParseException e) {
      throw new RuntimeException(e);
    }
  }

  //endregion
}
