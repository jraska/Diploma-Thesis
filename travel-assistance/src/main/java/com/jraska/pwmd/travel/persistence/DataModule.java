package com.jraska.pwmd.travel.persistence;

import android.content.Context;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.data.TravelDatabase;
import com.raizlabs.android.dbflow.config.FlowManager;
import dagger.Module;
import dagger.Provides;
import org.greenrobot.eventbus.EventBus;

import java.io.File;

@Module
public class DataModule {
  //region Provide Methods

  @Provides @PerApp
  public TravelDataRepository providePersistenceService(EventBus eventBus) {
    return new DBFlowDataRepository(eventBus);
  }

  @Provides @PerApp @DatabaseFile File databaseFile(Context context) {
    String databaseName = FlowManager.getDatabase(TravelDatabase.DATABASE_NAME).getDatabaseFileName();
    return context.getDatabasePath(databaseName);
  }

  //endregion
}
