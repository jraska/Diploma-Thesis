package com.jraska.common;

import android.util.Pair;

public abstract class ArgumentCheck
{
	//region Constructor

	private ArgumentCheck()
	{
	}

	//endregion

	//region Methods

	public static void notNull(Object argument)
	{
		notNull(argument, null);
	}

	public static void notNull(Object argument, String argumentName)
	{
		if (argument == null)
		{
			String message = String.format("Argument %scannot be null!", argumentName == null ? "" : argumentName + " ");
			throw new IllegalArgumentException(message);
		}
	}

	//endregion
}
