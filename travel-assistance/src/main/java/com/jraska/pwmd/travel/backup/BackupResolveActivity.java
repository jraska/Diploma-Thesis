package com.jraska.pwmd.travel.backup;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import butterknife.OnTouch;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.ui.BaseActivity;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import javax.inject.Inject;

public class BackupResolveActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

  //region Constants

  private static final String EXTRA_REQUEST_CODE = "requestCode";

  public static final int REQUEST_CODE_BACKUP = 311;
  public static final int REQUEST_CODE_RESTORE = 23123;

  private static final int REQUEST_CODE_RESOLUTION = 1213;

  public static final int RESULT_BACKUP_SUCCESS = RESULT_OK;
  public static final int RESULT_DRIVE_ERROR = 2;
  public static final int RESULT_DRIVE_DECLINED = 3;
  public static final int RESULT_UNKNOWN_ERROR = 4;

  //endregion

  //region Fields

  @Inject DriveBackupManager _backupManager;

  private GoogleApiClient _driveClient;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_backup_availability_resolve);

    TravelAssistanceApp.getComponent(this).inject(this);

    _driveClient = createClient();
    _driveClient.connect();
    onActionStarted();
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode,
                                  Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (requestCode == REQUEST_CODE_RESOLUTION && resultCode == RESULT_OK) {
      _driveClient.connect();
    } else {
      setResult(RESULT_DRIVE_DECLINED);
      finish();
    }
  }

  @Override
  protected void onDestroy() {
    if (_driveClient != null) {
      _driveClient.disconnect();
    }

    super.onDestroy();
  }

  //endregion

  //region Google client callbacks impl

  @Override public void onConnected(@Nullable Bundle bundle) {
    Timber.d("Drive API connected");
    if (isFinishing()) {
      return;
    }

    if (startedForBackup()) {
      Timber.i("Starting data backup");
      doBackup();
    } else {
      Timber.i("Starting data restore");
      doRestore();
    }
  }

  @Override public void onConnectionSuspended(int i) {
    Timber.w("Drive API Suspended: %d", i);
  }

  @Override
  public void onConnectionFailed(@NonNull ConnectionResult result) {
    Timber.w("Connection failed %s", result);

    if (!result.hasResolution()) {
      Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0);
      setResult(RESULT_DRIVE_ERROR);
      errorDialog.setOnCancelListener(dialog -> finish());
      errorDialog.setCanceledOnTouchOutside(false);
      errorDialog.setOnCancelListener(dialog -> finish());
      errorDialog.setOnDismissListener(dialog -> finish());
      errorDialog.show();
      return;
    }
    try {
      result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
    }
    catch (IntentSender.SendIntentException e) {
      Timber.e(e, "Exception while starting resolution activity for result %s", result);
      setResult(RESULT_DRIVE_ERROR);
      finish();
    }
  }

  //endregion

  //region Methods

  private GoogleApiClient createClient() {
    return new GoogleApiClient.Builder(this)
        .addConnectionCallbacks(this)
        .addOnConnectionFailedListener(this)
        .addApi(com.google.android.gms.drive.Drive.API)
        .addScope(com.google.android.gms.drive.Drive.SCOPE_APPFOLDER)
        .build();
  }

  @OnTouch(R.id.backup_resolve_root) boolean onRootTouch() {
    finish();
    return true;
  }

  private boolean startedForBackup() {
    return REQUEST_CODE_BACKUP == getIntent().getIntExtra(EXTRA_REQUEST_CODE, REQUEST_CODE_BACKUP);
  }

  private void doRestore() {
    _backupManager.restoreFromBackup(_driveClient)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new ActionSubscriber());
  }

  private void doBackup() {
    _backupManager.createBackup(_driveClient)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new ActionSubscriber());
  }

  private void onActionStarted() {
    showProgressDialog();
  }

  private void showProgressDialog() {
    String title;
    String message;
    if (startedForBackup()) {
      title = getString(R.string.backup_dialog_progress_title);
      message = getString(R.string.backup_dialog_progress_message);
    } else {
      title = getString(R.string.restore_dialog_progress_title);
      message = getString(R.string.restore_dialog_progress_message);
    }

    BackupProgressDialog progressDialog = BackupProgressDialog.newInstance(title, message);
    progressDialog.show(getSupportFragmentManager(), BackupProgressDialog.DIALOG_TAG);
  }

  private void onActionFinished(boolean success) {
    hideProgressDialog();

    if (success) {
      Timber.i("Backup action completed");
      setResult(RESULT_BACKUP_SUCCESS);
    } else {
      Timber.w("Resolving backup was unsuccessful");
      setResult(RESULT_UNKNOWN_ERROR);
    }
    finish();
  }

  private void hideProgressDialog() {
    Fragment progressDialog = getSupportFragmentManager().findFragmentByTag(BackupProgressDialog.DIALOG_TAG);
    if (progressDialog instanceof BackupProgressDialog) {
      ((BackupProgressDialog) progressDialog).dismiss();
    }
  }

  public static void startForRestore(Fragment fromFragment, int requestCode) {
    start(fromFragment, requestCode);
  }

  public static void startForBackup(Fragment fromFragment, int requestCode) {
    start(fromFragment, requestCode);
  }

  private static void start(Fragment fromFragment, int requestCode) {
    Intent intent = new Intent(fromFragment.getContext(), BackupResolveActivity.class);
    intent.putExtra(EXTRA_REQUEST_CODE, requestCode);
    fromFragment.startActivityForResult(intent, requestCode);
  }

  //endregion

  //region Nested classes

  class ActionSubscriber extends Subscriber<Boolean> {
    @Override public void onCompleted() {
      Timber.v("Completed");
      if (!isUnsubscribed()) {
        unsubscribe();
      }
    }

    @Override public void onError(Throwable e) {
      Timber.w(e, "Backup operation unsuccessful");
      onActionFinished(false);
    }

    @Override public void onNext(Boolean success) {
      onActionFinished(success);
    }
  }

  //endregion
}
