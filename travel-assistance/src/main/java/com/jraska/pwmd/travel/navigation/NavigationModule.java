package com.jraska.pwmd.travel.navigation;

import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

import javax.inject.Named;

@Module
public class NavigationModule {
  //region  Methods

  @PerApp @Provides @Named(Navigator.NAVIGATOR_BUS_NAME)
  public EventBus provideNavigatorBus() {
    return new EventBus();
  }

  //endregion
}
