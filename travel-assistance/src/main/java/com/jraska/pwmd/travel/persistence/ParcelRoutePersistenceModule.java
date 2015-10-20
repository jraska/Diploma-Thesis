package com.jraska.pwmd.travel.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import dagger.Module;
import dagger.Provides;

@Module
public class ParcelRoutePersistenceModule {
  @Provides @PerApp SQLiteOpenHelper provideOpenHelper(Context context) {
    return new TravelAssistanceParcelableDbHelper(context, TravelAssistanceApp.DB_NAME);
  }

  @Provides @PerApp TravelDataRepository providePersistenceSvc(SQLiteOpenHelper openHelper) {
    return new RouteParcelTravelDataRepository(openHelper);
  }
}
