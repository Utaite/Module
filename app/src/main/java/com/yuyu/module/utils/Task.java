package com.yuyu.module.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.yuyu.module.R;

public class Task extends AsyncTask<Void, Void, Void> {

    private String msg;
    private ProgressDialog dialog;

    public Task(Context context) {
        dialog = new ProgressDialog(context);
        msg = context.getString(R.string.dialog_msg);
    }


    @Override
    public void onPreExecute() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(msg);
        dialog.show();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        super.onPreExecute();
    }

    @Override
    public Void doInBackground(Void... arg0) {
        return null;
    }

    @Override
    public void onPostExecute(Void result) {
        dialog.dismiss();
        super.onPostExecute(result);
    }
}