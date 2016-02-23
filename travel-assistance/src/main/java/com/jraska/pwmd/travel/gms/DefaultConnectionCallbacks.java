package com.jraska.pwmd.travel.gms;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.google.android.gms.common.api.GoogleApiClient;
import timber.log.Timber;

import javax.inject.Inject;

public class DefaultConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
  //region Constructors

  @Inject
  public DefaultConnectionCallbacks() {
  }

  //endregion

  //region ConnectionCallbacks impl

  @Override public void onConnected(@Nullable Bundle connectionHint) {
    Timber.i("Connected to GoogleAPI connectionHint=%s", connectionHint);
  }

  @Override public void onConnectionSuspended(int cause) {
    Timber.w("Connection suspended cause: '%d'", cause);
  }

  //endregion
}
