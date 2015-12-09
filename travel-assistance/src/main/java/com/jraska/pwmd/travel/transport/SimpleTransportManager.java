package com.jraska.pwmd.travel.transport;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.data.TransportChangeSpec;

import javax.inject.Inject;

/**
 * Now just holder for current transport type, will be some analysis later
 */
@PerApp
public class SimpleTransportManager {
  //region Fields

  private int _currentTransportType = TransportChangeSpec.TRANSPORT_TYPE_WALK;

  //endregion

  //region Constructors

  @Inject
  public SimpleTransportManager() {
  }

  //endregion

  //region Properties

  public int getCurrentTransportType() {
    return _currentTransportType;
  }

  public void setCurrentTransportType(int currentTransportType) {
    _currentTransportType = currentTransportType;
  }

  //endregion
}
