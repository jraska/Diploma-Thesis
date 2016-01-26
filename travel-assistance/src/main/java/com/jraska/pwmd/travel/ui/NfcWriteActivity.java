package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;

public class NfcWriteActivity extends BaseActivity {
  //region Constants

  public static final int REQUEST_CODE_WRITE_NFC = 2384; // random value

  //endregion

  //region Fields

  @Bind(R.id.nfc_write_info_text) TextView _messageView;
  @Bind(R.id.nfc_write_success_text) TextView _successView;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write_nfc);
    TravelAssistanceApp.getComponent(this).inject(this);

    onNfcTagWriteRequested();
  }

  //endregion

  //region Methods

  @OnClick(R.id.nfc_write_info_text) void onIconClicked() {
    //// TODO: 26/01/16 Example code here
    onNfcTagWritten();
  }

  protected void onNfcTagWriteRequested() {
    _messageView.setVisibility(View.VISIBLE);
    _successView.setVisibility(View.GONE);

  }

  protected void onNfcTagWritten() {
    _messageView.setVisibility(View.GONE);
    _successView.setVisibility(View.VISIBLE);

    setResult(RESULT_OK);
  }

  //endregion
}
