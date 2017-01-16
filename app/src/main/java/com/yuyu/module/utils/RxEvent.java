package com.yuyu.module.utils;

import java.util.Map;

import rx.Observable;
import rx.subjects.PublishSubject;

public class RxEvent {

    private static RxEvent instance;
    private PublishSubject<Map<String, String>> subject;

    private RxEvent() {
        subject = PublishSubject.create();
    }

    public static RxEvent getInstance() {
        if (instance == null) {
            instance = new RxEvent();
        }
        return instance;
    }

    public void sendEvent(Map<String, String> map) {
        subject.onNext(map);
    }

    public Observable<Map<String, String>> getObservable() {
        return subject;
    }

}
