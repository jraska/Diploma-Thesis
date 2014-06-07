package com.jraska.gpsbatterytest;

import com.jraska.core.JRApplication;
import com.jraska.pwdm.core.battery.IBatteryStatsService;
import com.jraska.pwdm.core.battery.SimpleBatteryStatsService;
import com.jraska.pwdm.core.gps.ILocationService;
import com.jraska.pwdm.core.gps.ILocationStatusService;
import com.jraska.pwdm.core.gps.SimpleSystemLocationService;

public class GpsBatterTestApplication extends JRApplication
{
	//region Application overrides

	@Override
	public void onCreate()
	{
		super.onCreate();

		final SimpleSystemLocationService locationService = new SimpleSystemLocationService();
		putService(ILocationService.class, locationService);
		putService(ILocationStatusService.class, locationService);

		putService(IBatteryStatsService.class, new SimpleBatteryStatsService());
	}

	//endregion
}
