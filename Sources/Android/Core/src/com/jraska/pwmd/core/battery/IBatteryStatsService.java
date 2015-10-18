package com.jraska.pwmd.core.battery;

import com.jraska.core.JRApplication;
import com.jraska.core.services.IAppService;

public interface IBatteryStatsService extends IAppService
{
	//region Methods

	BatteryStats getCurrentBatteryStats();

	//endregion

	//region Nested classes

	static class Stub
	{
		public static IBatteryStatsService asInterface()
		{
			return JRApplication.getService(IBatteryStatsService.class);
		}
	}

	//endregion
}
