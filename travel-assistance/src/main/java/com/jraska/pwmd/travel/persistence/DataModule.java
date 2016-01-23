package com.jraska.pwmd.travel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import dagger.Module;
import dagger.Provides;
import de.greenrobot.event.EventBus;

import javax.inject.Named;
import java.io.File;

@Module
public class DataModule {
  //region Constants

  public static final String DATA_BUS_NAME = "dataBus";

  //endregion

  //region Provide Methods


  @Provides @PerApp
  public TravelDataRepository providePersistenceService(@Named(DATA_BUS_NAME) EventBus dataBus) {
    return new DBFlowDataRepository(dataBus);
  }

  @Provides @PerApp @Named(DATA_BUS_NAME) EventBus provideDataBus() {
    return new EventBus();
  }

  //endregion
}
