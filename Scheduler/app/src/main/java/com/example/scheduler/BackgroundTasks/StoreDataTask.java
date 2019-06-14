package com.example.scheduler.BackgroundTasks;

/**
 * Created by warrens on 08.08.17.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;
import com.example.schedulecreationlibrary.Action;
import com.example.schedulecreationlibrary.Schedule;
import com.example.scheduler.MainActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by warrens on 11.05.17.
 */

public class StoreDataTask extends AsyncTask<Schedule, Integer, String> {

    public Context context;
    public int progressChangedValue;
    private MainActivity activity;
    public String[] wattage;
    public Action[][] fullList;
    public StoreDataTask(Context c, int a, MainActivity main,String[] w){
        context = c;
        progressChangedValue = a;
        activity = main;
        wattage = w;
    }

    private String[] actionNames = {
            "Hob",
            "Oven",
            "TumbleDryer",
            "WashingMachine",
            "Computer",
            "Kettle",
            "DishWasher",
            "Shower"
    };
    protected void onPreExecute(){

    }
    //create a file for each device and schedule, indicating what times the device is on or off at every minute during the day the plan is for.
    protected String doInBackground(Schedule... list){
        long startTime = System.currentTimeMillis();
        Schedule lists = list[0];
        String display = "";

        if(progressChangedValue>1){
            fullList = lists.getTopNRankedSchedules(progressChangedValue);
        }else{
            fullList = lists.getTopNRankedSchedules(1);
        }
        //a[Schedule][Device][Whether on or off at this index converted to a time+1]
        StringBuilder displayBuilder = new StringBuilder(1000);
        for(int i = 0; i<fullList.length;i++){
            if(checkCancelled()){
                break;
            }
            for(int j = 0; j<fullList[i].length;j++){
                if(checkCancelled()){
                    break;
                }
                if(j<fullList[i].length-1){
                    displayBuilder.append(fullList[i][j].name+"\t"+fullList[i][j].getTimeString(fullList[i][j].windowStart)+"-"+fullList[i][j].getTimeString(fullList[i][j].windowEnd)+",");
                }
                else {
                    displayBuilder.append(fullList[i][j].name+"\t"+fullList[i][j].getTimeString(fullList[i][j].windowStart)+"-"+fullList[i][j].getTimeString(fullList[i][j].windowEnd));
                }
            }
            displayBuilder.append("\n");
        }
        display = displayBuilder.toString();
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        String TimingsFile = "timings.txt";
        try{
            FileOutputStream fos = activity.openFileOutput(TimingsFile,context.MODE_APPEND);
            String submit = elapsedTime+"\n";
            fos.write(submit.getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return display;
    }
    protected void onProgressUpdate(Integer... progress) {

    }
    protected void onPostExecute(String result) {
        if(!checkCancelled()){
            if(fullList.length>0){
                StringBuilder wattageBuilder = new StringBuilder(200);
                String wattages = "";
                for(int i =0;i<wattage.length;i++){
                    if(i<wattage.length-1){
                        wattageBuilder.append(wattage[i]+",");
                    }else{
                        wattageBuilder.append(wattage[i]);
                    }
                }
                wattages = wattageBuilder.toString();
                activity.setDisplay(result,wattages);
                activity.setW(wattage);
                activity.setFl(fullList);
                activity.choicesPopUp();
                String TimingsFile = "timings.txt";
                try{
                    FileInputStream fis = activity.openFileInput(TimingsFile);
                    int ti;
                    StringBuilder builder = new StringBuilder();
                    while((ti=fis.read())!=-1){
                        builder.append((char)ti);
                    }
                    System.out.print(builder.toString()+"\n");
                }catch(Exception e){
                    e.printStackTrace();
                }
                String toastString = "Tomorrow's Schedule Set";
                int durationOfToast = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastString, durationOfToast);
                toast.show();
            }else{
                String toastString = "No Schedules exist for this input";
                int durationOfToast = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(context, toastString, durationOfToast);
                toast.show();
            }
        }
    }

    public boolean checkCancelled(){
        return activity.checkTasksStop();
    }
}