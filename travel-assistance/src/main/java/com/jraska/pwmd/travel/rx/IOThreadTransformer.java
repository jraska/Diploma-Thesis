package com.jraska.pwmd.travel.rx;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class IOThreadTransformer<T> implements Observable.Transformer<T, T> {
  private static final IOThreadTransformer INSTANCE = new IOThreadTransformer();

  private IOThreadTransformer() {
  }

  @Override public Observable<T> call(Observable<T> observable) {
    return observable.subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread());
  }

  @SuppressWarnings("unchecked")
  public static <T> Observable.Transformer<T, T> get() {
    return INSTANCE;
  }
}
