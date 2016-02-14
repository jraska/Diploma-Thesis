package com.jraska.pwmd.travel.persistence;

import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

@Module
public class DataModule {
  //region Provide Methods

  @Provides @PerApp
  public TravelDataRepository providePersistenceService(EventBus eventBus) {
    return new DBFlowDataRepository(eventBus);
  }

  //endregion
}
