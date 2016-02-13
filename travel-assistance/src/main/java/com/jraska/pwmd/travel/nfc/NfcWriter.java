package com.jraska.pwmd.travel.nfc;

import android.content.Context;
import com.jraska.common.ArgumentCheck;

import javax.inject.Inject;

public class NfcWriter {
  //region Fields

  private final Context _context;

  //endregion

  //region Constructors

  @Inject
  public NfcWriter(Context context) {
    ArgumentCheck.notNull(context);

    _context = context;
  }

  //endregion
}
