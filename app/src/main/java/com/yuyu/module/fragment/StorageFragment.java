package com.yuyu.module.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class StorageFragment extends RxFragment {

    private final String TAG = StorageFragment.class.getSimpleName();

    private Context context;

    @BindView(R.id.storage_upload_btn)
    Button storage_upload_btn;
    @BindView(R.id.storage_download_btn)
    Button storage_download_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        return view;
    }

    @OnClick(R.id.storage_upload_btn)
    public void onUploadButtonClick(){

    }

    @OnClick(R.id.storage_download_btn)
    public void onDownloadButtonClick(){
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReferenceFromUrl(getString(R.string.storage_gs));
        StorageReference music = reference.child("Test/1.mp3");

        Observable.just(new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + "/"))
                .compose(bindToLifecycle())
                .filter(file1 -> !file1.exists())
                .subscribe(File::mkdirs);

        File f = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + "/" + "hanatan.mp3");
        music.getFile(f).addOnSuccessListener(taskSnapshot -> ((MainActivity) context).getToast().setTextShow(getString(R.string.storage_download_suc)))
                .addOnFailureListener(e -> {
                    Log.e(TAG, e.toString());
                    ((MainActivity) context).getToast().setTextShow(getString(R.string.storage_download_err));
                });
    }

}
