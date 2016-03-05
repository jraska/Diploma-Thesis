package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.backup.BackupPackager;
import com.jraska.pwmd.travel.settings.SettingsManager;
import lombok.SneakyThrows;
import timber.log.Timber;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;

public class SettingsActivity extends BaseActivity {

  //region Fields

  @Bind(R.id.settings_assistant_email) EditText _assistantEmailText;
  @Bind(R.id.settings_assistant_phone) EditText _assistantPhoneText;

  @Inject SettingsManager _settingsManager;
  @Inject BackupPackager _backupPackager;

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

  //region Methods

  // FIXME: 05/03/16 Debug code
  static File _lastBackup;

  @OnClick(R.id.settings_make_backup) void makeBackup() {
    File backupFile = _backupPackager.createBackupFile();
    Timber.d("backup created to %s", backupFile.getAbsolutePath());

    _lastBackup = backupFile;
    Toast.makeText(this, backupFile.getAbsolutePath(), Toast.LENGTH_LONG).show();
  }

  @SneakyThrows
  @OnClick(R.id.settings_restore_backup) void restoreBackup() {
    Timber.d("Restoring backup %s", _lastBackup);
    if (_lastBackup == null) {
      return;
    }

    _backupPackager.restoreBackup(new FileInputStream(_lastBackup));

    Toast.makeText(this, "Restored " + _lastBackup.getAbsolutePath(), Toast.LENGTH_LONG).show();
  }

  //endregion
}
