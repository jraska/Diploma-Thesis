package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;

import java.util.Arrays;

public class CompositeLogger implements Logger {
  //region Fields

  private final Logger[] _loggers;

  //endregion

  //region Constructors

  public CompositeLogger(Logger[] loggers) {
    ArgumentCheck.notNull(loggers);

    _loggers = Arrays.copyOf(loggers, loggers.length);
  }

  //endregion

  //region ILogger impl

  @Override
  public void log(@NonNull Object o) {
    for (Logger logger : _loggers) {
      logger.log(o);
    }
  }

  @Override
  public void dispose() {
    for (Logger logger : _loggers) {
      logger.dispose();
    }
  }

  //endregion
}
