package com.jraska.pwmd.core.battery;

import android.content.Context;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;

@Module
public class BatteryModule {
  @Provides @PerApp BatteryStatsReader provideBatteryStatsService(Context context) {
    return new SimpleBatteryStatsReader(context);
  }
}
