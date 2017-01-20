package com.yuyu.module.utils

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask

import com.yuyu.module.R

class Task(context: Context) : AsyncTask<Void, Void, Void>() {

    private val msg: String = context.getString(R.string.loading)
    private val dialog: ProgressDialog = ProgressDialog(context)

    public override fun onPreExecute() {
        super.onPreExecute()
        dialog.run {
            setProgressStyle(ProgressDialog.STYLE_SPINNER)
            setMessage(msg)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    public override fun doInBackground(vararg void: Void) = null

    public override fun onPostExecute(result: Void?) {
        super.onPostExecute(result)
        dialog.dismiss()
    }
}

/*
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
        msg = context.getString(R.string.loading);
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
        super.onPostExecute(result);
        dialog.dismiss();
    }
}*/
