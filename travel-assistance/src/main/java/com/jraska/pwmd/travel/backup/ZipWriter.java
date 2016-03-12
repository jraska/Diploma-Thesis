package com.jraska.pwmd.travel.backup;

import okio.BufferedSink;
import okio.Okio;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * One shot object for creating zip archives
 */
final class ZipWriter implements Closeable {
  private final ZipOutputStream _zipOutputStream;
  private final BufferedSink _zipSink;

  private ZipWriter(ZipOutputStream zipOutputStream, BufferedSink zipSink) {
    _zipOutputStream = zipOutputStream;
    _zipSink = zipSink;
  }

  @Override public void close() throws IOException {
    _zipSink.close();
  }

  void write(File file, String key) throws IOException {
    _zipOutputStream.putNextEntry(new ZipEntry(key));
    _zipSink.writeAll(Okio.source(file));
    _zipSink.flush();
  }

  static ZipWriter create(File file) throws FileNotFoundException {
    ZipOutputStream zipOutput = new ZipOutputStream(new FileOutputStream(file));
    BufferedSink zipSink = Okio.buffer(Okio.sink(zipOutput));

    return new ZipWriter(zipOutput, zipSink);
  }
}
