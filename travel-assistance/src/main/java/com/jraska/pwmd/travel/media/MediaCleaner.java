package com.jraska.pwmd.travel.media;

import com.jraska.common.ArgumentCheck;
import com.jraska.dagger.PerApp;
import com.jraska.pwmd.travel.data.NoteSpec;
import com.jraska.pwmd.travel.persistence.TravelDataRepository;
import dagger.Lazy;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import javax.inject.Inject;

@PerApp
public class MediaCleaner {
  private final Lazy<PicturesManager> _picturesManagerLazy;
  private final Lazy<SoundsManager> _soundsManagerLazy;

  @Inject
  public MediaCleaner(EventBus eventBus, Lazy<PicturesManager> picturesManagerLazy,
                      Lazy<SoundsManager> soundsManagerLazy) {
    ArgumentCheck.notNull(eventBus);
    ArgumentCheck.notNull(picturesManagerLazy);
    ArgumentCheck.notNull(soundsManagerLazy);

    eventBus.register(this);

    _picturesManagerLazy = picturesManagerLazy;
    _soundsManagerLazy = soundsManagerLazy;
  }

  @Subscribe
  public void onNoteSpecDeleted(TravelDataRepository.NoteSpecDeletedEvent deletedEvent) {
    NoteSpec spec = deletedEvent._noteSpec;
    if (spec.getImageId() != null) {
      _picturesManagerLazy.get().deleteImage(spec.getImageId());
    }
  }

  @Subscribe
  public void onRouteDeleted(TravelDataRepository.NoteSpecDeletedEvent deletedEvent) {
    NoteSpec spec = deletedEvent._noteSpec;
    if (spec.getSoundId() != null) {
      _soundsManagerLazy.get().deleteSound(spec.getSoundId());
    }
  }

}
