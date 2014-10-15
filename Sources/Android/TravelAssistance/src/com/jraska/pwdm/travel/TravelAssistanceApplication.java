package com.jraska.pwdm.travel;

import android.content.Context;
import android.location.LocationManager;
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
import com.jraska.pwdm.travel.tracking.ITrackingManagementService;
import com.jraska.pwdm.travel.tracking.TrackingManagementService;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class TravelAssistanceApplication extends PWDMApplication
{
	//region Constants

	public static String DB_NAME = "TravelAssistanceData";
	public static final DateFormat USER_DETAILED_TIME_FORMAT = new SimpleDateFormat("HH:mm:ss, MM.dd.yyyy");

	//endregion

	//region TravelAssistanceApplication overrides

	@Override
	public void onCreate()
	{
		super.onCreate();

		putService(IAppEnvironmentService.class, new DefaultExternalStorageAppEnvironmentService());

		TravelAssistanceDatabaseService databaseService = new TravelAssistanceDatabaseService(DB_NAME);
		putService(IDatabaseService.class, databaseService);
		putService(ITravelDataPersistenceService.class, new RouteParcelTravelDataPersistenceService(databaseService));

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
		SimpleSystemLocationService simpleSystemLocationService = new SimpleSystemLocationService(locationManager);

		//SimpleSystemLocationService has both implementations
		putService(ILocationService.class, simpleSystemLocationService);
		putService(ILocationStatusService.class, simpleSystemLocationService);

		putService(ITrackingManagementService.class, new TrackingManagementService(this));
	}

	//endregion
}
