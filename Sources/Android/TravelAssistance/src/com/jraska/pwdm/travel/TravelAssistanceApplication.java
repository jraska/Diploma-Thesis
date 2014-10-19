package com.jraska.pwdm.travel;

import com.jraska.core.AppContextModule;
import com.jraska.core.database.OpenHelperDbService;
import com.jraska.core.services.DefaultExternalStorageAppEnvironmentService;
import com.jraska.core.services.IAppEnvironmentService;
import com.jraska.pwdm.core.PWDMApplication;
import com.jraska.pwdm.core.gps.SimpleSystemLocationService;
import com.jraska.pwdm.travel.persistence.RouteParcelTravelDataPersistenceService;
import com.jraska.pwdm.travel.tracking.TrackingManagementService;
import dagger.Module;
import dagger.Provides;

import javax.inject.Named;
import javax.inject.Singleton;
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
					TrackingManagementService.Module.class,
					AppSettingsModule.class, OpenHelperDbService.Module.class,
					RouteParcelTravelDataPersistenceService.Module.class,

			}
	)
	static class AppModule
	{
	}

	@Module(injects = {String.class, IAppEnvironmentService.class}, library = true)
	public static class AppSettingsModule
	{
		@Provides
		@Named("dbName")
		String dbName()
		{
			return DB_NAME;
		}

		@Provides
		@Singleton
		IAppEnvironmentService provideSvc()
		{
			return new DefaultExternalStorageAppEnvironmentService();
		}
	}

	//endregion
}
