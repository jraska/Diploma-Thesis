package com.jraska.pwmd.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.jraska.common.events.IObserver;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.persistence.ITravelDataPersistenceService;
import com.jraska.pwmd.travel.tracking.ITrackingManagementService;

import java.util.*;

public class RoutesListActivity extends BaseTravelActivity
{
	//region Fields

//	@InjectView(R.id.btnTest)
//	Button mTestBtn;

	@InjectView(R.id.btnStartTracking)
	Button mStartTrackingButton;

	@InjectView(R.id.btnStopTracking)
	Button mStopTrackingButton;

	@InjectView(R.id.btnSaveRoute)
	Button mSaveRouteButton;

	@InjectView(android.R.id.list)
	ListView mRoutesList;

	@InjectView(android.R.id.empty)
	View mEmptyView;

	private RoutesAdapter mRoutesAdapter;

	private IObserver<RouteDescription> mDescriptionsObserver = new IObserver<RouteDescription>()
	{
		@Override
		public void update(Object sender, RouteDescription args)
		{
			mRoutesAdapter.add(args);
		}
	};

	//endregion

	//region Properties

	protected ITrackingManagementService getTrackingManagementService()
	{
		return ITrackingManagementService.Stub.asInterface();
	}

	//endregion

	//region Activity overrides

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		ButterKnife.inject(this);

		//started with nfc
		if ((getIntent().getFlags() & 272629760) == 272629760)
		{
			showLastSavedRoute();
		}

		setupRoutes();

		refreshRoutes();

		registerOnRouteChangedObservers();
	}

	private boolean showLastSavedRoute()
	{
		List<RouteDescription> routeDescriptions = getRoutesPersistenceService().selectAllRouteDescriptions();

		if(routeDescriptions.size() == 0)
		{
			return false;
		}

		showRoute(routeDescriptions.get(routeDescriptions.size() - 1));

		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		menu.add(getString(R.string.i_am_lost)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				startActivity(new Intent(RoutesListActivity.this, HelpRequestSendActivity.class));
				return true;
			}
		});

		return true;
	}

	@Override
	protected void onDestroy()
	{
		unregisterOnRouteChangeObservers();

		super.onDestroy();
	}

	@Override
	protected void onNewIntent(Intent intent)
	{
		super.onNewIntent(intent);
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
	{
		super.onCreateContextMenu(menu, v, menuInfo);

		AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

		final int position = adapterContextMenuInfo.position;

		menu.add(getString(R.string.delete)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener()
		{
			@Override
			public boolean onMenuItemClick(MenuItem item)
			{
				RouteDescription item1 = mRoutesAdapter.getItem(position);
				getRoutesPersistenceService().deleteRoute(item1.getId());
				refreshRoutes();

				return true;
			}
		});
	}

	//endregion

	//region Methods

	protected void setupRoutes()
	{
		mRoutesAdapter = new RoutesAdapter(this);

		mRoutesList.setAdapter(mRoutesAdapter);
		mRoutesList.setEmptyView(mEmptyView);
		mRoutesList.setOnItemClickListener(new AdapterView.OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id)
			{
				showRoute(position);
			}
		});


		registerForContextMenu(mRoutesList);
	}

	protected void showRoute(int position)
	{
		RouteDescription item = mRoutesAdapter.getItem(position);
		showRoute(item);
	}

	protected void showRoute(RouteDescription item)
	{
		Intent intent = new Intent(this, RouteDisplayActivity.class);
		intent.putExtra(RouteDisplayActivity.ROUTE_ID, item.getId());

		startActivity(intent);
	}

	void registerOnRouteChangedObservers()
	{
		getRoutesPersistenceService().getOnNewRoute().registerObserver(mDescriptionsObserver);
	}

	void unregisterOnRouteChangeObservers()
	{
		getRoutesPersistenceService().getOnNewRoute().registerObserver(mDescriptionsObserver);
	}

	void refreshRoutes()
	{
		ITravelDataPersistenceService service = getRoutesPersistenceService();
		List<RouteDescription> routeDescriptions = service.selectAllRouteDescriptions();

		mRoutesAdapter.clear();

		mRoutesAdapter.setNotifyOnChange(false);

		for (RouteDescription routeDescription : routeDescriptions)
		{
			mRoutesAdapter.add(routeDescription);
		}

		mRoutesAdapter.notifyDataSetChanged();
	}

	@OnClick(R.id.btnStartTracking)
	void startTracking()
	{
		getTrackingManagementService().startTracking();
	}

	@OnClick(R.id.btnStopTracking)
	void stopTracking()
	{
		getTrackingManagementService().stopTracking();
	}

	@OnClick(R.id.btnSaveRoute)
	void saveRoute()
	{
		ITrackingManagementService.PathInfo lastPath = getTrackingManagementService().getLastPath();
		if (lastPath == null)
		{
			Toast.makeText(this, getString(R.string.noRouteToSave), Toast.LENGTH_SHORT).show();
			return;
		}

		RouteData routeData = new RouteData(new RouteDescription(UUID.randomUUID(), lastPath.getStart(), lastPath.getEnd(), "Test"), lastPath.getPath());

		getRoutesPersistenceService().insertRoute(routeData);

		Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
	}

	private ITravelDataPersistenceService getRoutesPersistenceService()
	{
		return ITravelDataPersistenceService.Stub.asInterface();
	}

	//	@OnClick(R.id.btnTest)
	void test()
	{
//		testPersistencePositions();
	}

	protected void testPersistencePositions()
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

		ITravelDataPersistenceService persistenceService = getRoutesPersistenceService();

		//try insert
		long value = persistenceService.insertRoute(routeData);

		// try update
		positions.add(generatePosition());
		RouteData routeData2 = new RouteData(routeDescription, new Path(positions));

		//try get all
		List<RouteDescription> routeDescriptions = persistenceService.selectAllRouteDescriptions();

		for (RouteDescription description : routeDescriptions)
		{
			RouteData routeData1 = persistenceService.selectRouteData(description.getId());
			if (routeData1 != null)
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
			RouteData routeData1 = persistenceService.selectRouteData(description.getId());
			if (routeData1 != null)
			{
				//stub
				int i = 0;
				i++;
			}
		}

		persistenceService.deleteRoute(routeData2.getId());

		for (RouteDescription description : routeDescriptions)
		{
			RouteData routeData1 = persistenceService.selectRouteData(description.getId());
			if (routeData1 != null)
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
