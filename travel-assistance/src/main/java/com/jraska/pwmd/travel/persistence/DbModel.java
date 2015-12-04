package com.jraska.pwmd.travel.persistence;

public abstract class DbModel {
  //region Nested class

  public static abstract class RoutesTable {
    private RoutesTable() {
    }

    public static final String TABLE_NAME = "Routes";

    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_TITLE = "Title";
    public static final String COLUMN_START = "StartDate";
    public static final String COLUMN_END = "EndDate";
    public static final String COLUMN_PATH = "Path";

    public static final String CREATE_STATEMENT = "create table " + TABLE_NAME + "(" +
        COLUMN_ID + " text primary key, " +
        COLUMN_TITLE + " text not null, " +
        COLUMN_START + " text not null, " +
        COLUMN_END + " text not null, " +
        COLUMN_PATH + " blob null)";

    public static final String[] DESCRIPTION_COLUMNS = {COLUMN_ID, COLUMN_TITLE, COLUMN_START, COLUMN_END};
    public static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_TITLE, COLUMN_START, COLUMN_END, COLUMN_PATH};
  }

  public static abstract class PointsTable {
    private PointsTable() {
    }

    public static final String TABLE_NAME = "Points";

    public static final String COLUMN_ID = "Id";
    public static final String COLUMN_LATITUDE = "Lat";
    public static final String COLUMN_LONGITUDE = "Long";
    public static final String COLUMN_TIME = "Time";
    public static final String COLUMN_ACCURACY = "Accuracy";
    public static final String COLUMN_PROVIDER = "Provider";
    public static final String COLUMN_ROUTE_ID = "RouteId";

    public static final String CREATE_STATEMENT = "create table " + TABLE_NAME + "(" +
        COLUMN_ID + " text primary key, " +
        COLUMN_ROUTE_ID + " text not null, " +
        COLUMN_LATITUDE + " real not null, " +
        COLUMN_LONGITUDE + " real not null, " +
        COLUMN_TIME + " integer not null, " +
        COLUMN_ACCURACY + " real not null, " +
        COLUMN_PROVIDER + " text not null)";
  }

  //endregion

  //region Constructors

  private DbModel() {
    // no instances
  }

  //endregion
}