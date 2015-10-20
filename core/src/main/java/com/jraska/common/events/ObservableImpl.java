package com.jraska.common.events;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Basic implementation of {@link Observable}.
 * <p/>
 * Not thread safe.
 *
 * @param <TArgs>
 */
public class ObservableImpl<TArgs extends EventArgs> implements Observable<TArgs> {
  //region Fields

  private Collection<Observer<TArgs>> m_observers;

  //endregion

  //region Properties

  public Collection<Observer<TArgs>> getObservers() {
    if (m_observers == null) {
      m_observers = createObserversCollection();
    }

    return m_observers;
  }

  //endregion

  //region IObservable implementation

  @Override
  public boolean registerObserver(Observer<TArgs> observer) {
    return getObservers().add(observer);
  }

  @Override
  public boolean unregisterObserver(Observer observer) {
    return getObservers().remove(observer);
  }

  @Override
  public boolean hasObservers() {
    return !getObservers().isEmpty();
  }

  //endregion

  //region Methods

  public void notify(Object sender, TArgs eventArgs) {
    for (Observer<TArgs> observer : getObservers()) {
      observer.update(sender, eventArgs);
    }
  }

  public void unregisterAll() {
    m_observers.clear();
  }

  protected Collection<Observer<TArgs>> createObserversCollection() {
    return new ArrayList<>();
  }

  //endregion
}
