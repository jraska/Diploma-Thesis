package com.jraska.common.events;

public interface IObserver<TArgs extends IEventArgs> {
  void update(Object sender, TArgs args);
}
