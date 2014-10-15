package com.jraska.gpsbatterytest.logging;

import com.jraska.common.ArgumentCheck;

public class CompositeLogger implements ILogger
{
	//region Fields

	private final ILogger [] mLoggers;

	//endregion

	//region Constructors

	public CompositeLogger(ILogger[] loggers)
	{
		ArgumentCheck.notNull(loggers);

		mLoggers = loggers;
	}

	//endregion

	//region ILogger impl

	@Override
	public void log(Object o)
	{
		for (ILogger logger : mLoggers)
		{
			logger.log(o);
		}
	}

	@Override
	public void dispose()
	{
		for (ILogger logger : mLoggers)
		{
			logger.dispose();
		}
	}

	//endregion
}
