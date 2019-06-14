package com.example.scheduler;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.DialogFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import android.support.constraint.ConstraintLayout;
import android.util.DisplayMetrics;

import com.example.schedulecreationlibrary.Action;
import com.example.schedulecreationlibrary.Schedule;
import com.example.scheduler.BackgroundTasks.SendMailTask;
import com.example.scheduler.BackgroundTasks.createFilesTask;
import com.example.scheduler.BackgroundTasks.createSchedulesTask;
import com.example.scheduler.BackgroundTasks.parseableDataTask;
import com.example.scheduler.Interface.MyDialogCloseListener;
import com.example.scheduler.Notifications.NotificationService;
import com.example.scheduler.fragment.No_Network_Fragment;
import com.example.scheduler.fragment.addRemoveAppliance;
import com.example.scheduler.fragment.betterPlanPopUpFragment;
import com.example.scheduler.fragment.editApplianceSettings;
import com.example.scheduler.fragment.fragment_create;
import com.example.scheduler.fragment.surveyFragment;
import com.example.scheduler.fragment.tabsFragment;


public class MainActivity extends AppCompatActivity
        implements MyDialogCloseListener {
    Animation FabOpen, FabClose, FabRClockwise,FabRAntiClockwise;
    boolean isOpen = false;
    public static String display;
    public String list;
    private String fullTime;
    public String date;
    public String tomorrowsDate;
    public AsyncTask motherTask;
    public boolean tasksStop = false;
    public MainActivity me = this;
    public FloatingActionButton fabRevealFabs;
    public final String PREFS_NAME = "MyPrefsFile";
    public SharedPreferences settings;
    public View thisView ;
    public ArrayList<TextView> eventsAdded = new ArrayList<>();
    public int index = 0;
    public View mainLayoutView;
    public String[][][] p;
    public String[] w;

    private Action[][] fl;

    private String[] applianceNames = {
            "Hob",
            "Oven",
            "TumbleDryer",
            "WashingMachine",
            "Computer",
            "Kettle",
            "DishWasher",
            "Shower"
    };

    @SuppressLint({"NewAPI", "NewApi"})
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_bar_main);
        thisView= (View) findViewById(android.R.id.content);
        ScrollView scroll = (ScrollView) findViewById(R.id.scrollTable);
        focusOnView(scroll);
        mainLayoutView = (View) findViewById(R.id.table);
        final View layoutView = (View) findViewById(android.R.id.content);;
        final Context context = this;
        fabRevealFabs = (FloatingActionButton) findViewById(R.id.fabRevealFabs);
        fullTime = " ";
        date = " ";
        tomorrowsDate = " ";

        String counterFile = "counter.txt";

        File counterFil = new File(counterFile);

        if(!counterFil.exists()){
            try{
                FileOutputStream fos = this.openFileOutput(counterFile,MODE_PRIVATE);
                fos.write("0".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final LinearLayout textEdit = (LinearLayout) findViewById(R.id.fabEditAppliancesTextLayout);
        final LinearLayout textAddRemove = (LinearLayout) findViewById(R.id.fabAddRemoveAppliancesTextLayout);
        final LinearLayout textCreatePlan = (LinearLayout) findViewById(R.id.fabCreateTomorrowsPlanFabsTextLayout);
        FabOpen = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        FabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        FabRAntiClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_anticlockwise);
        FabRClockwise = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_clockwise);
        settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int MY_PERMISSIONS_REQUEST_STORAGE = 200;
        settings.edit().putBoolean("putMin", false).commit();
        settings.edit().putBoolean("putMax", false).commit();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        try{
            FileOutputStream fos = openFileOutput("tempChosenPlan.txt", Context.MODE_PRIVATE);
            fos.write(" ".getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        //If this is the first time the user is launching the application, the files that the application uses to store information and pass information from different parts
        // of the application need to be created.
        if(firstTimeStart()){
            System.out.print("First Time Launch\n");
            String[] perms = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
            int version = Build.VERSION.SDK_INT;
            if(version>=23){
                int permsRequestCode = MY_PERMISSIONS_REQUEST_STORAGE;
                requestPermissions(perms, permsRequestCode);
            }

            String appliancesEnabledData = "";
            String houseData = "House,Occupancy,Construction Year,Appliances Owned,Type,Size,Approximate Construction Year\n" +
                    "1,2,1975-1980,35,Detached,4 bed,1970 - 1979\n" +
                    "2,4,-,15,Semi-detached,3 bed,-\n" +
                    "3,2,1988,27,Detached,3 bed,1980 - 1989\n" +
                    "4,2,1850-1899,33,Detached,4 bed,Pre 1900s\n" +
                    "5,4,1878,44,Mid-terrace,4 bed,Pre 1900s\n" +
                    "6,2,2005,49,Detached,4 bed,2000 - 2009\n" +
                    "7,4,1965-1974,25,Detached,3 bed,1960 - 1969\n" +
                    "8,2,1966,35,Detached,2 bed,1960 - 1969\n" +
                    "9,2,1919-1944,24,Detached,3 bed,1920 - 1929\n" +
                    "10,4,1919-1944,31,Detached,3 bed,1920 - 1929\n" +
                    "11,1,1945-1964,25,Detached,3 bed,1950 - 1959\n" +
                    "12,3,1991-1995,26,Detached,3 bed,1990 - 1999\n" +
                    "13,4,post 2002,28,Detached,4 bed,2000 - 2010\n" +
                    "15,1,1965-1974,19,Semi-detached,3 bed,1960 - 1969\n" +
                    "16,6,1981-1990,48,Detached,5 bed,1980 - 1989\n" +
                    "17,3,mid 60s,22,Detached,3 bed,1960 - 1969\n" +
                    "18,2,1965-1974,34,Detached,3 bed,1960 - 1969\n" +
                    "19,4,1945-1964,26,Semi-detached,3 bed,1950 - 1959\n" +
                    "20,2,1965-1974,39,Detached,3 bed,1960 - 1969\n" +
                    "21,4,1981-1990,23,Detached,3 bed,1980 - 1989";
            //the app is being launched for first time, do something
            String lastSendPressDayDateFile = "lastSendPressDayDate.txt";
            display = null;
            fullTime= simpleDateFormat.format(new Date());
            date = fullTime.substring(0,10);
            fullTime = simpleDateFormat.format(new Date(((new Date()).getTime() + 86400000)));
            tomorrowsDate = fullTime.substring(0,10);
            //
            String fileName = "PastSchedules.txt";
            //This file is used to pass schedules to be displayed to be chosen by the user
            String tomorrowSchedule = "TomorrowSchedule.txt";
            String listAsItWas = "ActionInputList.txt";
            //This file stores the current date to test if the date has changed.
            String todayDate = "Date.txt";
            //This stores the date for tomorrow. This is used to check if the date is correct for the schedule for the next calendar day.
            String tomorrowDate = "TomorrowsDate.txt";
            //This was used to allow the user to use the output survey
            String password = "Password.txt";
            //This was used to allow the user to use the app and complete the initial survey
            String passwordMain = "PasswordMain.txt";
            //This is the file which contains to the wattages of the appliances of the houses of the REFIT data file
            String houseDataName = "houseData.txt";
            //This stores the plan the user chose to use for the next calendar day.
            String chosenPlanFile = "chosenPlan.txt";
            //This contains the users appliance wattages.
            String wattageFile = "wattagesFile.txt";
            //This stores the start times of today's schedule at which they're supposed to start using their tasks.
            String timesToNotifyFile = "timesToNotify.txt";
            //This stores what appliances the user told us they own.
            String appliancesEnabledDataFile = "appliancesEnabledDataFile.txt";
            //This file holds a count of how many times a user planned today.
            String countFile = "count.txt";
            //This stores the names of the appliances
            String applianceNamesFile = "applianceNames.txt";
            //This is used to store the plan that the EPOS system may suggest.
            String suggestedPlanFile = "suggestedPlan.txt";
            //This is used to store the schedule that is to be executed today.
            String todayScheduleFile = "TodaySchedule.txt";
            //This stores the results of the survey as the user answers the parts.
            String surveyResultsFiles = "surveyResults.txt";
            //This stores how far along the survey the user is.
            String surveyProgressFile = "surveyProgress.txt";
            //This stores whether or not to show the user the survey.
            String surveyCompleteFile = "surveyComplete.txt";
            try{
                FileOutputStream fos = this.openFileOutput(applianceNamesFile,MODE_APPEND);
                StringBuilder applianceEnableBuilder = new StringBuilder();
                for(int i = 0; i<applianceNames.length;i++){
                    String submit;
                    if(i<applianceNames.length-1){
                        submit = applianceNames[i]+",";
                        applianceEnableBuilder.append(applianceNames[i]+","+"false"+"\n");
                    }else{
                        submit = applianceNames[i];
                        applianceEnableBuilder.append(applianceNames[i]+","+"false");
                    }
                    appliancesEnabledData = applianceEnableBuilder.toString();
                    fos.write(submit.getBytes());
                }
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(surveyProgressFile,MODE_PRIVATE);
                fos.write("1".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(surveyCompleteFile,MODE_PRIVATE);
                fos.write("false".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(surveyResultsFiles,MODE_PRIVATE);
                fos.write(" ".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            try{
                FileOutputStream fos = this.openFileOutput(suggestedPlanFile,MODE_PRIVATE);
                fos.write(" ".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            try{
                FileOutputStream fos = this.openFileOutput("notifyPress.txt",MODE_PRIVATE);
                fos.write("true".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(countFile,MODE_PRIVATE);
                fos.write("0".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            String TimingsFile = "timings.txt";
            try{
                FileOutputStream fos = this.openFileOutput(TimingsFile,MODE_PRIVATE);
                fos.write("Make,Sort,Display,Store\n".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(appliancesEnabledDataFile,MODE_PRIVATE);
                fos.write(appliancesEnabledData.getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            try{
                FileOutputStream fos = this.openFileOutput(tomorrowSchedule,MODE_PRIVATE);
                fos.write(" ".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            try{
                FileOutputStream fos = this.openFileOutput(todayScheduleFile,MODE_PRIVATE);
                fos.write(" ".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            // first time task
            try{
                FileOutputStream fos = this.openFileOutput(timesToNotifyFile,MODE_PRIVATE);
                fos.write("--:--".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(wattageFile,MODE_PRIVATE);
                fos.write("Default,75,1000,3000,1350,1800,9000,2500,700".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(chosenPlanFile,MODE_PRIVATE);
                fos.write(" ".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fos = this.openFileOutput(lastSendPressDayDateFile,MODE_PRIVATE);
                fos.write("0".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fosPSched =this.openFileOutput(fileName, MODE_PRIVATE);
                fosPSched.write("0".getBytes());
                fosPSched.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fosPSched =this.openFileOutput(houseDataName, MODE_PRIVATE);
                fosPSched.write(houseData.getBytes());
                fosPSched.close();
            }catch(Exception e){
                e.printStackTrace();
            }

            try{
                FileOutputStream fosPass = this.openFileOutput(password, MODE_PRIVATE);
                fosPass.write("Password1234".getBytes());
                fosPass.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fosPassMain = this.openFileOutput(passwordMain, MODE_PRIVATE);
                fosPassMain.write("Password5678".getBytes());
                fosPassMain.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fosMin = this.openFileOutput("tempMin.txt", MODE_PRIVATE);
                fosMin.write("00:00".getBytes());
                fosMin.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fosMax = this.openFileOutput("tempMax.txt", MODE_PRIVATE);
                fosMax.write("23:59".getBytes());
                fosMax.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fosDate = this.openFileOutput(todayDate, MODE_PRIVATE);
                fosDate.write(date.getBytes());
                fosDate.close();
                fosDate = this.openFileOutput(tomorrowDate, MODE_PRIVATE);
                fosDate.write(tomorrowsDate.getBytes());
                fosDate.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            try{
                FileOutputStream fosInitialiseList = this.openFileOutput(listAsItWas, MODE_PRIVATE);
                fosInitialiseList.write("".getBytes());
                fosInitialiseList.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            //During the first open, open the survey.
            surveyFragment newFragment = new surveyFragment();
            FragmentManager fragManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, newFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

            //If a survey fragment is closed, check if its the last part or if a new part needs to be opened.
            MyDialogCloseListener closeListener = new MyDialogCloseListener() {
                @Override
                public void handleDialogClose(DialogInterface dialog) {
                    String progress = "0";
                    try{
                        FileInputStream fis = me.openFileInput("surveyProgress.txt");
                        StringBuilder builder = new StringBuilder();
                        int ch;
                        while((ch = fis.read()) != -1){
                            builder.append((char)ch);
                        }
                        progress = builder.toString();
                        fis.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    int nextScreen = Integer.parseInt(progress);
                    if(nextScreen!=0){
                        me.callSurvey();
                    }else{
                        //when the survey is finished, allow the user to interact with the app through the floating action buttons.
                        fabRevealFabs = (FloatingActionButton) findViewById(R.id.fabRevealFabs);
                        fabRevealFabs.setVisibility(View.VISIBLE);
                        fabRevealFabs.setClickable(true);
                        try{
                            FileOutputStream fos = me.openFileOutput("surveyComplete.txt",Context.MODE_PRIVATE);
                            fos.write("true".getBytes());
                            fos.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        settings.edit().putBoolean("show_survey", false).commit();
                    }
                }
            };

            newFragment.DismissListner(closeListener);
            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }else{
            if(showSurvey()){
                callSurvey();
            }else{
                fabRevealFabs.setVisibility(View.VISIBLE);
                fabRevealFabs.setClickable(true);
                settings.edit().putBoolean("show_survey", false).commit();
            }
        }
        final FloatingActionButton fabAddRemoveAppliances= (FloatingActionButton) findViewById(R.id.fabAddRemoveAppliances);
        final FloatingActionButton fabCreateTomorrowsPlan= (FloatingActionButton) findViewById(R.id.fabCreateTomorrowsPlan);
        final FloatingActionButton fabEdit = (FloatingActionButton) findViewById(R.id.fabEditAppliances);
        fabEdit.setClickable(false);
        fabCreateTomorrowsPlan.setClickable(false);
        fabAddRemoveAppliances.setClickable(false);
        //Allow the user to edit the details of their appliances by opening this FAB (Floating Action Button).
        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new editApplianceSettings();
                FragmentManager fragManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //set the other FAB options to not be usable until the user is done with this one.
                fabCreateTomorrowsPlan.startAnimation(FabClose);
                fabEdit.startAnimation(FabClose);
                fabAddRemoveAppliances.startAnimation(FabClose);
                fabRevealFabs.startAnimation(FabRAntiClockwise);

                textEdit.startAnimation(FabClose);
                textAddRemove.startAnimation(FabClose);
                textCreatePlan.startAnimation(FabClose);
                fabEdit.setClickable(false);
                fabCreateTomorrowsPlan.setClickable(false);
                fabAddRemoveAppliances.setClickable(false);
                fabRevealFabs.clearAnimation();
                fabRevealFabs.setVisibility(View.INVISIBLE);
                fabRevealFabs.setClickable(false);
                isOpen = false;
            }
        });

        //Allow the user to add appliances to the application or remove others.
        fabAddRemoveAppliances.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment newFragment = new addRemoveAppliance();
                FragmentManager fragManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container, newFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();

                //set the other FAB options to not be usable until the user is done with this one.
                fabCreateTomorrowsPlan.startAnimation(FabClose);
                fabEdit.startAnimation(FabClose);
                fabAddRemoveAppliances.startAnimation(FabClose);
                fabRevealFabs.startAnimation(FabRAntiClockwise);

                textEdit.startAnimation(FabClose);
                textAddRemove.startAnimation(FabClose);
                textCreatePlan.startAnimation(FabClose);

                fabEdit.setClickable(false);
                fabCreateTomorrowsPlan.setClickable(false);
                fabAddRemoveAppliances.setClickable(false);
                fabRevealFabs.clearAnimation();
                fabRevealFabs.setVisibility(View.INVISIBLE);
                fabRevealFabs.setClickable(false);
                isOpen = false;
            }
        });

        //Open the fragment in which the user inputs the details of the schedule they want produced.
        fabCreateTomorrowsPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkAvailable()){
                    FragmentManager fragManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                    DialogFragment newFragment = new fragment_create();
                    newFragment.show(fragmentTransaction, "fragment_create");
                    fabCreateTomorrowsPlan.startAnimation(FabClose);
                    fabEdit.startAnimation(FabClose);
                    fabAddRemoveAppliances.startAnimation(FabClose);
                    fabRevealFabs.startAnimation(FabRAntiClockwise);

                    textEdit.startAnimation(FabClose);
                    textAddRemove.startAnimation(FabClose);
                    textCreatePlan.startAnimation(FabClose);
                    fabEdit.setClickable(false);
                    fabCreateTomorrowsPlan.setClickable(false);
                    fabAddRemoveAppliances.setClickable(false);
                    isOpen = false;
                }else{
                    FragmentManager fragManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                    DialogFragment newFragment = new No_Network_Fragment();
                    newFragment.show(fragmentTransaction, "fragment_no_network");
                    fabCreateTomorrowsPlan.startAnimation(FabClose);
                    fabEdit.startAnimation(FabClose);
                    fabAddRemoveAppliances.startAnimation(FabClose);
                    fabRevealFabs.startAnimation(FabRAntiClockwise);

                    textEdit.startAnimation(FabClose);
                    textAddRemove.startAnimation(FabClose);
                    textCreatePlan.startAnimation(FabClose);
                    fabEdit.setClickable(false);
                    fabCreateTomorrowsPlan.setClickable(false);
                    fabAddRemoveAppliances.setClickable(false);
                    isOpen = false;
                }
            }
        });

        fabRevealFabs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isOpen){
                    fabCreateTomorrowsPlan.startAnimation(FabClose);
                    fabEdit.startAnimation(FabClose);
                    fabAddRemoveAppliances.startAnimation(FabClose);
                    fabRevealFabs.startAnimation(FabRAntiClockwise);

                    textEdit.startAnimation(FabClose);
                    textAddRemove.startAnimation(FabClose);
                    textCreatePlan.startAnimation(FabClose);
                    fabEdit.setClickable(false);
                    fabCreateTomorrowsPlan.setClickable(false);
                    fabAddRemoveAppliances.setClickable(false);
                    isOpen = false;
                }else{
                    fabCreateTomorrowsPlan.startAnimation(FabOpen);
                    fabEdit.startAnimation(FabOpen);
                    fabAddRemoveAppliances.startAnimation(FabOpen);
                    fabRevealFabs.startAnimation(FabRClockwise);

                    textEdit.startAnimation(FabOpen);
                    textAddRemove.startAnimation(FabOpen);
                    textCreatePlan.startAnimation(FabOpen);
                    fabEdit.setClickable(true);
                    fabCreateTomorrowsPlan.setClickable(true);
                    fabAddRemoveAppliances.setClickable(true);
                    isOpen = true;
                }
            }
        });

        checkForTodaysScheduleTask(this);

        ensureDisplay(this, layoutView);

    }
    //On returning to the main activity, ensure that everything is displayed correctly and that the notifications for today's tasks are queued.
    public void onResume() {
        super.onResume();
        ScrollView scroll = (ScrollView) findViewById(R.id.scrollTable);
        //Scroll to the current hour of the day.
        focusOnView(scroll);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        int minutes = prefs.getInt("interval",1); //user defined how often they want to be notified.

        AlarmManager am =(AlarmManager) getSystemService(ALARM_SERVICE);

        final View layoutView = (View) findViewById(android.R.id.content);
        ensureDisplay(this, layoutView);
        checkForTodaysScheduleTask(this);
        Intent i = new Intent(this, NotificationService.class);
        PendingIntent pi =PendingIntent.getService(this, 0, i, 0);
        am.cancel(pi);

        // by my own convention, minutes <= 0 means notifications are disabled
        if(minutes > 0){
            am.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + minutes*60*1000,minutes*60*1000, pi);
        }
    }
    //if back is pressed to exit one of the FAB options, ensure the FABs are revealed and usable again.
    @Override
    public void onBackPressed() {
        if(fabRevealFabs!=null){
            fabRevealFabs.setClickable(true);
            fabRevealFabs.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            onBackPressed();
        }

//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }
    //If the date changes, store the new date for today and tomorrow, change the title and, if one is available, get the schedule that was planned yesterday for today and display it.
    public void setDate(MainActivity mainActivity){
        String todayDate = "",tomorrowDate;
        FileInputStream fisDate;
        StringBuilder builder;
        int ch;
        try{
            fisDate = mainActivity.openFileInput("Date.txt");
            builder = new StringBuilder();
            while ((ch = fisDate.read()) != -1) {
                builder.append((char) ch);
            }
            fisDate.close();
            todayDate = builder.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
        String dateTest = simpleDateFormat.format(new Date());
        dateTest = dateTest.substring(0,10);
        if(todayDate.equals(dateTest)){

        }else{
            System.out.print("~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            System.out.print("SETDATE TASK BEING CALLED\n");
            System.out.print("~~~~~~~~~~~~~~~~~~~~~~~~~~\n");
            todayDate = dateTest;
            String fullTime = simpleDateFormat.format(new Date(((new Date()).getTime() + 86400000)));
            tomorrowDate = fullTime.substring(0,10);
            FileOutputStream fosDate;
            try{
                fosDate =mainActivity.openFileOutput("Date.txt",Context.MODE_PRIVATE);
                fosDate.write(todayDate.getBytes());
                fosDate.close();

                fosDate = mainActivity.openFileOutput("TomorrowsDate.txt",Context.MODE_PRIVATE);
                fosDate.write(tomorrowDate.getBytes());
                fosDate.close();
            }catch(IOException e){
                e.printStackTrace();
            }
            String todaySch = "";
            try {
                FileInputStream fis = mainActivity.openFileInput("chosenPlan.txt");
                builder = new StringBuilder();
                while((ch = fis.read()) != -1){
                    builder.append((char)ch);
                }
                fis.close();

                todaySch = builder.toString();
                System.out.print("Chosen Plan to Today Schedule: "+todaySch+"\n");
                FileOutputStream fos = mainActivity.openFileOutput("TodaySchedule.txt", Context.MODE_PRIVATE);
                fos.write(todaySch.getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            String[] planParts = todaySch.split("\n");
            char[][] planPartsCharArray = new char[planParts.length][];
            for(int i = 0; i<planParts.length;i++){
                planPartsCharArray[i]=planParts[i].toCharArray();
            }
            char[][] times = new char[planParts.length][11];
            for(int i=0; i<planPartsCharArray.length;i++){
                boolean time = false;
                int index = 0;
                for(int j=0; j<planPartsCharArray[i].length;j++){
                    if((planPartsCharArray[i][j] == '0')||(planPartsCharArray[i][j] == '1')||(planPartsCharArray[i][j] == '2')){
                        time = true;
                    }
                    if(time ==true){
                        if(index<5){
                            times[i][index] = planPartsCharArray[i][j];
                            index++;
                        }
                    }
                }
            }
            String[] timeStrings = new String[times.length];
            for(int i = 0; i<timeStrings.length;i++){
                timeStrings[i] = new String(""+times[i][0]+times[i][1]+times[i][2]+times[i][3]+times[i][4]);
                if(i==0){
                    try{
                        FileOutputStream fos = mainActivity.openFileOutput("timesToNotify.txt", Context.MODE_PRIVATE);
                        String submit = timeStrings[i]+",";
                        fos.write(submit.getBytes());
                        fos.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    if(i!=timeStrings.length-1){
                        try{
                            FileOutputStream fos = mainActivity.openFileOutput("timesToNotify.txt", Context.MODE_APPEND);
                            String submit = timeStrings[i]+",";
                            fos.write(submit.getBytes());
                            fos.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        try{
                            FileOutputStream fos = mainActivity.openFileOutput("timesToNotify.txt", Context.MODE_APPEND);
                            String submit = timeStrings[i]+",";
                            fos.write(submit.getBytes());
                            fos.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }

                }

            }
            final String PREFS_NAME = "MyPrefsFile";
            SharedPreferences settings = mainActivity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            settings.edit().putBoolean("defaultBool",true).commit();
            String tomorrowsSchedule = "TomorrowSchedule.txt";
            FileOutputStream fos;
            try{
                fos = this.openFileOutput("TomorrowSchedule.txt", this.MODE_PRIVATE);
                fos.write(" ".getBytes());
                fos.close();
            }catch(Exception e){
                e.printStackTrace();
            }
            this.display = " ";
            try {
                fos = mainActivity.openFileOutput("chosenPlan.txt", Context.MODE_PRIVATE);
                fos.write(" ".getBytes());
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            final View layoutView = (View) findViewById(android.R.id.content);
            ensureDisplay(this, layoutView);
            display = null;
        }
    }

    //set the TomorrowSchedule text file
    public void setDisplay(String s, String w){
        String details = "";
        try{
            FileOutputStream fos = this.openFileOutput("TomorrowSchedule.txt", this.MODE_PRIVATE);
            fos.write(s.getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        this.display = s;
        try{
            FileInputStream fis = this.openFileInput("details.txt");
            int ch;
            StringBuilder builder = new StringBuilder();
            while ((ch = fis.read()) != -1) {
                builder.append((char)ch);
            }
            details = builder.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        details = w+"-"+details;
        String android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        sendMail(details,"Desktop App Data for "+android_id);
    }

    //Start the process of creating, rating, displaying and storing possible plans.
    public void callBackgroundTasks(Action[] array, int progressChangedValue){
        tasksStop = false;
        Schedule lists = new Schedule(array);
        Schedule[] pass = new Schedule[]{lists};
        motherTask = new createSchedulesTask(MainActivity.this,progressChangedValue, this, getWattage()).execute(pass);
    }

    //If, before the background tasks are complete, they are called again, cancel the background tasks to reduce overhead and prevent crashing from overload.
    public void cancelBackgroundTasks(){
        if(motherTask!=null){
            tasksStop = true;
            motherTask.cancel(true);
        }
    }

    //Check if the tasks are cancelled.
    public boolean checkTasksStop(){
        return tasksStop;
    }

    //set the list the actions.
    public void setList(String a){
        try{
            FileOutputStream fos = this.openFileOutput("ActionInputList.txt", this.MODE_PRIVATE);
            fos.write(a.getBytes());
            fos.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        this.list = a;
    }

    //If an appliance is removed from the Add/Remove option, remove all instances of the appliance from the action list.
    public void removeItemWithName(String name){
        if(list!=null){
            ArrayList<Action> actionList = new ArrayList<Action>();
            ArrayList<String> actionStrings = new ArrayList<String>();
            String[] testArray = list.split("[\\r\\n]+");
            for(int i = 0; i<testArray.length;i++){
                String[] temp = testArray[i].split(",");
                if(temp.length==5){
                    if(temp[0].equals(name)){

                    }else{
                        boolean parallel;
                        switch(temp[0]){
                            case "Hob":
                                parallel = false;
                                break;
                            case "Oven":
                                parallel = true;
                                break;
                            case "TumbleDryer":
                                parallel = true;
                                break;
                            case "WashingMachine":
                                parallel = true;
                                break;
                            case "Computer":
                                parallel = false;
                                break;
                            case "Kettle":
                                parallel = true;
                                break;
                            case "DishWasher":
                                parallel = true;
                                break;
                            case "Shower":
                                parallel = false;
                                break;
                            default:
                                parallel = false;
                                break;
                        }
                        actionList.add(new Action(temp[0],temp[1],temp[2],temp[3],temp[4], parallel));
                        actionStrings.add(temp[0]+"\t"+temp[1]+"-"+temp[2]+"\t"+temp[4]);
                    }
                }else{
                    break;
                }

            }
            actionList.removeAll(Collections.singleton(null));
            actionList.trimToSize();
            String listCSV = "";
            StringBuilder listCSVBuilder = new StringBuilder();
            for(Action a: actionList){
                listCSVBuilder.append(a.name+","+a.getTimeString(a.windowStart)+","+a.getTimeString(a.windowEnd)+","+a.getTimeString(a.duration)+","+a.getTimeString(a.optimalTime)+"\n");
            }
            listCSV = listCSVBuilder.toString();
            setList(listCSV);
        }
    }

    //Check if a task should start soon and notify the user if so.
    public void checkForTodaysScheduleTask(MainActivity ma){
        final MainActivity m = ma;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run(){

                Intent intent = new Intent(m, MainActivity.class);
                PendingIntent pIntent = PendingIntent.getActivity(m, (int) System.currentTimeMillis(), intent, 0);

                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                String time = simpleDateFormat.format(new Date());
                char[] timeChars = time.toCharArray();
                String numeralS =""+timeChars[12];
                String decimalS=""+timeChars[11];
                int numeral = Integer.parseInt(numeralS);
                int decimal = Integer.parseInt(decimalS);
                int hour = (decimal*10)+numeral;

                String dayDate="";
                try{
                    FileInputStream fis = m.openFileInput("lastSendPressDayDate.txt");
                    StringBuilder builder = new StringBuilder();
                    int chr;
                    while ((chr = fis.read()) != -1) {
                        builder.append((char) chr);
                    }
                    fis.close();
                    dayDate = builder.toString();
                }catch(Exception e){
                    e.printStackTrace();
                }
                String notifyPress = "";
                try{
                    FileInputStream fis = m.openFileInput("notifyPress.txt");
                    StringBuilder builder = new StringBuilder();
                    int chr;
                    while ((chr = fis.read()) != -1) {
                        builder.append((char) chr);
                    }
                    fis.close();
                    notifyPress = builder.toString();
                }catch(Exception e){
                    e.printStackTrace();
                }
                String date = time.substring(0,10);
                m.setTitle(date+"'s Schedule");
                setDate(m);
                if(!dayDate.equals(date)){
                    if(hour >= 21){
                        if(notifyPress.equals("true")){
                            Notification noti = new Notification.Builder(m)
                                    .setContentTitle("No new plans created for tomorrow!")
                                    .setContentText("Open Scheduler now to create tomorrow's plans")
                                    .setContentIntent(pIntent)
                                    .setSmallIcon(R.mipmap.ic_launcher)
                                    .build();
                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                            // hide the notification after its selected
                            noti.flags |= Notification.FLAG_AUTO_CANCEL;

                            notificationManager.notify(0, noti);
                            if(dayDate.equals(date)){
                                notificationManager.cancelAll();
                            }
                        }
                        try{
                            FileOutputStream fos = openFileOutput("notifyPress.txt",MODE_PRIVATE);
                            fos.write("false".getBytes());
                            fos.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }else{
                        try{
                            FileOutputStream fos = openFileOutput("notifyPress.txt",MODE_PRIVATE);
                            fos.write("true".getBytes());
                            fos.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }
                handler.postDelayed(this, 100);
            }
        },100);
    }

    //Placeholder method to receive a new plan
    public void receiveBetterPlan(String a){
        String plans = "";
        try {
            FileInputStream fis = this.openFileInput("TomorrowSchedule.txt");
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            fis.close();
            plans = builder.toString();
        }catch(Exception e){
            String toastString = "Couldnt get Tomorrow's schedule.";
            int durationOfToast = Toast.LENGTH_SHORT;
            Context context = this;
            Toast toast = Toast.makeText(context, toastString, durationOfToast);
            toast.show();
            e.printStackTrace();
        }
        String[] list = plans.split("[\\r\\n]+");
        for(int i  = 0; i<list.length;i++){
            if(list[i].equals(a)){
                try{
                    FileOutputStream fos = this.openFileOutput("suggestedPlan.txt",this.MODE_PRIVATE);
                    fos.write(a.getBytes());
                    fos.close();
                    FragmentManager fragManager = getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                    DialogFragment newFragment = new betterPlanPopUpFragment();
                    newFragment.show(fragmentTransaction, "betterPlanPopUp");
                }catch(Exception e){
                    e.printStackTrace();
                }

            }else{

            }
        }


    }


    //Ensure the display is correct for the date.
    public void ensureDisplay(MainActivity ma, View v){
            final MainActivity m = ma;
            final View layoutView = v;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    String todayDisplay = " ";
                    try {
                        FileInputStream fis = openFileInput("TodaySchedule.txt");
                        StringBuilder builder = new StringBuilder();
                        int ch;
                        while((ch = fis.read()) != -1){
                            builder.append((char)ch);
                        }
                        fis.close();
                        todayDisplay = builder.toString();
                        System.out.print("Today's Schedule: "+todayDisplay+"\n");
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    ConstraintLayout constraintLayout = (ConstraintLayout) findViewById(R.id.table);
                    String[] parts = todayDisplay.split(",");
                    String[][] nameStartDuration = new String[parts.length][];
                    if(index>0){
                        for(int i = 0;i<index;i++){
                            constraintLayout.removeView(eventsAdded.get(i));
                        }
                        eventsAdded.clear();
                        index = 0;
                    }
                    if(parts.length>0) {
                        if(!parts[0].equals(" ")){
                            for(int i =0;i<parts.length;i++){
                                String[] temp = parts[i].split("\t");
                                String[] timesTemp = temp[1].split("-");
                                int timeOne = getIntTime(timesTemp[0]);
                                int timeTwo = getIntTime(timesTemp[1]);
                                int duration = timeTwo - timeOne;
                                String durString = getTimeString(duration);
                                temp[1] = timesTemp[0]+","+durString;
                                String partsTogether = temp[0]+","+temp[1];
                                parts[i] = partsTogether;
                                nameStartDuration[i] = parts[i].split(",");
                                addEvent(nameStartDuration[i][0],nameStartDuration[i][1],nameStartDuration[i][2],constraintLayout);
                            }
                        }
                    }
//                    TextView textView = (TextView)layoutView.findViewById(R.id.text);
//                    textView.setText(todayDisplay);
                    //addEvent(String eventName, String startTimeString, String durationString, ConstraintLayout constraintLayout)
                }
            },100);
    }

    //Show the survey.
    public void callSurvey(){
        fabRevealFabs = (FloatingActionButton) findViewById(R.id.fabRevealFabs);
        fabRevealFabs.setVisibility(View.INVISIBLE);
        fabRevealFabs.setClickable(false);

        surveyFragment newFragment = new surveyFragment();
        FragmentManager fragManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, newFragment);
        fragmentTransaction.commit();
        MyDialogCloseListener closeListener = new MyDialogCloseListener() {
            @Override
            public void handleDialogClose(DialogInterface dialog) {
                String progress = "0";
                try{
                    FileInputStream fis = me.openFileInput("surveyProgress.txt");
                    StringBuilder builder = new StringBuilder();
                    int ch;
                    while((ch = fis.read()) != -1){
                        builder.append((char)ch);
                    }
                    progress = builder.toString();
                    fis.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                int nextScreen = Integer.parseInt(progress);
                System.out.print(nextScreen+"\n");
                if(nextScreen!=0){
                    me.callSurvey();
                }else{
                    fabRevealFabs.setVisibility(View.VISIBLE);
                    fabRevealFabs.setClickable(true);
                    settings.edit().putBoolean("show_survey", false).commit();
                }
            }
        };
        newFragment.DismissListner(closeListener);
    }

    //If a survey fragment is closed, check if it was the end of the survey or if the next part of the survey should be displayed.
    @Override
    public void handleDialogClose(DialogInterface dialog) {
        String progress = "0";
        try{
            FileInputStream fis = me.openFileInput("surveyProgress.txt");
            StringBuilder builder = new StringBuilder();
            int ch;
            while((ch = fis.read()) != -1){
                builder.append((char)ch);
            }
            progress = builder.toString();
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        int nextScreen = Integer.parseInt(progress);
        if(nextScreen!=0){
            me.callSurvey();
        }else{
            fabRevealFabs.setVisibility(View.VISIBLE);
            fabRevealFabs.setClickable(true);
            settings.edit().putBoolean("show_survey",false).commit();
        }
    }

    //Get the wattage file of the user.
    public String[] getWattage(){
        String wattage = "";
        String[] wattageArray;
        try{
            FileInputStream fis = openFileInput("wattagesFile.txt");
            int chr;
            StringBuilder builder = new StringBuilder();
            while ((chr = fis.read()) != -1) {
                builder.append((char) chr);
            }
            wattage = builder.toString();
            //  0,              1,          2,          3,              4,    5,      6,        7,              8
            //House Number, Computer, Cooker (Hob),Cooker (Oven),Dishwasher,Kettle,Shower,Tumble Dryer, Washing Machine

            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        wattageArray = wattage.split(",");
        return wattageArray;
    }

    //get the FAB that when pressed reveals the other FABs.
    public FloatingActionButton getFabRevealFabs(){
        return fabRevealFabs;
    }

    //Check if the survey should be revealed.
    public boolean showSurvey(){
        return settings.getBoolean("show_survey",true);
    }
    //Check if this is the first time the application has been started.
    public boolean firstTimeStart(){
        return settings.getBoolean("my_first_time", true);
    }

    //Get the distance between the starting hour line and the items actual starting time.
    public double getBias(double minutesAfterStartingHour, double fullTimeFrame, double duration){
        double bias = (minutesAfterStartingHour/(fullTimeFrame-duration));
        if(minutesAfterStartingHour!=0){
            return bias;
        }else{
            return 0;
        }

    }

    //given a string input of format "00:00" where the first two numbers are the hours and the second two are the minutes,
    //return an integer of the total number of minutes that represents.
    public static int getIntTime(String a){
        int time;
        char[] timeCharArray = a.toCharArray();
        int hour = (Character.getNumericValue(timeCharArray[0])*10)+Character.getNumericValue(timeCharArray[1]);
        int minute = (Character.getNumericValue(timeCharArray[3])*10)+Character.getNumericValue(timeCharArray[4]);
        time=hour*60+minute;
        return time;
    }

    //given an integer whose value is equal to a total number of minutes, convert into a string in the format "00:00" where the first two numbers
    // are the hours and the second two are the minutes.
    public static String getTimeString(int a){
        int hour = a/60;
        int minute = a%60;
        String result;
        if(hour<10){
            result = "0"+hour+":";
        }else{
            result = hour+":";
        }
        if(minute<10){
            result+="0"+minute;
        }else{
            result+=minute;
        }
        return result;
    }

    //given an integer whose value is equal to a total number of minutes, get how many hours that is.
    public int getHour(int time){
        return time/60;
    }

    //convert dp to px for constraintSet inputs.
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    //Add an item to the constraint layout display.
    public void addEvent(String name, String startTimeString, String durationString, ConstraintLayout constraintLayout){
        String eventName = name;
        switch(name){
            case"Hob":
                eventName = "Hob";
                break;
            case "Oven":
                eventName = "Oven";
                break;
            case "TumbleDryer":
                eventName = "Tumble Dryer";
                break;
            case "WashingMachine":
                eventName = "Washing Machine";
                break;
            case "Computer":
                eventName = "Computer";
                break;
            case "Kettle":
                eventName = "Kettle";
                break;
            case "DishWasher":
                eventName = "Dish Washer";
                break;
            case "Shower":
                eventName = "Shower";
                break;
            default:
                eventName = name;
                break;
        }
        int startTime = getIntTime(startTimeString);
        int startingLine = getHour(startTime);
        int duration = getIntTime(durationString);
        int endTime = startTime+duration;
        int endLine = getHour(endTime);
        int frame = ((endLine+1)-startingLine)*60;
        double bias = getBias(startTime%60,frame,duration);
        View topOf;
        View bottomOf;
        switch (startingLine){
            case 0:
                topOf = (View) findViewById(R.id.topBar);
                break;
            case 1:
                topOf = (View) findViewById(R.id.bar1am);
                break;
            case 2:
                topOf = (View) findViewById(R.id.bar2am);
                break;
            case 3:
                topOf = (View) findViewById(R.id.bar3am);
                break;
            case 4:
                topOf = (View) findViewById(R.id.bar4am);
                break;
            case 5:
                topOf = (View) findViewById(R.id.bar5am);
                break;
            case 6:
                topOf = (View) findViewById(R.id.bar6am);
                break;
            case 7:
                topOf = (View) findViewById(R.id.bar7am);
                break;
            case 8:
                topOf = (View) findViewById(R.id.bar8am);
                break;
            case 9:
                topOf = (View) findViewById(R.id.bar9am);
                break;
            case 10:
                topOf = (View) findViewById(R.id.bar10am);
                break;
            case 11:
                topOf = (View) findViewById(R.id.bar11am);
                break;
            case 12:
                topOf = (View) findViewById(R.id.bar12pm);
                break;
            case 13:
                topOf = (View) findViewById(R.id.bar1pm);
                break;
            case 14:
                topOf = (View) findViewById(R.id.bar2pm);
                break;
            case 15:
                topOf = (View) findViewById(R.id.bar3pm);
                break;
            case 16:
                topOf = (View) findViewById(R.id.bar4pm);
                break;
            case 17:
                topOf = (View) findViewById(R.id.bar5pm);
                break;
            case 18:
                topOf = (View) findViewById(R.id.bar6pm);
                break;
            case 19:
                topOf = (View) findViewById(R.id.bar7pm);
                break;
            case 20:
                topOf = (View) findViewById(R.id.bar8pm);
                break;
            case 21:
                topOf = (View) findViewById(R.id.bar9pm);
                break;
            case 22:
                topOf = (View) findViewById(R.id.bar10pm);
                break;
            default:
                topOf = (View) findViewById(R.id.bar11pm);
                break;
        }
        switch (endLine+1){
            case 0:
                bottomOf = (View) findViewById(R.id.topBar);
                break;
            case 1:
                bottomOf = (View) findViewById(R.id.bar1am);
                break;
            case 2:
                bottomOf = (View) findViewById(R.id.bar2am);
                break;
            case 3:
                bottomOf = (View) findViewById(R.id.bar3am);
                break;
            case 4:
                bottomOf = (View) findViewById(R.id.bar4am);
                break;
            case 5:
                bottomOf = (View) findViewById(R.id.bar5am);
                break;
            case 6:
                bottomOf = (View) findViewById(R.id.bar6am);
                break;
            case 7:
                bottomOf = (View) findViewById(R.id.bar7am);
                break;
            case 8:
                bottomOf = (View) findViewById(R.id.bar8am);
                break;
            case 9:
                bottomOf = (View) findViewById(R.id.bar9am);
                break;
            case 10:
                bottomOf = (View) findViewById(R.id.bar10am);
                break;
            case 11:
                bottomOf = (View) findViewById(R.id.bar11am);
                break;
            case 12:
                bottomOf = (View) findViewById(R.id.bar12pm);
                break;
            case 13:
                bottomOf = (View) findViewById(R.id.bar1pm);
                break;
            case 14:
                bottomOf = (View) findViewById(R.id.bar2pm);
                break;
            case 15:
                bottomOf = (View) findViewById(R.id.bar3pm);
                break;
            case 16:
                bottomOf = (View) findViewById(R.id.bar4pm);
                break;
            case 17:
                bottomOf = (View) findViewById(R.id.bar5pm);
                break;
            case 18:
                bottomOf = (View) findViewById(R.id.bar6pm);
                break;
            case 19:
                bottomOf = (View) findViewById(R.id.bar7pm);
                break;
            case 20:
                bottomOf = (View) findViewById(R.id.bar8pm);
                break;
            case 21:
                bottomOf = (View) findViewById(R.id.bar9pm);
                break;
            case 22:
                bottomOf = (View) findViewById(R.id.bar10pm);
                break;
            case 23:
                bottomOf = (View) findViewById(R.id.bar11pm);
                break;
            default:
                bottomOf = (View) findViewById(R.id.bottomBar);
                break;
        }
        View separatorBar = (View) findViewById(R.id.timeSeparator);
        TextView tx = new TextView(this);
        tx.setText(eventName);
        tx.setTextSize(10);
        tx.setId(View.generateViewId());
        eventsAdded.add(tx);

        int[] androidColors = getResources().getIntArray(R.array.androidcolors);
        int colour = index%10;
        int randomAndroidColor = androidColors[colour];
        tx.setBackgroundColor(randomAndroidColor);
        tx.setTextColor(getResources().getColor(R.color.white));
        tx.setShadowLayer(1,1,1,getResources().getColor(R.color.black));
        tx.setPadding(0,0,0,0);

        constraintLayout.addView(tx);

        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.connect(tx.getId(),ConstraintSet.TOP,topOf.getId(),ConstraintSet.BOTTOM);
        constraintSet.connect(tx.getId(),ConstraintSet.BOTTOM,bottomOf.getId(),ConstraintSet.TOP);
        if (index == 0) {
            constraintSet.connect(tx.getId(),ConstraintSet.START,separatorBar.getId(),ConstraintSet.END);
        }else{
            constraintSet.connect(tx.getId(),ConstraintSet.START,eventsAdded.get(index-1).getId(),ConstraintSet.END);
        }
        constraintSet.connect(tx.getId(),ConstraintSet.RIGHT,ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);
        if(bias!=0){
            constraintSet.setVerticalBias(tx.getId(),(float)bias);
        }else{
            constraintSet.setVerticalBias(tx.getId(),0);
        }
        constraintSet.constrainHeight(tx.getId(),dpToPx(duration));
        constraintSet.constrainWidth(tx.getId(),dpToPx(60));
        constraintSet.setHorizontalBias(tx.getId(),(float)0);
        constraintSet.applyTo(constraintLayout);
        index++;
    }

    //When a new fragment is displayed, hide the mainlayout.
    public void setMainLayoutViewInvisible(boolean check){
        if(check){
            mainLayoutView.setVisibility(View.GONE);
        }else{
            mainLayoutView.setVisibility(View.VISIBLE);
        }
    }

    //Scroll to the current hour.
    private final void focusOnView(final ScrollView scroll) {
        final View view = getHour();
        scroll.post(new Runnable() {
            @Override
            public void run() {
                scroll.smoothScrollTo(0,view.getTop());
            }
        });
    }

    //get the line of the current hour.
    private View getHour(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentTimeHour = simpleDateFormat.format(new Date()).substring(11,13);
        View barToScrollTo;
        switch(currentTimeHour){
            case "00":
                barToScrollTo= (View) findViewById(R.id.topBar);
                break;
            case "01":
                barToScrollTo= (View) findViewById(R.id.bar1am);
                break;
            case "02":
                barToScrollTo= (View) findViewById(R.id.bar2am);
                break;
            case "03":
                barToScrollTo= (View) findViewById(R.id.bar3am);
                break;
            case "04":
                barToScrollTo= (View) findViewById(R.id.bar4am);
                break;
            case "05":
                barToScrollTo= (View) findViewById(R.id.bar5am);
                break;
            case "06":
                barToScrollTo= (View) findViewById(R.id.bar6am);
                break;
            case "07":
                barToScrollTo= (View) findViewById(R.id.bar7am);
                break;
            case "08":
                barToScrollTo= (View) findViewById(R.id.bar8am);
                break;
            case "09":
                barToScrollTo= (View) findViewById(R.id.bar9am);
                break;
            case "10":
                barToScrollTo= (View) findViewById(R.id.bar10am);
                break;
            case "11":
                barToScrollTo= (View) findViewById(R.id.bar11am);
                break;
            case "12":
                barToScrollTo= (View) findViewById(R.id.bar12pm);
                break;
            case "13":
                barToScrollTo= (View) findViewById(R.id.bar1pm);
                break;
            case "14":
                barToScrollTo= (View) findViewById(R.id.bar2pm);
                break;
            case "15":
                barToScrollTo= (View) findViewById(R.id.bar3pm);
                break;
            case "16":
                barToScrollTo= (View) findViewById(R.id.bar4pm);
                break;
            case "17":
                barToScrollTo= (View) findViewById(R.id.bar5pm);
                break;
            case "18":
                barToScrollTo= (View) findViewById(R.id.bar6pm);
                break;
            case "19":
                barToScrollTo= (View) findViewById(R.id.bar7pm);
                break;
            case "20":
                barToScrollTo= (View) findViewById(R.id.bar8pm);
                break;
            case "21":
                barToScrollTo= (View) findViewById(R.id.bar9pm);
                break;
            case "22":
                barToScrollTo= (View) findViewById(R.id.bar10pm);
                break;
            case "23":
                barToScrollTo= (View) findViewById(R.id.bar11pm);
                break;
            default:
                barToScrollTo= (View) findViewById(R.id.bar12pm);
                break;
        }
        return barToScrollTo;
    }

    //Open the fragment with the plans for the user to choose from in this activity.
    public void choicesPopUp(){
        onOpenDialog(thisView);
        callParseableDataTask();
    }

    public String[][][] getP(){
        return p;
    }
    public String[] getW(){
        return w;
    }

    public void setPandW(String[][][] pSet, String[] wSet){
        p = pSet;
        w = wSet;
    }

    public void callParseableDataTask(){
        new parseableDataTask(this,getW(),fl).execute();
    }
    public void setW(String[] wSet){
        w = wSet;
    }

    public void setFl(Action[][] flSet){
        fl = flSet;
    }


    public void callCreateFilesTask(){
        String[] pass = new String[]{"1"};
        new createFilesTask(p, this,this, w).execute(pass);
    }

    //open the fragment with the plans for the user to choose from.
    public void onOpenDialog(View view)
    {
        FragmentManager fm = getSupportFragmentManager();
        tabsFragment overlay = new tabsFragment();
        overlay.show(fm, "FragmentDialog");
    }

    //Set the fabs to visible or invisible.
    public void setFabRevealFabsVisibility(boolean a){
        if(fabRevealFabs!=null){
            if(a){
                fabRevealFabs.setVisibility(View.VISIBLE);
                fabRevealFabs.setClickable(true);
            }else{
                fabRevealFabs.setVisibility(View.INVISIBLE);
                fabRevealFabs.setClickable(false);
            }
        }
    }

    public void sendMail(String emailBody, String emailSubject){
        String fromEmail = "mailsenderforscheduler@gmail.com";
        String fromPassword = "TestMail56";
        String toEmails = "mailsenderforscheduler@gmail.com";
        String adminEmail = "admin@gmail.com";
        String adminSubject = "App Registration Mail";
        String adminBody = "Your message";
        new SendMailTask(this).execute(fromEmail,
                fromPassword, toEmails, emailSubject, emailBody);

    }

    public void getFilesToSend(String[] pass){
        String[] device = pass;

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH_mm_ss");
        String date = simpleDateFormat.format(new Date());

        String android_id;
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String fileFirstHalfTitle = android_id+"-";
        String setup = "0";

        try {
            FileInputStream fisPSched = openFileInput("PastSchedules.txt");
            int ch;
            StringBuilder builder = new StringBuilder();
            while ((ch = fisPSched.read()) != -1) {

                char s = (char) ch;
                String st = "" + s;
                if (!st.equals(null)) {
                    builder.append((char) ch);
                }
            }
            fisPSched.close();
            setup = builder.toString();
        }catch(Exception e) {
            e.printStackTrace();
        }
        int setupInt = Integer.parseInt(setup);
        setupInt++;
        String fileSecondHalfTitle = "-"+date+"-"+setupInt+".txt" ;
        FileOutputStream fOut;
        for(int i = 0; i<device.length;i++){
            String submitString = device[i];
            String fileName = fileFirstHalfTitle+applianceNames[i]+"-"+fileSecondHalfTitle;
            sendMail(submitString,fileName);
        }
        try{
            fOut = openFileOutput("PastSchedules.txt", Context.MODE_PRIVATE);
            setup = ""+setupInt;
            fOut.write(setup.getBytes());
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
