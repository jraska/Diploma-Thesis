package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.feedback.Feedback;

public class FeedbackActivity extends BaseActivity {

  //region Constants

  public static final int REQUEST_FEEDBACK = 1234;

  //endregion

  //region Fields

  @BindView(R.id.feedback_fab_send) FloatingActionButton _sendView;
  @BindView(R.id.feedback_title) TextView _titleTextView;
  @BindView(R.id.feedback_body) TextView _bodyText;

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
    sendFeedback(feedback);

    _sendView.hide();


    finish();
  }

  private void sendFeedback(Feedback feedback) {
    // TODO: 25/02/16 Not implemented
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
