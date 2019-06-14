package com.example.scheduler.BackgroundTasks;

/**
 * Created by warrens on 08.08.17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.schedulecreationlibrary.Schedule;
import com.example.scheduler.MainActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by warrens on 11.05.17.
 */
public class createSchedulesTask extends AsyncTask<Schedule, Integer, Schedule> {
    public Context context;
    private MainActivity main;
    public int progressChangedValue;
    public String[] wattage;
    public createSchedulesTask(Context c, int progress, MainActivity m, String[] w){
        context = c;
        main = m;
        progressChangedValue = progress;
        wattage = w;
    }
    protected void onPreExecute(){
        String toastString = "Please wait...";
        int durationOfToast = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, toastString, durationOfToast);
        toast.show();
    }

    //call the Schedule methods to create and sort the possible plans.
    protected Schedule doInBackground(Schedule... lists){
        String TimingsFile = "timings.txt";
        String counterFile = "counter.txt";
        int number = 0;
        try{
            FileInputStream fis = main.openFileInput(counterFile);
            int ti;
            StringBuilder builder = new StringBuilder();
            while((ti=fis.read())!=-1){
                builder.append((char)ti);
            }
            number = Integer.parseInt(builder.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
        number++;
        try{
            FileOutputStream fos = main.openFileOutput(TimingsFile,context.MODE_APPEND);
            String submit = number+",";
            fos.write(submit.getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        Schedule list = lists[0];
        long startTime = System.currentTimeMillis();
        list.makeScheduleList();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        //Note, two freezes occur in this app. Both are caused by the construction and storing of the huge details String which contains all the combinations which can be upwards of 400,000 longs
        //This causes the phone to be out of memory available to the app, causing the app to freeze.
        list.compileCombinations();
        String details = list.getFinalDetails();
        try{
            FileOutputStream fos = main.openFileOutput(TimingsFile,context.MODE_APPEND);
            String submit = elapsedTime+",";
            fos.write(submit.getBytes());
            fos.close();
            fos = main.openFileOutput("details.txt",context.MODE_PRIVATE);
            fos.write(details.getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        startTime = System.currentTimeMillis();
        list.sortSchedulesByRating();
        stopTime = System.currentTimeMillis();
        elapsedTime = stopTime - startTime;
        try{
            FileOutputStream fos = main.openFileOutput(TimingsFile,context.MODE_APPEND);
            String submit = elapsedTime+",";
            fos.write(submit.getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
    protected void onProgressUpdate(Integer... progress) {

    }

    protected void onCancelled(Schedule result){

    }

    //store the possible plans.
    protected void onPostExecute(Schedule result) {
        if(!isCancelled()){
            Schedule[] pass = new Schedule[]{result};
            new StoreDataTask(context,progressChangedValue, main,wattage).execute(pass);
        }

    }
}
