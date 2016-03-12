package com.jraska.pwmd.travel.backup;

import android.content.Context;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.io.CacheDir;
import com.jraska.pwmd.travel.io.PicturesDir;
import com.jraska.pwmd.travel.io.SoundsDir;
import com.jraska.pwmd.travel.persistence.DatabaseFile;
import com.raizlabs.android.dbflow.config.FlowManager;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.*;
import java.util.List;
import java.util.UUID;

class BackupPackager {
  //region Constants

  private static final String BACKUP_SUFFIX = "backup.zip";
  private static final String DATABASE_KEY = "database.db";
  private static final String PICTURE_PREFIX = "pic/";
  private static final String SOUNDS_PREFIX = "sound/";

  private static final FileFilter TEMP_BACKUP_FILES_FILTER = pathname -> pathname.getName().endsWith(BACKUP_SUFFIX);

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

  public InputStream createBackup() throws IOException {
    if (!_cacheDir.exists() && !_cacheDir.mkdirs()) {
      throw new IOException("Cannot find or create directory %s" + _cacheDir);
    }

    File backupFile = File.createTempFile(newFileName(), BACKUP_SUFFIX, _cacheDir);

    Timber.v("Starting backup to %s", backupFile.getAbsolutePath());
    ZipWriter zipWriter = ZipWriter.create(backupFile);

    Timber.v("Packaging database %s", _databaseFile.getName());
    zipWriter.write(_databaseFile, DATABASE_KEY);

    writeSounds(zipWriter);
    writePictures(zipWriter);

    zipWriter.close();

    Timber.d("Backup to %s finished", backupFile.getAbsolutePath());

    return new FileInputStream(backupFile);
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

  public void restoreBackup(InputStream backupInputStream) throws IOException {
    Timber.d("Starting restore from backup");
    File tempBackupFile = File.createTempFile(newFileName(), BACKUP_SUFFIX, _cacheDir);

    Timber.v("Copying backup stream to temp file %s", tempBackupFile);
    BufferedSource backupSource = Okio.buffer(Okio.source(backupInputStream));
    BufferedSink sink = Okio.buffer(Okio.sink(tempBackupFile));

    try {
      sink.writeAll(backupSource);
    }
    finally {
      sink.close();
      backupSource.close();
    }

    Timber.v("Temp file %s, created, starting restore.", tempBackupFile);
    restoreFromFile(tempBackupFile);

    Timber.d("Backup successful");

    clearTempData();
  }

  private void restoreFromFile(File tempBackupFile) throws IOException {
    Timber.v("Restoring backup from temp file %s", tempBackupFile);

    ZipReader zipReader = ZipReader.create(tempBackupFile);

    // need to reinitialize db manager because underlying file changed
    Context context = FlowManager.getContext();
    FlowManager.destroy();
    zipReader.readToFile(DATABASE_KEY, _databaseFile);
    FlowManager.init(context);

    readSounds(zipReader);
    readPictures(zipReader);

    zipReader.close();

    Timber.v("Backup restored from file %s", tempBackupFile);
  }

  private void readSounds(ZipReader zipReader) throws IOException {
    readDir(zipReader, _soundsDir, SOUNDS_PREFIX);
  }

  private void readPictures(ZipReader zipReader) throws IOException {
    readDir(zipReader, _picturesDir, PICTURE_PREFIX);
  }

  private void readDir(ZipReader zipReader, File toDir, String prefix) throws IOException {
    if (!toDir.exists() && !toDir.mkdirs()) {
      throw new FileNotFoundException("COuld not found or create %s" + toDir);
    }

    List<String> entries = zipReader.getKeysWithPrefix(prefix);
    Timber.v("Restoring %d entries to %s", entries.size(), toDir.getName());
    for (String entryKey : entries) {
      String fileName = entryKey.substring(prefix.length() - 1);
      File intoFile = new File(toDir, fileName);

      Timber.v("File %s into %s", fileName, intoFile.getPath());
      zipReader.readToFile(entryKey, intoFile);
    }
  }

  public void clearTempData() {
    File[] files = _cacheDir.listFiles(TEMP_BACKUP_FILES_FILTER);

    if (files != null && files.length > 0) {
      for (File file : files) {
        Timber.v("Deleting temp file %s", file.getName());
        if (!file.delete()) {
          Timber.e("Could not delete temp file %s", file);
        }
      }
    }

    Timber.d("Temp backup data cleaned.");
  }

  //endregion
}
