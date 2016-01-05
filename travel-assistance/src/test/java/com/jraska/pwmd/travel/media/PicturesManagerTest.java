package com.jraska.pwmd.travel.media;

import android.net.Uri;
import com.jraska.BaseTest;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class PicturesManagerTest extends BaseTest {

  //region Test Methods

  @Test
  public void testGetIdForUri() throws Exception {
    UUID testId = UUID.randomUUID();

    PicturesManager picturesManager = new PicturesManager(new File("."));
    Uri pictureUri = picturesManager.createPictureUri(testId);

    UUID idForUri = picturesManager.getIdForUri(pictureUri);

    assertThat(idForUri, equalTo(testId));
  }

  //endregion
}