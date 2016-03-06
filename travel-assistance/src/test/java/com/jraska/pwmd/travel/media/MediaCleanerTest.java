package com.jraska.pwmd.travel.media;

import com.jraska.BaseTest;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import dagger.Lazy;
import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

// Registration of cleaner to bus is in constructor
// and then no other interaction is needed
@SuppressWarnings("unused")
public class MediaCleanerTest extends BaseTest {

  //region Setup Methods

  private File _testDataDir;

  @Before
  public void preparePicsDir() throws Exception {
    _testDataDir = new File("./unit-test-data");
    if (!_testDataDir.exists()) {
      assertThat(_testDataDir.mkdirs()).isTrue();
    }
  }

  @After
  public void cleanPicsDir() throws Exception {
    for (File f : _testDataDir.listFiles()) {
      assertThat(f.delete()).isTrue();
    }

    assertThat(_testDataDir.delete()).isTrue();
  }

  //endregion

  @Test
  public void testImageDeletedOnNoteSpecDelete() throws Exception {
    UUID testId = UUID.randomUUID();
    EventBus eventBus = new EventBus();

    PicturesManager picturesManager = new PicturesManager(_testDataDir);
    MediaCleaner mediaCleaner = new MediaCleaner(eventBus,
        lazy(picturesManager), lazy(mock(SoundsManager.class)));

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

  @Test
  public void testSoundFileDeletedOnNoteSpecDeleted() throws Exception {

    UUID testId = UUID.randomUUID();
    EventBus eventBus = new EventBus();

    SoundsManager soundsManager = new SoundsManager(_testDataDir);
    MediaCleaner mediaCleaner = new MediaCleaner(eventBus,
        lazy(mock(PicturesManager.class)), lazy(soundsManager));

    File soundFile = soundsManager.getFileForId(testId);
    if (soundFile.exists()) {
      assertThat(soundFile.delete()).isTrue();
    }
    assertThat(soundFile.createNewFile()).isTrue();

    NoteSpec noteSpec = mock(NoteSpec.class);
    when(noteSpec.getSoundId()).thenReturn(testId);

    eventBus.post(new TravelDataRepository.NoteSpecDeletedEvent(noteSpec));

    assertThat(soundFile.exists()).isFalse();
  }

  <T> Lazy<T> lazy(T object) {
    return new DirectLazy<>(object);
  }

  static class DirectLazy<T> implements Lazy<T> {
    private final T _object;

    public DirectLazy(T object) {
      _object = object;
    }

    @Override public T get() {
      return _object;
    }
  }
}