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

public abstract class DbPersistenceServiceBase implements IDisposable {
  //region Fields

  private final IDatabaseService _databaseService;

  private DateFormat _dbDateFormat;

  //endregion

  //region Constructors

  public DbPersistenceServiceBase(IDatabaseService databaseService) {
    ArgumentCheck.notNull(databaseService);

    _databaseService = databaseService;
  }

  //endregion

  //region Properties

  protected DateFormat getDbDateFormat() {
    if (_dbDateFormat == null) {
      _dbDateFormat = new SimpleDateFormat(DateHelper.APP_DATE_PATTERN);
    }

    return _dbDateFormat;
  }

  protected SQLiteDatabase getReadableDatabase() {
    return _databaseService.getReadableDatabase();
  }

  protected SQLiteDatabase getWritableDatabase() {
    return _databaseService.getWritableDatabase();
  }

  //endregion

  //region IDisposable impl

  @Override
  public void dispose() {
    _databaseService.dispose();
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
      throw new JRRuntimeException(e);
    }
  }

  //endregion
}
