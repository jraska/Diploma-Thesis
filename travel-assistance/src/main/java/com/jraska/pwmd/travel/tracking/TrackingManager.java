package com.jraska.pwmd.travel.tracking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.pwmd.travel.data.RouteData;

import java.util.UUID;

public interface TrackingManager {
  //region Properties

  boolean isTracking();

  @Nullable
  UserInput getLastUserInput();

  //endregion

  //region Methods

  void startTracking();

  @Nullable RouteData getRouteData(@NonNull UserInput userInput);

  void stopTracking();

  boolean addChange(int type, @NonNull String title);

  boolean addNote(@Nullable UUID imageId, @NonNull String caption, @Nullable UUID soundId);

  //endregion

  class UserInput {
    private final String _title;

    public UserInput(String title) {
      _title = title;
    }

    public String getTitle() {
      return _title;
    }
  }
}
