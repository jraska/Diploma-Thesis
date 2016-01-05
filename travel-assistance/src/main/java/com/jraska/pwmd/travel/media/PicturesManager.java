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

  public static final String PHOTOS_DIR = "photos";

  //endregion

  //region Fields

  private final File _imagesDir;

  private UUID _lastImageId;

  //endregion

  //region Constructors

  @Inject @PerApp
  public PicturesManager(@Named(PHOTOS_DIR) @NonNull File imagesDir) {
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

  public Uri createImageUri() {
    _lastImageId = UUID.randomUUID();

    File imageFile = new File(_imagesDir, UUID.randomUUID().toString() + ".jpg");
    Uri imageUri = Uri.fromFile(imageFile);

    return imageUri;
  }

  public UUID getIdForUri(@NonNull Uri imageUri) {
    // TODO: 05/01/16 We should check the uri content and not rely on previous state
    return _lastImageId;
  }

  //endregion
}
