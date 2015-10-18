package com.jraska.pwmd.core.battery;

import com.jraska.core.BaseApplication;
import com.jraska.core.services.IAppService;

public interface IBatteryStatsService extends IAppService {
  //region Methods

  BatteryStats getCurrentBatteryStats();

  //endregion

  //region Nested classes

  class Stub {
    public static IBatteryStatsService asInterface() {
      return BaseApplication.getService(IBatteryStatsService.class);
    }
  }

  //endregion
}
