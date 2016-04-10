package com.jraska.pwmd.travel.backup;

import okio.BufferedSink;
import okio.BufferedSource;
import timber.log.Timber;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static okio.Okio.*;

/**
 * One shot helper object reader
 */
final class ZipReader implements Closeable {
  private final ZipFile _zipFile;
  private final List<? extends ZipEntry> _entries;

  private ZipReader(File file) throws IOException {
    _zipFile = new ZipFile(file);

    _entries = Collections.list(_zipFile.entries());
  }

  List<String> getKeysWithPrefix(String prefix) {
    ArrayList<String> result = new ArrayList<>();
    for (ZipEntry entry : _entries) {
      if (entry.getName().startsWith(prefix)) {
        result.add(entry.getName());
      }
    }

    return result;
  }

  void readToFile(String key, File intoFile) throws IOException {
    if (intoFile.exists()) {
      Timber.v("File %s already exists, overwriting", intoFile.getName());
      if (!intoFile.delete()) {
        throw new IOException("Cannot overwrite file " + intoFile);
      }
    }

    if (!intoFile.createNewFile()) {
      throw new IOException("Cannot create file " + intoFile);
    }

    InputStream inputStream = _zipFile.getInputStream(_zipFile.getEntry(key));

    BufferedSource source = buffer(source(inputStream));
    BufferedSink sink = buffer(sink(intoFile));

    try {
      sink.writeAll(source);
    }
    finally {
      sink.close();
      source.close();
    }

    Timber.v("File %s wrote", intoFile.getName());
  }

  @Override
  public void close() throws IOException {
    _zipFile.close();
  }

  static ZipReader create(File tempBackupFile) throws IOException {
    return new ZipReader(tempBackupFile);
  }
}
