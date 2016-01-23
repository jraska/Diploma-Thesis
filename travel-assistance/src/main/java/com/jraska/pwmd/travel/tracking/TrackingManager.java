package com.jraska.pwmd.travel.tracking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.data.TransportChangeSpec;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface TrackingManager {
  //region Properties

  boolean isTracking();

  //endregion

  //region Methods

  void startTracking();

  PathInfo getLastPath();

  void stopTracking();

  boolean addChange(int type, @NonNull String title);

  boolean addNote(@Nullable UUID imageId, @NonNull String caption, @Nullable UUID soundId);

  //endregion

  //region Nested classes

  class PathInfo {
    private final Date _start;
    private final Date _end;
    private final List<LatLng> _path;
    private final List<TransportChangeSpec> _transportChangeSpecs;
    private final List<NoteSpec> _noteSpecs;

    public PathInfo(Date start, Date end, List<LatLng> path, List<TransportChangeSpec> specs, List<NoteSpec> noteSpecs) {
      _start = start;
      _end = end;
      _path = path;
      _transportChangeSpecs = specs;
      _noteSpecs = noteSpecs;
    }

    public Date getStart() {
      return _start;
    }

    public Date getEnd() {
      return _end;
    }

    public List<LatLng> getPath() {
      return _path;
    }

    public List<TransportChangeSpec> getTransportChangeSpecs() {
      return _transportChangeSpecs;
    }

    public List<NoteSpec> getNoteSpecs() {
      return _noteSpecs;
    }
  }

  //endregion
}
