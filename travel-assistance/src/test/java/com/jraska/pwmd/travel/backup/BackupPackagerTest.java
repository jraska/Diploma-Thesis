package com.jraska.pwmd.travel.backup;

import com.jraska.BaseTest;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

public class BackupPackagerTest extends BaseTest {

  private File _cacheDir;
  private File _databaseFile;
  private File _picsDir;
  private File _soundsDir;

  BackupPackager _backupPackager;

  private File _rootTestDir;

  @Before
  public void setUp() {
    _rootTestDir = new File(BackupPackagerTest.class.getSimpleName());
    _cacheDir = new File(_rootTestDir, "cache");
    _databaseFile = createFile(_rootTestDir, "database.db", 1000);
    _picsDir = new File(_rootTestDir, "pics");
    _soundsDir = new File(_rootTestDir, "sounds");

    _backupPackager = new BackupPackager(_cacheDir, _databaseFile, _picsDir, _soundsDir);
  }

  @After
  public void tearDown() {
    deleteRecursive(_rootTestDir);
  }

  @Test
  public void whenRestoredFromBackup_thenRestoredStructureEqual() throws Exception {
    createFile(_picsDir, "pic1");
    createFile(_picsDir, "pic2");
    createFile(_picsDir, "pic3");
    createFile(_soundsDir, "sound1");
    createFile(_soundsDir, "sound2");
    assertThat(_databaseFile.exists()).isTrue();
    File[] expectedSounds = _soundsDir.listFiles();
    File[] expectedPics = _picsDir.listFiles();
    assertThat(expectedSounds).hasSize(2);
    assertThat(expectedPics).hasSize(3);

    InputStream backup = _backupPackager.createBackup();

    deleteRecursive(_picsDir);
    deleteRecursive(_soundsDir);
    deleteRecursive(_databaseFile);

    assertThat(_databaseFile.exists()).isFalse();
    assertThat(_soundsDir.exists()).isFalse();
    assertThat(_picsDir.exists()).isFalse();

    _backupPackager.restoreBackup(backup);

    assertThat(_databaseFile.exists()).isTrue();
    assertThat(_soundsDir.listFiles()).isEqualTo(expectedSounds);
    assertThat(_picsDir.listFiles()).isEqualTo(expectedPics);
  }

  @Test
  public void whenDeleteTempDataCalled_thenCacheDataCleared() throws Exception {
    assertThat(_cacheDir).doesNotExist();
    InputStream backup = _backupPackager.createBackup();
    assertThat(_cacheDir.listFiles()).isNotEmpty();

    backup.close();
    _backupPackager.clearTempData();
    assertThat(_cacheDir.listFiles()).isEmpty();
  }

  @Test
  public void whenRestoredFromBackup_thenCacheDataCleared() throws Exception {
    assertThat(_cacheDir).doesNotExist();
    InputStream backup = _backupPackager.createBackup();
    assertThat(_cacheDir.listFiles()).isNotEmpty();

    _backupPackager.restoreBackup(backup);
    assertThat(_cacheDir.listFiles()).isEmpty();
  }

  static File createFile(File dir, String name) {
    return createFile(dir, name, 1024);
  }

  @SneakyThrows
  static File createFile(File dir, String name, int size) {
    if (!dir.exists()) {
      assertThat(dir.mkdirs()).isTrue();
    }

    File file = new File(dir, name);
    assertThat(file.createNewFile()).isTrue();

    FileOutputStream outputStream = new FileOutputStream(file);
    Random random = new Random();
    for (int i = 0; i < size; i++) {
      outputStream.write(random.nextInt());
    }

    outputStream.close();

    return file;
  }

  static void deleteRecursive(File file) {
    if (file.isDirectory()) {
      for (File f : file.listFiles()) {
        deleteRecursive(f);
      }

      assertThat(file.delete()).isTrue();
    } else {
      assertThat(file.delete()).isTrue();
    }
  }
}