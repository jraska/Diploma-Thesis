package com.jraska.pwmd.core.gps;

import com.jraska.common.ArgumentCheck;
import com.jraska.common.events.EventArgs;

public class Position implements EventArgs {
  //region Fields

  public final LatLng latLng;
  public final long time;
  public final float accuracy;
  public final String provider;


  //endregion

  //region Constructors

  @Deprecated
  public Position(double latitude, double longitude, long time, float accuracy, String provider) {
    this(new LatLng(latitude, longitude), time, accuracy, provider);
  }

  public Position(LatLng latLng, long time, float accuracy, String provider) {
    ArgumentCheck.notNull(provider);

    this.time = time;
    this.accuracy = accuracy;
    this.provider = provider;
    this.latLng = latLng;
  }

  //endregion

  //region Object impl

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Position position = (Position) o;

    if (time != position.time) return false;
    if (Float.compare(position.accuracy, accuracy) != 0) return false;
    if (provider != null ? !provider.equals(position.provider) : position.provider != null) {
      return false;
    }
    return !(latLng != null ? !latLng.equals(position.latLng) : position.latLng != null);

  }

  @Override public int hashCode() {
    int result = (int) (time ^ (time >>> 32));
    result = 31 * result + (accuracy != +0.0f ? Float.floatToIntBits(accuracy) : 0);
    result = 31 * result + (provider != null ? provider.hashCode() : 0);
    result = 31 * result + (latLng != null ? latLng.hashCode() : 0);
    return result;
  }


  //endregion
}
