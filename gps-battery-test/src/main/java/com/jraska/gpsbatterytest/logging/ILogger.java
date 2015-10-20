package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;

public interface ILogger {
  //region Methods

  void log(@NonNull Object o);

  void dispose();

  //endregion
}
