package com.jraska.pwdm.travel;

import com.jraska.core.database.IDatabaseService;
import com.jraska.core.services.DefaultExternalStorageAppEnvironmentService;
import com.jraska.core.services.IAppEnvironmentService;
import com.jraska.pwdm.core.PWDMApplication;
import com.jraska.pwdm.core.gps.ILocationService;
import com.jraska.pwdm.core.gps.ILocationStatusService;
import com.jraska.pwdm.core.gps.SimpleSystemLocationService;
import com.jraska.pwdm.travel.database.TravelAssistanceDatabaseService;
import com.jraska.pwdm.travel.persistence.ITravelDataPersistenceService;
import com.jraska.pwdm.travel.persistence.RouteParcelTravelDataPersistenceService;

public class TravelAssistanceApplication extends PWDMApplication
{
	//region Constants

	public static String DB_NAME = "TravelAssistanceData";

	//endregion

	//region TravelAssistanceApplication overrides

	@Override
	public void onCreate()
	{
		super.onCreate();

		putService(IAppEnvironmentService.class, new DefaultExternalStorageAppEnvironmentService());
		putService(IDatabaseService.class, new TravelAssistanceDatabaseService(DB_NAME));
		putService(ITravelDataPersistenceService.class, new RouteParcelTravelDataPersistenceService());

		//SimpleSystemLocationService has both implementations
		SimpleSystemLocationService simpleSystemLocationService = new SimpleSystemLocationService();
		putService(ILocationService.class, simpleSystemLocationService);
		putService(ILocationStatusService.class, simpleSystemLocationService);
	}

	//endregion
}
