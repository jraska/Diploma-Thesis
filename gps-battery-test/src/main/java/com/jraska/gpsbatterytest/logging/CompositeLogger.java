package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;

public class CompositeLogger implements ILogger {
  //region Fields

  private final ILogger[] _loggers;

  //endregion

  //region Constructors

  public CompositeLogger(ILogger[] loggers) {
    ArgumentCheck.notNull(loggers);

    _loggers = loggers;
  }

  //endregion

  //region ILogger impl

  @Override
  public void log(@NonNull Object o) {
    for (ILogger logger : _loggers) {
      logger.log(o);
    }
  }

  @Override
  public void dispose() {
    for (ILogger logger : _loggers) {
      logger.dispose();
    }
  }

  //endregion
}
