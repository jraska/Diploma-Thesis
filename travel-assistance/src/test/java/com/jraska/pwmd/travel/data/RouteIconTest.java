package com.jraska.pwmd.travel.data;


import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import com.jraska.BaseTest;
import org.junit.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class RouteIconTest extends BaseTest {
  @Test
  public void whenGotAllIconTypes_thenTheyHaveCorrectCount() {
    List<RouteIcon> allIcons = RouteIcon.ALL;

    assertThat(allIcons).hasSize(9);
  }

  @Test
  public void whenGetsIconId_thenFromIdReturnsSameRouteIcon() {
    for (RouteIcon icon : RouteIcon.ALL) {
      RouteIcon routeIcon = RouteIcon.fromId(icon.id);

      assertThat(routeIcon).isEqualTo(icon);
    }
  }

  @Test
  public void whenIconResUsedAsDrawable_thenDrawableReturned() {
    for (RouteIcon icon : RouteIcon.ALL) {
      Drawable drawable = ContextCompat.getDrawable(getApplication(), icon.iconResId);

      assertThat(drawable).isNotNull();
    }
  }

  @Test
  public void whenGotAllIcons_thenIdsAreDistinct() {
    Set<Integer> ids = new HashSet<>();

    for (RouteIcon icon : RouteIcon.ALL) {
      assertThat(ids).doesNotContain(icon.id);

      ids.add(icon.id);
    }
  }

  @Test
  public void whenGotAllIcons_thenIconResIdsAreDistinct() {
    Set<Integer> ids = new HashSet<>();

    for (RouteIcon icon : RouteIcon.ALL) {
      assertThat(ids).doesNotContain(icon.iconResId);

      ids.add(icon.iconResId);
    }
  }
}