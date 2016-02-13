package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.widget.Toast;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.nfc.NfcRouteEncoder;
import timber.log.Timber;

import javax.inject.Inject;

public class NfcRouteDispatchActivity extends BaseActivity {

  //region Fields

  @Inject NfcRouteEncoder _routeEncoder;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Timber.i("Dispatch activity created");

    setContentView(R.layout.activity_nfc_dispatch);

    TravelAssistanceApp.getComponent(this).inject(this);

    long routeId = _routeEncoder.extractRouteId(getIntent());
    Timber.i("Route id extracted %d", routeId);

    Toast.makeText(this, String.valueOf(routeId), Toast.LENGTH_SHORT).show();
  }

  //endregion
}
