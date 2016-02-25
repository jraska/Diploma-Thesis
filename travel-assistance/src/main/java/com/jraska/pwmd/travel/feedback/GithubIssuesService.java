package com.jraska.pwmd.travel.feedback;

import android.content.Context;
import com.jraska.common.ArgumentCheck;
import rx.Observable;
import timber.log.Timber;

class GitHubIssuesService implements FeedbackService {
  //region Fields

  private final Context _context;
  private final GitHubIssuesApi _gitHubIssuesApi;

  //endregion

  //region Constructors

  public GitHubIssuesService(Context context, GitHubIssuesApi gitHubIssuesApi) {
    ArgumentCheck.notNull(context);
    ArgumentCheck.notNull(gitHubIssuesApi);

    _context = context;
    _gitHubIssuesApi = gitHubIssuesApi;
  }

  //endregion

  //region FeedbackService impl

  @Override
  public Observable<FeedbackSendResult> sendFeedback(Feedback feedback) {
    GitHubIssueRequest request = createRequest(feedback);
    return _gitHubIssuesApi.postIssue(request)
        .map(this::successResult)
        .onErrorReturn(this::errorResult);
  }

  //endregion

  //region Methods

  private GitHubIssueRequest createRequest(Feedback feedback) {
    GitHubIssueRequest request = new GitHubIssueRequest();
    request.title = feedback.getTitle();
    request.body = feedback.getBody();

    return request;
  }

  private FeedbackSendResult successResult(GitHubIssueResponse response) {
    return new FeedbackSendResult(true, response.url);
  }

  private FeedbackSendResult errorResult(Throwable throwable) {
    Timber.w(throwable, "Error sending feedback.");
    return new FeedbackSendResult(false, "");
  }

  //endregion
}
