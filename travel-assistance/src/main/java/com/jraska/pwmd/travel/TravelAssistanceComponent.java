package com.jraska.pwmd.travel;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.GpsModule;
import com.jraska.pwmd.travel.media.MediaModule;
import com.jraska.pwmd.travel.persistence.TableRouteDataRepositoryModule;
import com.jraska.pwmd.travel.settings.SettingsModule;
import com.jraska.pwmd.travel.tracking.TrackingModule;
import com.jraska.pwmd.travel.tracking.TrackingService;
import com.jraska.pwmd.travel.ui.*;
import dagger.Component;

@PerApp
@Component(
    modules = {
        GpsModule.class,
        SettingsModule.class,
        TableRouteDataRepositoryModule.class,
        TrackingModule.class,
        MediaModule.class,
        TravelAssistanceModule.class
    }
)

public interface TravelAssistanceComponent {
  void inject(HelpRequestSendActivity helpRequestSendActivity);

  void inject(RouteDetailActivity routeDetailActivity);

  void inject(RoutesListActivity routesListActivity);

  void inject(SettingsActivity settingsActivity);

  void inject(TrackingService trackingService);

  void inject(RouteRecordActivity routeRecordActivity);

  void inject(NavigationActivity navigationActivity);
}
