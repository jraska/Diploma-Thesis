package com.jraska.pwmd.travel.media;

import android.net.Uri;
import com.jraska.BaseTest;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import org.greenrobot.eventbus.EventBus;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PicturesManagerTest extends BaseTest {

  //region Test Methods

  @Test
  public void testGetIdForUri() throws Exception {
    UUID testId = UUID.randomUUID();

    PicturesManager picturesManager = new PicturesManager(new File("."), new EventBus());
    Uri pictureUri = picturesManager.createPictureUri(testId);

    UUID idForUri = picturesManager.getIdForUri(pictureUri);

    assertThat(idForUri).isEqualTo(testId);
  }

  @Test
  public void testImageDeletedOnNoteSpecDelete() throws Exception {
    UUID testId = UUID.randomUUID();
    EventBus eventBus = new EventBus();

    PicturesManager picturesManager = new PicturesManager(new File("."), eventBus);
    File imageFile = picturesManager.getImageFile(testId);
    if (imageFile.exists()) {
      assertThat(imageFile.delete()).isTrue();
    }
    assertThat(imageFile.createNewFile()).isTrue();

    NoteSpec noteSpec = mock(NoteSpec.class);
    when(noteSpec.getImageId()).thenReturn(testId);

    eventBus.post(new TravelDataRepository.NoteSpecDeletedEvent(noteSpec));

    assertThat(imageFile.exists()).isFalse();
  }

  //endregion
}