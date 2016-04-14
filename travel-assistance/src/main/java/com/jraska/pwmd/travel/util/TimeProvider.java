package com.jraska.pwmd.travel.util;

public class TimeProvider {
  public static final TimeProvider INSTANCE = new TimeProvider();

  private TimeProvider() {
  }

  public long currentTime(){
    return System.currentTimeMillis();
  }
}
