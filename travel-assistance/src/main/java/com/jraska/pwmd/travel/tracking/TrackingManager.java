package com.jraska.pwmd.travel.tracking;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.pwmd.travel.data.Path;
import com.jraska.pwmd.travel.data.PictureSpec;
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

  boolean addPicture(UUID imageId, String caption);

  //endregion

  //region Nested classes

  class PathInfo {
    private final Date _start;
    private final Date _end;
    private final Path _path;
    private final List<TransportChangeSpec> _transportChangeSpecs;
    private final List<PictureSpec> _pictureSpecs;

    public PathInfo(Date start, Date end, Path path, List<TransportChangeSpec> specs, List<PictureSpec> pictureSpecs) {
      _start = start;
      _end = end;
      _path = path;
      _transportChangeSpecs = specs;
      _pictureSpecs = pictureSpecs;
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

    public List<TransportChangeSpec> getTransportChangeSpecs() {
      return _transportChangeSpecs;
    }

    public List<PictureSpec> getPictureSpecs() {
      return _pictureSpecs;
    }
  }

  //endregion
}
