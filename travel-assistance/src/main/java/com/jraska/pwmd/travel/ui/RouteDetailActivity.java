package com.jraska.pwmd.travel.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.RouteData;
import com.jraska.pwmd.travel.media.SoundsManager;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import com.jraska.pwmd.travel.rx.IOThreadTransformer;
import timber.log.Timber;

import javax.inject.Inject;

public class RouteDetailActivity extends BaseActivity implements RouteDisplayFragment.EventListener {
  //region Constants

  public static final String KEY_INTENT_ROUTE_ID = "RouteId";

  //endregion

  //region Fields

  private RouteDisplayFragment _routeDisplayFragment;

  @Inject TravelDataRepository _travelDataRepository;
  @Inject SoundsManager _soundsManager;

  private long _routeId;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_route_detail);
    TravelAssistanceApp.getComponent(this).inject(this);

    _routeDisplayFragment = (RouteDisplayFragment) getSupportFragmentManager().findFragmentById(R.id.map);

    _routeId = getIntent().getLongExtra(KEY_INTENT_ROUTE_ID, 0);

    showRoute();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.menu_route_detail, menu);

    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_route_navigate:
        startNavigation();
        return true;
      case R.id.action_route_write_nfc:
        startNfcWrite();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == NfcWriteActivity.REQUEST_CODE_WRITE_NFC) {
      onResultWriteNfc(resultCode, data);
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  //endregion

  //region RouteDisplayFragment.EventListener impl

  @Override
  public boolean onNoteSpecClicked(NoteSpec noteSpec) {
    if (noteSpec.getSoundId() != null) {
      _soundsManager.play(noteSpec.getSoundId());
    }

    if (noteSpec.getImageId() != null) {
      ImageDialog dialog = ImageDialog.newInstance(noteSpec.getImageId(), noteSpec.getCaption());
      dialog.show(this);
    }

    return false;
  }

  protected void startNfcWrite() {
    Intent intent = new Intent(this, NfcWriteActivity.class);
    intent.putExtra(NfcWriteActivity.KEY_ROUTE_ID, _routeId);

    startActivityForResult(intent, NfcWriteActivity.REQUEST_CODE_WRITE_NFC);
  }

  protected void onResultWriteNfc(int resultCode, Intent data) {
    if (resultCode == RESULT_OK) {
      Toast.makeText(this, R.string.nfc_write_success, Toast.LENGTH_SHORT).show();
    }
  }

  //endregion

  //region Methods

  public void showRoute() {
    if (!_routeDisplayFragment.isRouteDisplayed()) {
      _travelDataRepository.select(_routeId)
          .compose(IOThreadTransformer.get())
          .subscribe(this::showRoute);
    }
  }

  void showRoute(RouteData routeData) {
    if (routeData == null) {
      onRouteNotFound();
    } else {
      _routeDisplayFragment.displayRoute(routeData);
    }
  }

  private void onRouteNotFound() {
    Timber.w("Route with id %s not found.", _routeId);

    finish();
  }

  protected void startNavigation() {
    NavigationActivity.startNew(this, _routeId);
  }

  public static void startNew(Activity fromActivity, long routeId) {
    Intent startDetailIntent = createIntent(fromActivity, routeId);

    fromActivity.startActivity(startDetailIntent);
  }

  public static Intent createIntent(Activity fromActivity, long routeId) {
    Intent startDetailIntent = new Intent(fromActivity, RouteDetailActivity.class);
    startDetailIntent.putExtra(KEY_INTENT_ROUTE_ID, routeId);
    return startDetailIntent;
  }

  //endregion
}

