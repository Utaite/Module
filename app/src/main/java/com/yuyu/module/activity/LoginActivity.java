package com.yuyu.module.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.trello.rxlifecycle.components.support.RxAppCompatActivity;
import com.yuyu.module.R;
import com.yuyu.module.chain.ChainedArrayList;
import com.yuyu.module.chain.ChainedToast;
import com.yuyu.module.utils.Constant;
import com.yuyu.module.utils.MainParcel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.subjects.PublishSubject;

public class LoginActivity extends RxAppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private ChainedToast toast;

    @BindView(R.id.login_id_edit)
    AutoCompleteTextView login_id_edit;
    @BindView(R.id.login_pw_edit)
    EditText login_pw_edit;
    @BindView(R.id.login_check_btn)
    AppCompatCheckBox login_check_btn;
    @BindView(R.id.login_save_btn)
    AppCompatCheckBox login_save_btn;
    @BindView(R.id.login_login_btn)
    Button login_login_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        toast = new ChainedToast(this).makeTextTo(this, "", Toast.LENGTH_SHORT);
        initialize();
    }

    @Override
    public void onBackPressed() {
        if (Constant.CURRENT_TIME + Constant.BACK_TIME < System.currentTimeMillis()) {
            Constant.CURRENT_TIME = System.currentTimeMillis();
            toast.setTextShow(getString(R.string.onBackPressed));

        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.login_login_btn)
    public void onLogin(View view) {
        loginPrepare();
    }

    @OnClick({R.id.login_check_btn, R.id.login_check_txt})
    public void onCheck(View view) {
        if (view.getId() == R.id.login_check_txt) {
            login_check_btn.setChecked(!login_check_btn.isChecked());
        }
        login_save_btn.setChecked(false);
    }

    @OnClick({R.id.login_save_btn, R.id.login_save_txt})
    public void onSave(View view) {
        if (view.getId() == R.id.login_save_txt) {
            login_save_btn.setChecked(!login_save_btn.isChecked());
        }
        login_check_btn.setChecked(false);
    }

    public void initialize() {
    }

    public void loginPrepare() {
        start(getString(login_id_edit), Integer.parseInt(getString(login_pw_edit)));
    }

    public String getString(EditText editText) {
        return editText.getText().toString().trim();
    }

    public void start(String name, int age) {
        startActivity(Henson.with(this)
                .gotoMainActivity()
                .mainParcel(new MainParcel(name, age))
                .build());
    }
}
