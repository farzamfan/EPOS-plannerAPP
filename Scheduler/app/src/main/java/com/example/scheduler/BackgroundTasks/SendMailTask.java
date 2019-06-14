package com.example.scheduler.BackgroundTasks;

/**
 * Created by warrens on 14.09.17.
 */

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Config;
import android.util.Log;

import com.example.scheduler.Mail.GMail;

@SuppressWarnings("rawtypes")
public class SendMailTask extends AsyncTask {

    private ProgressDialog statusDialog;
    private Activity sendMailActivity;

    public SendMailTask(Activity activity) {
        sendMailActivity = activity;

    }

    protected void onPreExecute() {
//        statusDialog = new ProgressDialog(sendMailActivity);
//        statusDialog.setMessage("Getting ready...");
//        statusDialog.setIndeterminate(false);
//        statusDialog.setCancelable(false);
//        statusDialog.show();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Object doInBackground(Object... args) {
        if(isNetworkAvailable()){
                try {
                    Log.i("SendMailTask", "About to instantiate GMail...");
                    publishProgress("Processing input....");
                    if (args[3].toString().contains("Desktop") || args[3].toString().contains("Survey")) {
                        GMail androidEmail = new GMail(args[0].toString(),
                                args[1].toString(), args[2].toString(), args[3].toString(),
                                args[4].toString());
                        publishProgress("Preparing mail message....");
                        androidEmail.createEmailMessage();
                        publishProgress("Sending email....");
                        androidEmail.sendEmail();
                        publishProgress("Email Sent.");
                        Log.i("SendMailTask", "Mail Sent.");
                    }
                } catch (Exception e) {
                    publishProgress(e.getMessage());
                    Log.e("SendMailTask", e.getMessage(), e);
                }
        }
        return null;
    }

    @Override
    public void onProgressUpdate(Object... values) {
        //statusDialog.setMessage(values[0].toString());

    }

    @Override
    public void onPostExecute(Object result) {
        //statusDialog.dismiss();
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) sendMailActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

