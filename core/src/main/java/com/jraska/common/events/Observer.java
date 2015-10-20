package com.jraska.common.events;

public interface Observer<TArgs extends EventArgs> {
  void update(Object sender, TArgs args);
}
