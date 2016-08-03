package com.jraska.dialog.lambda;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import io.github.benas.randombeans.EnhancedRandomBuilder;
import io.github.benas.randombeans.api.EnhancedRandom;
import io.github.benas.randombeans.api.Randomizer;
import io.github.benas.randombeans.randomizers.text.StringRandomizer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class DialogFieldsBundleAdapterTest {
  @Test
  public void putsIntoBundleCorrectly() {
    DialogFieldsBundleAdapter bundleAdapter = new DialogFieldsBundleAdapter();
    DialogFields randomFields = createRandomFields();
    Bundle bundle = new Bundle();

    bundleAdapter.intoBundle(randomFields, bundle);
    DialogFields fieldsFromBundle = bundleAdapter.fromBundle(bundle);

    assertThat(fieldsFromBundle).isEqualTo(fieldsFromBundle);
  }

  static DialogFields createRandomFields() {
    EnhancedRandom random = EnhancedRandomBuilder
        .aNewEnhancedRandomBuilder()
        .randomize(CharSequence.class, new StringRandomizer())
        .randomize(ActivityAction.class, new Randomizer<ActivityAction>() {
          @Override public ActivityAction getRandomValue() {
            return new TestAction();
          }
        }).build();
    return random.nextObject(DialogFields.class);
  }

  static class TestAction implements ActivityAction {
    @Override public void call(FragmentActivity activity) {
      throw new UnsupportedOperationException();
    }
  }
}
