package com.jraska.pwmd.travel.data;

import com.jraska.pwmd.core.gps.LatLng;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

@Table(database = TravelDatabase.class)
public class DbPosition extends BaseModel {
  @PrimaryKey(autoincrement = true) long _id;
  @Column long _routeId;
  @Column
  public LatLng latLng;

  DbPosition() {
  }

  public DbPosition(LatLng latLng) {
    this.latLng = latLng;
  }

  //region Object impl

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    DbPosition that = (DbPosition) o;

    if (_id != that._id) return false;
    if (_routeId != that._routeId) return false;
    return latLng != null ? latLng.equals(that.latLng) : that.latLng == null;

  }

  @Override public int hashCode() {
    int result = (int) (_id ^ (_id >>> 32));
    result = 31 * result + (int) (_routeId ^ (_routeId >>> 32));
    result = 31 * result + (latLng != null ? latLng.hashCode() : 0);
    return result;
  }

  @Override public String toString() {
    return "DbPosition{" +
        "_id=" + _id +
        ", _routeId=" + _routeId +
        ", latLng=" + latLng +
        '}';
  }

  //endregion
}
