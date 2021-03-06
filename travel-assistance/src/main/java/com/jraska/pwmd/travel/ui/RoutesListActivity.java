package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnLongClick;
import com.jraska.dialog.LambdaDialogFragment;
import com.jraska.pwmd.travel.BuildConfig;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.feedback.Feedback;
import com.jraska.pwmd.travel.nfc.NfcRouteEncoder;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.rx.IOThreadTransformer;
import com.jraska.pwmd.travel.util.ShowContentDescriptionLongClickListener;
import hugo.weaving.DebugLog;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.List;

public class RoutesListActivity extends BaseActivity
    implements RoutesAdapter.OnItemMenuListener {
  //region Constants

  public static final String KEY_NFC_PROCESSED = "ForwardIntent";

  long[] items = new long[1024 * 1024];

  //endregion

  //region Fields

  @BindView(R.id.routes_recycler_view) RecyclerView _routesRecycler;
  @BindView(R.id.routes_empty_view) View _emptyView;

  @Inject TravelDataRepository _travelDataRepository;
  @Inject RoutesAdapter _routesAdapter;
  @Inject EventBus _eventBus;
  @Inject TravelAssistanceApp _app;
  @Inject NfcRouteEncoder _routeEncoder;

  //endregion

  //region Activity overrides

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_routes);

    TravelAssistanceApp.getComponent(this).inject(this);

    setupViews();

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
        openHelpRequest();
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
    refreshRoutes();

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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == FeedbackActivity.REQUEST_FEEDBACK && resultCode == RESULT_OK) {
      Toast.makeText(this, R.string.thank_you_for_feedback, Toast.LENGTH_LONG).show();
    }
  }

  //endregion

  //region BaseActivity overrides

  @Override
  protected int getNavigationIconId() {
    return R.drawable.ic_logo;
  }

  @Override
  @DebugLog
  protected boolean onNavigationIconClicked() {
    LambdaDialogFragment.builder(this)
        .title(getAppInfoTitle())
        .validateEagerly(BuildConfig.DEBUG)
        .message(R.string.about)
        .icon(R.drawable.ic_logo_no_padding)
        .positiveText(android.R.string.ok)
        .neutralText(R.string.about_feedback)
        .neutralMethod(RoutesListActivity::onFeedbackRequested)
        .show();

    return true;
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

  public void onFeedbackRequested() {
    Feedback.startFeedback(this, "");
  }

  //endregion

  @Subscribe
  public void onRouteDeleted(TravelDataRepository.RouteDeleteEvent routeDeleted) {
    Timber.d("Delete route event received");

    runOnUiThread(this::refreshRoutes);
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
    LambdaDialogFragment.builder(this)
        .title(R.string.route_nfc_not_found_title)
        .message(R.string.route_nfc_not_found_message)
        .icon(android.R.drawable.ic_dialog_alert)
        .positiveText(android.R.string.cancel)
        .show();
  }

  private boolean routeExists(long routeId) {
    for (RouteData routeData : _routesAdapter) {
      if (routeData.getId() == routeId) {
        return true;
      }
    }

    return _travelDataRepository.routeExists(routeId).toBlocking().first();
  }

  private void startNavigation(long id) {
    NavigationActivity.startNew(this, id);
  }

  protected void setupViews() {
    _routesRecycler.setAdapter(_routesAdapter);
    _routesRecycler.setLayoutManager(new LinearLayoutManager(this));

    _routesAdapter.setItemClickListener((position, itemView) -> showRoute(position));
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

  @DebugLog void refreshRoutes() {
    TravelDataRepository routesRepository = _travelDataRepository;
    routesRepository.selectAll()
        .compose(IOThreadTransformer.get())
        .subscribe(this::setRoutes);
  }

  void setRoutes(List<RouteData> routes) {
    _routesAdapter.clear();
    _routesAdapter.addAll(routes);
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
    _travelDataRepository.delete(route)
        .compose(IOThreadTransformer.get())
        .subscribe();
  }

  protected void openHelpRequest() {
    startActivity(new Intent(this, HelpRequestSendActivity.class));
  }

  protected void openSettings() {
    startActivity(new Intent(this, SettingsActivity.class));
  }

  @OnClick(R.id.routes_empty_view_btn_recording)
  protected void onEmptyViewRecordButtonClick() {
    openRouteRecording();
  }

  protected void openRouteRecording() {
    startActivity(new Intent(this, RouteRecordActivity.class));
  }

  @OnLongClick({R.id.routes_empty_view_btn_recording}) boolean showContentDescription(View v) {
    return ShowContentDescriptionLongClickListener.showContentDescription(v);
  }

  public String getAppInfoTitle() {
    return Feedback.getAppInfoTitle(this);
  }

  //endregion
}
