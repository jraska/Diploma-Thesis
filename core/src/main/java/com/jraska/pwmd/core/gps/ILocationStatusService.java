package com.jraska.pwmd.core.gps;

import com.jraska.core.services.IAppService;

public interface ILocationStatusService extends IAppService {
  boolean isGpsLocationOn();

  boolean isNetworkLocationOn();
}
