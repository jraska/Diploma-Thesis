package com.jraska.pwdm.travel.persistence;

import com.jraska.core.database.OpenHelperDbService;
import com.jraska.pwdm.travel.database.TravelAssistanceDbHelper;
import com.jraska.pwdm.travel.database.TravelAssistanceParcelableDbHelper;

public class RouteParcelTravelDataPersistenceServiceTest extends RoutePersistenceServiceTestBase
{
	//region RouteParcelTravelDataPersistenceServiceTest impl

	@Override
	protected ITravelDataPersistenceService createPersistenceSvc()
	{
		TravelAssistanceParcelableDbHelper dbHelper = new TravelAssistanceParcelableDbHelper(getAppContext(), DB_NAME);
		return new RouteParcelTravelDataPersistenceService(new OpenHelperDbService(dbHelper));
	}

	//endregion
}
