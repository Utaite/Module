package com.yuyu.module.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.yuyu.module.R;
import com.yuyu.module.utils.MainParcel;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialize();
    }

    public void initialize() {
        startActivity(Henson.with(this)
                .gotoMainActivity()
                .mainParcel(new MainParcel("송정환", 20))
                .build());
    }
}
