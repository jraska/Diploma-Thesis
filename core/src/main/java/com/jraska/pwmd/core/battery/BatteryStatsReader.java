package com.jraska.pwmd.core.battery;

import com.jraska.core.BaseApp;

public interface BatteryStatsReader {
  //region Methods

  BatteryStats getCurrentBatteryStats();

  //endregion
}
