package com.jraska.pwdm.travel.persistence;

import android.location.LocationManager;
import com.jraska.pwdm.core.gps.Position;
import com.jraska.pwdm.test.BaseTest;
import com.jraska.pwdm.travel.data.Path;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.data.RouteDescription;
import com.jraska.pwdm.travel.database.TravelAssistanceDatabaseService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public class RouteParcelTravelDataPersistenceServiceTest extends BaseTest
{
	//region Constants

	public static final String DB_NAME = "test.db";

	//endregion

	//region Fields

	private RouteParcelTravelDataPersistenceService mService;

	//endregion

	//region Setup Methods

	@Before
	public void setUp() throws Exception
	{
		mService = new RouteParcelTravelDataPersistenceService(new TravelAssistanceDatabaseService(getAppContext(), DB_NAME));
	}

	@After
	public void tearDown() throws Exception
	{
		File dbFile = new File(DB_NAME);

		if (dbFile.exists())
		{
			dbFile.delete();
		}
	}

	//endregion

	//region Test Methods

	@Test
	public void testInsert() throws Exception
	{
		long l = mService.insertRoute(newRouteData());

		assertThat(l, greaterThan(0L));
		assertThat(mService.getRouteDescriptions().size(), equalTo(1));
	}

	@Test
	public void testInsertSelect() throws Exception
	{
		RouteData routeData = newRouteData();

		mService.insertRoute(routeData);

		RouteData restoredData = mService.getRouteData(routeData.getId());

		assertThat(routeData, equalTo(restoredData));
	}

	//endregion

	//region Methods

	private static RouteData newRouteData()
	{
		RouteDescription description = new RouteDescription(UUID.randomUUID(), new Date(), new Date(), "Test");

		List<Position> positions = Arrays.asList(newPosition(), newPosition());

		return new RouteData(description, new Path(positions));
	}

	private static Position newPosition()
	{
		Random random = new Random();
		return new Position(random.nextDouble() * 90, random.nextDouble() * 90, new Date().getTime(), random.nextFloat() * 30, LocationManager.GPS_PROVIDER);
	}

	//endregion
}
