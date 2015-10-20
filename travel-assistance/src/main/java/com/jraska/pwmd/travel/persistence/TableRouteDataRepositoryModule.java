package com.jraska.pwmd.travel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import dagger.Module;
import dagger.Provides;

@Module
public class TableRouteDataRepositoryModule {
  @Provides @PerApp SQLiteOpenHelper provideOpenHelper(Context context) {
    return new TravelAssistanceDbHelper(context, TravelAssistanceApp.DB_NAME);
  }

  @Provides @PerApp
  TravelDataPersistenceService providePersistenceSvc(SQLiteOpenHelper openHelper) {
    return new TableRouteDataRepository(openHelper);
  }
}
