package com.jraska.pwmd.travel.feedback;

import android.content.Context;
import com.jraska.dagger.PerApp;
import dagger.Module;
import dagger.Provides;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class FeedbackModule {
  //region Module Methods

  @Provides @PerApp
  FeedbackService provideFeedbackService(Context context, GitHubIssuesApi gitHubIssuesApi) {
    return new GitHubIssuesService(context, gitHubIssuesApi);
  }

  @Provides @PerApp GitHubIssuesApi provideGitHubIssuesApi() {
    Retrofit retrofit = new Retrofit.Builder()
        .baseUrl("https://api.github.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
        .build();

    GitHubIssuesApi issuesApi = retrofit.create(GitHubIssuesApi.class);
    return issuesApi;
  }

  //endregion
}
