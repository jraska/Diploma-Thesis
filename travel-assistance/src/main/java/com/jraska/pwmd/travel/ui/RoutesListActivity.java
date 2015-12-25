package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.common.events.Observer;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.data.RouteDescription;
import com.jraska.pwmd.travel.data.TransportChangeSpec;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.tracking.TrackingManager;
import com.jraska.pwmd.travel.transport.SimpleTransportManager;

import javax.inject.Inject;
import java.util.List;
import java.util.UUID;

public class RoutesListActivity extends BaseActivity {
  //region Fields

  @Bind(R.id.routes_recycler_view) RecyclerView _routesRecycler;
  @Bind(R.id.routes_empty_view) View _emptyView;
  @Bind(R.id.btnStartTracking) View _startTrackingButton;
  @Bind(R.id.btnStopTracking) View _stopTrackingButton;
  @Bind(R.id.btnSaveRoute) View _saveRouteButton;
  @Bind(R.id.btnChangeTransportType) ImageView _changeTransportButton;

  @Inject TrackingManager _trackingManager;
  @Inject TravelDataRepository _travelDataRepository;
  @Inject RoutesAdapter _routesAdapter;
  @Inject SimpleTransportManager _transportManager;

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
    updateStartStopButtons();
    updateTransportIcon();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.routes, menu);

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

      default:
        return super.onOptionsItemSelected(item);
    }
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
    _routesAdapter.setItemDeleteClickListener(new RoutesAdapter.OnItemDeleteListener() {
      @Override public void onItemDelete(int position, View itemView) {
        deleteRoute(_routesAdapter.getItem(position));
      }
    });
  }

  protected void showRoute(int position) {
    RouteDescription item = _routesAdapter.getItem(position);
    showRoute(item);
  }

  protected void showRoute(RouteDescription route) {
    Intent intent = new Intent(this, RouteDetailActivity.class);
    intent.putExtra(RouteDetailActivity.ROUTE_ID, route.getId());

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

  protected void deleteRoute(RouteDescription route) {
    _travelDataRepository.deleteRoute(route.getId());
    refreshRoutes();
  }

  protected void openHelpRequests() {
    startActivity(new Intent(RoutesListActivity.this, HelpRequestSendActivity.class));
  }

  protected void openSettings() {
    startActivity(new Intent(this, SettingsActivity.class));
  }

  @OnClick(R.id.btnStartTracking) void startTracking() {
    _trackingManager.startTracking();

    updateStartStopButtons();
  }

  @OnClick(R.id.btnStopTracking) void stopTracking() {
    _trackingManager.stopTracking();

    updateStartStopButtons();
  }

  private void updateStartStopButtons() {
    if (_trackingManager.isTracking()) {
      _startTrackingButton.setVisibility(View.GONE);
      _stopTrackingButton.setVisibility(View.VISIBLE);
      _saveRouteButton.setEnabled(true);
      _changeTransportButton.setEnabled(true);
    } else {
      _startTrackingButton.setVisibility(View.VISIBLE);
      _stopTrackingButton.setVisibility(View.GONE);
      _saveRouteButton.setEnabled(false);
      _changeTransportButton.setEnabled(false);
    }
  }

  @OnClick(R.id.btnSaveRoute) void saveRoute() {
    TrackingManager.PathInfo lastPath = _trackingManager.getLastPath();
    if (lastPath == null) {
      Toast.makeText(this, getString(R.string.noRouteToSave), Toast.LENGTH_SHORT).show();
      return;
    }

    RouteData routeData = new RouteData(new RouteDescription(UUID.randomUUID(),
        lastPath.getStart(), lastPath.getEnd(), "Test"), lastPath.getPath(),
        lastPath.getTransportChangeSpecs());

    _travelDataRepository.insertRoute(routeData);

    Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show();

    refreshRoutes();
  }

  @OnClick(R.id.btnChangeTransportType) void changeTransportType() {
    Dialog dialog = new Dialog(this);
    FrameLayout dialogView = new FrameLayout(this);

    LayoutInflater.from(this).inflate(R.layout.dialog_choose_new_transport, dialogView);
    dialog.setContentView(dialogView);
    dialog.setTitle(R.string.transport_change_select);
    DialogHolder dialogHolder = new DialogHolder(dialog, dialogView);
    dialogHolder.show();
  }

  protected boolean addTransportationChange(int type, @NonNull String title, String description) {
    return _trackingManager.addChange(type, title, description);
  }

  protected void updateTransportIcon() {
    int iconRes = TransportChangeSpec.getDarkIconRes(_transportManager.getCurrentTransportType());

    _changeTransportButton.setImageResource(iconRes);
  }

  protected void onNewTransportationChange(int type, @NonNull String title, String description) {
    _transportManager.setCurrentTransportType(type);
    addTransportationChange(type, title, description);
    updateTransportIcon();
  }

  //endregion

  //region Nested classes

  static class DialogHolder {
    private final Dialog _dialog;
    private final View _rootView;

    @Bind(R.id.transport_change_title_input) EditText _titleInput;
    @Bind(R.id.transport_change_description_input) EditText _descriptionInput;

    public DialogHolder(Dialog dialog, View rootView) {
      _dialog = dialog;
      _rootView = rootView;

      ButterKnife.bind(this, rootView);
    }

    @OnClick(R.id.btn_transport_change_select_bus) void selectBus() {
      addTransportInfo(TransportChangeSpec.TRANSPORT_TYPE_BUS);
    }

    @OnClick(R.id.btn_transport_change_select_train) void selectTrain() {
      addTransportInfo(TransportChangeSpec.TRANSPORT_TYPE_TRAIN);
    }

    @OnClick(R.id.btn_transport_change_select_walking) void selectWalking() {
      addTransportInfo(TransportChangeSpec.TRANSPORT_TYPE_WALK);
    }

    @OnClick({R.id.btn_transport_change_select_walking,
        R.id.btn_transport_change_select_train, R.id.btn_transport_change_select_bus})
    void dismissDialog() {
      _dialog.dismiss();
    }

    void addTransportInfo(int type) {
      RoutesListActivity activity = (RoutesListActivity) _rootView.getContext();

      activity.onNewTransportationChange(type, _titleInput.getText().toString(),
          _descriptionInput.getText().toString());
    }

    public void show() {
      _dialog.show();
    }
  }

  //endregion
}
