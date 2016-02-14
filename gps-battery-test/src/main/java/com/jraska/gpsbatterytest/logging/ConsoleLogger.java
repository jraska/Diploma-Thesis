package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;
import com.jraska.common.view.console.Console;

public class ConsoleLogger implements Logger {
  //region ILogger impl

  @Override
  public void log(@NonNull Object object) {
    Console.writeLn(object);
  }

  @Override
  public void dispose() {
    Console.clear();
  }

  //endregion
}
