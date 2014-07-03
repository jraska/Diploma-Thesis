package com.jraska.pwdm.travel.persistence;

import com.jraska.core.JRApplication;
import com.jraska.core.services.IAppService;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.data.RouteDescription;

import java.util.List;
import java.util.UUID;

public interface ITravelDataPersistenceService extends IAppService
{
	//region Methods

	List<RouteDescription> getRouteDescriptions();

	RouteData getRouteData(UUID id);

	long deleteRoute(RouteData routeData);

	long updateRoute(RouteData routeData);

	long insertRoute(RouteData routeData);

	//endregion

	//region Nested class

	static class Stub
	{
		public static ITravelDataPersistenceService asInterface()
		{
			return JRApplication.getService(ITravelDataPersistenceService.class);
		}
	}

	//endregion
}
