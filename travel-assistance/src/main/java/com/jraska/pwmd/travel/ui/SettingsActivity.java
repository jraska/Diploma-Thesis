package com.jraska.pwmd.travel.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

  //region Constants

  public static final DateFormat USER_FORMAT = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);

  //endregion

  //region Fields

  @Bind(R.id.settings_assistant_email) EditText _assistantEmailText;
  @Bind(R.id.settings_assistant_phone) EditText _assistantPhoneText;
  @Bind(R.id.settings_make_backup) View _backupView;
  @Bind(R.id.settings_make_restore_time) TextView _restoreTime;

  @Inject SettingsManager _settingsManager;
  @Inject BackupChecker _backupChecker;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);

    TravelAssistanceApp.getComponent(this).inject(this);

    _assistantEmailText.setText(_settingsManager.getAssistantEmail());
    _assistantPhoneText.setText(_settingsManager.getAssistantPhone());

    refreshBackupViewVisibility();
  }

  @Override protected void onResume() {
    super.onResume();

    refreshLastBackupTime();
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
      _settingsManager.setLastBackupTime(new Date());
    } else if (requestCode == REQUEST_CODE_RESTORE) {
      handleBackupRequestResult(resultCode);
    }
  }

  //endregion

  //region Methods

  private void refreshBackupViewVisibility() {
    _backupChecker.isAnythingToBackup()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onAnythingToBackup);
  }

  private void onAnythingToBackup(boolean anythingToBackup) {
    Timber.d("Anything to backup = %s", anythingToBackup);
    if (anythingToBackup) {
      _backupView.setVisibility(View.VISIBLE);
    } else {
      _backupView.setVisibility(View.GONE);
    }
  }

  protected void refreshLastBackupTime() {
    _backupChecker.getLastBackupDate()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onNewLastBackupDate);
  }

  private void onNewLastBackupDate(Date backupTime) {
    Timber.i("New last backup time: %s", backupTime);

    if (backupTime == null) {
      _restoreTime.setVisibility(View.GONE);
    } else {
      _restoreTime.setVisibility(View.VISIBLE);
      String backupText = getString(R.string.settings_last_backup_time, format(backupTime));
      _restoreTime.setText(backupText);
    }
  }

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

  private static String format(Date date) {
    synchronized (USER_FORMAT) {
      return USER_FORMAT.format(date);
    }
  }

  //endregion
}
