package com.jraska.pwmd.travel.media;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.UUID;

public class PicturesManager {
  //region Constants

  public static final String PICTURES_DIR = "pictures";
  public static final String JPG_EXTENSION = ".jpg";

  //endregion

  //region Fields

  private final File _imagesDir;

  //endregion

  //region Constructors

  @Inject @PerApp
  public PicturesManager(@Named(PICTURES_DIR) @NonNull File imagesDir) {
    ArgumentCheck.notNull(imagesDir);

    _imagesDir = imagesDir;
  }

  //endregion

  //region Properties

  public File getImagesDir() {
    return _imagesDir;
  }

  //endregion

  //region Methods

  public Uri createPictureUri() {
    UUID uuid = UUID.randomUUID();

    return createPictureUri(uuid);
  }

  protected Uri createPictureUri(UUID uuid) {
    File imageFile = new File(_imagesDir, uuid.toString() + JPG_EXTENSION);
    Uri imageUri = Uri.fromFile(imageFile);

    return imageUri;
  }

  public UUID getIdForUri(@NonNull Uri imageUri) {
    String uriValue = imageUri.toString();

    int lastIndexOfPath = uriValue.lastIndexOf("/");
    int lastIndex = uriValue.length() - JPG_EXTENSION.length();
    String uuidValue = uriValue.substring(lastIndexOfPath + 1, lastIndex);

    return UUID.fromString(uuidValue);
  }

  //endregion
}
