package com.jraska.pwmd.core.gps;

import android.os.Parcel;
import android.os.Parcelable;

public class LatLng implements Parcelable {
  //region Fields

  public final double _latitude;
  public final double _longitude;

  //endregion

  //region Constructors

  public LatLng(double latitude, double longitude) {
    _latitude = latitude;
    _longitude = longitude;
  }


  //endregion

  //region Parcelable impl

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeDouble(_latitude);
    dest.writeDouble(_longitude);
  }

  public static final Parcelable.Creator<LatLng> CREATOR = new Parcelable.Creator<LatLng>() {
    public LatLng createFromParcel(Parcel p) {
      return new LatLng(p.readDouble(), p.readDouble());
    }

    public LatLng[] newArray(int size) {
      return new LatLng[size];
    }
  };

  //endregion

  //region Object impl

  @Override
  public String toString() {
    return getClass().getSimpleName() +
        " _latitude=" + _latitude +
        ", _longitude=" + _longitude +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    LatLng latLng = (LatLng) o;

    if (Double.compare(latLng._latitude, _latitude) != 0) return false;
    if (Double.compare(latLng._longitude, _longitude) != 0) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(_latitude);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(_longitude);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  //endregion
}
