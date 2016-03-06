package com.jraska.pwmd.travel.backup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.settings.SettingsManager;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import javax.inject.Inject;
import java.text.DateFormat;
import java.util.Date;

import static com.jraska.pwmd.travel.backup.BackupResolveActivity.REQUEST_CODE_BACKUP;
import static com.jraska.pwmd.travel.backup.BackupResolveActivity.REQUEST_CODE_RESTORE;

public class BackupFragment extends Fragment {
  //region Constants

  public static final DateFormat USER_FORMAT = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.MEDIUM);

  //endregion


  //region Fields

  @Bind(R.id.settings_make_backup) View _backupView;
  @Bind(R.id.settings_make_restore_time) TextView _restoreTime;

  @Inject BackupChecker _backupChecker;
  @Inject SettingsManager _settingsManager;

  //endregion

  //region Constructors

  public BackupFragment() {
    setRetainInstance(true);
  }

  //endregion

  //region Fragment overrides

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TravelAssistanceApp.getComponent(getContext()).inject(this);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_backup, container);

    ButterKnife.bind(this, view);
    refreshBackupViewVisibility();

    return view;
  }

  @Override public void onResume() {
    super.onResume();

    refreshLastBackupTime();
  }

  @Override public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == REQUEST_CODE_BACKUP) {
      handleBackupRequestResult(resultCode);
    } else if (requestCode == REQUEST_CODE_RESTORE) {
      handleRestoreRequestResult(resultCode);
    }
  }

  //endregion

  //region Methods

  private void saveDataBackedUpTime() {
    _settingsManager.setLastBackupTime(new Date());
  }

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
    BackupResolveActivity.startForBackup(getActivity(), REQUEST_CODE_BACKUP);
  }

  @OnClick(R.id.settings_make_restore) void restore() {
    BackupResolveActivity.startForRestore(getActivity(), REQUEST_CODE_RESTORE);
  }

  private void handleBackupRequestResult(int resultCode) {
    switch (resultCode) {
      case BackupResolveActivity.RESULT_BACKUP_SUCCESS:
        Timber.d("Backup successful");
        saveDataBackedUpTime();
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
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

  private void handleRestoreRequestResult(int resultCode) {
    switch (resultCode) {
      case BackupResolveActivity.RESULT_BACKUP_SUCCESS:
        Timber.d("Backup successful");
        saveDataBackedUpTime();
        Toast.makeText(getContext(), "Success", Toast.LENGTH_SHORT).show();
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
