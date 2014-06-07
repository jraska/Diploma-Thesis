package com.jraska.pwdm.core.date;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class DateHelper
{
	//region Constants

	private static final DateFormat APP_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

	//endregion

	//region Constructors

	private DateHelper()
	{
	}

	//endregion

	//region Methods

	public static String formatToDateTimeValue(long millis)
	{
		return formatToDateTimeValue(new Date(millis));
	}

	public static String formatToDateTimeValue(Date date)
	{
		return APP_TIME_FORMAT.format(date);
	}

	//endregion
}
