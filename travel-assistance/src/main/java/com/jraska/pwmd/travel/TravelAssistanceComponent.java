package com.jraska.pwmd.travel;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.GpsModule;
import com.jraska.pwmd.travel.persistence.TableRouteDataRepositoryModule;
import dagger.Component;

@PerApp
@Component(
    modules = {
        GpsModule.class,
        TableRouteDataRepositoryModule.class,
        TravelAssistanceModule.class
    }
)

public interface TravelAssistanceComponent {
  void inject(HelpRequestSendActivity helpRequestSendActivity);

  void inject(RouteDisplayActivity routeDisplayActivity);
}
