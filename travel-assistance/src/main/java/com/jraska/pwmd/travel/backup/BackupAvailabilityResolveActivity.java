package com.jraska.pwmd.travel.backup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import butterknife.OnTouch;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.ui.BaseActivity;

public class BackupAvailabilityResolveActivity extends BaseActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_backup_availability_resolve);
  }

  @OnTouch(R.id.backup_resolve_root) boolean onRootTouch() {
    finish();
    return true;
  }

  public static void start(Activity fromActivity) {
    Intent intent = new Intent(fromActivity, BackupAvailabilityResolveActivity.class);
    fromActivity.startActivity(intent);
  }
}
