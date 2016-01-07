package com.jraska.pwmd.travel.data;

import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.core.gps.LatLng;

import java.util.UUID;

/**
 * Note spec can also contain images or text
 */
public class NoteSpec {
  //region Constants

  public static final UUID EMPTY_UUID = UUID.fromString("00000000-0000-0000-0000-012345678901");

  //endregion

  //region Fields

  @NonNull public final LatLng latLng;
  @NonNull public final String caption;
  @NonNull public final UUID imageId;
  @NonNull public final UUID soundId;

  //endregion

  //region Constructors

  public NoteSpec(@NonNull LatLng latLng, @NonNull String caption) {
    this(latLng, EMPTY_UUID, caption);
  }

  public NoteSpec(@NonNull LatLng latLng, @NonNull UUID imageId, @NonNull String caption) {
    this(latLng, imageId, caption, EMPTY_UUID);
  }

  public NoteSpec(@NonNull LatLng latLng, @NonNull UUID imageId,
                  @NonNull String caption, @NonNull UUID soundId) {
    ArgumentCheck.notNull(latLng);
    ArgumentCheck.notNull(imageId);
    ArgumentCheck.notNull(caption);
    ArgumentCheck.notNull(soundId);

    this.latLng = latLng;
    this.caption = caption;
    this.imageId = imageId;
    this.soundId = soundId;
  }


  //endregion

  //region Object impl

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    NoteSpec noteSpec = (NoteSpec) o;

    if (!latLng.equals(noteSpec.latLng)) return false;
    if (!caption.equals(noteSpec.caption)) return false;
    if (!imageId.equals(noteSpec.imageId)) return false;
    return soundId.equals(noteSpec.soundId);

  }

  @Override public int hashCode() {
    int result = latLng.hashCode();
    result = 31 * result + caption.hashCode();
    result = 31 * result + imageId.hashCode();
    result = 31 * result + soundId.hashCode();
    return result;
  }

  //endregion
}
