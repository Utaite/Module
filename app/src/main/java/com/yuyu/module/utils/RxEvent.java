package com.yuyu.module.utils;

import rx.Observable;
import rx.subjects.PublishSubject;

public class RxEvent {

    private static RxEvent instance;
    private PublishSubject<Object> subject;

    private RxEvent() {
        subject = PublishSubject.create();
    }

    public static RxEvent getInstance() {
        if (instance == null) {
            instance = new RxEvent();
        }
        return instance;
    }

    public void sendEvent(Object object) {
        subject.onNext(object);
    }

    public Observable<Object> getObservable() {
        return subject;
    }

}
