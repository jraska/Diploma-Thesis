package com.jraska.pwmd.travel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.backup.BackupChecker;
import com.jraska.pwmd.travel.backup.BackupResolveActivity;
import com.jraska.pwmd.travel.settings.SettingsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import javax.inject.Inject;
import java.text.DateFormat;
import java.util.Date;

import static com.jraska.pwmd.travel.backup.BackupResolveActivity.REQUEST_CODE_BACKUP;
import static com.jraska.pwmd.travel.backup.BackupResolveActivity.REQUEST_CODE_RESTORE;

public class SettingsActivity extends BaseActivity {
  //region Fields

  @Bind(R.id.settings_assistant_email) EditText _assistantEmailText;
  @Bind(R.id.settings_assistant_phone) EditText _assistantPhoneText;

  @Inject SettingsManager _settingsManager;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    TravelAssistanceApp.getComponent(this).inject(this);

    _assistantEmailText.setText(_settingsManager.getAssistantEmail());
    _assistantPhoneText.setText(_settingsManager.getAssistantPhone());
  }

  @Override
  protected void onDestroy() {
    _settingsManager.setAssistantEmail(_assistantEmailText.getText().toString());
    _settingsManager.setAssistantPhone(_assistantPhoneText.getText().toString());

    super.onDestroy();
  }

  //endregion
}
