package com.jraska;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import timber.log.Timber;

@Implements(Timber.DebugTree.class)
public class UnitTestTree extends Timber.Tree {
  @Implementation
  protected void log(int priority, String tag, String message, Throwable t) {
    if (tag != null) {
      System.out.print(tag);
      System.out.print("/");
    }
    System.out.println(message);

    if (t != null) {
      System.err.println(t);
    }
  }
}
