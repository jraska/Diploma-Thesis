package com.jraska.gpsbatterytest;

import com.jraska.core.JRApplication;
import com.jraska.core.gps.IGpsService;
import com.jraska.core.gps.SimpleGpsService;

public class GpsBatterTestApplication extends JRApplication
{
	//region Application overrides

	@Override
	public void onCreate()
	{
		super.onCreate();

		putService(IGpsService.class, new SimpleGpsService());
	}

	//endregion
}
