package com.jraska.pwmd.travel.backup;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.*;
import com.jraska.common.ArgumentCheck;
import lombok.SneakyThrows;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.File;
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

  private GoogleApiClient _client;
  private DriveApi _driveApi;
  private BackupPackager _packager;

  //endregion

  //region Constructors

  @Inject
  public DriveBackupManager(GoogleApiClient client, DriveApi driveApi, BackupPackager packager) {
    ArgumentCheck.notNull(client);
    ArgumentCheck.notNull(driveApi);
    ArgumentCheck.notNull(packager);

    _client = client;
    _driveApi = driveApi;
    _packager = packager;
  }


  //endregion

  //region Methods

  private String createNewBackupFileName() {
    return BACKUP_PREFIX + currentDateString() + ZIP_SUFFIX;
  }

  private String currentDateString() {
    synchronized (BACKUP_DATE_FORMAT) {
      return BACKUP_DATE_FORMAT.format(new Date());
    }
  }

  private DriveFolder getAppFolder() {
    return _driveApi.getAppFolder(_client);
  }

  private MetadataChangeSet creteBackupChangeSet() {
    return new MetadataChangeSet.Builder()
        .setTitle(createNewBackupFileName())
        .setMimeType(BACKUP_MIME_TYPE)
        .build();
  }

  @SneakyThrows
  public boolean makeBackup() {
    File backup = _packager.createBackupFile();

    DriveFolder appFolder = getAppFolder();

    MetadataChangeSet backupFileSet = creteBackupChangeSet();
    DriveFile driveBackupFile = appFolder.createFile(_client, backupFileSet, null).await().getDriveFile();

    DriveContents driveContents = driveBackupFile.open(_client, MODE_WRITE_ONLY, null).await().getDriveContents();
    OutputStream outputStream = driveContents.getOutputStream();

    try (BufferedSource inputSink = Okio.buffer(Okio.source(backup));
         BufferedSink outputSink = Okio.buffer(Okio.sink(outputStream))) {
      outputSink.writeAll(inputSink);
    }

    Metadata metadata = driveBackupFile.getMetadata(_client).await().getMetadata();
    deleteOldBackups(metadata);

    Status status = driveContents.commit(_client, null).await();
    return status.isSuccess();
  }


  private Metadata getLastBackupMetadata() {
    DriveFolder appFolder = getAppFolder();

    Metadata foundMetadata = null;
    Date lastBackupDate = new Date(0);
    for (Metadata metadata : appFolder.listChildren(_client).await().getMetadataBuffer()) {
      if (!BACKUP_MIME_TYPE.equals(metadata.getMimeType())) {
        continue;
      }

      Date createdDate = metadata.getCreatedDate();
      if (createdDate.after(lastBackupDate)) {
        lastBackupDate = createdDate;
        foundMetadata = metadata;
      }
    }

    if (foundMetadata == null) {
      return null;
    }
    return foundMetadata;
  }

  private void deleteOldBackups(Metadata onlyKeepBackupMetadata) {
    DriveFolder appFolder = getAppFolder();

    for (Metadata metadata : appFolder.listChildren(_client).await().getMetadataBuffer()) {
      if (BACKUP_MIME_TYPE.equals(metadata.getMimeType())) {
        Status deleteStatus = metadata.getDriveId().asDriveFile().delete(_client).await();
        if (deleteStatus.isSuccess()) {
          Timber.v("Backup %s deleted", metadata.getTitle());
        } else {
          Timber.w("Backup %s failed to delete status=%s", metadata.getTitle(), deleteStatus);
        }
      }
    }
  }

  public boolean restoreFromBackup() {
    Metadata foundMetadata = getLastBackupMetadata();
    if (foundMetadata == null) {
      return false;
    }

    deleteOldBackups(foundMetadata);

    DriveFile driveFile = foundMetadata.getDriveId().asDriveFile();
    DriveContents lastBackupContents = driveFile.open(_client, MODE_READ_ONLY, null).await().getDriveContents();

    if (lastBackupContents == null) {
      return false;
    }

    _packager.restoreBackup(lastBackupContents.getInputStream());
    return true;
  }

  //endregion
}