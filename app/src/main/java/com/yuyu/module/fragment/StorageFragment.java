package com.yuyu.module.fragment;

import android.Manifest;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.chain.Chained;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

public class StorageFragment extends RxFragment {

    private final String TAG = StorageFragment.class.getSimpleName();

    private Context context;
    private StorageReference reference;

    @BindView(R.id.storage_upload_btn)
    Button storage_upload_btn;
    @BindView(R.id.storage_download_btn)
    Button storage_download_btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_storage, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        Chained.setVisibilityMany(View.GONE, storage_upload_btn, storage_download_btn);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        requestPermission();
    }

    public void requestPermission() {
        new TedPermission(context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        Chained.setVisibilityMany(View.VISIBLE, storage_upload_btn, storage_download_btn);
                        initialize();
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    }
                })
                .setDeniedMessage(getString(R.string.permission_denied))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @OnClick(R.id.storage_upload_btn)
    public void onUploadButtonClick() {
        Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + "/" + "Music.mp3"));
        StorageReference music = reference.child("Test/" + file.getLastPathSegment());
        UploadTask uploadTask = music.putFile(file);
        uploadTask.addOnSuccessListener(taskSnapshot -> ((MainActivity) context).getToast().setTextShow(getString(R.string.storage_upload_suc)))
                .addOnFailureListener(e -> {
                    Log.e(TAG, e.toString());
                    ((MainActivity) context).getToast().setTextShow(getString(R.string.storage_upload_err));
                });

    }

    @OnClick(R.id.storage_download_btn)
    public void onDownloadButtonClick() {
        StorageReference music = reference.child("Test/1.mp3");
        File f = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name) + "/" + "Music.mp3");

        music.getFile(f).addOnSuccessListener(taskSnapshot -> ((MainActivity) context).getToast().setTextShow(getString(R.string.storage_download_suc)))
                .addOnFailureListener(e -> {
                    Log.e(TAG, e.toString());
                    ((MainActivity) context).getToast().setTextShow(getString(R.string.storage_download_err));
                });
    }

    public void initialize() {
        File dir = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        Observable.just(dir)
                .compose(bindToLifecycle())
                .filter(dir1 -> !dir1.exists())
                .subscribe(File::mkdirs);

        reference = FirebaseStorage.getInstance().getReferenceFromUrl(getString(R.string.storage_gs));
    }

}
