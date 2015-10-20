package com.jraska.pwmd.core.battery;

import com.jraska.core.BaseApp;
import com.jraska.core.services.AppService;

public interface BatteryStatsReader extends AppService {
  //region Methods

  BatteryStats getCurrentBatteryStats();

  //endregion

  //region Nested classes

  class Stub {
    public static BatteryStatsReader asInterface() {
      return BaseApp.getService(BatteryStatsReader.class);
    }
  }

  //endregion
}
