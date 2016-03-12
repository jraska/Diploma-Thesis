package com.jraska.pwmd.travel.feedback;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.util.Base64;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.help.EmailSender;
import com.jraska.pwmd.travel.ui.AboutDialog;
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
    emailSender.sendEmail(getEmail(), AboutDialog.getAppInfoTitle(activity), message);
  }

  private static String getEmail() {
    byte[] decode = Base64.decode(Base64.decode("Y21Gek1EQXlPVUIyYzJJdVkzbz0=", 0), 0);
    return new String(decode);
  }

  //endregion
}
