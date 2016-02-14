package com.jraska;

import timber.log.Timber;

import java.io.PrintWriter;
import java.io.StringWriter;

public class UnitTestTree extends Timber.Tree {
  protected void log(int priority, String tag, String message, Throwable t) {
    if (tag != null) {
      System.out.print(tag);
      System.out.print("/");
    }
    System.out.println(message);

    if (t != null) {
      System.err.println(getStackTraceString(t));
    }
  }

  private String getStackTraceString(Throwable t) {
    StringWriter sw = new StringWriter(256);
    PrintWriter pw = new PrintWriter(sw, false);
    t.printStackTrace(pw);
    pw.flush();
    return sw.toString();
  }
}
