package com.jraska.common.events;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Basic implementation of {@link com.jraska.common.events.IObservable}.
 * <p/>
 * Not thread safe.
 *
 * @param <TArgs>
 */
public class Observable<TArgs extends IEventArgs> implements IObservable<TArgs> {
  //region Fields

  private Collection<IObserver<TArgs>> m_observers;

  //endregion

  //region Properties

  public Collection<IObserver<TArgs>> getObservers() {
    if (m_observers == null) {
      m_observers = createObserversCollection();
    }

    return m_observers;
  }

  //endregion

  //region IObservable implementation

  @Override
  public boolean registerObserver(IObserver<TArgs> observer) {
    return getObservers().add(observer);
  }

  @Override
  public boolean unregisterObserver(IObserver observer) {
    return getObservers().remove(observer);
  }

  @Override
  public boolean hasObservers() {
    return !getObservers().isEmpty();
  }

  //endregion

  //region Methods

  public void notify(Object sender, TArgs eventArgs) {
    for (IObserver<TArgs> observer : getObservers()) {
      observer.update(sender, eventArgs);
    }
  }

  public void unregisterAll() {
    m_observers.clear();
  }

  protected Collection<IObserver<TArgs>> createObserversCollection() {
    return new ArrayList<>();
  }

  //endregion
}
