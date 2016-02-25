package com.jraska.pwmd.travel.feedback;

import android.support.annotation.Nullable;
import com.jraska.common.ArgumentCheck;
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
      body = "";
    }

    _body = body;
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
}
