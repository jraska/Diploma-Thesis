package com.jraska.pwmd.travel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import dagger.Module;
import dagger.Provides;

import java.io.File;

@Module
public class TableRouteDataRepositoryModule {
  @Provides @PerApp SQLiteOpenHelper provideOpenHelper(Context context) {
    // FIXME: 09/12/15 for testing purposes db is not where it should be
    File dbFile = context.getDatabasePath(TravelAssistanceApp.DB_NAME);
    return new TravelAssistanceDbHelper(context, dbFile.getAbsolutePath());
  }

  @Provides @PerApp TravelDataRepository providePersistenceSvc(SQLiteOpenHelper openHelper) {
    return new TableRouteDataRepository(openHelper);
  }
}
