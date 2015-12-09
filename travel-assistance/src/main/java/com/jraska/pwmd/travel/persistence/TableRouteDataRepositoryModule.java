package com.jraska.pwmd.travel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import dagger.Module;
import dagger.Provides;

@Module
public class TableRouteDataRepositoryModule {
  @Provides @PerApp SQLiteOpenHelper provideOpenHelper(Context context) {
    // FIXME: 09/12/15 for testing purposes db is not where it should be
    return new TravelAssistanceDbHelper(context, Environment.getExternalStorageDirectory() + "/" + TravelAssistanceApp.DB_NAME);
  }

  @Provides @PerApp TravelDataRepository providePersistenceSvc(SQLiteOpenHelper openHelper) {
    return new TableRouteDataRepository(openHelper);
  }
}
