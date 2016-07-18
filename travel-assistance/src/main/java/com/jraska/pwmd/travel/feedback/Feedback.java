package com.jraska.pwmd.travel.feedback;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.BuildConfig;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.help.EmailSender;
import lombok.ToString;

@ToString
public class Feedback {
  //region Fields

  private final String _title;
  private final String _body;

  //endregion

  //region Constructors

  public Feedback(String title, @Nullable String body) {
    ArgumentCheck.notNull(title);

    _title = title;

    if (body == null) {
      _body = "";
    } else {
      _body = body;
    }
  }

  //endregion

  //region Properties

  public String getTitle() {
    return _title;
  }

  public String getBody() {
    return _body;
  }

  //endregion

  //region Methods

  public static void startFeedback(Activity activity, String message) {
    EmailSender emailSender = new EmailSender(activity);
    emailSender.sendEmail(getEmail(), getAppInfoTitle(activity), message);
  }

  private static String getEmail() {
    byte[] decode = Base64.decode(Base64.decode("Y21Gek1EQXlPVUIyYzJJdVkzbz0=", 0), 0);
    return new String(decode);
  }

  public static String getAppInfoTitle(Context context) {
    return context.getString(R.string.app_name) + " v" + BuildConfig.VERSION_NAME;
  }

  //endregion
}
