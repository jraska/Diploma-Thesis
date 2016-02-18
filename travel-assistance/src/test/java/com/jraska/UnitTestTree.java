package com.jraska;

import android.util.Log;
import timber.log.Timber;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UnitTestTree extends Timber.DebugTree {
  private static final DateFormat DATE_FORMAT = new SimpleDateFormat("HH:mm:ss.SSS", Locale.US);

  protected void log(int priority, String tag, String message, Throwable t) {
    String logMesage = String.format("%s %s/%s: %s", time(), toText(priority), tag, message);

    if (priority >= Log.ERROR) {
      System.err.println(logMesage);
    } else {
      System.out.println(logMesage);
    }
  }

  private static String time() {
    synchronized (DATE_FORMAT) {
      return DATE_FORMAT.format(new Date());
    }
  }

  private static String toText(int priority) {
    switch (priority) {
      case Log.ASSERT:
        return "WTF";
      case Log.ERROR:
        return "E";
      case Log.WARN:
        return "W";
      case Log.INFO:
        return "I";
      case Log.DEBUG:
        return "D";
      case Log.VERBOSE:
        return "V";

      default:
        throw new IllegalArgumentException();
    }
  }
}
