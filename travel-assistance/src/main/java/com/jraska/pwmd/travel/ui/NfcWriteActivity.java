package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
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

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_write_nfc);
    TravelAssistanceApp.getComponent(this).inject(this);
  }

  //endregion

  //region Methods

  @OnClick(R.id.nfc_write_info_text) void onIconClicked() {
    showWritedText();
  }

  private void showWritedText() {
    _messageView.setText(R.string.nfc_write_success);
  }

  //endregion
}
