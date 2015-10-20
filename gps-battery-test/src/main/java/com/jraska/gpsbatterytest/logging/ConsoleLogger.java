package com.jraska.gpsbatterytest.logging;

import android.support.annotation.NonNull;
import com.jraska.common.view.console.Console;

public class ConsoleLogger implements ILogger {
  //region ILogger impl

  @Override
  public void log(@NonNull Object o) {
    Console.writeLn(o);
  }

  @Override
  public void dispose() {
    Console.clear();
  }

  //endregion
}
