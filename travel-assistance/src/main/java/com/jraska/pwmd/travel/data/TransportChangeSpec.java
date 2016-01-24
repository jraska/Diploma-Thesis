package com.jraska.pwmd.travel.data;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;
import com.jraska.pwmd.travel.R;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = TravelDatabase.class)
public class TransportChangeSpec extends BaseModel {
  //region Constants

  public static final int TRANSPORT_TYPE_WALK = 0;
  public static final int TRANSPORT_TYPE_BUS = 1;
  public static final int TRANSPORT_TYPE_TRAIN = 2;

  //endregion

  //region Fields

  @PrimaryKey(autoincrement = true) long _id;
  @Column long _routeId;
  @Column LatLng latLng;
  @Column int transportType;
  @Column String title;

  //endregion

  //region Constructors

  TransportChangeSpec() {
  }

  public TransportChangeSpec(@NonNull LatLng latLng, int transportType,
                             @NonNull String title) {
    ArgumentCheck.notNull(latLng, "latLng");
    ArgumentCheck.notNull(title);

    this.latLng = latLng;
    this.transportType = transportType;
    this.title = title;
  }

  //endregion

  //region Properties

  public LatLng getLatLng() {
    return latLng;
  }

  public int getTransportType() {
    return transportType;
  }

  public String getTitle() {
    return title;
  }


  //endregion

  //region Methods

  /**
   * Gets icon displayable as static icon over maps as markers
   *
   * @return res of icon.
   */
  public int getHardIconRes() {
    return getHardIconRes(transportType);
  }

  @DrawableRes
  public static int getHardIconRes(int transportType) {
    switch (transportType) {
      case TRANSPORT_TYPE_WALK:
        return R.drawable.ic_directions_walk_black;
      case TRANSPORT_TYPE_BUS:
        return R.drawable.ic_directions_bus_black;
      case TRANSPORT_TYPE_TRAIN:
        return R.drawable.ic_directions_railway_black;

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
    return title.equals(spec.title);

  }

  @Override
  public int hashCode() {
    int result = latLng.hashCode();
    result = 31 * result + transportType;
    result = 31 * result + title.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "TransportChangeSpec{" +
        "latLng=" + latLng +
        ", transportType=" + transportType +
        ", title='" + title + '\'' +
        '}';
  }

  //endregion
}
