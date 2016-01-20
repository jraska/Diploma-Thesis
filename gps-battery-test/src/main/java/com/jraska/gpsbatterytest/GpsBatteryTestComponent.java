package com.jraska.gpsbatterytest;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.battery.BatteryModule;
import com.jraska.pwmd.core.gps.LocationModule;
import dagger.Component;

@PerApp
@Component(
    modules = {
        LocationModule.class,
        BatteryModule.class,
        GpsBatteryTestModule.class
    }
)
public interface GpsBatteryTestComponent {
  void inject(GpsBatteryTestService svc);
}
