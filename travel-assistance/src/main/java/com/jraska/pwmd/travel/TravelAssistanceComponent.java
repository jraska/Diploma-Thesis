package com.jraska.pwmd.travel;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.GpsModule;
import com.jraska.pwmd.travel.persistence.TableRouteDataRepositoryModule;
import com.jraska.pwmd.travel.tracking.TrackingModule;
import com.jraska.pwmd.travel.tracking.TrackingService;
import com.jraska.pwmd.travel.ui.HelpRequestSendActivity;
import com.jraska.pwmd.travel.ui.RouteDisplayActivity;
import com.jraska.pwmd.travel.ui.RoutesListActivity;
import dagger.Component;

@PerApp
@Component(
    modules = {
        GpsModule.class,
        TableRouteDataRepositoryModule.class,
        TrackingModule.class,
        TravelAssistanceModule.class
    }
)

public interface TravelAssistanceComponent {
  void inject(HelpRequestSendActivity helpRequestSendActivity);

  void inject(RouteDisplayActivity routeDisplayActivity);

  void inject(RoutesListActivity routesListActivity);

  void inject(TrackingService trackingService);
}
