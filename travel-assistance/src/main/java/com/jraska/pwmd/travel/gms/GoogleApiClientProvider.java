package com.jraska.pwmd.travel.gms;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;

import javax.inject.Inject;
import javax.inject.Provider;


@PerApp
public class GoogleApiClientProvider implements Provider<GoogleApiClient> {
  //region Fields

  private final Context _context;
  private final GoogleApiClient.ConnectionCallbacks _connectionCallbacks;
  private final GoogleApiClient.OnConnectionFailedListener _connectionFailedListener;

  //endregion

  //region Constructors

  @Inject
  public GoogleApiClientProvider(Context context, DefaultConnectionCallbacks connectionCallbacks,
                                 DefaultConnectionFailedListener connectionFailedListener) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(connectionCallbacks);
    ArgumentCheck.notNull(connectionFailedListener);

    _context = context.getApplicationContext();
    _connectionCallbacks = connectionCallbacks;
    _connectionFailedListener = connectionFailedListener;
  }

  //endregion


  //region Provider impl

  @Override
  public GoogleApiClient get() {
    return new GoogleApiClient.Builder(_context)
        .addConnectionCallbacks(_connectionCallbacks)
        .addOnConnectionFailedListener(_connectionFailedListener)
        .addApi(LocationServices.API)
        .build();
  }

  //endregion
}
