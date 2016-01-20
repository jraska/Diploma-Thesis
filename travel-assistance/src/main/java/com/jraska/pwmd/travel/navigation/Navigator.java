package com.jraska.pwmd.travel.navigation;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import de.greenrobot.event.EventBus;

import javax.inject.Inject;
import javax.inject.Named;

public class Navigator {
  //region Constants

  public static final String NAVIGATOR_BUS_NAME = "navigatorBus";

  //endregion

  //region Fields

  private final EventBus _eventBus;

  private int _lastDirectionDegrees;

  //endregion

  //region Constructors

  @Inject @PerApp
  public Navigator(@NonNull @Named(NAVIGATOR_BUS_NAME) EventBus eventBus) {
    ArgumentCheck.notNull(eventBus);

    _eventBus = eventBus;
  }

  //endregion

  //region Properties

  public EventBus getEventBus() {
    return _eventBus;
  }

  public int getLastDirectionDegrees() {
    return _lastDirectionDegrees;
  }

  //endregion

  //region Methods

  protected void onDesiredDirectionChanged(int degrees) {
    _lastDirectionDegrees = degrees;

    _eventBus.post(new DirectionChangedEvent(degrees));
  }

  //endregion

  //region Nested classes

  public static final class DirectionChangedEvent {
    public final int _directionDegrees;

    public DirectionChangedEvent(int directionDegrees) {
      _directionDegrees = directionDegrees;
    }
  }

  //endregion
}
