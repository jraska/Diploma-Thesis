package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;

public interface Logger {
  //region Methods

  void log(@NonNull Object o);

  void dispose();

  //endregion
}
