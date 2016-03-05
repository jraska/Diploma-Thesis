package com.jraska.pwmd.travel.backup;

import com.jraska.BaseTest;
import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
    assertThat(_soundsDir.exists()).isTrue();
    assertThat(_picsDir.exists()).isTrue();

    File backupFile = _backupPackager.createBackupFile();

    deleteRecursive(_picsDir);
    deleteRecursive(_soundsDir);
    deleteRecursive(_databaseFile);

    assertThat(_databaseFile.exists()).isFalse();
    assertThat(_soundsDir.exists()).isFalse();
    assertThat(_picsDir.exists()).isFalse();

    _backupPackager.restoreBackup(new FileInputStream(backupFile));

    assertThat(_databaseFile.exists()).isTrue();
    assertThat(_soundsDir.listFiles()).hasSize(2);
    assertThat(_picsDir.listFiles()).hasSize(3);
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