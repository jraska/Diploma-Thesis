package com.jraska.dialog.lambda;

import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

public final class LambdaDialogFragment extends DialogFragment {
  public static final String TAG = LambdaDialogFragment.class.getSimpleName();

  static final String DIALOG_FACTORY = "factory";

  public static Builder builder(FragmentActivity context) {
    return new Builder(context.getResources());
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

  public LambdaDialogFragment show(FragmentManager fragmentManager) {
    show(fragmentManager, TAG);
    return this;
  }

  public static class Builder {
    private final Resources _resources;
    private final DialogFieldsBundleAdapter _fieldsBundleAdapter;
    private final DialogFields.Builder _fieldsBuilder;

    private boolean validateEagerly;
    private DialogFactory _dialogFactory = new AlertDialogFactory();

    private Builder(Resources resources) {
      _resources = resources;
      _fieldsBundleAdapter = DialogFieldsBundleAdapter.INSTANCE;
      _fieldsBuilder = DialogFields.builder();
    }

    private CharSequence string(@StringRes int res) {
      return _resources.getString(res);
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

    private Builder positiveProvider(DialogDelegateProvider provider) {
      _fieldsBuilder.positiveProvider(provider);
      return this;
    }

    @SuppressWarnings("unchecked")
    public <A extends FragmentActivity> Builder setPositiveMethod(ActivityAction1<A> method) {
      return positiveProvider((activity) -> (d, w) -> method.call((A) activity));
    }

    public Builder setPositiveText(CharSequence text) {
      _fieldsBuilder.positiveText(text);
      return this;
    }

    public Builder setPositiveText(@StringRes int res) {
      return setPositiveText(string(res));
    }

    private Builder neutralProvider(DialogDelegateProvider provider) {
      _fieldsBuilder.neutralProvider(provider);
      return this;
    }

    @SuppressWarnings("unchecked")
    public <A extends FragmentActivity> Builder setNeutralMethod(ActivityAction1<A> method) {
      return neutralProvider((activity) -> (d, w) -> method.call((A) activity));
    }

    public Builder setNeutralText(CharSequence text) {
      _fieldsBuilder.negativeText(text);
      return this;
    }

    public Builder setNeutralText(@StringRes int res) {
      return setNeutralText(string(res));
    }

    private Builder negativeProvider(DialogDelegateProvider provider) {
      _fieldsBuilder.negativeProvider(provider);
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

    public LambdaDialogFragment show(FragmentManager fragmentManager) {
      return build().show(fragmentManager);
    }

    public LambdaDialogFragment show(FragmentManager fragmentManager, String tag) {
      LambdaDialogFragment dialog = build();
      dialog.show(fragmentManager, tag);
      return dialog;
    }
  }

}
