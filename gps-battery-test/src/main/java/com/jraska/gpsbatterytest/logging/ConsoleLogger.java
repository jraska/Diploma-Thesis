package com.jraska.gpsbatterytest.logging;

import com.jraska.common.view.console.Console;

public class ConsoleLogger implements ILogger {
  //region ILogger impl

  @Override
  public void log(Object o) {
    Console.writeLn(o);
  }

  @Override
  public void dispose() {
    Console.clear();
  }

  //endregion
}
