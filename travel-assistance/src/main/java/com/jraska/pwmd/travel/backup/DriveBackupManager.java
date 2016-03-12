package com.jraska.pwmd.travel.backup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.*;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.settings.SettingsManager;
import lombok.SneakyThrows;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import rx.Observable;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.google.android.gms.drive.DriveFile.MODE_READ_ONLY;
import static com.google.android.gms.drive.DriveFile.MODE_WRITE_ONLY;
import static java.util.Locale.US;

/**
 * Expects already authorized client
 */
public class DriveBackupManager {
  //region Constants

  static final String ZIP_SUFFIX = ".zip";
  static final String BACKUP_PREFIX = "backup";
  static final String BACKUP_MIME_TYPE = "application/zip";
  static final DateFormat BACKUP_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", US);

  //endregion

  //region Fields

  private final DriveApi _driveApi;
  private final BackupPackager _packager;
  private final SettingsManager _settingsManager;

  //endregion

  //region Constructors

  @Inject
  public DriveBackupManager(BackupPackager packager, SettingsManager settings) {
    this(Drive.DriveApi, packager, settings);
  }

  public DriveBackupManager(DriveApi driveApi, BackupPackager packager, SettingsManager settings) {
    ArgumentCheck.notNull(driveApi);
    ArgumentCheck.notNull(packager);
    ArgumentCheck.notNull(settings);

    _driveApi = driveApi;
    _packager = packager;
    _settingsManager = settings;
  }

  //endregion

  //region Methods

  public Observable<Boolean> createBackup(GoogleApiClient client) {
    return Observable.fromCallable(() -> createBackupSync(client));
  }

  public Observable<Boolean> restoreFromBackup(GoogleApiClient client) {
    return Observable.fromCallable(() -> restoreFromBackupSync(client));
  }

  public Observable<Date> getLastBackupTime(GoogleApiClient client) {
    return Observable.fromCallable(() -> getLastBackupTimeSync(client));
  }

  private Date getLastBackupTimeSync(GoogleApiClient client) {
    Metadata backupMetadata = getLastBackupMetadata(client);
    if (backupMetadata == null) {
      return null;
    } else {
      return backupMetadata.getCreatedDate();
    }
  }

  @SneakyThrows
  private boolean restoreFromBackupSync(GoogleApiClient client) {
    Metadata foundMetadata = getLastBackupMetadata(client);
    if (foundMetadata == null) {
      Timber.d("No backup found for restore");
      return false;
    }

    DriveFile driveFile = foundMetadata.getDriveId().asDriveFile();
    DriveContents lastBackupContents = driveFile.open(client, MODE_READ_ONLY, null).await().getDriveContents();

    if (lastBackupContents == null) {
      return false;
    }

    _packager.restoreBackup(lastBackupContents.getInputStream());

    deleteOldBackups(client, foundMetadata);

    return true;
  }

  @SneakyThrows
  private boolean createBackupSync(GoogleApiClient client) {
    InputStream backupStream = _packager.createBackup();

    DriveFolder appFolder = _driveApi.getAppFolder(client);

    MetadataChangeSet backupFileSet = createBackupChangeSet();

    DriveFile newBackupFile = appFolder.createFile(client, backupFileSet, null).await().getDriveFile();
    Timber.v("Created new drive file %s", backupFileSet.getTitle());

    DriveContents driveContents = newBackupFile.open(client, MODE_WRITE_ONLY, null).await().getDriveContents();
    OutputStream outputStream = driveContents.getOutputStream();

    BufferedSource inputSource = Okio.buffer(Okio.source(backupStream));
    BufferedSink outputSink = Okio.buffer(Okio.sink(outputStream));

    Timber.v("Writing to Drive file");
    try {
      outputSink.writeAll(inputSource);
    }
    finally {
      inputSource.close();
      outputSink.close();
    }

    Status status = driveContents.commit(client, null).await();
    Timber.v("Status on Drive file commit %s", status);
    if (!status.isSuccess()) {
      return false;
    }

    _packager.clearTempData();
    Metadata metadata = newBackupFile.getMetadata(client).await().getMetadata();
    deleteOldBackups(client, metadata);

    return true;
  }

