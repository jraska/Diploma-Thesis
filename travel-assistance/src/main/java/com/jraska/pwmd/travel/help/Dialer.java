package com.jraska.pwmd.travel.help;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import com.jraska.common.ArgumentCheck;
import timber.log.Timber;

public class Dialer {
  //region Fields

  private final Activity _activity;

  //endregion

  //region Constructors

  public Dialer(Activity activity) {
    ArgumentCheck.notNull(activity);

    _activity = activity;
  }

  //endregion

  //region Methods

  public boolean phoneCall(String number) {
    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
    if (intent.resolveActivity(_activity.getPackageManager()) != null) {
      _activity.startActivity(intent);
      return true;
    } else {
      Timber.w("Cannot perform phone call.");
      return false;
    }
  }

  //endregion
}
