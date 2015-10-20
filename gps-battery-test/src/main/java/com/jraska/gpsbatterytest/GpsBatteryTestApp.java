package com.jraska.gpsbatterytest;

import android.content.Context;
import com.jraska.core.BaseApp;

public class GpsBatteryTestApp extends BaseApp {
  //region Fields

  private GpsBatteryTestComponent _component;

  //endregion

  //region Properties

  public GpsBatteryTestComponent getComponent() {
    return _component;
  }

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
