package com.jraska.pwmd.travel.media;

import android.net.Uri;
import android.support.annotation.NonNull;
import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.persistence.DataModule;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import de.greenrobot.event.EventBus;
import timber.log.Timber;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.UUID;

@PerApp
public class PicturesManager {
  //region Constants

  public static final String PICTURES_DIR = "pictures";
  public static final String JPG_EXTENSION = ".jpg";

  //endregion

  //region Fields

  private final File _imagesDir;
  private final EventBus _dataBus;

  //endregion

  //region Constructors

  @Inject
  public PicturesManager(@Named(PICTURES_DIR) @NonNull File imagesDir,
                         @Named(DataModule.DATA_BUS_NAME) EventBus dataBus) {
    ArgumentCheck.notNull(imagesDir);
    ArgumentCheck.notNull(dataBus);

    _imagesDir = imagesDir;
    _dataBus = dataBus;

    _dataBus.register(this);
  }

  //endregion

  //region Properties

  public File getImagesDir() {
    return _imagesDir;
  }

  //endregion

  //region Events handling

  public void onEvent(TravelDataRepository.NoteSpecDeletedEvent deletedEvent) {
    NoteSpec spec = deletedEvent._noteSpec;
    if (spec.getImageId() != null) {
      deleteImage(spec.getImageId());
    }
  }
  //endregion

  //region Methods

  public Uri createPictureUri() {
    UUID uuid = UUID.randomUUID();

    return createPictureUri(uuid);
  }

  public Uri createPictureUri(UUID uuid) {
    File imageFile = getImageFile(uuid);
    Uri imageUri = Uri.fromFile(imageFile);

    return imageUri;
  }

  @NonNull
  public File getImageFile(UUID uuid) {
    return new File(_imagesDir, uuid.toString() + JPG_EXTENSION);
  }

  public UUID getIdForUri(@NonNull Uri imageUri) {
    String uriValue = imageUri.toString();

    int lastIndexOfPath = uriValue.lastIndexOf("/");
    int lastIndex = uriValue.length() - JPG_EXTENSION.length();
    String uuidValue = uriValue.substring(lastIndexOfPath + 1, lastIndex);

    return UUID.fromString(uuidValue);
  }

  private void deleteImage(UUID imageId) {
    File imageFile = getImageFile(imageId);
    if (imageFile.exists() && imageFile.delete()) {
      Timber.i("Image " + imageId + " successfully deleted");
    } else {
      Timber.w("Image " + imageId + " was not deleted ");
    }
  }

  //endregion
}
