package com.jraska.pwdm.travel.persistence;

import android.location.LocationManager;
import com.jraska.pwdm.core.gps.Position;
import com.jraska.pwdm.test.BaseTest;
import com.jraska.pwdm.travel.data.Path;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.data.RouteDescription;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertThat;

public abstract class RoutePersistenceServiceTestBase extends BaseTest
{
	//region Constants

	public static final String DB_NAME = "test.db";

	//endregion

	//region Fields

	private ITravelDataPersistenceService mService;

	//endregion

	//region Setup Methods

	@Before
	public void setUp() throws Exception
	{
		mService = createPersistenceSvc();
	}

	protected abstract ITravelDataPersistenceService createPersistenceSvc();

	@After
	public void tearDown() throws Exception
	{
		mService.dispose();
	}

	//endregion

	//region Test Methods

	@Test
	public void testInsert() throws Exception
	{
		long l = mService.insertRoute(newRouteData());

		assertThat(l, greaterThan(0L));
		assertThat(mService.selectAllRouteDescriptions().size(), equalTo(1));
	}

	@Test
	public void testInsertSelect() throws Exception
	{
		RouteData routeData = newRouteData(10);

		mService.insertRoute(routeData);

		RouteData restoredData = mService.selectRouteData(routeData.getId());

		assertThat(routeData, equalTo(restoredData));
	}

	@Test
	public void testUpdate() throws Exception
	{
		RouteData routeData = newRouteData();

		mService.insertRoute(routeData);

		RouteDescription routeDescription = new RouteDescription(routeData.getId(), new Date(), new Date(), "Other text");
		RouteData updateRouteData = new RouteData(routeDescription, routeData.getPath());

		mService.updateRoute(updateRouteData);

		assertThat(mService.selectAllRouteDescriptions().size(), equalTo(1));

		RouteData restoredData = mService.selectRouteData(updateRouteData.getId());
		assertThat(restoredData, equalTo(updateRouteData));
	}

	@Test
	public void testDelete() throws Exception
	{
		mService.insertRoute(newRouteData());
		mService.insertRoute(newRouteData());
		mService.insertRoute(newRouteData());

		List<RouteDescription> routeDescriptions = mService.selectAllRouteDescriptions();
		assertThat(routeDescriptions.size(), equalTo(3));

		for (RouteDescription routeDescription : routeDescriptions)
		{
			mService.deleteRoute(routeDescription.getId());
		}

		List<RouteDescription> descriptions = mService.selectAllRouteDescriptions();
		assertThat(descriptions.size(), equalTo(0));
	}

	//endregion

	//region Methods

	private static RouteData newRouteData()
	{
		return newRouteData(10);
	}

	private static RouteData newRouteData(int count)
	{
		RouteDescription description = new RouteDescription(UUID.randomUUID(), new Date(), new Date(), "Test");

		return new RouteData(description, newRoute(count));
	}

	private static Path newRoute(int count)
	{
		return new Path(newPositions(count));
	}

	private static List<Position> newPositions(int count)
	{
		ArrayList<Position> positions = new ArrayList<Position>(count);

		for (int i = 0; i < count; i++)
		{
			positions.add(newPosition());
		}

		return positions;
	}

	private static Position newPosition()
	{
		Random random = new Random();

		return new Position(random.nextDouble() * 90, random.nextDouble() * 90, new Date().getTime(), random.nextFloat() * 30, LocationManager.GPS_PROVIDER);
	}

	//endregion

}
