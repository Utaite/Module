package com.yuyu.module.fcm;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.yuyu.module.R;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        getSharedPreferences(getString(R.string.token), MODE_PRIVATE).edit().putString(
                getString(R.string.token), FirebaseInstanceId.getInstance().getToken()).apply();
    }

}