  private String createNewBackupFileName() {
    return BACKUP_PREFIX + currentDateString() + ZIP_SUFFIX;
  }

  private String currentDateString() {
    synchronized (BACKUP_DATE_FORMAT) {
      return BACKUP_DATE_FORMAT.format(new Date());
    }
  }

  private MetadataChangeSet createBackupChangeSet() {
    return new MetadataChangeSet.Builder()
        .setTitle(createNewBackupFileName())
        .setMimeType(BACKUP_MIME_TYPE)
        .build();
  }

  private Metadata getLastBackupMetadata(GoogleApiClient client) {
    DriveFolder appFolder = _driveApi.getAppFolder(client);

    DriveApi.MetadataBufferResult childrenResult = appFolder.listChildren(client).await();
    if (!childrenResult.getStatus().isSuccess()) {
      Timber.v("Unsuccessful listing appFolder children %s", childrenResult.getStatus());
      return null;
    }

    MetadataBuffer metadataBuffer = childrenResult.getMetadataBuffer();
    if (metadataBuffer.getCount() == 0) {
      tryRefreshDrive(client);

      // check again after refresh
      metadataBuffer = appFolder.listChildren(client).await().getMetadataBuffer();
    }

    Metadata foundMetadata = findLatestMetadata(metadataBuffer);

    if (foundMetadata == null) {
      return null;
    }
    return foundMetadata;
  }

  private void tryRefreshDrive(GoogleApiClient client) {
    // http://stackoverflow.com/questions/23840846/android-google-drive-app-data-folder-return-empty-when-i-use-querychildren
    Timber.v("Nothing found on listing, trying refresh to fix Uninstall GooglePlayServices bug");

    if (isTooManySyncRequests()) {
      Timber.v("Too many drive sync requests, not force syncing");
      return;
    }

    Timber.v("Force sync Drive");
    Status status = _driveApi.requestSync(client).await();
    if (!status.isSuccess()) {
      Timber.v("Sync unsuccessful status %s", status);
    }
  }

  private boolean isTooManySyncRequests() {
    Date lastSync = _settingsManager.getLastDriveRequestSync();
    if (lastSync != null && wasLessThenMinuteAgo(lastSync)) {
      return true;
    }

    _settingsManager.setLastDriveSyncRequest(new Date());
    return false;
  }

  private boolean wasLessThenMinuteAgo(Date lastSync) {
    return System.currentTimeMillis() - lastSync.getTime() < 1000 * 60;
  }

  private Metadata findLatestMetadata(MetadataBuffer metadataBuffer) {
    Metadata foundMetadata = null;
    Date lastBackupDate = new Date(0);
    for (Metadata metadata : metadataBuffer) {
      if (!BACKUP_MIME_TYPE.equals(metadata.getMimeType())) {
        continue;
      }

      Date createdDate = metadata.getCreatedDate();
      if (createdDate.after(lastBackupDate)) {
        lastBackupDate = createdDate;
        foundMetadata = metadata;
      }
    }
    return foundMetadata;
  }

  private void deleteOldBackups(GoogleApiClient client, Metadata onlyKeepBackupMetadata) {
    Timber.v("Deleting old backups except %s", onlyKeepBackupMetadata.getTitle());

    DriveFolder appFolder = _driveApi.getAppFolder(client);

    String keepTitle = onlyKeepBackupMetadata.getTitle();

    for (Metadata metadata : appFolder.listChildren(client).await().getMetadataBuffer()) {
      if (!keepTitle.equals(metadata.getTitle()) && BACKUP_MIME_TYPE.equals(metadata.getMimeType())) {

        Status deleteStatus = metadata.getDriveId().asDriveFile().delete(client).await();

        if (deleteStatus.isSuccess()) {
          Timber.v("Backup %s deleted", metadata.getTitle());
        } else {
          Timber.w("Backup %s failed to delete status=%s", metadata.getTitle(), deleteStatus);
        }
      }
    }
  }

  //endregion
}
