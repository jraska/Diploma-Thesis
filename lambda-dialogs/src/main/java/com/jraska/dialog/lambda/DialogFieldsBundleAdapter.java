package com.jraska.dialog.lambda;

import android.os.Bundle;

final class DialogFieldsBundleAdapter {
  private static final String TITLE = "title";
  private static final String MESSAGE = "message";
  private static final String ICON_RES = "setIcon";
  private static final String POSITIVE_PROVIDER = "positiveProvider";
  private static final String POSITIVE_TEXT = "positiveText";
  private static final String NEUTRAL_PROVIDER = "neutralMethod";
  private static final String NEUTRAL_TEXT = "neutralText";
  private static final String NEGATIVE_PROVIDER = "setNegativeMethod";
  private static final String NEGATIVE_TEXT = "negativeText";
  private static final String CANCELABLE = "setCancelable";

  public static final DialogFieldsBundleAdapter INSTANCE = new DialogFieldsBundleAdapter();

  void intoBundle(DialogFields fields, Bundle bundle) {
    bundle.putCharSequence(TITLE, fields.title);
    bundle.putCharSequence(MESSAGE, fields.message);
    bundle.putInt(ICON_RES, fields.iconRes);
    bundle.putSerializable(POSITIVE_PROVIDER, fields.positiveProvider);
    bundle.putCharSequence(POSITIVE_TEXT, fields.positiveText);
    bundle.putSerializable(NEUTRAL_PROVIDER, fields.neutralProvider);
    bundle.putCharSequence(NEUTRAL_TEXT, fields.neutralText);
    bundle.putSerializable(NEGATIVE_PROVIDER, fields.negativeProvider);
    bundle.putCharSequence(NEGATIVE_TEXT, fields.negativeText);
    bundle.putBoolean(CANCELABLE, fields.cancelable);
  }

  DialogFields fromBundle(Bundle bundle) {
    return DialogFields.builder()
        .title(bundle.getCharSequence(TITLE))
        .message(bundle.getCharSequence(MESSAGE))
        .iconRes(bundle.getInt(ICON_RES))
        .positiveProvider((DialogDelegateProvider) bundle.getSerializable(POSITIVE_PROVIDER))
        .positiveText(bundle.getCharSequence(POSITIVE_TEXT))
        .negativeProvider((DialogDelegateProvider) bundle.getSerializable(NEUTRAL_PROVIDER))
        .negativeText(bundle.getCharSequence(NEUTRAL_TEXT))
        .negativeProvider((DialogDelegateProvider) bundle.getSerializable(NEGATIVE_PROVIDER))
        .negativeText(bundle.getCharSequence(NEGATIVE_TEXT))
        .build();
  }
}
