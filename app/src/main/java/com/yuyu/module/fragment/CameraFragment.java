package com.yuyu.module.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import com.trello.rxlifecycle.components.RxFragment;
import com.yuyu.module.R;
import com.yuyu.module.activity.MainActivity;
import com.yuyu.module.chain.Chained;
import com.yuyu.module.rest.RestUtils;
import com.yuyu.module.utils.Constant;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observable;

import static android.app.Activity.RESULT_OK;

public class CameraFragment extends RxFragment {

    private final String TAG = CameraFragment.class.getSimpleName();

    private File file;
    private Context context;

    private boolean isResult;

    @BindView(R.id.camera_camera_btn)
    Button camera_camera_btn;
    @BindView(R.id.camera_gallery_btn)
    Button camera_gallery_btn;
    @BindView(R.id.camera_submit_btn)
    Button camera_submit_btn;
    @BindView(R.id.camera_img)
    ImageView camera_img;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        ButterKnife.bind(this, view);
        context = getActivity();
        Chained.setVisibilityMany(View.GONE, camera_camera_btn, camera_gallery_btn, camera_submit_btn, camera_img);
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
                        Chained.setVisibilityMany(View.VISIBLE, camera_camera_btn, camera_gallery_btn, camera_submit_btn, camera_img);
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
        File dir = new File(Environment.getExternalStorageDirectory(), getString(R.string.app_name));
        Observable.just(dir)
                .compose(bindToLifecycle())
                .filter(file1 -> !file1.exists())
                .subscribe(File::mkdirs);

        try {
            String name = getString(R.string.camera_name) + new SimpleDateFormat(getString(R.string.camera_date_type)).format(new Date());
            file = File.createTempFile(name, getString(R.string.camera_file), dir);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (context.getPackageManager().queryIntentActivities(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), PackageManager.MATCH_DEFAULT_ONLY).size() > 0 && file != null) {
            Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).setData(uri));

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
            startActivityForResult(intent, Constant.CAMERA_REQUEST_CODE);
        } else {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.camera_none));
            file.delete();
        }
    }

    @OnClick(R.id.camera_gallery_btn)
    public void onGalleryButtonClick() {
        Intent intent = new Intent(Intent.ACTION_PICK).setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, Constant.GALLERY_REQUEST_CODE);
    }

    @OnClick(R.id.camera_submit_btn)
    public void onSubmitButtonClick() {
        if (file == null || camera_img == null) {
            ((MainActivity) context).getToast().setTextShow(getString(R.string.camera_file_none));
        } else {
            UploadTask uploadTask = new UploadTask();
            uploadTask.onPreExecute();

            String message = "message";
            MultipartBody.Part body = MultipartBody.Part.createFormData(getString(R.string.file), file.getName(), RequestBody.create(MediaType.parse(getString(R.string.multipart)), file));


            RestUtils.getRetrofit()
                    .create(RestUtils.Upload.class)
                    .upload(message, body)
                    .subscribe(o -> {
                                isResult = true;
                                uploadTask.onPostExecute(null);
                            },
                            e -> {
                                Log.e(TAG, e.toString());
                                isResult = false;
                                uploadTask.onPostExecute(null);
                            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            if (requestCode == Constant.CAMERA_REQUEST_CODE) {
                file.delete();
            }
            final int ALPHA = 255;
            Chained.setAlpha(ALPHA, camera_camera_btn, camera_submit_btn, camera_gallery_btn);
            camera_img.setImageResource(android.R.color.white);
            return;
        }

        switch (requestCode) {
            case Constant.GALLERY_REQUEST_CODE: {
                file = new File(getName(data.getData()));
                setImageBitmap();
            }
            break;

            case Constant.CAMERA_REQUEST_CODE: {
                setImageBitmap();
            }
            break;
        }

        final int ALPHA = 150;
        Chained.setAlpha(ALPHA, camera_camera_btn, camera_submit_btn, camera_gallery_btn);
    }

    public String getName(Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
    }

    public void setImageBitmap() {
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            int orientation = getRotate(new ExifInterface(file.getAbsolutePath()).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
            camera_img.setImageBitmap(getBitmap(bitmap, orientation));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    public int getRotate(int orientation) {
        return orientation == ExifInterface.ORIENTATION_ROTATE_90 ?
                90 : orientation == ExifInterface.ORIENTATION_ROTATE_180 ?
                180 : orientation == ExifInterface.ORIENTATION_ROTATE_270 ?
                270 : 0;
    }

    public Bitmap getBitmap(Bitmap bitmap, int degrees) {
        if (bitmap != null && degrees != 0) {
            Matrix m = new Matrix();
            m.setRotate(degrees, (float) bitmap.getWidth() / 2, (float) bitmap.getHeight() / 2);

            try {
                Bitmap converted = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (bitmap != converted) {
                    bitmap.recycle();
                    bitmap = converted;
                }
            } catch (Exception e) {
                Log.e(TAG, e.toString());
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
            ((MainActivity) context).getToast().setTextShow(getString(isResult ? R.string.camera_suc: R.string.camera_err));
        }
    }

}
