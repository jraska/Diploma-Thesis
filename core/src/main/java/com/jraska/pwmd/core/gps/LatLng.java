package com.jraska.pwmd.core.gps;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class LatLng implements Parcelable {
  //region Fields

  public final double _latitude;
  public final double _longitude;

  //endregion

  //region Constructors

  public LatLng(Location location) {
    this(location.getLatitude(), location.getLongitude());
  }

  public LatLng(double latitude, double longitude) {
    _latitude = latitude;
    _longitude = longitude;
  }

  //endregion

  //region Methods

  public float bearingTo(Location location) {
    return this.toLocation().bearingTo(location);
  }

  public float bearingTo(LatLng latLng) {
    return bearingTo(latLng.toLocation());
  }

  public float distanceTo(Location location) {
    return this.toLocation().distanceTo(location);
  }

  public float distanceTo(LatLng latLng) {
    return distanceTo(latLng.toLocation());
  }

  public Location toLocation() {
    Location location = new Location("");
    location.setLatitude(_latitude);
    location.setLongitude(_longitude);

    return location;
  }

  public static LatLng fromLocation(Location location) {
    return new LatLng(location);
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
    public LatLng createFromParcel(Parcel parcel) {
      return new LatLng(parcel.readDouble(), parcel.readDouble());
    }

    public LatLng[] newArray(int size) {
      return new LatLng[size];
    }
  };

  //endregion
}
