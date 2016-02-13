package com.jraska;

import timber.log.Timber;

public class UnitTestTree extends Timber.Tree {
  protected void log(int priority, String tag, String message, Throwable t) {
    if (tag != null) {
      System.out.print(tag);
      System.out.print("/");
    }
    System.out.println(message);

    if (t != null) {
      t.printStackTrace();
    }
  }
}
