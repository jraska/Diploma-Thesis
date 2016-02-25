package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.TextView;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.feedback.Feedback;
import com.jraska.pwmd.travel.help.EmailSender;

public class FeedbackActivity extends BaseActivity {

  //region Constants

  public static final int REQUEST_FEEDBACK = 1234;

  //endregion

  //region Fields

  @Bind(R.id.feedback_fab_send) FloatingActionButton _sendView;
  @Bind(R.id.feedback_title) TextView _titleTextView;
  @Bind(R.id.feedback_body) TextView _bodyText;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);
  }

  //endregion

  //region Methods

  @OnClick(R.id.feedback_fab_send) void sendFeedback() {
    if (!validate()) {
      return;
    }

    Feedback feedback = collectFeedback();
    _sendView.hide();
    EmailSender emailSender = new EmailSender(this);

    emailSender.sendEmail(getEmail(), feedback.getTitle(), feedback.getBody());

    finish();
  }

  @NonNull private String getEmail() {
    byte[] decode = Base64.decode(Base64.decode("Y21Gek1EQXlPVUIyYzJJdVkzbz0=", 0), 0);
    return new String(decode);
  }

  private Feedback collectFeedback() {
    Feedback feedback = new Feedback(_titleTextView.getText().toString(),
        _bodyText.getText().toString());

    return feedback;
  }

  private boolean validate() {
    if (TextUtils.isEmpty(_titleTextView.getText())) {
      _titleTextView.setError(getString(R.string.feedback_error_empty_title));
      return false;
    }

    return true;
  }

  //endregion

}
