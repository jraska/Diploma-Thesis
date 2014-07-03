package com.jraska.pwdm.travel.database;

public abstract class DatabaseModel
{
	//region Constructors

	private DatabaseModel()
	{
	}

	//endregion

	//region Nested class

	public static abstract class RoutesTable
	{
		private RoutesTable()
		{
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
				COLUMN_PATH + " blob not null)";

		public static String[] DESCRIPTION_COLUMNS = {COLUMN_ID, COLUMN_TITLE, COLUMN_START, COLUMN_END};
		public static String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_TITLE, COLUMN_START, COLUMN_END, COLUMN_PATH};
	}

	//endregion
}
