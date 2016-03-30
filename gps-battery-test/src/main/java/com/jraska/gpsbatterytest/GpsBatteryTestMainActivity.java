package com.jraska.gpsbatterytest;

import android.app.Activity;
import android.os.Bundle;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class GpsBatteryTestMainActivity extends Activity {

  //region Activity overrides

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    ButterKnife.bind(this);
  }

  //endregion

  //region Methods

  @OnClick(R.id.btnStart) void startBatteryService() {
    GpsBatteryTestService.start(this);
  }

  @OnClick(R.id.btnStartNoGps) void startBatteryServiceWithoutGpsLogging() {
    GpsBatteryTestService.startNoGps(this);
  }

  @OnClick(R.id.btnStop) void stopBatteryService() {
    GpsBatteryTestService.stop(this);
  }

  //endregion
}
