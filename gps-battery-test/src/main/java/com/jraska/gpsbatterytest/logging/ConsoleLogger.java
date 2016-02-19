package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;
import com.jraska.console.Console;

public class ConsoleLogger implements Logger {
  //region ILogger impl

  @Override
  public void log(@NonNull Object object) {
    Console.writeLine(object);
  }

  @Override
  public void dispose() {
    Console.clear();
  }

  //endregion
}
