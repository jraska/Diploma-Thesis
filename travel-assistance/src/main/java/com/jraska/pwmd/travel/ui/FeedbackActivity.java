package com.jraska.pwmd.travel.ui;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.TextUtils;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.Bind;
import butterknife.OnClick;
import com.jraska.pwmd.travel.R;
import com.jraska.pwmd.travel.TravelAssistanceApp;
import com.jraska.pwmd.travel.feedback.Feedback;
import com.jraska.pwmd.travel.feedback.FeedbackSendResult;
import com.jraska.pwmd.travel.feedback.FeedbackService;
import com.jraska.pwmd.travel.help.EmailSender;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

import javax.inject.Inject;

public class FeedbackActivity extends BaseActivity {

  //region Constants

  public static final int REQUEST_FEEDBACK = 1234;

  //endregion

  //region Fields

  @Inject FeedbackService _feedbackService;

  @Bind(R.id.feedback_fab_send) FloatingActionButton _sendView;
  @Bind(R.id.feedback_title) TextView _titleTextView;
  @Bind(R.id.feedback_body) TextView _bodyText;

  //endregion

  //region Activity overrides

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_feedback);

    TravelAssistanceApp.getComponent(this).inject(this);
  }

  //endregion

  //region Methods

  @OnClick(R.id.feedback_fab_send) void sendFeedback() {
    if (!validate()) {
      return;
    }

    Feedback feedback = collectFeedback();
    _sendView.hide();

    _feedbackService.sendFeedback(feedback)
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(this::onFeedbackResult);
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

  void onFeedbackResult(FeedbackSendResult result) {
    Timber.i("Received feedback result %s", result);
    if (result.isSuccess()) {
      // TODO: 24/02/16 show dialog with link and allow user to check Github issue
      setResult(RESULT_OK);
      finish();
    } else {
      onErrorResult();
    }
  }

  private void onErrorResult() {
    _sendView.show();
    Toast.makeText(this, R.string.feedback_error_send_error, Toast.LENGTH_LONG).show();
  }

  //endregion

}
