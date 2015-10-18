package com.jraska.common.utils;

public interface IFilter<TObject> {
  boolean accept(TObject object);
}
