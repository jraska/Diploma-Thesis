package com.jraska.pwmd.travel.feedback;

import retrofit2.http.Body;
import retrofit2.http.POST;
import rx.Observable;

interface GitHubIssuesApi {
  @POST("/repos/jraska/Diploma-Thesis/issues")
  Observable<GitHubIssueResponse> postIssue(@Body GitHubIssueRequest issueRequest);
}
