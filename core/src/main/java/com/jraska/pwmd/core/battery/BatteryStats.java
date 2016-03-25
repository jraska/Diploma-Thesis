package com.jraska.pwmd.core.battery;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@EqualsAndHashCode
@ToString
public class BatteryStats {
  //region Constants

  static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss.SSS", Locale.US);

  //endregion

  //region Fields

  public final float percent;
  public final boolean isCharging;
  public final String time;

  //endregion

  //region Constructors

  public BatteryStats(float percent, boolean isCharging) {
    this(percent, isCharging, System.currentTimeMillis());
  }

  public BatteryStats(float percent, boolean isCharging, long time) {
    this.percent = percent;
    this.isCharging = isCharging;
    this.time = format(new Date(time));
  }

  //endregion

  //region Methds

  static String format(Date date) {
    synchronized (DATE_FORMAT) {
      return DATE_FORMAT.format(date);
    }
  }

  //endregion
}
