package com.jraska.pwmd.travel.backup;

import android.content.Context;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import rx.Observable;
import timber.log.Timber;

import javax.inject.Inject;
import java.util.Date;

public class BackupChecker {
  //region Fields

  private final Context _context;
  private final DriveBackupManager _driveBackupManager;
  private final TravelDataRepository _travelDataRepository;

  //endregion

  //region Constructors

  @Inject
  public BackupChecker(Context context, DriveBackupManager driveBackupManager,
                       TravelDataRepository travelDataRepository) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(driveBackupManager);
    ArgumentCheck.notNull(travelDataRepository);

    _context = context;
    _driveBackupManager = driveBackupManager;
    _travelDataRepository = travelDataRepository;
  }

  //endregion

  //region Methods

  public Observable<Date> getLastBackupDate() {
    return Observable.fromCallable(this::getDateSync);
  }

  public Observable<Boolean> isAnythingToBackup() {
    return Observable.fromCallable(this::isAnythingToBackupSync);
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

  private boolean isAnythingToBackupSync() {
    return !_travelDataRepository.selectAll().toBlocking().first().isEmpty();
  }

  private GoogleApiClient createClient() {
    return new GoogleApiClient.Builder(_context)
        .addApi(com.google.android.gms.drive.Drive.API)
        .addScope(com.google.android.gms.drive.Drive.SCOPE_APPFOLDER)
        .build();
  }

  //endregion
}
