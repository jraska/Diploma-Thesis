package com.jraska.pwmd.travel.gms.drive;

import android.content.Context;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.location.LocationServices;

import javax.inject.Inject;
import javax.inject.Provider;

public class GoogleDriveApiClientProvider implements Provider<GoogleApiClient> {
  //region Fields

  private final Context _context;
  private final ConnectionCallbacks _connectionCallbacks;
  private final OnConnectionFailedListener _connectionFailedListener;

  //endregion

  //region Constructors

  @Inject
  public GoogleDriveApiClientProvider(Context context, ConnectionCallbacks connectionCallbacks,
                                      OnConnectionFailedListener connectionFailedListener) {
    _context = context;
    _connectionCallbacks = connectionCallbacks;
    _connectionFailedListener = connectionFailedListener;
  }


  //endregion

  //region Provider impl

  @Override public GoogleApiClient get() {
    return new GoogleApiClient.Builder(_context)
        .addConnectionCallbacks(_connectionCallbacks)
        .addOnConnectionFailedListener(_connectionFailedListener)
        .addApi(Drive.API)
        .addScope(Drive.SCOPE_APPFOLDER)
        .build();
  }

  //endregion
}
