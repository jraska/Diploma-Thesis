package com.jraska.pwmd.travel.backup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import javax.inject.Inject;
import javax.inject.Provider;

public class GoogleDriveApiClientProvider implements Provider<GoogleApiClient> {
  //region Fields

  private final BackupResolveActivity _resolveActivity;

  //endregion

  //region Constructors

  @Inject
  public GoogleDriveApiClientProvider(BackupResolveActivity resolveActivity) {
    _resolveActivity = resolveActivity;
  }


  //endregion

  //region Provider impl

  @Override public GoogleApiClient get() {
    return new GoogleApiClient.Builder(_resolveActivity)
        .addConnectionCallbacks(_resolveActivity)
        .addOnConnectionFailedListener(_resolveActivity)
        .addApi(Drive.API)
        .addScope(Drive.SCOPE_APPFOLDER)
        .build();
  }

  //endregion
}
