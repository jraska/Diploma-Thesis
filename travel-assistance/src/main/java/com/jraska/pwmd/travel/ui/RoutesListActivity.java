package com.jraska.pwmd.travel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

public class RoutesListActivity extends BaseActivity implements RoutesAdapter.OnItemMenuListener {
  //region Fields

  @Bind(R.id.routes_recycler_view) RecyclerView _routesRecycler;
  @Bind(R.id.routes_empty_view) View _emptyView;

  @Inject TravelDataRepository _travelDataRepository;
  @Inject RoutesAdapter _routesAdapter;
  @Inject EventBus _eventBus;

  //endregion

  //region Activity overrides

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_routes);

    TravelAssistanceApp.getComponent(this).inject(this);

    setupRoutes();
    refreshRoutes();

    _eventBus.register(this);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_routes, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_routes_help:
        openHelpRequests();
        return true;

      case R.id.action_routes_settings:
        openSettings();
        return true;

      case R.id.action_routes_recording:
        openRouteRecording();
        return true;

      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onDestroy() {
    _eventBus.unregister(this);

    super.onDestroy();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
  }

  //endregion

  //region BaseActivity overrides

  @Override
  protected int getNavigationIconId() {
    return R.drawable.ic_logo;
  }

  @Override
  protected boolean onNavigationIconClicked() {
    // TODO: 24/01/16 Show about dialog
    return false;
  }

  //endregion

  //region OnItemMenuListener impl

  @Override
  public void onItemNavigateClick(int position, View itemView) {
    RouteData item = _routesAdapter.getItem(position);
    NavigationActivity.startNavigationActivity(this, item.getId());
  }

  @Override
  public void onItemDeleteClick(int position, View itemView) {
    deleteRoute(_routesAdapter.getItem(position));
  }


  //endregion

  //region Methods

  public void onEvent(TravelDataRepository.NewRouteEvent newRouteEvent) {
    Timber.d("New route event received");

    _routesAdapter.add(newRouteEvent._newRoute);

    refreshRoutes();
  }

  public void onEvent(TravelDataRepository.RouteDeletedEvent routeDeleted) {
    Timber.d("Delete route event received");

    _routesAdapter.remove(routeDeleted._deletedRoute);

    refreshRoutes();
  }

  protected void setupRoutes() {
    _routesRecycler.setAdapter(_routesAdapter);
    _routesRecycler.setLayoutManager(new LinearLayoutManager(this));

    _routesAdapter.setItemClickListener(new RoutesAdapter.OnItemClickListener() {
      @Override public void onItemClick(int position, View itemView) {
        showRoute(position);
      }
    });
    _routesAdapter.setItemDeleteClickListener(this);
  }

  protected void showRoute(int position) {
    RouteData item = _routesAdapter.getItem(position);
    showRoute(item);
  }

  protected void showRoute(RouteData route) {
    Intent intent = new Intent(this, RouteDetailActivity.class);
    intent.putExtra(RouteDetailActivity.ROUTE_ID, route.getId());

    startActivity(intent);
  }

  void refreshRoutes() {
    TravelDataRepository service = _travelDataRepository;
    List<RouteData> routeDescriptions = service.selectAll();

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

  protected void deleteRoute(RouteData route) {
    _travelDataRepository.delete(route);
  }

  protected void openHelpRequests() {
    startActivity(new Intent(RoutesListActivity.this, HelpRequestSendActivity.class));
  }

  protected void openSettings() {
    startActivity(new Intent(this, SettingsActivity.class));
  }

  @OnClick(R.id.routes_empty_view_btn_tracking)
  protected void onEmptyViewRecordButtonClick() {
    openRouteRecording();
  }

  protected void openRouteRecording() {
    startActivity(new Intent(this, RouteRecordActivity.class));
  }

  //endregion
}
