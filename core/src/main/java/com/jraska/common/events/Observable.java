package com.jraska.common.events;

public interface Observable<TArgs extends EventArgs> {
  boolean hasObservers();

  boolean registerObserver(Observer<TArgs> observer);

  boolean unregisterObserver(Observer<TArgs> observer);
}
