package com.jraska.pwdm.travel.persistence;

import com.jraska.core.database.OpenHelperDbService;
import com.jraska.pwdm.travel.database.TravelAssistanceDbHelper;
import com.jraska.pwdm.travel.database.TravelAssistanceParcelableDbHelper;

public class SimpleRouteDataPersistenceServiceTest extends RoutePersistenceServiceTestBase
{
	//region RoutePersistenceServiceTestBase impl

	@Override
	protected ITravelDataPersistenceService createPersistenceSvc()
	{
		TravelAssistanceDbHelper dbHelper = new TravelAssistanceDbHelper(getAppContext(), DB_NAME);
		return new SimpleRouteDataPersistenceService(new OpenHelperDbService(dbHelper));
	}

	//endregion
}
