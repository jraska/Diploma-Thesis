package com.jraska.common.utils;

public interface Filter<TObject> {
  boolean accept(TObject object);
}
