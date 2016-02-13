package com.jraska.pwmd.core.battery;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class BatteryStats {
  //region Fields

  public final float percent;
  public final boolean isCharging;
  public final long time;

  //endregion

  //region Constructors

  public BatteryStats(float percent, boolean isCharging) {
    this(percent, isCharging, System.currentTimeMillis());
  }

  public BatteryStats(float percent, boolean isCharging, long time) {
    this.percent = percent;
    this.isCharging = isCharging;
    this.time = time;
  }

  //endregion
}
