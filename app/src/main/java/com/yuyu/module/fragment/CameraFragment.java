package com.yuyu.module.fragment;

import android.Manifest;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {

    private final String TAG = CameraFragment.class.getSimpleName();

    private final int CAMERA_REQUEST_CODE = 7888;

    private File file;
    private Context context;
    private String imagePath;

    @BindView(R.id.camera_btn)
    Button camera_btn;
    @BindView(R.id.camera_img)
    ImageView camera_img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        camera_btn.setVisibility(View.GONE);
        camera_img.setVisibility(View.GONE);
        requestPermission();
    }

    public void requestPermission() {
        new TedPermission(context)
                .setPermissionListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted() {
                        camera_btn.setVisibility(View.VISIBLE);
                        camera_img.setVisibility(View.VISIBLE);
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

    public void initialize() {
        File dir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name) + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            file = File.createTempFile(getString(R.string.camera_name) + new SimpleDateFormat(getString(R.string.camera_date_type)).format(new Date()), getString(R.string.camera_file), dir);
            imagePath = file.getAbsolutePath();
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(Uri.fromFile(new File(imagePath))));
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
    }

    @OnClick(R.id.camera_btn)
    public void onButtonClick() {
        if (context.getPackageManager().queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PackageManager.MATCH_DEFAULT_ONLY).size() > 0 && file != null) {
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)), CAMERA_REQUEST_CODE);
        } else {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.camera_none));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            Bitmap bitmap;
            try {
                bitmap = rotate(BitmapFactory.decodeFile(imagePath, options), getRotate(new ExifInterface(imagePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)));
                camera_img.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getRotate(int orientation) {
        return orientation == ExifInterface.ORIENTATION_ROTATE_90 ? 90 : orientation == ExifInterface.ORIENTATION_ROTATE_180 ? 180 : orientation == ExifInterface.ORIENTATION_ROTATE_270 ? 270 : 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (bitmap != null && degrees != 0) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2,
                    (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (Exception e) {
                Log.e(TAG, String.valueOf(e));
            }
        }
        return bitmap;
    }

}