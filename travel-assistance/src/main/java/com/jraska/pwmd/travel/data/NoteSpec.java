package com.jraska.pwmd.travel.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.UUID;

/**
 * Note spec can also contain images or text
 */
@Table(database = TravelDatabase.class)
public class NoteSpec extends BaseModel {
  //region Fields

  @PrimaryKey(autoincrement = true) long _id;
  @Column long _routeId;
  @Column LatLng latLng;
  @Column String caption;
  @Column @Nullable UUID imageId;
  @Column @Nullable UUID soundId;

  //endregion

  //region Constructors

  NoteSpec() {
  }

  public NoteSpec(@NonNull LatLng latLng, @NonNull String caption) {
    this(latLng, null, caption);
  }

  public NoteSpec(@NonNull LatLng latLng, @Nullable UUID imageId, @NonNull String caption) {
    this(latLng, imageId, caption, null);
  }

  public NoteSpec(@NonNull LatLng latLng, @Nullable UUID imageId,
                  @NonNull String caption, @Nullable UUID soundId) {
    ArgumentCheck.notNull(latLng);
    ArgumentCheck.notNull(caption);

    this.latLng = latLng;
    this.caption = caption;
    this.imageId = imageId;
    this.soundId = soundId;
  }

  //endregion

  //region Properties

  public long getRouteId() {
    return _routeId;
  }

  public LatLng getLatLng() {
    return latLng;
  }

  public String getCaption() {
    return caption;
  }

  @Nullable
  public UUID getImageId() {
    return imageId;
  }

  @Nullable
  public UUID getSoundId() {
    return soundId;
  }


  //endregion

  //region Object impl

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NoteSpec noteSpec = (NoteSpec) o;

    if (_id != noteSpec._id) return false;
    if (_routeId != noteSpec._routeId) return false;
    if (latLng != null ? !latLng.equals(noteSpec.latLng) : noteSpec.latLng != null) return false;
    if (caption != null ? !caption.equals(noteSpec.caption) : noteSpec.caption != null) {
      return false;
    }
    if (imageId != null ? !imageId.equals(noteSpec.imageId) : noteSpec.imageId != null) {
      return false;
    }
    return soundId != null ? soundId.equals(noteSpec.soundId) : noteSpec.soundId == null;

  }

  @Override public int hashCode() {
    int result = (int) (_id ^ (_id >>> 32));
    result = 31 * result + (int) (_routeId ^ (_routeId >>> 32));
    result = 31 * result + (latLng != null ? latLng.hashCode() : 0);
    result = 31 * result + (caption != null ? caption.hashCode() : 0);
    result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
    result = 31 * result + (soundId != null ? soundId.hashCode() : 0);
    return result;
  }


  //endregion
}
