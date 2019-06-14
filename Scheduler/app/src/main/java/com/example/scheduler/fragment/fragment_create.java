package com.example.scheduler.fragment;

import android.support.v4.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.schedulecreationlibrary.Action;
import com.example.scheduler.MainActivity;
import com.example.scheduler.R;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;


/**
 * Created by warrens on 21.08.17.
 */

public class fragment_create extends DialogFragment {
    public Context context;
    public ArrayList<Action> list = new ArrayList<>();
    public final MainActivity m = (MainActivity)getActivity();
    public String[][]enableTable;
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        final View layoutView=inflater.inflate(R.layout.fragment_create,container,false);

        final String PREFS_NAME = "MyPrefsFile";
        final SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        context = getActivity();
        FileOutputStream fileOutputStream;
        ArrayList<String> arrayListString = new ArrayList<>();
        String name = getResources().getString(R.string.name),start = getResources().getString(R.string.start_time),dur=getResources().getString(R.string.duration),flex=getResources().getString(R.string.flexibility);
        try{
            fileOutputStream = getActivity().openFileOutput("currentItems.txt", Context.MODE_PRIVATE);
            String submit = name+","+start+","+dur+","+flex+"\n";
            fileOutputStream.write(submit.getBytes());
            fileOutputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        String dataRaw = "";
        String[] dataFirstPass;
        FileInputStream fis;
        try{
            fis = getActivity().openFileInput("appliancesEnabledDataFile.txt");
            int ch;
            StringBuilder builder = new StringBuilder();
            while((ch = fis.read())!=-1){
                builder.append((char)ch);
            }
            dataRaw = builder.toString();
            fis.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        dataFirstPass = dataRaw.split("\n");
        enableTable = new String[dataFirstPass.length][];
        for(int i = 0; i <dataFirstPass.length;i++){
            enableTable[i] = dataFirstPass[i].split(",");
            if(enableTable.length>1){
                if(enableTable[i][1].equals("true")){
                    arrayListString.add(enableTable[i][0]);
                }
            }

        }


        String[] hours = {
                "00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19","20","21","22","23"
        };
        String[] minutes = {"00","01","02","03","04","05","06","07","08","09",
                "10","11","12","13","14","15","16","17","18","19",
                "20","21","22","23","24","25","26","27","28","29",
                "30","31","32","33","34","35","36","37","38","39",
                "40","41","42","43","44","45","46","47","48","49",
                "50","51","52","53","54","55","56","57","58","59"};
        final Spinner applianceNames = (Spinner) layoutView.findViewById(R.id.applianceNames);
        final Spinner startHour = (Spinner)layoutView.findViewById(R.id.startHour);
        final Spinner startMinute = (Spinner)layoutView.findViewById(R.id.startMinute);
        final Spinner durationHour = (Spinner)layoutView.findViewById(R.id.durationHour);
        final Spinner durationMinute = (Spinner)layoutView.findViewById(R.id.durationMinute);
        final Spinner flexHour = (Spinner)layoutView.findViewById(R.id.flexHour);
        final Spinner flexMinute = (Spinner)layoutView.findViewById(R.id.flexMinute);

        final CheckBox cbBeforeAndAfter = (CheckBox)layoutView.findViewById(R.id.beforeAndAfter);
        final CheckBox cbBeforeOnly = (CheckBox)layoutView.findViewById(R.id.beforeOnly);
        final CheckBox cbAfterOnly = (CheckBox)layoutView.findViewById(R.id.afterOnly);

        final Button add= (Button)layoutView.findViewById(R.id.add);
        ArrayAdapter<String> applianceNamesAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,arrayListString);
        ArrayAdapter<String> startHourAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,hours);
        ArrayAdapter<String> startMinuteAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,minutes);
        ArrayAdapter<String> durationHourAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,hours);
        ArrayAdapter<String> durationMinutesAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,minutes);
        ArrayAdapter<String> flexHourAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,hours);
        ArrayAdapter<String> flexMinuteAdapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,minutes);

        applianceNamesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        startMinuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        durationMinutesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flexHourAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        flexMinuteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        applianceNames.setAdapter(applianceNamesAdapter);
        startHour.setAdapter(startHourAdapter);
        startMinute.setAdapter(startMinuteAdapter);
        durationHour.setAdapter(durationHourAdapter);
        durationMinute.setAdapter(durationMinutesAdapter);
        flexHour.setAdapter(flexHourAdapter);
        flexMinute.setAdapter(flexMinuteAdapter);
        //create an item with the details selected by the user and add it to the list of items.
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pickedAppliance = applianceNames.getSelectedItem().toString();
                String pickedStartHour = startHour.getSelectedItem().toString();
                String pickedStartMinute = startMinute.getSelectedItem().toString();
                String pickedDurationHour = durationHour.getSelectedItem().toString();
                String pickedDurationMinute = durationMinute.getSelectedItem().toString();
                String pickedFlexHour = flexHour.getSelectedItem().toString();
                String pickedFlexMinute = flexMinute.getSelectedItem().toString();
                int sHour = Integer.parseInt(pickedStartHour);
                int sMinute = Integer.parseInt(pickedStartMinute);
                int dHour = Integer.parseInt(pickedDurationHour);
                int dMinute = Integer.parseInt(pickedDurationMinute);

                int rangeInHours = Integer.parseInt(pickedFlexHour);
                int rangeRemainingMinutes = Integer.parseInt(pickedFlexMinute);
                int flexibility= 1;
                if(rangeInHours==0&&rangeRemainingMinutes==0){
                    flexibility=0;
                }
                boolean errorAdd = false;
                if(dHour ==0){
                    if(dMinute==0){
                        errorAdd = true;
                        String toastString = "This task has no duration.";
                        int durationOfToast = Toast.LENGTH_SHORT;
                        Context context = getActivity();
                        Toast toast = Toast.makeText(context, toastString, durationOfToast);
                        toast.show();
                    }
                }
                if(flexibility==0){
                    if(dMinute+sMinute>=60){
                        if(sHour+dHour+1>24){
                            errorAdd = true;
                            String toastString = "This task cannot fit in the available time.";
                            int durationOfToast = Toast.LENGTH_SHORT;
                            Context context = getActivity();
                            Toast toast = Toast.makeText(context, toastString, durationOfToast);
                            toast.show();
                        }
                    }else{
                        if(sHour+dHour>24){
                            errorAdd = true;
                            String toastString = "This task cannot fit in the available time.";
                            int durationOfToast = Toast.LENGTH_SHORT;
                            Context context = getActivity();
                            Toast toast = Toast.makeText(context, toastString, durationOfToast);
                            toast.show();
                        }
                    }
                }else{
                    if(cbBeforeAndAfter.isChecked()){
                        if(sMinute-rangeRemainingMinutes<0){
                            if((sHour-rangeInHours-1)+dHour>24){
                                errorAdd = true;
                                String toastString = "This task cannot fit in the available time.";
                                int durationOfToast = Toast.LENGTH_SHORT;
                                Context context = getActivity();
                                Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                toast.show();
                            }
                        }else{
                            if((sHour-rangeInHours)+dHour>24){
                                errorAdd = true;
                                String toastString = "This task cannot fit in the available time.";
                                int durationOfToast = Toast.LENGTH_SHORT;
                                Context context = getActivity();
                                Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                toast.show();
                            }
                        }

                    }else{
                        if(cbAfterOnly.isChecked()){
                            if(dMinute+sMinute>=60){
                                if(sHour+dHour+1>24){
                                    errorAdd = true;
                                    String toastString = "This task cannot fit in the available time.";
                                    int durationOfToast = Toast.LENGTH_SHORT;
                                    Context context = getActivity();
                                    Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                    toast.show();
                                }
                            }else{
                                if(sHour+dHour>24){
                                    errorAdd = true;
                                    String toastString = "This task cannot fit in the available time.";
                                    int durationOfToast = Toast.LENGTH_SHORT;
                                    Context context = getActivity();
                                    Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                    toast.show();
                                }
                            }
                        }else{
                            if(cbBeforeOnly.isChecked()){
                                if(sMinute-rangeRemainingMinutes<0){
                                    if((sHour-rangeInHours-1)+dHour>24){
                                        errorAdd = true;
                                        String toastString = "This task cannot fit in the available time.";
                                        int durationOfToast = Toast.LENGTH_SHORT;
                                        Context context = getActivity();
                                        Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                        toast.show();
                                    }
                                }else{
                                    if((sHour-rangeInHours)+dHour>24){
                                        errorAdd = true;
                                        String toastString = "This task cannot fit in the available time.";
                                        int durationOfToast = Toast.LENGTH_SHORT;
                                        Context context = getActivity();
                                        Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                        toast.show();
                                    }
                                }
                            }
                        }
                    }
                }
                if(errorAdd){
                    //message to say it is an error
                }else{
                    int startTimeHour=0,startTimeMinute=0,endTimeHour=23,endTimeMinute=59;
                    if(cbBeforeAndAfter.isChecked()){
                        if(sHour-rangeInHours>=0){
                            if(sMinute-rangeRemainingMinutes<0){
                                if(sHour-rangeInHours==0){
                                    startTimeHour = 0;
                                    startTimeMinute = 0;
                                }else{
                                    int result = rangeRemainingMinutes-sMinute;
                                    startTimeMinute = 60-result;
                                    startTimeHour = sHour-rangeInHours-1;
                                }
                            }else{
                                startTimeHour = sHour-rangeInHours;
                                startTimeMinute = sMinute-rangeRemainingMinutes;
                            }
                        }
                        if(sHour+rangeInHours+dHour>=24){
                            if(sMinute+rangeRemainingMinutes+dMinute>=60){
                                endTimeHour = 23;
                                endTimeMinute = 59;
                            }else{
                                if(sMinute+rangeRemainingMinutes+dMinute<60){
                                    endTimeHour = 23;
                                    endTimeMinute = sMinute+rangeRemainingMinutes;
                                }
                            }
                        }else{
                            if(sMinute+rangeRemainingMinutes+dMinute>=60){
                                if(sHour+rangeInHours+dHour+1>=24){
                                    endTimeHour = 23;
                                    endTimeMinute = 59;
                                }else{
                                    endTimeHour = sHour+rangeInHours+dHour+1;
                                    endTimeMinute = (sMinute+rangeRemainingMinutes+dMinute)%60;
                                }
                            }else{
                                endTimeHour = sHour+rangeInHours+dHour;
                                endTimeMinute = (sMinute+rangeRemainingMinutes+dMinute)%60;
                            }
                        }
                    }else{
                        if(cbAfterOnly.isChecked()){
                            startTimeHour = sHour;
                            startTimeMinute = sMinute;
                            if(sHour+rangeInHours+dHour>=24){
                                if(sMinute+rangeRemainingMinutes+dMinute>=60){
                                    endTimeHour = 23;
                                    endTimeMinute = 59;
                                }else{
                                    if(sMinute+rangeRemainingMinutes+dMinute<60){
                                        endTimeHour = 23;
                                        endTimeMinute = sMinute+rangeRemainingMinutes;
                                    }
                                }
                            }else{
                                if(sMinute+rangeRemainingMinutes+dMinute>=60){
                                    if(sHour+rangeInHours+dHour+1>=24){
                                        endTimeHour = 23;
                                        endTimeMinute = 59;
                                    }else{
                                        endTimeHour = sHour+rangeInHours+dHour+1;
                                        endTimeMinute = (sMinute+rangeRemainingMinutes+dMinute)%60;
                                    }
                                }else{
                                    endTimeHour = sHour+rangeInHours+dHour;
                                    endTimeMinute = (sMinute+rangeRemainingMinutes+dMinute)%60;
                                }
                            }
                        }else{
                            if(cbBeforeOnly.isChecked()){
                                if(sHour+dHour<24){
                                    if(sMinute+dMinute<60){
                                        endTimeHour = sHour+dHour;
                                        endTimeMinute = sMinute+dMinute;
                                    }else{
                                        if(sHour+dHour+1<24){
                                            endTimeHour = sHour+dHour+1;
                                            endTimeMinute = (sMinute+dMinute)%60;
                                        }else{
                                            endTimeHour =23;
                                            endTimeMinute = 59;
                                        }
                                    }
                                }else{
                                    endTimeHour =23;
                                    endTimeMinute = 59;
                                }
                                if(sHour-rangeInHours>=0){
                                    if(sMinute-rangeRemainingMinutes<0){
                                        if(sHour-rangeInHours==0){
                                            startTimeHour = 0;
                                            startTimeMinute = 0;
                                        }else{
                                            int result = rangeRemainingMinutes-sMinute;
                                            startTimeMinute = 60-result;
                                            startTimeHour = sHour-rangeInHours-1;
                                        }
                                    }else{
                                        startTimeHour = sHour-rangeInHours;
                                        startTimeMinute = sMinute-rangeRemainingMinutes;
                                    }
                                }
                            }
                        }
                    }
                    String timeWindowStart,timeWindowEnd,durationInput,flexString,optimalStartTime;
                    if(startTimeHour>9){
                        if(startTimeMinute>9){
                            timeWindowStart = startTimeHour+":"+startTimeMinute;
                        }else{
                            timeWindowStart = startTimeHour+":0"+startTimeMinute;
                        }
                    }else{
                        if(startTimeMinute>9){
                            timeWindowStart = "0"+startTimeHour+":"+startTimeMinute;
                        }else{
                            timeWindowStart = "0"+startTimeHour+":0"+startTimeMinute;
                        }
                    }
                    if(endTimeHour>9){
                        if(endTimeMinute>9){
                            timeWindowEnd = endTimeHour+":"+endTimeMinute;
                        }else{
                            timeWindowEnd = endTimeHour+":0"+endTimeMinute;
                        }
                    }else{
                        if(endTimeMinute>9){
                            timeWindowEnd = "0"+endTimeHour+":"+endTimeMinute;
                        }else{
                            timeWindowEnd = "0"+endTimeHour+":0"+endTimeMinute;
                        }
                    }
                    if(dHour>9){
                        if(dMinute>9){
                            durationInput = dHour+":"+dMinute;
                        }else{
                            durationInput = dHour+":0"+dMinute;
                        }
                    }else{
                        if(dMinute>9){
                            durationInput = "0"+dHour+":"+dMinute;
                        }else{
                            durationInput = "0"+dHour+":0"+dMinute;
                        }
                    }
                    if(rangeInHours>9){
                        if(rangeRemainingMinutes>9){
                            flexString = rangeInHours+":"+rangeRemainingMinutes;
                        }else{
                            flexString = rangeInHours+":0"+rangeRemainingMinutes;
                        }
                    }else{
                        if(rangeRemainingMinutes>9){
                            flexString = "0"+rangeInHours+":"+rangeRemainingMinutes;
                        }else{
                            flexString = "0"+rangeInHours+":0"+rangeRemainingMinutes;
                        }
                    }
                    if(sHour>9){
                        if(sMinute>9){
                            optimalStartTime = sHour+":"+sMinute;
                        }else{
                            optimalStartTime = sHour+":0"+sMinute;
                        }
                    }else{
                        if(sMinute>9){
                            optimalStartTime = "0"+sHour+":"+sMinute;
                        }else{
                            optimalStartTime = "0"+sHour+":0"+sMinute;
                        }
                    }
                    boolean parallel;
                    switch(pickedAppliance){
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
                    Action newAction =new Action(pickedAppliance,timeWindowStart,timeWindowEnd,durationInput,optimalStartTime,parallel);
                    list.add(newAction);
                    addItem(pickedAppliance,timeWindowStart,durationInput,flexString,layoutView, newAction);
                    //add the action to the schedule
                }
            }
        });
        if(!cbBeforeAndAfter.isChecked()){
            cbBeforeAndAfter.toggle();
        }
        cbBeforeAndAfter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbAfterOnly.isChecked()){
                    cbAfterOnly.toggle();
                }
                if(cbBeforeOnly.isChecked()){
                    cbBeforeOnly.toggle();
                }
                TextView tx = (TextView) layoutView.findViewById(R.id.titleFlexibility);
                String beforeAndAfter = getActivity().getResources().getString(R.string.window_size_title);
                tx.setText(beforeAndAfter);
            }
        });
        cbBeforeOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbAfterOnly.isChecked()){
                    cbAfterOnly.toggle();
                }
                if(cbBeforeAndAfter.isChecked()){
                    cbBeforeAndAfter.toggle();
                }
                TextView tx = (TextView) layoutView.findViewById(R.id.titleFlexibility);
                String before = getActivity().getResources().getString(R.string.window_size_title_before);
                tx.setText(before);
            }
        });
        cbAfterOnly.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cbBeforeOnly.isChecked()){
                    cbBeforeOnly.toggle();
                }
                if(cbBeforeAndAfter.isChecked()){
                    cbBeforeAndAfter.toggle();
                }
                TextView tx = (TextView) layoutView.findViewById(R.id.titleFlexibility);
                String after = getActivity().getResources().getString(R.string.window_size_title_after);
                tx.setText(after);
            }
        });

        //Once all items are added, this will start the background tasks for the possible plan generation and dismiss the fragment.
        Button submit = (Button) layoutView.findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity passMain = ((MainActivity)getActivity());
                passMain.cancelBackgroundTasks();
                settings.edit().putBoolean("defaultBool", true).commit();
                try {
                    Context context = getActivity();
                    list.removeAll(Collections.singleton(null));
                    list.trimToSize();

                    Action[] array = new Action[list.size()];
                    list.toArray(array);
                    if(array.length>0){
                        passMain.callBackgroundTasks(array,5);
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String time = simpleDateFormat.format(new Date());
                        char[] timeChars = time.toCharArray();
                        String date = ""+timeChars[0]+timeChars[1]+timeChars[2]+timeChars[3]+timeChars[4]+timeChars[5]+timeChars[6]+timeChars[7]+timeChars[8]+timeChars[9];
                        try{
                            FileOutputStream fos = getActivity().openFileOutput("lastSendPressDayDate.txt", Context.MODE_PRIVATE);
                            fos.write(date.getBytes());
                            fos.close();
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        dismiss();
                    }else{
                        String toastString = "No input";
                        int durationOfToast = Toast.LENGTH_SHORT;
                        Toast toast = Toast.makeText(context, toastString, durationOfToast);
                        toast.show();
                    }
                } catch (NullPointerException ex) {
                    String toastString = "Error Send.";
                    Context context = getActivity();
                    int durationOfToast = Toast.LENGTH_SHORT;
                    Toast toast = Toast.makeText(context, toastString, durationOfToast);
                    toast.show();
                    ex.printStackTrace();
                }

            }
        });

        return layoutView;
    }

    //add the item with these parameters to the list of current items.
    public void addItem(String name, String start,String dur, String flex, View va, Action a){
        final View passView =va;
        String currentItems = "";
        try{
            FileInputStream fis = getActivity().openFileInput("currentItems.txt");
            int ch;
            StringBuilder builder = new StringBuilder();
            while((ch=fis.read())!=-1){
                builder.append((char)ch);
            }
            currentItems = builder.toString();
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        int numberOfRows = 1;
        if(!currentItems.equals("")){
            String[] rowCount = currentItems.split("\n");
            numberOfRows = rowCount.length;
        }
        GridLayout displayGrid = (GridLayout)va.findViewById(R.id.displayGrid);
        TextView actionName = new TextView(context);
        actionName.setText(name);
        GridLayout.Spec row = GridLayout.spec(numberOfRows);
        GridLayout.Spec col = GridLayout.spec(0);
        GridLayout.LayoutParams gridparams = new GridLayout.LayoutParams(row,col);
        gridparams.rightMargin = 10;
        displayGrid.addView(actionName,gridparams);


        TextView actionStart = new TextView(context);
        actionStart.setText(start);
        col = GridLayout.spec(1);
        gridparams = new GridLayout.LayoutParams(row,col);
        gridparams.rightMargin = 10;
        displayGrid.addView(actionStart,gridparams);

        TextView actionDur = new TextView(context);
        actionDur.setText(dur);
        col = GridLayout.spec(2);
        gridparams = new GridLayout.LayoutParams(row,col);
        gridparams.rightMargin = 10;
        displayGrid.addView(actionDur,gridparams);

        TextView actionFlex = new TextView(context);
        actionFlex.setText(flex);
        col = GridLayout.spec(3);
        gridparams = new GridLayout.LayoutParams(row,col);
        gridparams.rightMargin = 10;
        displayGrid.addView(actionFlex,gridparams);
        FileOutputStream fileOutputStream;

        final String associatedName = name,associatedStart = start,associatedDuration = dur,associatedFlex = flex;
        final Action associatedAction = a;
        Button b = new Button(context);
        b.setText("-");
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItem(associatedName,associatedStart,associatedDuration,associatedFlex,passView,associatedAction);
                list.remove(associatedAction);
            }
        });
        col = GridLayout.spec(4);
        gridparams = new GridLayout.LayoutParams(row,col);
        displayGrid.addView(b,gridparams);
        try{
            fileOutputStream = getActivity().openFileOutput("currentItems.txt", Context.MODE_APPEND);
            String submit = name+","+start+","+dur+","+flex+"\n";
            fileOutputStream.write(submit.getBytes());
            fileOutputStream.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //Remove an item from the list of current items with the following parameters.
    public void removeItem(String name, String start,String dur, String flex, View v,Action a){
        final View passView =v;
        String currentItems = "";
        final Action associatedAction =a;
        try{
            FileInputStream fis = getActivity().openFileInput("currentItems.txt");
            int ch;
            StringBuilder builder = new StringBuilder();
            while((ch=fis.read())!=-1){
                builder.append((char)ch);
            }
            currentItems = builder.toString();
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        int numberOfRows = 0;
        String[] rowCount = new String[1];
        if(!currentItems.equals("")){
            rowCount = currentItems.split("\n");
            numberOfRows = rowCount.length;
        }
        String[][] data = new String[numberOfRows][];
        for(int i =0;i<numberOfRows;i++){
            data[i] = rowCount[i].split(",");
        }
        String[][] dataPostRemoval = new String[data.length-1][];
        boolean removed = false;
        for(int i =0; i<data.length;i++){
            if(data[i][0].equals(name)){
                if(data[i][1].equals(start)){
                    if(data[i][2].equals(dur)){
                        if(data[i][3].equals(flex)) {
                            if (!removed){
                                data[i][0] = "0";
                                removed=true;
                            }
                        }
                    }
                }
            }
        }
        int j = 0;
        for(int i =0; i<data.length;i++){
            if(data[i][0].equals("0")){

            }else{
                try{
                    dataPostRemoval[j] = data[i];
                    j++;
                }catch(Exception e){
                    e.printStackTrace();
                }

            }
        }
        //reset the view and display it again with the old item removed.
        GridLayout displayGrid = (GridLayout)v.findViewById(R.id.displayGrid);
        displayGrid.removeAllViews();
        for(int i = 0 ; i < dataPostRemoval.length ; i++){
            final String associatedName = dataPostRemoval[i][0],associatedStart = dataPostRemoval[i][1],associatedDuration =dataPostRemoval[i][2],associatedFlex = dataPostRemoval[i][3];
            TextView actionName = new TextView(context);
            if(i==0){
                actionName.setTextSize(15);
                actionName.setTextColor(getResources().getColor(R.color.black));
            }
            actionName.setText(dataPostRemoval[i][0]);
            GridLayout.Spec row = GridLayout.spec(i);
            GridLayout.Spec col = GridLayout.spec(0);
            GridLayout.LayoutParams gridparams = new GridLayout.LayoutParams(row,col);
            gridparams.rightMargin = 20;
            displayGrid.addView(actionName,gridparams);

            TextView actionStart = new TextView(context);
            if(i==0){
                actionStart.setTextSize(15);
                actionStart.setTextColor(getResources().getColor(R.color.black));
            }
            actionStart.setText(dataPostRemoval[i][1]);
            col = GridLayout.spec(1);
            gridparams = new GridLayout.LayoutParams(row,col);
            gridparams.rightMargin = 15;
            displayGrid.addView(actionStart,gridparams);

            TextView actionDur = new TextView(context);
            if(i==0){
                actionDur.setTextSize(15);
                actionDur.setTextColor(getResources().getColor(R.color.black));
            }
            actionDur.setText(dataPostRemoval[i][2]);
            col = GridLayout.spec(2);
            gridparams = new GridLayout.LayoutParams(row,col);
            gridparams.rightMargin = 15;
            displayGrid.addView(actionDur,gridparams);

            TextView actionFlex = new TextView(context);
            if(i==0){
                actionFlex.setTextSize(15);
                actionFlex.setTextColor(getResources().getColor(R.color.black));
            }
            actionFlex.setText(dataPostRemoval[i][3]);
            col = GridLayout.spec(3);
            gridparams = new GridLayout.LayoutParams(row,col);
            displayGrid.addView(actionFlex,gridparams);

            if(i!=0){
                Button b = new Button(context);

                b.setText("-");
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeItem(associatedName,associatedStart,associatedDuration,associatedFlex, passView,associatedAction);
                        list.remove(associatedAction);
                    }
                });
                col = GridLayout.spec(4);
                gridparams = new GridLayout.LayoutParams(row,col);
                displayGrid.addView(b,gridparams);
            }


        }
        String submit = "";
        try{
            FileOutputStream fileOutputStream = getActivity().openFileOutput("currentItems.txt", Context.MODE_PRIVATE);
            StringBuilder submitBuilder = new StringBuilder();
            for(int i =0;i<dataPostRemoval.length;i++){
                submitBuilder.append(dataPostRemoval[i][0]+","+dataPostRemoval[i][1]+","+dataPostRemoval[i][2]+","+dataPostRemoval[i][3]+"\n");
            }
            submit = submitBuilder.toString();
            fileOutputStream.write(submit.getBytes());
            fileOutputStream.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}