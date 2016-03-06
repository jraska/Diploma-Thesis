package com.jraska.pwmd.travel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.backup.BackupResolveActivity;
import com.jraska.pwmd.travel.settings.SettingsManager;
import timber.log.Timber;

import javax.inject.Inject;

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

  @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_BACKUP) {
      handleBackupRequestResult(resultCode);
    } else if (requestCode == REQUEST_CODE_RESTORE) {
      handleBackupRequestResult(resultCode);
    }
  }

  //endregion

  //region Methods

  @OnClick(R.id.settings_make_backup) void makeBackup() {
    BackupResolveActivity.startForBackup(this, REQUEST_CODE_BACKUP);
  }

  @OnClick(R.id.settings_make_restore) void restore() {
    BackupResolveActivity.startForRestore(this, REQUEST_CODE_RESTORE);
  }

  private void handleBackupRequestResult(int resultCode) {
    switch (resultCode) {
      case BackupResolveActivity.RESULT_BACKUP_SUCCESS:
        Timber.d("Backup successful");
        break;
      case BackupResolveActivity.RESULT_DRIVE_DECLINED:
        Timber.d("Drive result declined, check connection or accept the request");
        break;
      case BackupResolveActivity.RESULT_DRIVE_ERROR:
      case BackupResolveActivity.RESULT_UNKNOWN_ERROR:
        Timber.w("Issue on backup code: %d", resultCode);
        break;

      default:
        Timber.w("Unknown backup request result: %d", resultCode);
    }
  }

  //endregion
}
