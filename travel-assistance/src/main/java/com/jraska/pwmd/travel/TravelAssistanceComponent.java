package com.jraska.pwmd.travel;

import com.jraska.dagger.PerApp;
import com.jraska.pwmd.core.gps.LocationModule;
import com.jraska.pwmd.travel.feedback.FeedbackModule;
import com.jraska.pwmd.travel.media.MediaModule;
import com.jraska.pwmd.travel.navigation.NavigationModule;
import com.jraska.pwmd.travel.persistence.DataModule;
import com.jraska.pwmd.travel.settings.SettingsModule;
import com.jraska.pwmd.travel.tracking.TrackingModule;
import com.jraska.pwmd.travel.tracking.TrackingService;
import com.jraska.pwmd.travel.ui.*;
import dagger.Component;

@PerApp
@Component(
    modules = {
        LocationModule.class,
        SettingsModule.class,
        DataModule.class,
        TrackingModule.class,
        MediaModule.class,
        NavigationModule.class,
        FeedbackModule.class,
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

  void inject(VoiceRecordActivity voiceRecordActivity);

  void inject(RouteDisplayFragment routeDisplayFragment);

  void inject(NfcWriteActivity nfcWriteActivity);

  void inject(FeedbackActivity feedbackActivity);
}
