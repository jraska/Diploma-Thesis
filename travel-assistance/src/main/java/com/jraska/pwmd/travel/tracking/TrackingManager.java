package com.jraska.pwmd.travel.tracking;

import com.jraska.pwmd.travel.data.Path;

import java.util.Date;

public interface TrackingManager {
  //region Properties

  boolean isTracking();

  //endregion

  //region Methods

  void startTracking();

  PathInfo getLastPath();

  void stopTracking();

  //endregion

  //region Nested classes

  class PathInfo {
    private final Date _start;
    private final Date _end;
    private final Path _path;

    public PathInfo(Date start, Date end, Path path) {
      _start = start;
      _end = end;
      _path = path;
    }

    public Date getStart() {
      return _start;
    }

    public Date getEnd() {
      return _end;
    }

    public Path getPath() {
      return _path;
    }
  }

  //endregion
}
