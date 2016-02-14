package com.jraska.pwmd.travel.media;

import com.jraska.BaseTest;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import org.greenrobot.eventbus.EventBus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SoundsManagerTest extends BaseTest {
  private File _soundsDir;

  @Before
  public void prepareSoundsDir() throws Exception {
    _soundsDir = new File("./unit-test-sounds");
    if (!_soundsDir.exists()) {
      assertThat(_soundsDir.mkdirs()).isTrue();
    }
  }

  @After
  public void cleanSoundsDir() throws Exception {
    for (File f : _soundsDir.listFiles()) {
      assertThat(f.delete()).isTrue();
    }

    assertThat(_soundsDir.delete()).isTrue();
  }

  @Test
  public void testSoundFileDeletedOnNoteSpecDeleted() throws Exception {

    UUID testId = UUID.randomUUID();
    EventBus eventBus = new EventBus();

    SoundsManager soundsManager = new SoundsManager(_soundsDir, eventBus);
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
}