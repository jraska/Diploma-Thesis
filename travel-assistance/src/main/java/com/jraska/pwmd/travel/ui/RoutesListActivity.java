package com.jraska.pwmd.travel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.common.events.Observer;
import com.jraska.pwmd.core.gps.Position;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.tracking.TrackingManager;

import javax.inject.Inject;
import java.util.*;

public class RoutesListActivity extends BaseActivity {
  //region Fields

  @Bind(R.id.routes_recycler_view) RecyclerView _routesRecycler;
  @Bind(R.id.routes_empty_view) View _emptyView;

  @Inject TrackingManager _trackingManager;
  @Inject TravelDataRepository _travelDataRepository;
  @Inject RoutesAdapter _routesAdapter;

  private Observer<RouteDescription> _descriptionsObserver = new Observer<RouteDescription>() {
    @Override
    public void update(Object sender, RouteDescription args) {
      _routesAdapter.add(args);
    }
  };

  //endregion

  //region Activity overrides

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_routes);

    TravelAssistanceApp.getComponent(this).inject(this);

    setupRoutes();
    refreshRoutes();
    registerOnRouteChangedObservers();
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

    // TODO add delete functionality to dropdown menu
    menu.add(getString(R.string.delete)).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
      @Override
      public boolean onMenuItemClick(MenuItem item) {
        RouteDescription item1 = _routesAdapter.getItem(position);
        _travelDataRepository.deleteRoute(item1.getId());
        refreshRoutes();

        return true;
      }
    });
  }

  //endregion

  //region Methods

  protected void setupRoutes() {
    _routesRecycler.setAdapter(_routesAdapter);
    _routesRecycler.setLayoutManager(new LinearLayoutManager(this));

    _routesAdapter.setItemClickListener(new RoutesAdapter.OnItemClickListener() {
      @Override public void onItemClick(int position, View itemView) {
        showRoute(position);
      }
    });
  }

  protected void showRoute(int position) {
    RouteDescription item = _routesAdapter.getItem(position);
    showRoute(item);
  }

  protected void showRoute(RouteDescription route) {
    Intent intent = new Intent(this, RouteDisplayActivity.class);
    intent.putExtra(RouteDisplayActivity.ROUTE_ID, route.getId());

    startActivity(intent);
  }

  void registerOnRouteChangedObservers() {
    _travelDataRepository.getOnNewRoute().registerObserver(_descriptionsObserver);
  }

  void unregisterOnRouteChangeObservers() {
    _travelDataRepository.getOnNewRoute().registerObserver(_descriptionsObserver);
  }

  void refreshRoutes() {
    TravelDataRepository service = _travelDataRepository;
    List<RouteDescription> routeDescriptions = service.selectAllRouteDescriptions();

    _routesAdapter.clear();

    _routesAdapter.addAll(routeDescriptions);
    _routesAdapter.notifyDataSetChanged();

    updateEmptyView();
  }

  private void updateEmptyView() {
    if (_routesAdapter.getItemCount() == 0) {
      _routesRecycler.setVisibility(View.GONE);
      _emptyView.setVisibility(View.VISIBLE);
    } else {
      _routesRecycler.setVisibility(View.VISIBLE);
      _emptyView.setVisibility(View.GONE);
    }
  }

  @OnClick(R.id.btnStartTracking) void startTracking() {
    _trackingManager.startTracking();
  }

  @OnClick(R.id.btnStopTracking) void stopTracking() {
    _trackingManager.stopTracking();
  }

  @OnClick(R.id.btnSaveRoute) void saveRoute() {
    TrackingManager.PathInfo lastPath = _trackingManager.getLastPath();
    if (lastPath == null) {
      Toast.makeText(this, getString(R.string.noRouteToSave), Toast.LENGTH_SHORT).show();
      return;
    }

    RouteData routeData = new RouteData(new RouteDescription(UUID.randomUUID(), lastPath.getStart(), lastPath.getEnd(), "Test"), lastPath.getPath());

    _travelDataRepository.insertRoute(routeData);

    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();
  }

  // TODO: move to unit tests
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

    TravelDataRepository persistenceService = _travelDataRepository;

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
