package com.jraska.core.gps;

import com.jraska.core.JRApplication;
import com.jraska.core.services.IAppService;

public interface IGpsService extends IAppService
{
	//region Methods

	LatLng getLastPosition();

	//endregion

	//region Nested classes

	static class Stub
	{
		public static IGpsService asInterface()
		{
			return JRApplication.getService(IGpsService.class);
		}
	}

	//endregion
}
