package com.jraska.dialog.lambda;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public final class LambdaDialogFragment extends DialogFragment {
  public static final String TAG = LambdaDialogFragment.class.getSimpleName();

  private static final String DIALOG_FACTORY = "factory";

  public static Builder builder(FragmentActivity fragmentActivity) {
    if (fragmentActivity == null) {
      throw new IllegalArgumentException("fragmentActivity cannot be null");
    }

    return new Builder(fragmentActivity);
  }

  private final DialogFieldsBundleAdapter _fieldsAdapter = DialogFieldsBundleAdapter.INSTANCE;

  DialogFields fields() {
    return _fieldsAdapter.fromBundle(getArguments());
  }

  DialogFactory factory() {
    return (DialogFactory) getArguments().getSerializable(DIALOG_FACTORY);
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return factory().onCreateDialog(new DialogFactory.FactoryData(getActivity(), fields()));
  }

  private LambdaDialogFragment show(String tag, FragmentManager fragmentManager) {
    show(fragmentManager, tag);
    return this;
  }

  public LambdaDialogFragment show(FragmentManager fragmentManager) {
    return show(TAG, fragmentManager);
  }

  public static class Builder {
    private final FragmentActivity _fragmentActivity;
    private final DialogFieldsBundleAdapter _fieldsBundleAdapter;
    private final DialogFields.Builder _fieldsBuilder;

    private boolean validateEagerly;
    private DialogFactory _dialogFactory = new AlertDialogFactory();

    private Builder(FragmentActivity fragmentActivity) {
      _fragmentActivity = fragmentActivity;
      _fieldsBundleAdapter = DialogFieldsBundleAdapter.INSTANCE;
      _fieldsBuilder = DialogFields.builder();
    }

    private CharSequence string(@StringRes int res) {
      return _fragmentActivity.getString(res);
    }

    private FragmentManager fragmentManager() {
      return _fragmentActivity.getSupportFragmentManager();
    }

    public Builder validateEagerly(boolean validate) {
      validateEagerly = validate;
      return this;
    }

    public void setDialogFactory(@NonNull DialogFactory dialogFactory) {
      _dialogFactory = dialogFactory;
    }

    public Builder setIcon(@DrawableRes int res) {
      _fieldsBuilder.iconRes(res);
      return this;
    }

    public Builder setTitle(CharSequence message) {
      _fieldsBuilder.title(message);
      return this;
    }

    public Builder setTitle(@StringRes int res) {
      return setTitle(string(res));
    }

    public Builder setCancelable(boolean cancelable) {
      _fieldsBuilder.cancelable(cancelable);
      return this;
    }

    public Builder setMessage(CharSequence message) {
      _fieldsBuilder.message(message);
      return this;
    }

    public Builder setMessage(@StringRes int res) {
      return setMessage(string(res));
    }

    public <A extends FragmentActivity> Builder setPositiveMethod(ActivityAction<A> method) {
      _fieldsBuilder.positiveAction(method);
      return this;
    }

    public Builder setPositiveText(CharSequence text) {
      _fieldsBuilder.positiveText(text);
      return this;
    }

    public Builder setPositiveText(@StringRes int res) {
      return setPositiveText(string(res));
    }

    public <A extends FragmentActivity> Builder setNeutralMethod(ActivityAction<A> method) {
      _fieldsBuilder.neutralAction(method);
      return this;
    }

    public Builder setNeutralText(CharSequence text) {
      _fieldsBuilder.negativeText(text);
      return this;
    }

    public Builder setNeutralText(@StringRes int res) {
      return setNeutralText(string(res));
    }

    public <A extends FragmentActivity> Builder setNegativeMethod(ActivityAction<A> method) {
      _fieldsBuilder.negativeAction(method);
      return this;
    }

    public Builder setNegativeText(@StringRes int res) {
      return setNegativeText(string(res));
    }

    public Builder setNegativeText(CharSequence text) {
      _fieldsBuilder.negativeText(text);
      return this;
    }

    public LambdaDialogFragment build() {
      DialogFields dialogFields = _fieldsBuilder.build();

      if (validateEagerly) {
        DialogFields.validateSerializable(_dialogFactory);
        dialogFields.validate();
      }

      Bundle arguments = new Bundle();
      arguments.putSerializable(DIALOG_FACTORY, _dialogFactory);
      _fieldsBundleAdapter.intoBundle(dialogFields, arguments);

      LambdaDialogFragment fragment = new LambdaDialogFragment();
      fragment.setArguments(arguments);
      return fragment;
    }

    public LambdaDialogFragment show() {
      return build().show(fragmentManager());
    }

    public LambdaDialogFragment show(String tag) {
      return build().show(tag, fragmentManager());
    }
  }

}
