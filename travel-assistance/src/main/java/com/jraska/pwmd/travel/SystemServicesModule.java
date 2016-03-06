package com.jraska.pwmd.travel;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Vibrator;
import android.view.LayoutInflater;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;

@Module
public class SystemServicesModule {
  @Provides @PerApp Vibrator provideVibrator(Context context) {
    return (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
  }

  @Provides @PerApp LayoutInflater provideInflater(Context context) {
    return LayoutInflater.from(context);
  }

  @Provides @PerApp ConnectivityManager provideConnectivityManager(Context context) {
    return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
  }
}
