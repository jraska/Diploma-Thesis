package com.jraska.pwmd.travel.data;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.R;

public class TransportChangeSpec {
  //region Constants

  public static final int TRANSPORT_TYPE_WALK = 0;
  public static final int TRANSPORT_TYPE_BUS = 1;
  public static final int TRANSPORT_TYPE_TRAIN = 2;

  //endregion

  //region Fields

  @NonNull public final LatLng latLng;
  public final int transportType;
  @NonNull public final String title;
  @NonNull public final String description;

  //endregion

  //region Constructors

  public TransportChangeSpec(@NonNull LatLng latLng, int transportType,
                             @NonNull String title, @Nullable String description) {
    ArgumentCheck.notNull(latLng, "latLng");
    ArgumentCheck.notNull(title);

    this.latLng = latLng;
    this.transportType = transportType;
    this.title = title;
    this.description = description == null ? "" : description;
  }

  //endregion

  //region Methods

  public int getLightIconRes() {
    return getLightIconRes(transportType);
  }

  @DrawableRes
  public static int getLightIconRes(int transportType) {
    switch (transportType) {
      case TRANSPORT_TYPE_WALK:
        return R.drawable.ic_directions_walk_white;
      case TRANSPORT_TYPE_BUS:
        return R.drawable.ic_directions_bus_white;
      case TRANSPORT_TYPE_TRAIN:
        return R.drawable.ic_directions_railway_white;

      default:
        throw new IllegalStateException("unknown transport type");
    }
  }

  @DrawableRes
  public static int getDarkIconRes(int transportType) {
    switch (transportType) {
      case TRANSPORT_TYPE_WALK:
        return R.drawable.ic_directions_walk_black_24dp;
      case TRANSPORT_TYPE_BUS:
        return R.drawable.ic_directions_bus_black_24dp;
      case TRANSPORT_TYPE_TRAIN:
        return R.drawable.ic_directions_railway_black_24dp;

      default:
        throw new IllegalStateException("unknown transport type");
    }
  }

  //endregion

  //region Object impl

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    TransportChangeSpec spec = (TransportChangeSpec) o;

    if (transportType != spec.transportType) return false;
    if (!latLng.equals(spec.latLng)) return false;
    if (!title.equals(spec.title)) return false;
    return description.equals(spec.description);

  }

  @Override public int hashCode() {
    int result = latLng.hashCode();
    result = 31 * result + transportType;
    result = 31 * result + title.hashCode();
    result = 31 * result + description.hashCode();
    return result;
  }

  @Override public String toString() {
    return "TransportChangeSpec{" +
        "latLng=" + latLng +
        ", transportType=" + transportType +
        ", title='" + title + '\'' +
        ", description='" + description + '\'' +
        '}';
  }

  //endregion
}
