package com.jraska.pwmd.travel.feedback;

import rx.Observable;

public interface FeedbackService {
  Observable<FeedbackSendResult> sendFeedback(Feedback feedback);
}
