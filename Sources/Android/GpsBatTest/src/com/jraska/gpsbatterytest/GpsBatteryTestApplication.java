package com.jraska.gpsbatterytest;

import com.jraska.core.AppContextModule;
import com.jraska.core.JRApplication;
import com.jraska.pwmd.core.battery.SimpleBatteryStatsService;
import com.jraska.pwmd.core.gps.SimpleSystemLocationService;
import dagger.Module;

public class GpsBatteryTestApplication extends JRApplication
{
	//region Application overrides

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	protected Object[] getModules()
	{
		return new AppModule[]{new AppModule()};
	}

	//endregion

	//region Nested classes

	@Module(includes =
			{
					AppContextModule.class,
					SimpleSystemLocationService.Module.class,
					SimpleBatteryStatsService.Module.class
			}
	)
	static class AppModule
	{
	}

	//endregion
}
