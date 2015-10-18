package com.jraska.pwmd.travel.data;

import android.os.Parcel;
import android.os.Parcelable;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.core.gps.Position;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Path implements Parcelable {
  //region Fields

  private final List<Position> _points;

  //endregion

  //region Constructors

  public Path(List<Position> points) {
    ArgumentCheck.notNull(points, "points");

    _points = Collections.unmodifiableList(points);
  }

  //endregion

  //region Properties

  public List<Position> getPoints() {
    return _points;
  }

  //endregion

  //region Parcelable impl

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeParcelableArray(_points.toArray(new LatLng[_points.size()]), 0);
  }

  public static final Parcelable.Creator<Path> CREATOR = new Parcelable.Creator<Path>() {
    public Path createFromParcel(Parcel p) {
      Parcelable[] values = p.readParcelableArray(getClass().getClassLoader());
      Position[] positions = new Position[values.length];

      for (int i = 0; i < values.length; i++) {
        positions[i] = (Position) values[i];
      }


      return new Path(Arrays.asList(positions));
    }

    public Path[] newArray(int size) {
      return new Path[size];
    }
  };


  //endregion

  //region Object impl

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Path path = (Path) o;

    if (!_points.equals(path._points)) return false;

    return true;
  }

  @Override
  public int hashCode() {
    return _points.hashCode();
  }


  //endregion
}
