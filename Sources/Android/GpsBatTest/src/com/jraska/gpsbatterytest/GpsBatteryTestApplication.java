package com.jraska.gpsbatterytest;

import android.content.Context;
import android.location.LocationManager;
import com.jraska.core.JRApplication;
import com.jraska.pwdm.core.battery.IBatteryStatsService;
import com.jraska.pwdm.core.battery.SimpleBatteryStatsService;
import com.jraska.pwdm.core.gps.ILocationService;
import com.jraska.pwdm.core.gps.ILocationStatusService;
import com.jraska.pwdm.core.gps.SimpleSystemLocationService;

public class GpsBatteryTestApplication extends JRApplication
{
	//region Application overrides

	@Override
	public void onCreate()
	{
		super.onCreate();

		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		final SimpleSystemLocationService locationService = new SimpleSystemLocationService(locationManager);
		putService(ILocationService.class, locationService);
		putService(ILocationStatusService.class, locationService);

		putService(IBatteryStatsService.class, new SimpleBatteryStatsService(this));
	}

	//endregion
}
