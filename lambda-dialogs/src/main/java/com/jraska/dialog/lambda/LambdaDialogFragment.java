package com.jraska.dialog.lambda;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import lombok.SneakyThrows;

import java.io.*;

public class LambdaDialogFragment extends DialogFragment {
  public static final String TAG = LambdaDialogFragment.class.getSimpleName();

  private static final String TITLE = "title";
  private static final String MESSAGE = "message";
  private static final String ICON_RES = "setIcon";
  private static final String POSITIVE_PROVIDER = "positiveProvider";
  private static final String POSITIVE_TEXT = "okText";
  private static final String NEUTRAL_PROVIDER = "neutralMethod";
  private static final String NEUTRAL_TEXT = "neutralText";
  private static final String NEGATIVE_PROVIDER = "setNegativeMethod";
  private static final String NEGATIVE_TEXT = "negativeText";
  private static final String CANCELABLE = "setCancelable";

  public static Builder builder(FragmentActivity context) {
    return new Builder(context.getResources());
  }

  CharSequence title() {
    return argument(TITLE);
  }

  CharSequence message() {
    return argument(MESSAGE);
  }

  int iconRes() {
    return argument(ICON_RES, 0);
  }

  DialogDelegateProvider positiveProvider() {
    return argument(POSITIVE_PROVIDER);
  }

  CharSequence okText() {
    return argument(POSITIVE_TEXT);
  }

  DialogDelegateProvider neutralProvider() {
    return argument(NEUTRAL_PROVIDER);
  }

  CharSequence neutralText() {
    return argument(NEUTRAL_TEXT);
  }

  DialogDelegateProvider negativeProvider() {
    return argument(NEGATIVE_PROVIDER);
  }

  CharSequence negativeText() {
    return argument(NEGATIVE_TEXT);
  }

  boolean cancelable() {
    return argument(CANCELABLE, false);
  }

  <T> T argument(String key, T defaultValue) {
    T value = argument(key);
    if (value == null) {
      return defaultValue;
    }

    return value;
  }

  @SuppressWarnings("unchecked") <T> T argument(String key) {
    return (T) getArguments().get(key);
  }

  @SuppressWarnings("unchecked")
  DialogInterface.OnClickListener delegate(DialogDelegateProvider provider) {
    if (provider == null) {
      return null;
    } else {
      return provider.delegate(getActivity());
    }
  }

  @NonNull @SuppressWarnings("unchecked")
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

    return builder.setTitle(title())
        .setMessage(message())
        .setIcon(iconRes())
        .setPositiveButton(okText(), delegate(positiveProvider()))
        .setNeutralButton(neutralText(), delegate(neutralProvider()))
        .setNegativeButton(negativeText(), delegate(negativeProvider()))
        .setCancelable(cancelable())
        .create();
  }

  public LambdaDialogFragment show(FragmentManager fragmentManager) {
    show(fragmentManager, TAG);
    return this;
  }

  public static class Builder {
    private final Resources _resources;
    private Bundle _bundle = new Bundle();
    private boolean validateEagerly;

    private Builder(Resources resources) {
      _resources = resources;
    }

    private CharSequence string(@StringRes int res) {
      return _resources.getString(res);
    }

    public Builder validateEagerly(boolean validate) {
      validateEagerly = validate;
      return this;
    }

    public Builder setIcon(@DrawableRes int res) {
      _bundle.putInt(ICON_RES, res);
      return this;
    }

    public Builder setTitle(CharSequence message) {
      _bundle.putCharSequence(TITLE, message);
      return this;
    }

    public Builder setTitle(@StringRes int res) {
      return setTitle(string(res));
    }

    public Builder setCancelable(boolean cancelable) {
      _bundle.putBoolean(CANCELABLE, cancelable);
      return this;
    }

    public Builder setMessage(CharSequence message) {
      _bundle.putCharSequence(MESSAGE, message);
      return this;
    }

    public Builder setMessage(@StringRes int res) {
      return setMessage(string(res));
    }

    public Builder positiveProvider(DialogDelegateProvider provider) {
      _bundle.putSerializable(POSITIVE_PROVIDER, provider);
      return this;
    }

    @SuppressWarnings("unchecked")
    public <A extends FragmentActivity> Builder setPositiveMethod(ActivityAction1<A> method) {
      return positiveProvider((activity) -> (d, w) -> method.call((A) activity));
    }

    public Builder setPositiveText(CharSequence text) {
      _bundle.putCharSequence(POSITIVE_TEXT, text);
      return this;
    }

    public Builder setPositiveText(@StringRes int res) {
      return setPositiveText(string(res));
    }

    private Builder neutralProvider(DialogDelegateProvider provider) {
      _bundle.putSerializable(NEUTRAL_PROVIDER, provider);
      return this;
    }

    @SuppressWarnings("unchecked")
    public <A extends FragmentActivity> Builder setNeutralMethod(ActivityAction1<A> method) {
      return neutralProvider((activity) -> (d, w) -> method.call((A) activity));
    }

    public Builder setNeutralText(CharSequence text) {
      _bundle.putCharSequence(NEUTRAL_TEXT, text);
      return this;
    }

    public Builder setNeutralText(@StringRes int res) {
      return setNeutralText(string(res));
    }

    private Builder negativeProvider(DialogDelegateProvider provider) {
      _bundle.putSerializable(NEGATIVE_PROVIDER, provider);
      return this;
    }

    @SuppressWarnings("unchecked")
    public <A extends FragmentActivity> Builder setNegativeMethod(ActivityAction1<A> method) {
      return negativeProvider((activity) -> (d, w) -> method.call((A) activity));
    }

    public Builder setNegativeText(@StringRes int res) {
      return setNegativeText(string(res));
    }

    public Builder setNegativeText(CharSequence text) {
      _bundle.putCharSequence(NEGATIVE_TEXT, text);
      return this;
    }

    public LambdaDialogFragment build() {
      if (validateEagerly) {
        eagerValidate();
      }

      LambdaDialogFragment fragment = new LambdaDialogFragment();
      fragment.setArguments(_bundle); // TODO: 14/07/16 Have fields, because the Bundle remains mutable
      return fragment;
    }

    private void eagerValidate() {
      validateSerializable(_bundle.getSerializable(NEUTRAL_PROVIDER));
      validateSerializable(_bundle.getSerializable(POSITIVE_PROVIDER));
      validateSerializable(_bundle.getSerializable(NEGATIVE_PROVIDER));
    }

    public LambdaDialogFragment show(FragmentManager fragmentManager) {
      return build().show(fragmentManager);
    }

    public LambdaDialogFragment show(FragmentManager fragmentManager, String tag) {
      LambdaDialogFragment dialog = build();
      dialog.show(fragmentManager, tag);
      return dialog;
    }

    private static void validateSerializable(Serializable serializable) {
      try {
        validateSerializableUnchecked(serializable);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @SneakyThrows
  private static void validateSerializableUnchecked(Serializable serializable) throws IOException {
    if (serializable == null) {
      return;
    }

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

    outputStream.writeObject(serializable);

    ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    Object deserialized = inputStream.readObject();

    if (deserialized == null) {
      throw new IllegalArgumentException(serializable.getClass() + " does not implement Serializable properly");
    }
  }
}
