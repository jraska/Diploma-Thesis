package com.jraska.pwmd.travel.backup;

import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.io.CacheDir;
import com.jraska.pwmd.travel.io.PicturesDir;
import com.jraska.pwmd.travel.io.SoundsDir;
import com.jraska.pwmd.travel.persistence.DatabaseFile;
import lombok.SneakyThrows;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class BackupPackager {
  //region Constants

  private static final String BACKUP_SUFFIX = "backup.zip";
  private static final String DATABASE_KEY = "database.db";
  private static final String PICTURE_PREFIX = "pic/";
  private static final String SOUNDS_PREFIX = "sound/";

  //endregion

  //region Fields

  private final File _cacheDir;
  private final File _databaseFile;
  private final File _picturesDir;
  private final File _soundsDir;

  //endregion

  //region Constructors

  @Inject
  public BackupPackager(@CacheDir File cacheDir, @DatabaseFile File databaseFile,
                        @PicturesDir File picturesDir, @SoundsDir File soundsDir) {
    ArgumentCheck.notNull(cacheDir);
    ArgumentCheck.notNull(databaseFile);
    ArgumentCheck.notNull(picturesDir);
    ArgumentCheck.notNull(soundsDir);

    _cacheDir = cacheDir;
    _databaseFile = databaseFile;
    _picturesDir = picturesDir;
    _soundsDir = soundsDir;
  }

  //endregion

  //region Methods

  String newFileName() {
    return UUID.randomUUID().toString();
  }

  @SneakyThrows // TODO: 05/03/16 handle exceptions
  public File createBackupFile() {
    File backupFile = File.createTempFile(newFileName(), BACKUP_SUFFIX, _cacheDir);

    Timber.v("Starting backup to %s", backupFile.getAbsolutePath());
    ZipWriter zipWriter = ZipWriter.create(backupFile);

    Timber.v("Packaging database %s", _databaseFile.getName());
    zipWriter.write(_databaseFile, DATABASE_KEY);

    writeSounds(zipWriter);
    writePictures(zipWriter);

    zipWriter.close();

    Timber.d("Backup to %s finished", backupFile.getAbsolutePath());

    return backupFile;
  }

  private void writePictures(ZipWriter zipWriter) throws IOException {
    writeDir(zipWriter, _picturesDir, PICTURE_PREFIX);
  }

  private void writeSounds(ZipWriter zipWriter) throws IOException {
    writeDir(zipWriter, _soundsDir, SOUNDS_PREFIX);
  }

  private void writeDir(ZipWriter zipWriter, File dir, String prefix) throws IOException {
    File[] files = dir.listFiles();

    if (files == null || files.length == 0) {
      Timber.v("Nothing found for backup in %s dir", dir.getName());
      return;
    }

    Timber.v("Backing up %d files for dir %s", files.length, dir.getName());
    for (File soundFile : files) {
      if (soundFile.isDirectory()) {
        Timber.w("There is unexpected directory %s, which will be skipped from backup", soundFile.getPath());
        continue;
      }

      Timber.v("File %s", soundFile.getName());
      zipWriter.write(soundFile, prefix + soundFile.getName());
    }
  }

  void restoreBackup(InputStream backupInputStream) {

  }

  //endregion
}
