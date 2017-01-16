package com.yuyu.module.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;


public class CallFragment extends RxFragment {

    private final String TAG = CallFragment.class.getSimpleName();

    private View view;
    private Context context;
    private Handler handler;

    @BindView(R.id.call_edit)
    AutoCompleteTextView call_edit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_call, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        initialize();
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(call_edit.getWindowToken(), 0);
    }

    public void initialize() {
        call_edit.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                call_edit.getText().clear();
                call_edit.clearFocus();
            }
        };
    }

    @OnClick(R.id.call_btn)
    public void onCallButtonClick() {
        Observable.just(call_edit)
                .compose(bindToLifecycle())
                .map(call_edit1 -> call_edit1.getText().toString())
                .filter(s -> {
                    if (TextUtils.isEmpty(s)) {
                        ((MainActivity) context).getToast().setTextShow(getString(R.string.call_empty));
                    }
                    return !TextUtils.isEmpty(s);
                })
                .subscribe(s -> {
                    startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(getString(R.string.call_intent) + s)));
                    handler.sendEmptyMessageDelayed(0, 500);
                });
    }

}
