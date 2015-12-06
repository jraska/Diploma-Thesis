package com.jraska.common.view;

import android.support.annotation.NonNull;
import android.view.View;

public class VisibilitySwitcher {
  //region Fields

  private final View[] _mainViews;
  private final View[] _secondaryViews;

  //endregion

  //region Constructors

  public VisibilitySwitcher(@NonNull View main, @NonNull View secondary) {
    this(new View[]{main}, new View[]{secondary});
  }

  public VisibilitySwitcher(@NonNull View[] mainViews, @NonNull View[] secondaryViews) {
    _mainViews = mainViews;
    _secondaryViews = secondaryViews;
  }

  //endregion

  //region Methods

  public void showPrimary() {
    setVisibility(_mainViews, View.VISIBLE);
    setVisibility(_secondaryViews, View.GONE);
  }

  public void showSecondary() {
    setVisibility(_mainViews, View.GONE);
    setVisibility(_secondaryViews, View.VISIBLE);
  }

  private void setVisibility(View[] views, int visibility) {
    for (View v : views) {
      v.setVisibility(visibility);
    }
  }

  //endregion
}
