package com.example.scheduler.Notifications;

/**
 * Created by warrens on 08.08.17.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.PowerManager;


import com.example.scheduler.MainActivity;
import com.example.scheduler.R;

import java.io.FileInputStream;
import java.io.StringBufferInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static android.content.Context.POWER_SERVICE;

/**
 * Created by warrens on 19.07.17.
 */
//check if any notifications need to be given to the user depending on whether
    //A) they've used the app today.
    //B) A task for today has started.
public class NotificationService extends Service {
    private PowerManager.WakeLock mWakeLock;
    /**
     * Simply return null, since our Service will not be communicating with
     * any other components. It just does its work silently.
     */
    @Override
    public IBinder onBind(Intent intent) {
        System.out.print("called\n");
        return null;
    }
    /**
     * This is where we initialize. We call this when onStart/onStartCommand is
     * called by the system. We won't do anything with the intent here, and you
     * probably won't, either.
     */
    private void handleIntent(Intent intent) {
        // obtain the wake lock
        String TAG = "Noti";
        PowerManager pm = (PowerManager)getSystemService(POWER_SERVICE);
        mWakeLock =pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
        mWakeLock.acquire();
        // check the global background data setting
        ConnectivityManager cm = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);

        if(!cm.getBackgroundDataSetting()) {
            System.out.print("here\n");
            stopSelf();
            return;
        }
        // do the actual work, in a separate thread
        new PollTask(this).execute();
    }
    private class PollTask extends AsyncTask<Void, Void, Void> {
        /**
         * This is where YOU do YOUR work. There's nothing for me to write here
         * you have to fill this in. Make your HTTP request(s) or whatever it is
         * you have to do to get your updates in here, because this is run in a
         * separate thread
         */
        public Context context;
        public PollTask(Context c){
            context = c;
        }
        @Override protected
        Void doInBackground(Void... params) {
            String times = "";
            try {
                FileInputStream fis = context.openFileInput("timesToNotify.txt");
                StringBuilder builder = new StringBuilder();
                int ch;
                while((ch = fis.read()) != -1){
                    builder.append((char)ch);
                }
                fis.close();
                times = builder.toString();
            }catch(Exception e){
                e.printStackTrace();
            }


            Intent intent = new Intent(context, MainActivity.class);
            intent.putExtra("fragment","3");
            PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), intent, 0);
            String[] timesA = times.split(",");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            String fullTime = simpleDateFormat.format(new Date());
            String currentTime = fullTime.substring(11,16);
            for(int i = 0; i<timesA.length;i++){
                System.out.print("\nNotification Service:"+i+" "+currentTime+" "+timesA[i]+"\n");
                if(currentTime.equals(timesA[i])){
                    Notification noti = new Notification.Builder(context)
                            .setContentTitle("Your Plan is starting!")
                            .setContentText("Open Scheduler to check your plan")
                            .setContentIntent(pIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .build();
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    // hide the notification after its selected
                    noti.flags |= Notification.FLAG_AUTO_CANCEL;

                    notificationManager.notify(0, noti);
                }
            }
            return null;
        }
        /**
         * In here you should interpret whatever you fetched in doInBackground
         * and push any notifications you need to the status bar, using the
         * NotificationManager. I will not cover this here, go check the docs on
         * NotificationManager.
         *
         * What you HAVE to do is call stopSelf() after you've pushed your
         * notification(s). This will:
         * 1) Kill the service so it doesn't waste precious resources
         * 2) Call onDestroy() which will release the wake lock, so the device
         * can go to sleep again and save precious battery.
         */
        @Override
        protected void onPostExecute(Void result) {
            // handle your data
            stopSelf();
        }
    }
    /**
     * This is deprecated, but you have to implement it if you're planning on
     * supporting devices with an API level lower than 5 (Android 2.0).
     */
    @Override
    public void onStart(Intent intent,int startId) {
        handleIntent(intent);
    }
    /**
     * This is called on 2.0+ (API level 5 or higher). Returning
     * START_NOT_STICKY tells the system to not restart the service if it is
     * killed because of poor resource (memory/cpu) conditions.
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handleIntent(intent);
        return START_NOT_STICKY;
    }
    /**
     * In onDestroy() we release our wake lock. This ensures that whenever the
     * Service stops (killed for resources, stopSelf() called, etc.), the wake
     * lock will be released.
     */
    public void onDestroy() {
        super.onDestroy();
        mWakeLock.release();
    }
}
