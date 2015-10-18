package com.jraska.common.exceptions;

/**
 * Base class for all exceptions
 */
public class JRRuntimeException extends RuntimeException {
  //region Constants

  private static final long serialVersionUID = -3822107985486075772L;

  //endregion

  //region Constructors

  public JRRuntimeException() {
  }

  public JRRuntimeException(String detailMessage) {
    super(detailMessage);
  }

  public JRRuntimeException(String detailMessage, Throwable throwable) {
    super(detailMessage, throwable);
  }

  public JRRuntimeException(Throwable throwable) {
    super(throwable);
  }

  //endregion
}
