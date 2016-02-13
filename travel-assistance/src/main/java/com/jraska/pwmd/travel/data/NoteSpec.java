package com.jraska.pwmd.travel.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.UUID;

/**
 * Note spec can also contain images or text
 */
@Table(database = TravelDatabase.class)
@EqualsAndHashCode(callSuper = false)
@ToString
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
}
