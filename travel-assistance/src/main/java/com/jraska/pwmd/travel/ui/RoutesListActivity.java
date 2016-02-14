package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.annotations.Event;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.nfc.NfcRouteEncoder;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.settings.SettingsManager;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

public class RoutesListActivity extends BaseActivity implements RoutesAdapter.OnItemMenuListener {
  //region Constants

  public static final String KEY_NFC_PROCESSED = "ForwardIntent";

  //endregion

  //region Fields

  @Bind(R.id.routes_recycler_view) RecyclerView _routesRecycler;
  @Bind(R.id.routes_empty_view) View _emptyView;

  @Inject TravelDataRepository _travelDataRepository;
  @Inject RoutesAdapter _routesAdapter;
  @Inject EventBus _eventBus;
  @Inject SettingsManager _settingsManager;
  @Inject TravelAssistanceApp _app;
  @Inject NfcRouteEncoder _routeEncoder;

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

  @Override protected void onResume() {
    super.onResume();

    Intent intent = getIntent();
    checkNfcUsed(intent);
  }

  @Override
  protected void onDestroy() {
    _eventBus.unregister(this);

    super.onDestroy();
  }

  @Override
  protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);

    checkNfcIntent(intent);
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
    startNavigation(item.getId());
  }

  @Override
  public void onItemDeleteClick(int position, View itemView) {
    deleteRoute(_routesAdapter.getItem(position));
  }

  //endregion

  //region Methods

  @Event
  public void onEvent(TravelDataRepository.NewRouteEvent newRouteEvent) {
    Timber.d("New route event received");

    _routesAdapter.add(newRouteEvent._newRoute);

    refreshRoutes();
  }

  @Event
  public void onEvent(TravelDataRepository.RouteDeletedEvent routeDeleted) {
    Timber.d("Delete route event received");

    _routesAdapter.remove(routeDeleted._deletedRoute);

    refreshRoutes();
  }

  private void checkNfcUsed(Intent intent) {
    checkNfcIntent(intent);
  }

  private boolean isNfcIntent(Intent intent) {
    return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction());
  }

  private void checkNfcIntent(Intent intent) {
    if (isNfcIntent(intent) && !intent.getBooleanExtra(KEY_NFC_PROCESSED, false)) {
      intent.putExtra(KEY_NFC_PROCESSED, true);

      Iterable<Activity> runningActivities = _app.getRunningActivities();
      // clear top
      for (Activity activity : runningActivities) {
        if (activity != this) {
          activity.finish();
        }
      }

      long routeId = _routeEncoder.extractNavigationRouteId(intent);
      boolean routeExists = routeExists(routeId);
      if (!routeExists) {
        Timber.w("Route form NFC tag with id %d does not exist in database.", routeId);
        showNfcRouteNotExistsMessage();
        return; // Do nothing more
      }

      startNavigation(routeId);
    }
  }

  private void showNfcRouteNotExistsMessage() {
    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
    dialogBuilder.setTitle(R.string.route_nfc_not_found_title)
        .setMessage(R.string.route_nfc_not_found_message)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setPositiveButton(android.R.string.cancel, null)
        .show();
  }

  private boolean routeExists(long routeId) {
    for (RouteData routeData : _routesAdapter) {
      if (routeData.getId() == routeId) {
        return true;
      }
    }

    return _travelDataRepository.routeExists(routeId);
  }

  private void startNavigation(long id) {
    NavigationActivity.startNew(this, id);
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
    long id = route.getId();
    startDetail(id);
  }

  private void startDetail(long id) {
    RouteDetailActivity.startNew(this, id);
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
