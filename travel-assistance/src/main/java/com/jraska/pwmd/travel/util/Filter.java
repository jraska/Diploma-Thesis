package com.jraska.pwmd.travel.util;

public interface Filter<TObject> {
  boolean accept(TObject object);
}
