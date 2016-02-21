package com.jraska.pwmd.travel.util;

import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import timber.log.Timber;

import static android.widget.Toast.LENGTH_LONG;

public final class ShowContentDescriptionLongClickListener implements View.OnLongClickListener {
  //region OnLongClickListener impl

  @Override
  public boolean onLongClick(View view) {
    return showContentDescription(view);
  }

  //endregion

  //region Methods

  public static boolean showContentDescription(View view) {
    if (view == null) {
      return false;
    }

    if (TextUtils.isEmpty(view.getContentDescription())) {
      Timber.w("%s with id '%s' in %s has no content description.",
          view.getClass().getSimpleName(), view.getId(),
          view.getContext().getClass().getSimpleName());
      return false;
    }

    Toast toast = Toast.makeText(view.getContext(), view.getContentDescription(), LENGTH_LONG);
    positionToast(toast, view);
    toast.show();
    return true;
  }

  private static void positionToast(Toast toast, View v) {
    final int[] screenPos = new int[2];
    final Rect displayFrame = new Rect();
    v.getLocationOnScreen(screenPos);
    v.getWindowVisibleDisplayFrame(displayFrame);

    final Context context = v.getContext();
    final int width = v.getWidth();
    final int height = v.getHeight();
    final int midy = screenPos[1] + height / 2;
    int referenceX = screenPos[0] + width / 2;
    if (ViewCompat.getLayoutDirection(v) == ViewCompat.LAYOUT_DIRECTION_LTR) {
      final int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
      referenceX = screenWidth - referenceX; // mirror
    }

    if (midy < displayFrame.height()) {
      // Show along the top; follow action buttons
      toast.setGravity(Gravity.TOP | GravityCompat.END, referenceX,
          screenPos[1] + height - displayFrame.top);
    } else {
      // Show along the bottom center
      toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
    }
  }

  //endregion
}
