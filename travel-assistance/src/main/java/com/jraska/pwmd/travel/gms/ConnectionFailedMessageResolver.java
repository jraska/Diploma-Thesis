package com.jraska.pwmd.travel.gms;

import android.content.Context;
import android.support.annotation.NonNull;
import com.google.android.gms.common.ConnectionResult;
import com.jraska.common.ArgumentCheck;
import com.jraska.pwmd.travel.R;

import javax.inject.Inject;

// TODO: 23/02/16 Better messages
public class ConnectionFailedMessageResolver {
  //region Fields

  private final Context _context;

  //endregion

  //region Constructors

  @Inject
  public ConnectionFailedMessageResolver(Context context) {
    ArgumentCheck.notNull(context);

    _context = context;
  }

  //endregion

  //region Methods

  public String resolveTitle(@NonNull ConnectionResult connectionResult) {
    return _context.getString(R.string.google_services_error_title);
  }

  public String resolveMessage(@NonNull ConnectionResult connectionResult) {
    return _context.getString(R.string.google_services_error_default_message, connectionResult);
  }

  //endregion
}
