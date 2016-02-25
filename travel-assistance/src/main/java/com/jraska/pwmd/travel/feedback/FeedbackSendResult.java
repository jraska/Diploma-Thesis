package com.jraska.pwmd.travel.feedback;

import lombok.ToString;

@ToString
public class FeedbackSendResult {
  //region Fields

  private final boolean _success;
  private final String _message;

  //endregion

  //region Constructors

  FeedbackSendResult(boolean success, String message) {
    _success = success;
    _message = message;
  }

  //endregion

  //region Properties

  public boolean isSuccess() {
    return _success;
  }

  public String getMessage() {
    return _message;
  }

  //endregion
}
