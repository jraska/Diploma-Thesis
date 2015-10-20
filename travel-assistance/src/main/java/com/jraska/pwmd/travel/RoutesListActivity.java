package com.jraska.pwmd.travel;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.common.events.Observer;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.persistence.TravelDataPersistenceService;
import com.jraska.pwmd.travel.tracking.TrackingManagementService;

import java.util.*;

public class RoutesListActivity extends BaseTravelActivity {
  //region Fields

//	@Bind(R.id.btnTest)
//	Button _testBtn;

  @Bind(android.R.id.list) ListView _routesList;
  @Bind(android.R.id.empty) View _emptyView;

  private RoutesAdapter _routesAdapter;

  private Observer<RouteDescription> _descriptionsObserver = new Observer<RouteDescription>() {
    @Override
    public void update(Object sender, RouteDescription args) {
      _routesAdapter.add(args);
    }
  };

  //endregion

  //region Properties

  protected TrackingManagementService getTrackingManagementService() {
    return TrackingManagementService.Stub.asInterface();
  }

  //endregion

  //region Activity overrides

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    ButterKnife.bind(this);

    setupRoutes();

    refreshRoutes();

    registerOnRouteChangedObservers();
  }

  private boolean showLastSavedRoute() {
    List<RouteDescription> routeDescriptions = getRoutesPersistenceService().selectAllRouteDescriptions();

    if (routeDescriptions.size() == 0) {
      return false;
    }

    showRoute(routeDescriptions.get(routeDescriptions.size() - 1));

    return true;
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    menu.add(getString(R.string.i_am_lost)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        startActivity(new Intent(RoutesListActivity.this, HelpRequestSendActivity.class));
        return true;
      }
    });

    return true;
  }

  @Override
  protected void onDestroy() {
    unregisterOnRouteChangeObservers();

    super.onDestroy();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);

    AdapterView.AdapterContextMenuInfo adapterContextMenuInfo = (AdapterView.AdapterContextMenuInfo) menuInfo;

    final int position = adapterContextMenuInfo.position;

    menu.add(getString(R.string.delete)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        RouteDescription item1 = _routesAdapter.getItem(position);
        getRoutesPersistenceService().deleteRoute(item1.getId());
        refreshRoutes();

        return true;
      }
    });
  }

  //endregion

  //region Methods

  protected void setupRoutes() {
    _routesAdapter = new RoutesAdapter(this);

    _routesList.setAdapter(_routesAdapter);
    _routesList.setEmptyView(_emptyView);
    _routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        showRoute(position);
      }
    });


    registerForContextMenu(_routesList);
  }

  protected void showRoute(int position) {
    RouteDescription item = _routesAdapter.getItem(position);
    showRoute(item);
  }

  protected void showRoute(RouteDescription item) {
    Intent intent = new Intent(this, RouteDisplayActivity.class);
    intent.putExtra(RouteDisplayActivity.ROUTE_ID, item.getId());

    startActivity(intent);
  }

  void registerOnRouteChangedObservers() {
    getRoutesPersistenceService().getOnNewRoute().registerObserver(_descriptionsObserver);
  }

  void unregisterOnRouteChangeObservers() {
    getRoutesPersistenceService().getOnNewRoute().registerObserver(_descriptionsObserver);
  }

  void refreshRoutes() {
    TravelDataPersistenceService service = getRoutesPersistenceService();
    List<RouteDescription> routeDescriptions = service.selectAllRouteDescriptions();

    _routesAdapter.clear();

    _routesAdapter.setNotifyOnChange(false);

    for (RouteDescription routeDescription : routeDescriptions) {
      _routesAdapter.add(routeDescription);
    }

    _routesAdapter.notifyDataSetChanged();
  }

  @OnClick(R.id.btnStartTracking)
  void startTracking() {
    getTrackingManagementService().startTracking();
  }

  @OnClick(R.id.btnStopTracking)
  void stopTracking() {
    getTrackingManagementService().stopTracking();
  }

  @OnClick(R.id.btnSaveRoute)
  void saveRoute() {
    TrackingManagementService.PathInfo lastPath = getTrackingManagementService().getLastPath();
    if (lastPath == null) {
      Toast.makeText(this, getString(R.string.noRouteToSave), Toast.LENGTH_SHORT).show();
      return;
    }

    RouteData routeData = new RouteData(new RouteDescription(UUID.randomUUID(), lastPath.getStart(), lastPath.getEnd(), "Test"), lastPath.getPath());

    getRoutesPersistenceService().insertRoute(routeData);

    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
  }

  private TravelDataPersistenceService getRoutesPersistenceService() {
    return TravelDataPersistenceService.Stub.asInterface();
  }

  protected void testPersistencePositions() {
    UUID testId = UUID.fromString("07684a55-f8d4-498a-a313-609965a2b3df");

    //build test path
    int pointsCount = 3;
    List<Position> positions = new ArrayList<>(pointsCount);
    for (int i = 0; i < pointsCount; i++) {
      positions.add(generatePosition());
    }

    //build test route
    RouteDescription routeDescription = new RouteDescription(testId, new Date(), new Date(), "Test");
    RouteData routeData = new RouteData(routeDescription, new Path(positions));

    TravelDataPersistenceService persistenceService = getRoutesPersistenceService();

    //try insert
    long value = persistenceService.insertRoute(routeData);

    // try update
    positions.add(generatePosition());
    RouteData routeData2 = new RouteData(routeDescription, new Path(positions));

    //try get all
    List<RouteDescription> routeDescriptions = persistenceService.selectAllRouteDescriptions();

    for (RouteDescription description : routeDescriptions) {
      RouteData routeData1 = persistenceService.selectRouteData(description.getId());
      if (routeData1 != null) {
        //stub
        int i = 0;
        i++;
      }
    }


    //try get current

    persistenceService.updateRoute(routeData2);

    for (RouteDescription description : routeDescriptions) {
      RouteData routeData1 = persistenceService.selectRouteData(description.getId());
      if (routeData1 != null) {
        //stub
        int i = 0;
        i++;
      }
    }

    persistenceService.deleteRoute(routeData2.getId());

    for (RouteDescription description : routeDescriptions) {
      RouteData routeData1 = persistenceService.selectRouteData(description.getId());
      if (routeData1 != null) {
        //stub
        int i = 0;
        i++;
      }
    }
  }

  private Position generatePosition() {
    Random random = new Random();
    return new Position(random.nextDouble() * 50, random.nextDouble() * 50, System.currentTimeMillis(), 30.0f, "GPS");
  }

  //endregion
}
