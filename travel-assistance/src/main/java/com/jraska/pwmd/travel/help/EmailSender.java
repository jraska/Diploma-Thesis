package com.jraska.pwmd.travel.help;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.support.v4.app.ShareCompat;
import com.jraska.common.ArgumentCheck;
import timber.log.Timber;

public class EmailSender {
  //region Fields

  private final Activity _activity;

  //endregion

  //region Constructors

  public EmailSender(Activity activity) {
    ArgumentCheck.notNull(activity, "activity");

    _activity = activity;
  }

  //endregion

  //region Methods

  /**
   * Starts app with email prepared.
   *
   * @return True if the email could be sent, false fi no app for email found.
   */
  public boolean sendEmail(String email, String subject, String message) {
    ShareCompat.IntentBuilder emailIntentBuilder = ShareCompat.IntentBuilder.from(_activity)
        .addEmailTo(email)
        .setSubject(subject)
        .setType("text/html")
        .setText(message);

    Intent intent = emailIntentBuilder.getIntent();
    ComponentName componentName = intent.resolveActivity(_activity.getPackageManager());

    if (componentName != null) {
      _activity.startActivity(intent);
      return true;
    } else {
      Timber.w("Could not resolve email intent. Nothing sent.");
      return false;
    }
  }

  //endregion
}
