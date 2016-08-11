package com.jraska.pwmd.travel.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jraska.pwmd.travel.R;

public final class TransportChangeDialog extends DialogFragment {
  //region Constants

  public static final String DIALOG_TAG = TransportChangeDialog.class.getSimpleName();
  public static final String ARG_CAPTION = "caption";
  public static final String ARG_ICON_RES = "icon";

  //endregion

  //region Fields

  LayoutInflater _inflater;

  @BindView(R.id.dialog_transport_change_caption) TextView _captionView;
  @BindView(R.id.dialog_transport_change_img) ImageView _iconView;

  //endregion

  //region Constructors

  public static TransportChangeDialog newInstance(String caption, int iconRes) {
    Bundle args = new Bundle();
    args.putString(ARG_CAPTION, caption);
    args.putInt(ARG_ICON_RES, iconRes);

    TransportChangeDialog dialog = new TransportChangeDialog();
    dialog.setArguments(args);
    return dialog;
  }

  //endregion

  //region Properties

  String getCaption() {
    return getArguments().getString(ARG_CAPTION);
  }

  int getIconRes() {
    return getArguments().getInt(ARG_ICON_RES);
  }

  //endregion

  //region DialogFragment overrides

  @NonNull @Override public Dialog onCreateDialog(Bundle savedInstanceState) {
    LayoutInflater inflater = LayoutInflater.from(getContext());
    View inflated = inflater.inflate(R.layout.dialog_transport_change, null);
    ButterKnife.bind(this, inflated);

    _captionView.setText(getCaption());
    _iconView.setImageResource(getIconRes());

    Dialog dialog = new Dialog(getContext());
    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    dialog.setContentView(inflated);

    return dialog;
  }

  //endregion
}
