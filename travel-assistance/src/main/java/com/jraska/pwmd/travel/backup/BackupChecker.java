package com.jraska.pwmd.travel.backup;

import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jraska.common.ArgumentCheck;
import rx.Observable;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.Date;

public class BackupChecker {
  //region Fields

  private final Context _context;
  private final DriveBackupManager _driveBackupManager;

  //endregion

  //region Constructors

  @Inject
  public BackupChecker(Context context, DriveBackupManager driveBackupManager) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(driveBackupManager);

    _context = context;
    _driveBackupManager = driveBackupManager;
  }

  //endregion

  //region Methods

  public Observable<Date> getLastBackupDate() {
    return Observable.fromCallable(this::getDateSync);
  }

  private Date getDateSync() {
    GoogleApiClient client = createClient();
    ConnectionResult connectionResult = client.blockingConnect();
    if (!connectionResult.isSuccess()) {
      Timber.v("Could not connect to google play services %s", connectionResult);
      return null;
    }

    Date result;
    try {
      result = _driveBackupManager.getLastBackupTime(client).toBlocking().first();
    }
    catch (Exception e) {
      Timber.w(e, "Error finding last backup date");
      result = null;
    }

    client.disconnect();
    return result;
  }

  private GoogleApiClient createClient() {
    return new GoogleApiClient.Builder(_context)
        .addApi(com.google.android.gms.drive.Drive.API)
        .addScope(com.google.android.gms.drive.Drive.SCOPE_APPFOLDER)
        .build();
  }

  //endregion
}
