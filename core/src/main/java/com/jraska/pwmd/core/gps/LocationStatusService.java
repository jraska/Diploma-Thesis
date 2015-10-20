package com.jraska.pwmd.core.gps;

import com.jraska.core.services.AppService;

public interface LocationStatusService extends AppService {
  boolean isGpsLocationOn();

  boolean isNetworkLocationOn();
}
