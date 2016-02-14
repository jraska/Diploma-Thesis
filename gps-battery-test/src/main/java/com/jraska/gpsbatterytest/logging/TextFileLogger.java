package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;

import java.io.*;

public class TextFileLogger implements Logger {
  //region Fields

  private final File _file;
  private boolean _fileCreated;
  private BufferedWriter _writer;

  private boolean _disposed;

  //endregion

  //region Constructors

  public TextFileLogger(@NonNull File textFile) {
    ArgumentCheck.notNull(textFile);

    if (!textFile.getParentFile().exists()) {
      throw new IllegalArgumentException("Parent folder of file " + textFile + " does not exist.");
    }

    _file = textFile;

    if (_file.exists()) {
      _fileCreated = true;
    }
  }

  //endregion

  //region ILogger impl

  @Override
  public void log(@NonNull Object o) {
    ArgumentCheck.notNull(o);

    ensureNotDisposed();

    try {
      ensureCreatedAndOpened();
      _writer.write(o.toString());
      _writer.newLine();
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void dispose() {
    if (_disposed) {
      return;
    }

    try {
      if (_writer != null) {
        _writer.close();
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }
    finally {
      _disposed = true;
    }
  }

  //endregion

  //region Methods

  protected final void ensureNotDisposed() {
    if (_disposed) {
      throw new IllegalStateException(getClass().getSimpleName() + " is disposed!");
    }
  }

  protected final void ensureCreatedAndOpened() throws IOException {
    if (!_fileCreated) {
      _fileCreated = _file.createNewFile();
    }

    if (_writer == null) {
      FileOutputStream outputStream = new FileOutputStream(_file);
      _writer = new BufferedWriter(new OutputStreamWriter(outputStream), 256);
    }
  }

  //endregion
}
