package com.yuyu.module.fragment;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import com.yuyu.module.chain.Chained;
import com.yuyu.module.rest.RestUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Subscriber;

import static android.app.Activity.RESULT_OK;


public class CameraFragment extends Fragment {

    private final String TAG = CameraFragment.class.getSimpleName();

    private final int CAMERA_REQUEST_CODE = 7888;

    private File file;
    private Context context;
    private String imagePath;

    @BindView(R.id.camera_camera_btn)
    Button camera_camera_btn;
    @BindView(R.id.camera_submit_btn)
    Button camera_submit_btn;
    @BindView(R.id.camera_img)
    ImageView camera_img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        Chained.setVisibilityMany(View.GONE, camera_camera_btn, camera_submit_btn, camera_img);
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
                        Chained.setVisibilityMany(View.VISIBLE, camera_camera_btn, camera_submit_btn, camera_img);
                    }

                    @Override
                    public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                    }
                })
                .setDeniedMessage(getString(R.string.permission_denied))
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();
    }

    @OnClick(R.id.camera_camera_btn)
    public void onCameraButtonClick() {
        File dir = new File(Environment.getExternalStorageDirectory(), Environment.DIRECTORY_PICTURES + "/" + getString(R.string.app_name) + "/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        try {
            String name = getString(R.string.camera_name) + new SimpleDateFormat(getString(R.string.camera_date_type)).format(new Date());
            file = File.createTempFile(name.replace("-", ""), getString(R.string.camera_file), dir);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        if (context.getPackageManager().queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PackageManager.MATCH_DEFAULT_ONLY).size() > 0 && file != null) {
            Uri uri = Uri.fromFile(new File(imagePath = file.getAbsolutePath()));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(uri));
            startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file)), CAMERA_REQUEST_CODE);
        } else {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.camera_none));
        }
    }

    @OnClick(R.id.camera_submit_btn)
    public void onSubmitButtonClick() {
        if (file == null) {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.camera_file_none));
        } else {
            UploadTask uploadTask = new UploadTask();
            uploadTask.onPreExecute();

            RequestBody message = RequestBody.create(MediaType.parse("multipart/form-data"), "TEST");
            MultipartBody.Part body = MultipartBody.Part.createFormData("picture", file.getName(), RequestBody.create(MediaType.parse("multipart/form-data"), file));

            RestUtils.getRestClient()
                    .create(RestUtils.FileUploadService.class)
                    .upload(message, body)
                    .subscribe(new Subscriber<Void>() {
                        @Override
                        public void onCompleted() {
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.e(TAG, e.getMessage());
                        }

                        @Override
                        public void onNext(Void aVoid) {
                            uploadTask.onPostExecute(null);
                        }
                    });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            try {
                Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
                int orientation = getRotate(new ExifInterface(imagePath).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
                camera_img.setImageBitmap(rotate(bitmap, orientation));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
    }

    public int getRotate(int orientation) {
        return orientation == ExifInterface.ORIENTATION_ROTATE_90 ?
                90 : orientation == ExifInterface.ORIENTATION_ROTATE_180 ?
                180 : orientation == ExifInterface.ORIENTATION_ROTATE_270 ?
                270 : 0;
    }

    public Bitmap rotate(Bitmap bitmap, int degrees) {
        if (bitmap != null && degrees != 0) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);
            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return bitmap;
    }

    private class UploadTask extends AsyncTask<Void, Void, Void> {

        private ProgressDialog asyncDialog = new ProgressDialog(context);

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            asyncDialog.setMessage(getString(R.string.camera_ing));
            asyncDialog.setCancelable(false);
            asyncDialog.setCanceledOnTouchOutside(false);
            asyncDialog.show();
        }

        @Override
        public Void doInBackground(Void... arg0) {
            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            asyncDialog.dismiss();
            ((MainActivity) context).getToast().setTextShow(getString(R.string.camera_complete));
        }
    }

}