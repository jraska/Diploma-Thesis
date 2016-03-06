package com.jraska.pwmd.travel.network;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.jraska.common.ArgumentCheck;

import javax.inject.Inject;

public class OnlineProperty {
  //region Fields

  private final ConnectivityManager _connectivityManager;

  //endregion

  //region Constructors

  @Inject
  public OnlineProperty(ConnectivityManager connectivityManager) {
    ArgumentCheck.notNull(connectivityManager);

    _connectivityManager = connectivityManager;
  }

  //endregion

  //region Methods

  public boolean isOnline() {
    NetworkInfo activeNetwork = _connectivityManager.getActiveNetworkInfo();
    boolean isConnected = activeNetwork != null &&
        activeNetwork.isConnectedOrConnecting();

    return isConnected;
  }

  public boolean isOffline() {
    return !isOnline();
  }

  //endregion
}
