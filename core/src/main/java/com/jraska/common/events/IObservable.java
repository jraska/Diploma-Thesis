package com.jraska.common.events;

public interface IObservable<TArgs extends IEventArgs> {
  boolean hasObservers();

  boolean registerObserver(IObserver<TArgs> observer);

  boolean unregisterObserver(IObserver<TArgs> observer);
}
