package com.jraska.pwdm.travel;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.jraska.pwdm.core.gps.Position;
import com.jraska.pwdm.travel.data.Path;
import com.jraska.pwdm.travel.data.RouteData;
import com.jraska.pwdm.travel.data.RouteDescription;
import com.jraska.pwdm.travel.persistence.ITravelDataPersistenceService;

import java.util.*;

public class MainActivity extends Activity
{
	//region Fields

	@InjectView(R.id.btnTest)
	Button mTestBtn;

	//endregion

	//region Activity overrides

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ButterKnife.inject(this);
	}

	//endregion

	//region Methods

	@OnClick(R.id.btnTest)
	void test()
	{
		UUID testId = UUID.fromString("07684a55-f8d4-498a-a313-609965a2b3df");

		//build test path
		int pointsCount = 3;
		List<Position> positions = new ArrayList<Position>(pointsCount);
		for (int i = 0; i < pointsCount; i++)
		{
			positions.add(generatePosition());
		}

		//build test route
		RouteDescription routeDescription = new RouteDescription(testId, new Date(), new Date(), "Test");
		RouteData routeData = new RouteData(routeDescription, new Path(positions));

		ITravelDataPersistenceService persistenceService = ITravelDataPersistenceService.Stub.asInterface();

		//try insert
		long value = persistenceService.insertRoute(routeData);

		// try update
		positions.add(generatePosition());
		RouteData routeData2 = new RouteData(routeDescription, new Path(positions));

		//try get all
		List<RouteDescription> routeDescriptions = persistenceService.getRouteDescriptions();

		for (RouteDescription description : routeDescriptions)
		{
			RouteData routeData1 = persistenceService.getRouteData(description.getId());
			if(routeData1 != null)
			{
				//stub
				int i = 0;
				i++;
			}
		}


		//try get current

		persistenceService.updateRoute(routeData2);

		for (RouteDescription description : routeDescriptions)
		{
			RouteData routeData1 = persistenceService.getRouteData(description.getId());
			if(routeData1 != null)
			{
				//stub
				int i = 0;
				i++;
			}
		}

		persistenceService.deleteRoute(routeData2);

		for (RouteDescription description : routeDescriptions)
		{
			RouteData routeData1 = persistenceService.getRouteData(description.getId());
			if(routeData1 != null)
			{
				//stub
				int i = 0;
				i++;
			}
		}
	}

	private Position generatePosition()
	{
		Random random = new Random();
		return new Position(random.nextDouble() * 50, random.nextDouble() * 50, System.currentTimeMillis(), 30.0f, "GPS");
	}

	//endregion
}
