package com.example.scheduler.fragment;

/**
 * Created by warrens on 07.09.17.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.example.scheduler.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by warrens on 06.09.17.
 */

public class fragment_page extends Fragment {

    public ArrayList<TextView> eventsAdded = new ArrayList<>();
    public int index = 0;
    public String data;
    public DialogFragment reference;

    static fragment_page newInstance(String a){
        fragment_page p = new fragment_page();
        Bundle args  = new Bundle();
        args.putString("data",a);
        p.setArguments(args);
        return p;
    }
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View lv = inflater.inflate(R.layout.fragment_choose, container, false);
        ConstraintLayout constraintLayout = (ConstraintLayout)lv.findViewById(R.id.table);
        data = getArguments().getString("data");
        String[] removeComma = data.split(",");
        //take the items from the plan provided and display it for the user to choose from.
        String[][] removeTab = new String[removeComma.length][];
        String[][] eventsDetails = new String[removeComma.length][3];
        for(int i =0; i<removeComma.length;i++){
            String[] removeHyphen;
            removeTab[i] = removeComma[i].split("\t");
            removeHyphen = removeTab[i][1].split("-");
            eventsDetails[i][0] = removeTab[i][0];//name
            eventsDetails[i][1] = removeHyphen[0];//start time
            eventsDetails[i][2] = removeHyphen[1];//end time
            int end = getIntTime(eventsDetails[i][2]);
            int start = getIntTime(eventsDetails[i][1]);
            int duration = end-start;
            eventsDetails[i][2] = getTimeString(duration);
            addEvent(eventsDetails[i][0],eventsDetails[i][1],eventsDetails[i][2],constraintLayout,lv);
        }
        final String associatedText = data;
        //If the user checks the box for this plan, save its details.
        final CheckBox cb = (CheckBox)lv.findViewById(R.id.check);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(cb.isChecked()){
                    try{
                        System.out.print(associatedText+"\n");
                        FileOutputStream fos = getActivity().openFileOutput("tempChosenPlan.txt", Context.MODE_PRIVATE);
                        fos.write(associatedText.getBytes());
                        fos.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        });
        Button conf = (Button) lv.findViewById(R.id.confirmChoice);
        conf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileInputStream fis;
                FileOutputStream fos;
                try{
                    fis = getActivity().openFileInput("tempChosenPlan.txt");
                    int ch;
                    StringBuilder builder = new StringBuilder();
                    while((ch=fis.read())!=-1){
                        builder.append((char)ch);
                    }
                    String print = builder.toString();
                    System.out.print(print+"\n");
                    fos = getActivity().openFileOutput("chosenPlan.txt",Context.MODE_PRIVATE);
                    fos.write(print.getBytes());
                    fis.close();
                    fos.close();
                    reference.dismiss();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        return lv;
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
    public void addEvent(String name, String startTimeString, String durationString, ConstraintLayout constraintLayout, View view){
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
                topOf = (View) view.findViewById(R.id.topBar);
                break;
            case 1:
                topOf = (View) view.findViewById(R.id.bar1am);
                break;
            case 2:
                topOf = (View) view.findViewById(R.id.bar2am);
                break;
            case 3:
                topOf = (View) view.findViewById(R.id.bar3am);
                break;
            case 4:
                topOf = (View) view.findViewById(R.id.bar4am);
                break;
            case 5:
                topOf = (View) view.findViewById(R.id.bar5am);
                break;
            case 6:
                topOf = (View) view.findViewById(R.id.bar6am);
                break;
            case 7:
                topOf = (View) view.findViewById(R.id.bar7am);
                break;
            case 8:
                topOf = (View) view.findViewById(R.id.bar8am);
                break;
            case 9:
                topOf = (View) view.findViewById(R.id.bar9am);
                break;
            case 10:
                topOf = (View) view.findViewById(R.id.bar10am);
                break;
            case 11:
                topOf = (View) view.findViewById(R.id.bar11am);
                break;
            case 12:
                topOf = (View) view.findViewById(R.id.bar12pm);
                break;
            case 13:
                topOf = (View) view.findViewById(R.id.bar1pm);
                break;
            case 14:
                topOf = (View) view.findViewById(R.id.bar2pm);
                break;
            case 15:
                topOf = (View) view.findViewById(R.id.bar3pm);
                break;
            case 16:
                topOf = (View) view.findViewById(R.id.bar4pm);
                break;
            case 17:
                topOf = (View) view.findViewById(R.id.bar5pm);
                break;
            case 18:
                topOf = (View) view.findViewById(R.id.bar6pm);
                break;
            case 19:
                topOf = (View) view.findViewById(R.id.bar7pm);
                break;
            case 20:
                topOf = (View) view.findViewById(R.id.bar8pm);
                break;
            case 21:
                topOf = (View) view.findViewById(R.id.bar9pm);
                break;
            case 22:
                topOf = (View) view.findViewById(R.id.bar10pm);
                break;
            case 23:
                topOf = (View) view.findViewById(R.id.bar11pm);
                break;
            default:
                topOf = null;
                break;
        }
        switch (endLine+1){
            case 0:
                bottomOf = (View) view.findViewById(R.id.topBar);
                break;
            case 1:
                bottomOf = (View) view.findViewById(R.id.bar1am);
                break;
            case 2:
                bottomOf = (View) view.findViewById(R.id.bar2am);
                break;
            case 3:
                bottomOf = (View) view.findViewById(R.id.bar3am);
                break;
            case 4:
                bottomOf = (View) view.findViewById(R.id.bar4am);
                break;
            case 5:
                bottomOf = (View) view.findViewById(R.id.bar5am);
                break;
            case 6:
                bottomOf = (View) view.findViewById(R.id.bar6am);
                break;
            case 7:
                bottomOf = (View) view.findViewById(R.id.bar7am);
                break;
            case 8:
                bottomOf = (View) view.findViewById(R.id.bar8am);
                break;
            case 9:
                bottomOf = (View) view.findViewById(R.id.bar9am);
                break;
            case 10:
                bottomOf = (View) view.findViewById(R.id.bar10am);
                break;
            case 11:
                bottomOf = (View) view.findViewById(R.id.bar11am);
                break;
            case 12:
                bottomOf = (View) view.findViewById(R.id.bar12pm);
                break;
            case 13:
                bottomOf = (View) view.findViewById(R.id.bar1pm);
                break;
            case 14:
                bottomOf = (View) view.findViewById(R.id.bar2pm);
                break;
            case 15:
                bottomOf = (View) view.findViewById(R.id.bar3pm);
                break;
            case 16:
                bottomOf = (View) view.findViewById(R.id.bar4pm);
                break;
            case 17:
                bottomOf = (View) view.findViewById(R.id.bar5pm);
                break;
            case 18:
                bottomOf = (View) view.findViewById(R.id.bar6pm);
                break;
            case 19:
                bottomOf = (View) view.findViewById(R.id.bar7pm);
                break;
            case 20:
                bottomOf = (View) view.findViewById(R.id.bar8pm);
                break;
            case 21:
                bottomOf = (View) view.findViewById(R.id.bar9pm);
                break;
            case 22:
                bottomOf = (View) view.findViewById(R.id.bar10pm);
                break;
            case 23:
                bottomOf = (View) view.findViewById(R.id.bar11pm);
                break;
            default:
                bottomOf = null;
                break;
        }

        //System.out.print("End Line: "+endLine+"\n"+"Start Line: "+startingLine+"\n"+"Bias: "+bias+"\n");
        View separatorBar = (View) view.findViewById(R.id.timeSeparator);
        TextView tx = new TextView(getActivity());
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
        constraintSet.constrainWidth(tx.getId(),dpToPx((int)getResources().getDimension(R.dimen.sizeOfCellsForTomorrow)));
        constraintSet.setHorizontalBias(tx.getId(),(float)0);
        constraintSet.applyTo(constraintLayout);
        index++;
    }
    public void setReference(DialogFragment r){
        reference = r;
    }
}

