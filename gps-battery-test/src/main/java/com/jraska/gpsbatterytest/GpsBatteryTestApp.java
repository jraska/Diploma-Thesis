package com.jraska.gpsbatterytest;

import android.app.Application;
import android.content.Context;

public class GpsBatteryTestApp extends Application {
  //region Fields

  private GpsBatteryTestComponent _component;

  //endregion

  //region Application overrides

  @Override
  public void onCreate() {
    super.onCreate();

    _component = DaggerGpsBatteryTestComponent.builder()
        .gpsBatteryTestModule(new GpsBatteryTestModule(this)).build();
  }

  //endregion

  //region Methods

  public static GpsBatteryTestComponent getComponent(Context context) {
    return ((GpsBatteryTestApp) context.getApplicationContext())._component;
  }

  //endregion
}
