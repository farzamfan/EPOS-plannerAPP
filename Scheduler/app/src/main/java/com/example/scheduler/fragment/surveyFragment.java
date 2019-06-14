package com.example.scheduler.fragment;


import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.scheduler.Interface.MyDialogCloseListener;
import com.example.scheduler.MainActivity;
import com.example.scheduler.ObjectGroup.ToggleButtonsGroup;
import com.example.scheduler.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by warrens on 10.08.17.
 */

//check which page the survey should show, then save the resulting answers from the user.
//During questions about the house details, try match the user to a REFIT data house.

public class surveyFragment extends Fragment {
    public MyDialogCloseListener closeListener;
    public final String PREFS_NAME = "MyPrefsFile";
    public SharedPreferences settings;
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setMainLayoutViewInvisible(true);
        View v = null;
        final Fragment thisFrag = this;
        if(!((MainActivity)getActivity()).showSurvey()){
            ((MainActivity)getActivity()).setMainLayoutViewInvisible(false);
            ((MainActivity)getActivity()).setFabRevealFabsVisibility(true);
            getActivity().getSupportFragmentManager().beginTransaction().remove(thisFrag).commit();
        }else{
            settings = getActivity().getSharedPreferences(PREFS_NAME, getActivity().MODE_PRIVATE);
            FloatingActionButton fab = ((MainActivity)getActivity()).getFabRevealFabs();
            fab.setVisibility(View.INVISIBLE);
            fab.setClickable(false);

            Context context = getActivity();
            int nextScreen;
            String surveyProgress = "0";
            try{
                FileInputStream fis = getActivity().openFileInput("surveyProgress.txt");
                int chr;
                StringBuilder builder = new StringBuilder();
                while ((chr = fis.read()) != -1) {
                    builder.append((char) chr);
                }
                surveyProgress = builder.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            nextScreen = Integer.parseInt(surveyProgress);
            switch(nextScreen){
                case 2:
                    v = inflater.inflate(R.layout.survey_fragment_part_two, container, false);
                    final String[] QID = {"5","6","7","8"};
                    TextView txEducationQ = (TextView) v.findViewById(R.id.educationLevelQ);
                    txEducationQ.setText("What is the highest level of education you have completed?");
                    TextView txEmploymentStatusQ = (TextView) v.findViewById(R.id.employmentStatusQ);
                    txEmploymentStatusQ.setText("Which of the following best describes your employment status?");
                    TextView txHouseTypeQ = (TextView) v.findViewById(R.id.houseTypeQ);
                    txHouseTypeQ.setText("What type of house do you live in?");
                    TextView txHouseSizeQ = (TextView) v.findViewById(R.id.houseSizeQ);
                    txHouseSizeQ.setText("What size is your house?");

                    final Spinner spEducationA = (Spinner) v.findViewById(R.id.educationLevelA);
                    final Spinner spHouseTypeA = (Spinner)v.findViewById(R.id.houseTypeA);
                    final Spinner spHouseSizeA = (Spinner) v.findViewById(R.id.houseSizeA);

                    ArrayAdapter<String> educationLevelQ;
                    ArrayAdapter<String> houseTypeQ;
                    ArrayAdapter<String> houseSizeQ;

                    String[] educationLevels = {
                            "-",
                            "Level 1 - Primary Education",
                            "Level 2 - Lower Secondary Education",
                            "Level 3 - Upper Secondary Education",
                            "Level 4 - Post-Secondary Non-Tertiary Education",
                            "Level 5 - Short Cycle Tertiary Education",
                            "Level 6 - Bachelor's or equivalent level",
                            "Level 7 - Master's or equivalent level",
                            "Level 8 - Doctoral or equivalent level"
                    };

                    educationLevelQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,educationLevels);
                    educationLevelQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spEducationA.setAdapter(educationLevelQ);


                    String[] houseTypes = {
                            "-",
                            "Detached",
                            "Semi-detached",
                            "Mid-terrace",
                            "Apartment/Flat",
                            "Other"
                    };

                    houseTypeQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,houseTypes);
                    houseTypeQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spHouseTypeA.setAdapter(houseTypeQ);


                    String[] houseSizes = {
                            "-",
                            "1 Bed",
                            "2 Bed",
                            "3 Bed",
                            "4 Bed",
                            "5 Bed",
                            "6 Bed +"
                    };

                    houseSizeQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,houseSizes);
                    houseSizeQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spHouseSizeA.setAdapter(houseSizeQ);



                    final ToggleButton[] bEmploymentStatusA = new ToggleButton[5];
                    bEmploymentStatusA[0] = (ToggleButton)v.findViewById(R.id.employmentStatusA1);
                    bEmploymentStatusA[0].setTextSize(10);
                    bEmploymentStatusA[0].setText("Full-time");
                    bEmploymentStatusA[0].setTextOn("Full-time");
                    bEmploymentStatusA[0].setTextOff("Full-time");

                    bEmploymentStatusA[1] = (ToggleButton)v.findViewById(R.id.employmentStatusA2);
                    bEmploymentStatusA[1].setTextSize(10);
                    bEmploymentStatusA[1].setText("Part-Time");
                    bEmploymentStatusA[1].setTextOn("Part-Time");
                    bEmploymentStatusA[1].setTextOff("Part-Time");

                    bEmploymentStatusA[2] = (ToggleButton)v.findViewById(R.id.employmentStatusA3);
                    bEmploymentStatusA[2].setTextSize(10);
                    bEmploymentStatusA[2].setText("Self-Employed");
                    bEmploymentStatusA[2].setTextOn("Self-Employed");
                    bEmploymentStatusA[2].setTextOff("Self-Employed");

                    bEmploymentStatusA[3] = (ToggleButton)v.findViewById(R.id.employmentStatusA4);
                    bEmploymentStatusA[3].setTextSize(10);
                    bEmploymentStatusA[3].setText("Student");
                    bEmploymentStatusA[3].setTextOn("Student");
                    bEmploymentStatusA[3].setTextOff("Student");

                    bEmploymentStatusA[4] = (ToggleButton)v.findViewById(R.id.employmentStatusA5);
                    bEmploymentStatusA[4].setTextSize(10);
                    bEmploymentStatusA[4].setText("Unemployed");
                    bEmploymentStatusA[4].setTextOn("Unemployed");
                    bEmploymentStatusA[4].setTextOff("Unemployed");

                    ToggleButtonsGroup toggleButtonsGroup = new ToggleButtonsGroup();
                    toggleButtonsGroup.addButton(bEmploymentStatusA[0]);
                    toggleButtonsGroup.addButton(bEmploymentStatusA[1]);
                    toggleButtonsGroup.addButton(bEmploymentStatusA[2]);
                    toggleButtonsGroup.addButton(bEmploymentStatusA[3]);
                    toggleButtonsGroup.addButton(bEmploymentStatusA[4]);


                    Button next = (Button) v.findViewById(R.id.toPartThree);

                    next.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectedEducationLevel = spEducationA.getSelectedItem().toString();
                            String selectedHouseType = spHouseTypeA.getSelectedItem().toString();
                            String selectedHouseSize = spHouseSizeA.getSelectedItem().toString();
                            String selectedEmploymentStatus = "-";
                            for(int i = 0; i<bEmploymentStatusA.length;i++){
                                if(bEmploymentStatusA[i].isChecked()){
                                    selectedEmploymentStatus = bEmploymentStatusA[i].getTextOn().toString();
                                    break;
                                }
                            }
                            if(selectedEducationLevel.equals("-")||selectedEmploymentStatus.equals("-")||selectedHouseType.equals("-")||selectedHouseSize.equals("-")){
                                String toastString = "Please answer all questions.";
                                int durationOfToast = Toast.LENGTH_SHORT;
                                Context context = getActivity();
                                Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                toast.show();
                            }else{
                                String submitAnswers = QID[0]+","+selectedEducationLevel+"\n"+
                                        QID[1]+","+selectedEmploymentStatus+"\n"+
                                        QID[2]+","+selectedHouseType+"\n"+
                                        QID[3]+","+selectedHouseSize+"\n";

                                String answersForHouseMatching = selectedHouseType+","+selectedHouseSize;
                                try{
                                    FileOutputStream fos = getActivity().openFileOutput("answersForHouseMatching.txt",Context.MODE_PRIVATE);
                                    fos.write(answersForHouseMatching.getBytes());
                                    fos.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                    String nextScreen = 3+"";
                                    fos.write(nextScreen.getBytes());
                                    fos.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                    fos.write(submitAnswers.getBytes());
                                    fos.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                Fragment stageTwo = new surveyFragment();
                                FragmentManager fragManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                                fragmentTransaction.commit();
                            }
                        }
                    });
                    break;
                case 3:
                    v = inflater.inflate(R.layout.survey_fragment_part_three, container, false);
                    final String[] QIDS = {
                            "5",
                            "6"
                    };
                    String[] houseAges = new String[]{
                            "-",
                            "Pre 1900s",
                            "1900 - 1909",
                            "1910 - 1919",
                            "1920 - 1929",
                            "1930 - 1939",
                            "1940 - 1949",
                            "1950 - 1959",
                            "1960 - 1969",
                            "1970 - 1979",
                            "1980 - 1989",
                            "1990 - 1999",
                            "2000 - 2009",
                            "2010+"
                    };
                    String[] numberOfOccupants = new String[]{
                            "-",
                            "1",
                            "2",
                            "3",
                            "4",
                            "5",
                            "6+"
                    };
                    TextView txHouseAgeQ = (TextView) v.findViewById(R.id.houseAgeQ);
                    txHouseAgeQ.setText("Approximately when was your house built?");
                    TextView txHouseOccupantNumberQ = (TextView) v.findViewById(R.id.houseOccupantNumberQ);
                    txHouseOccupantNumberQ.setText("How many people live in your house?");

                    final Spinner spHouseAgeA = (Spinner) v.findViewById(R.id.houseAgeA);
                    final Spinner spHouseOccupantNumberA = (Spinner) v.findViewById(R.id.houseOccupantNumberA);

                    ArrayAdapter<String> houseAgeQ;
                    ArrayAdapter<String> houseOccupantNumberQ;

                    houseAgeQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,houseAges);
                    houseAgeQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spHouseAgeA.setAdapter(houseAgeQ);

                    houseOccupantNumberQ = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item,numberOfOccupants);
                    houseOccupantNumberQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spHouseOccupantNumberA.setAdapter(houseOccupantNumberQ);

                    Button bFour = (Button) v.findViewById(R.id.toPartFour);
                    bFour.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectedHouseAge = spHouseAgeA.getSelectedItem().toString();
                            String selectedOccupantNumber = spHouseOccupantNumberA.getSelectedItem().toString();
                            if(selectedHouseAge.equals("-")||selectedOccupantNumber.equals("-")){
                                String toastString = "Please answer all questions.";
                                int durationOfToast = Toast.LENGTH_SHORT;
                                Context context = getActivity();
                                Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                toast.show();
                            }else{
                                String results = "";
                                try{
                                    FileInputStream fis = getActivity().openFileInput("answersForHouseMatching.txt");
                                    int chr;
                                    StringBuilder builder = new StringBuilder();
                                    while ((chr = fis.read()) != -1) {
                                        builder.append((char) chr);
                                    }
                                    results = builder.toString();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                String[] temp = results.split(",");
                                String[] matchingFactors = new String[4];
                                matchingFactors[0] = temp[0];//Type
                                matchingFactors[1] = temp[1];//Size
                                matchingFactors[2] = selectedHouseAge;//Age
                                matchingFactors[3] = selectedOccupantNumber;//Occupancy

                                String[] houseWattages = {
                                /*
                                "Hob","Oven","TumbleDryer","WashingMachine","Computer","Kettle","DishWasher","Shower"
                                 */
                                        //House Number,Cooker (Hob),Cooker (Oven),Tumble Dryer, Washing Machine, Computer, Kettle,Dishwasher,Shower
                                        "1,1000,3000,472,513,29,1800,1379,9000",
                                        "2,1000,3000,2500,327,75,2257,770,9000",
                                        "3,1000,3000,1373,492,16,1550,1150,9000",
                                        "4,1000,3000,2500,700,52,1703,1350,9000",
                                        "5,1000,3000,766,700,66,2352,1350,9000",
                                        "6,1000,3000,2500,369,66,2192,778,9000",
                                        "7,1000,3000,2075,442,75,1913,613,9000",
                                        "8,1000,3000,2500,273,19,2340,1350,9000",
                                        "9,1000,3000,2500,507,75,2359,700,9000",
                                        "10,1000,3000,2500,349,75,1800,1350,9000",
                                        "11,1000,3000,2500,700,10,1841,753,9000",
                                        "12,1000,3000,2500,700,75,2482,1350,9000",
                                        "13,1000,3000,1510,203,39,1542,1250,9000",
                                        "15,1000,3000,1476,495,20,2521,1350,9000",
                                        "16,1000,3000,2500,300,27,1800,1239,9000",
                                        "17,1000,3000,1594,373,20,1689,1350,9000",
                                        "18,1000,3000,2500,377,26,1800,1021,9000",
                                        "19,1000,3000,2500,700,75,2448,1350,9000",
                                        "20,1000,3000,1097,293,75,2350,1350,9000",
                                        "21,1000,3000,1240,434,75,1276,1350,9000",
                                        "Default,1000,3000,2500,700,75,1800,1350,9000"};


                                String[] houseDataArray = {
                                        //0,        	1,      2,      	3,          4,    	5,          6
                                        //number,   occupancy, age, # of Appliances,    type,   size,   approx age
                                        "1,2,1975-1980,35,Detached,4 bed,1970 - 1979",
                                        "2,4,-,15,Semi-detached,3 bed,-",
                                        "3,2,1988,27,Detached,3 bed,1980 - 1989",
                                        "4,2,1850-1899,33,Detached,4 bed,Pre 1900s",
                                        "5,4,1878,44,Mid-terrace,4 bed,Pre 1900s",
                                        "6,2,2005,49,Detached,4 bed,2000 - 2009",
                                        "7,4,1965-1974,25,Detached,3 bed,1960 - 1969",
                                        "8,2,1966,35,Detached,2 bed,1960 - 1969",
                                        "9,2,1919-1944,24,Detached,3 bed,1920 - 1929",
                                        "10,4,1919-1944,31,Detached,3 bed,1920 - 1929",
                                        "11,1,1945-1964,25,Detached,3 bed,1950 - 1959",
                                        "12,3,1991-1995,26,Detached,3 bed,1990 - 1999",
                                        "13,4,post 2002,28,Detached,4 bed,2000 - 2010",
                                        "15,1,1965-1974,19,Semi-detached,3 bed,1960 - 1969",
                                        "16,6,1981-1990,48,Detached,5 bed,1980 - 1989",
                                        "17,3,mid 60s,22,Detached,3 bed,1960 - 1969",
                                        "18,2,1965-1974,34,Detached,3 bed,1960 - 1969",
                                        "19,4,1945-1964,26,Semi-detached,3 bed,1950 - 1959",
                                        "20,2,1965-1974,39,Detached,3 bed,1960 - 1969",
                                        "21,4,1981-1990,23,Detached,3 bed,1980 - 1989"};

                                String houseToUse = "";
                                double[] houseCount = {
                                        0,//House 1
                                        0,//House 2
                                        0,//House 3
                                        0,//House 4
                                        0,//House 5
                                        0,//House 6
                                        0,//House 7
                                        0,//House 8
                                        0,//House 9
                                        0,//House 10
                                        0,//House 11
                                        0,//House 12
                                        0,//House 13
                                        0,//House 15
                                        0,//House 16
                                        0,//House 17
                                        0,//House 18
                                        0,//House 19
                                        0,//House 20
                                        0 //House 21
                                };
                                String[][] houseDataArraySplit = new String[houseDataArray.length][];
                                for(int i =0; i<houseDataArraySplit.length;i++){
                                    houseDataArraySplit[i] = houseDataArray[i].split(",");
                                }
                                for(int i = 0; i<houseDataArraySplit.length;i++){
                                    //Occupancy
                                    if(matchingFactors[3].equals(houseDataArraySplit[i][1])){
                                        houseCount[i]+=0.533;
                                    }
                                    //Year Built
                                    if(matchingFactors[2].equals(houseDataArraySplit[i][6])){
                                        houseCount[i]+=0.067;
                                    }
                                    //Size
                                    if(matchingFactors[1].equals(houseDataArraySplit[i][5])){
                                        houseCount[i]+=0.267;
                                    }
                                    //House Type
                                    if(matchingFactors[0].equals(houseDataArraySplit[i][4])){
                                        houseCount[i]+=0.133;
                                    }
                                }
                                int closestHouseIndex = 0;
                                for(int i = 1; i<houseCount.length;i++){
                                    double newNumber = houseCount[i];
                                    if(newNumber>houseCount[closestHouseIndex]){
                                        closestHouseIndex = i;
                                    }
                                }
                                String wattages = "";
                                if(houseCount[closestHouseIndex]<0.5){
                                    wattages = houseWattages[houseWattages.length-1];
                                }else{
                                    wattages = houseWattages[closestHouseIndex];
                                }

                                String wattageFile = "wattagesFile.txt";

                                FileOutputStream fOut;
                                try{
                                    fOut = getActivity().openFileOutput(wattageFile, Context.MODE_PRIVATE);
                                    fOut.write(wattages.getBytes());
                                    fOut.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }

                                String submit = QIDS[0]+","+selectedHouseAge+"\n"+
                                        QIDS[1]+","+selectedOccupantNumber+"\n";
                                try{
                                    FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                    String nextScreen = 4+"";
                                    fos.write(nextScreen.getBytes());
                                    fos.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                    fos.write(submit.getBytes());
                                    fos.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                Fragment stageTwo = new surveyFragment();
                                FragmentManager fragManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                                fragmentTransaction.commit();
                            }
                        }
                    });
                    break;
                case 4:
                    v = inflater.inflate(R.layout.survey_fragment_part_four, container, false);
                    final String[] Q = {
                            "11-1",
                            "11-2",
                            "11-3",
                            "11-4",
                            "11-5",
                            "11-6",
                            "11-7",
                            "11-8",
                            "11-9",
                            "11-10",
                            "11-11",
                            "11-12",
                            "11-13",
                            "11-14"
                    };

                    TextView txWhichAppliancesQ = (TextView)v.findViewById(R.id.appliancesOwnedQ);
                    txWhichAppliancesQ.setText("Which appliances do you have at home?");

                    final CheckBox cbWashingMachine = (CheckBox)v.findViewById(R.id.washingMachine);
                    cbWashingMachine.setText("Washing Machine");
                    final CheckBox cbTumbleDryer = (CheckBox)v.findViewById(R.id.tumbleDryer);
                    cbTumbleDryer.setText("Tumble Dryer");
                    final CheckBox cbComputerLaptop = (CheckBox)v.findViewById(R.id.laptop);
                    cbComputerLaptop.setText("Computer(Laptop)");
                    final CheckBox cbComputerDesktop = (CheckBox)v.findViewById(R.id.desktop);
                    cbComputerDesktop.setText("Computer(Desktop)");
                    final CheckBox cbOven = (CheckBox)v.findViewById(R.id.oven);
                    cbOven.setText("Oven");
                    final CheckBox cbHob = (CheckBox)v.findViewById(R.id.hob);
                    cbHob.setText("Hob");
                    final CheckBox cbElectricShower = (CheckBox)v.findViewById(R.id.electricShower);
                    cbElectricShower.setText("Electric Shower");
                    final CheckBox cbDishwasher = (CheckBox)v.findViewById(R.id.dishwasher);
                    cbDishwasher.setText("Dishwasher");
                    final CheckBox cbElectricHeater = (CheckBox)v.findViewById(R.id.electricHeater);
                    cbElectricHeater.setText("Electric Heater");
                    final CheckBox cbAirConditioner = (CheckBox)v.findViewById(R.id.airConditioner);
                    cbAirConditioner.setText("Air Conditioner");
                    final CheckBox cbKettle = (CheckBox)v.findViewById(R.id.kettle);
                    cbKettle.setText("Kettle");
                    final CheckBox cbMicrowave = (CheckBox)v.findViewById(R.id.microwave);
                    cbMicrowave.setText("Microwave");
                    final CheckBox cbFreezer = (CheckBox)v.findViewById(R.id.freezer);
                    cbFreezer.setText("Freezer");
                    final CheckBox cbFridge = (CheckBox)v.findViewById(R.id.fridge);
                    cbFridge.setText("Fridge");

                    Button bFive = (Button) v.findViewById(R.id.toPartFive);
                    bFive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /**
                             * "Hob",0
                             * "Oven",1
                             * "TumbleDryer",2
                             * "WashingMachine",3
                             * "Computer",4
                             * "Kettle",5
                             * "DishWasher",6
                             * "Shower"7
                             */
                            String[] enableTable = new String[8];
                            String washingMachine,tumbleDryer,computerLaptop,computerDesktop,oven,hob,electricShower,dishWasher,electricHeater,airConditioner,kettle,microwave,freezer,fridge;
                            if(cbWashingMachine.isChecked()){
                                washingMachine = "true";
                                enableTable[3] = "WashingMachine,true";
                            }else{
                                washingMachine = "false";
                                enableTable[3] = "WashingMachine,false";
                            }
                            if(cbTumbleDryer.isChecked()){
                                tumbleDryer = "true";
                                enableTable[2] = "TumbleDryer,true";
                            }else{
                                tumbleDryer = "false";
                                enableTable[2] = "TumbleDryer,false";
                            }
                            if(cbComputerLaptop.isChecked()){
                                computerLaptop = "true";
                                enableTable[4] = "Computer,true";
                            }else{
                                computerLaptop = "false";
                                enableTable[4] = "Computer,false";
                            }
                            if(cbComputerDesktop.isChecked()){
                                computerDesktop = "true";
                                enableTable[4] = "Computer,true";
                            }else{
                                computerDesktop = "false";
                                enableTable[4] = "Computer,false";
                            }
                            if(cbOven.isChecked()){
                                oven = "true";
                                enableTable[1] = "Oven,true";
                            }else{
                                oven = "false";
                                enableTable[1] = "Oven,false";
                            }
                            if(cbHob.isChecked()){
                                hob = "true";
                                enableTable[0] = "Hob,true";
                            }else{
                                hob = "false";
                                enableTable[0] = "Hob,false";
                            }
                            if(cbElectricShower.isChecked()){
                                electricShower = "true";
                                enableTable[7] = "Shower,true";
                            }else{
                                electricShower = "false";
                                enableTable[7] = "Shower,false";
                            }
                            if(cbDishwasher.isChecked()){
                                dishWasher = "true";
                                enableTable[6] = "DishWasher,true";
                            }else{
                                dishWasher = "false";
                                enableTable[6] = "DishWasher,false";
                            }
                            if(cbElectricHeater.isChecked()){
                                electricHeater = "true";
                            }else{
                                electricHeater = "false";
                            }
                            if(cbAirConditioner.isChecked()){
                                airConditioner = "true";
                            }else{
                                airConditioner = "false";
                            }
                            if(cbKettle.isChecked()){
                                kettle = "true";
                                enableTable[5] = "Kettle,true";
                            }else{
                                kettle = "false";
                                enableTable[5] = "Kettle,false";
                            }
                            if(cbMicrowave.isChecked()){
                                microwave = "true";
                            }else{
                                microwave = "false";
                            }
                            if(cbFreezer.isChecked()){
                                freezer = "true";
                            }else{
                                freezer = "false";
                            }
                            if(cbFridge.isChecked()){
                                fridge = "true";
                            }else{
                                fridge = "false";
                            }
                            String submit =
                                    Q[0]+","+washingMachine+"\n"+
                                            Q[1]+","+tumbleDryer+"\n"+
                                            Q[2]+","+computerLaptop+"\n"+
                                            Q[3]+","+computerDesktop+"\n"+
                                            Q[4]+","+oven+"\n"+
                                            Q[5]+","+hob+"\n"+
                                            Q[6]+","+electricShower+"\n"+
                                            Q[7]+","+dishWasher+"\n"+
                                            Q[8]+","+electricHeater+"\n"+
                                            Q[9]+","+airConditioner+"\n"+
                                            Q[10]+","+kettle+"\n"+
                                            Q[11]+","+microwave+"\n"+
                                            Q[12]+","+freezer+"\n"+
                                            Q[13]+","+fridge+"\n";
                            String enableTableData=enableTable[0];
                            for(int q = 1; q<enableTable.length;q++){
                                enableTableData+="\n"+enableTable[q];
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("appliancesEnabledDataFile.txt",Context.MODE_PRIVATE);
                                fos.write(enableTableData.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                String nextScreen = 5+"";
                                fos.write(nextScreen.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                fos.write(submit.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            Fragment stageTwo = new surveyFragment();
                            FragmentManager fragManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                            fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                            fragmentTransaction.commit();
                        }
                    });
                    break;
                case 5:
                    v = inflater.inflate(R.layout.survey_fragment_part_five, container, false);
                    final String[] QuestionIDs = {"12","13","14","15-1","15-2","15-3","15-4","15-5"};
                    TextView txResidentialEnergyConcernQ = (TextView)v.findViewById(R.id.residentialEnergyConcernQ);
                    txResidentialEnergyConcernQ.setText("I am concerned about the amount of my residential energy consumption.");
                    TextView txDesireToReduceResidentialEnergyQ =(TextView)v.findViewById(R.id.desireToReduceResidentialEnergyQ);
                    txDesireToReduceResidentialEnergyQ.setText("I would like to consume lower energy at home.");
                    TextView txDesireToBeMoreEfficientWithResidentialEnergyQ = (TextView)v.findViewById(R.id.desireToBeMoreEfficientWithResidentialEnergyQ);
                    txDesireToBeMoreEfficientWithResidentialEnergyQ.setText("I would like to consume energy at home more efficiently.");
                    TextView txReasonsForEfficiencyAndReductionQ = (TextView)v.findViewById(R.id.reasonsForEfficiencyAndReductionQ);
                    txReasonsForEfficiencyAndReductionQ.setText("I would like to make a more efficient energy usage for the following reasons:");

                    final SeekBar sbResidentialEnergyConcernA = (SeekBar)v.findViewById(R.id.residentialEnergyConcernA);
                    final SeekBar sbDesireToReduceResidentialEnergyA = (SeekBar)v.findViewById(R.id.desireToReduceResidentialEnergyA);
                    final SeekBar sbDesireToBeMoreEfficientWithResidentialEnergyA = (SeekBar)v.findViewById(R.id.desireToBeMoreEfficientWithResidentialEnergyA);

                    final CheckBox cbReasonOne = (CheckBox)v.findViewById(R.id.reason1);
                    cbReasonOne.setText("Reduce my energy bill");
                    final CheckBox cbReasonTwo= (CheckBox)v.findViewById(R.id.reason2);
                    cbReasonTwo.setText("Contribute to the grid reliability, e.g. prevent a blackout");
                    final CheckBox cbReasonThree= (CheckBox)v.findViewById(R.id.reason3);
                    cbReasonThree.setText("Protect the environment");
                    final CheckBox cbReasonFour= (CheckBox)v.findViewById(R.id.reason4);
                    cbReasonFour.setText("Others do, so I do.");
                    final CheckBox cbReasonFive= (CheckBox)v.findViewById(R.id.reason5);
                    cbReasonFive.setText("Others do not, so I do");

                    Button bSix = (Button) v.findViewById(R.id.toPartSix);
                    bSix.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String questionFifteenAnswers ="";
                            if(cbReasonOne.isChecked()){
                                questionFifteenAnswers = QuestionIDs[3]+","+"true"+"\n";
                            }else{
                                questionFifteenAnswers = QuestionIDs[3]+","+"false"+"\n";
                            }
                            if(cbReasonTwo.isChecked()){
                                questionFifteenAnswers += QuestionIDs[4]+","+"true"+"\n";
                            }else{
                                questionFifteenAnswers += QuestionIDs[4]+","+"false"+"\n";
                            }
                            if(cbReasonThree.isChecked()){
                                questionFifteenAnswers += QuestionIDs[5]+","+"true"+"\n";
                            }else{
                                questionFifteenAnswers += QuestionIDs[5]+","+"false"+"\n";
                            }
                            if(cbReasonFour.isChecked()){
                                questionFifteenAnswers += QuestionIDs[6]+","+"true"+"\n";
                            }else{
                                questionFifteenAnswers += QuestionIDs[6]+","+"false"+"\n";
                            }
                            if(cbReasonFive.isChecked()){
                                questionFifteenAnswers += QuestionIDs[7]+","+"true"+"\n";
                            }else{
                                questionFifteenAnswers += QuestionIDs[7]+","+"false"+"\n";
                            }
                            int progressResidentialEnergyConcernA = sbResidentialEnergyConcernA.getProgress();
                            int progressDesireToReduceResidentialEnergyA = sbDesireToReduceResidentialEnergyA.getProgress();
                            int progressDesireToBeMoreEfficientWithResidentialEnergyA = sbDesireToBeMoreEfficientWithResidentialEnergyA.getProgress();
                            String submit = QuestionIDs[0]+","+progressResidentialEnergyConcernA+"\n"+
                                    QuestionIDs[1]+","+progressDesireToReduceResidentialEnergyA+"\n"+
                                    QuestionIDs[2]+","+progressDesireToBeMoreEfficientWithResidentialEnergyA+"\n"+
                                    questionFifteenAnswers;
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                String nextScreen = 6+"";
                                fos.write(nextScreen.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                fos.write(submit.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            Fragment stageTwo = new surveyFragment();
                            FragmentManager fragManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                            fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                            fragmentTransaction.commit();
                        }
                    });
                    break;
                case 6:
                    v = inflater.inflate(R.layout.survey_fragment_part_six, container, false);
                    final String[] QuestionIDS = {"16-1","16-2","17-1","17-2","17-3","17-4"};

                    TextView txDesiredMeansToBeEfficientQ = (TextView) v.findViewById(R.id.desiredMeansToBeEfficientQ);
                    txDesiredMeansToBeEfficientQ.setText("I would like to use the following means to make a more efficient residential usage of energy:");
                    TextView txDiscomfortQ = (TextView) v.findViewById(R.id.discomfortQ);
                    txDiscomfortQ.setText("Which of the following would you, as a result of the automated control of residential appliances for a more efficient energy usage, find creates discomfort:");

                    final CheckBox cbMeansOne = (CheckBox)v.findViewById(R.id.means1);
                    cbMeansOne.setText("Lowering the consumption of appliances.");
                    final CheckBox cbMeansTwo = (CheckBox)v.findViewById(R.id.means2);
                    cbMeansTwo.setText("Shifting the consumption of appliances at different times, e.g. during off-peak night times.");

                    final CheckBox cbResultOne = (CheckBox)v.findViewById(R.id.result1);
                    cbResultOne.setText("Feeling cold in cold winters or feeling warm in warm summers.");
                    final CheckBox cbResultTwo = (CheckBox)v.findViewById(R.id.result2);
                    cbResultTwo.setText("Extra costs for special equipment and appliances.");
                    final CheckBox cbResultThree = (CheckBox)v.findViewById(R.id.result3);
                    cbResultThree.setText("Changing my overall lifestyle at home.");
                    final CheckBox cbResultFour = (CheckBox)v.findViewById(R.id.result4);
                    cbResultFour.setText("Doing my daily residential activities at different and maybe undesirable times.");

                    Button bSeven = (Button) v.findViewById(R.id.toPartSeven);
                    bSeven.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String questionSixteenAnswers = "";
                            String questionSevenTeenAnswers = "";
                            if(cbMeansOne.isChecked()){
                                questionSixteenAnswers += QuestionIDS[0]+",true"+"\n";
                            }else{
                                questionSixteenAnswers += QuestionIDS[0]+",false"+"\n";
                            }
                            if(cbMeansTwo.isChecked()){
                                questionSixteenAnswers += QuestionIDS[1]+",true"+"\n";
                            }else{
                                questionSixteenAnswers += QuestionIDS[1]+",false"+"\n";
                            }
                            if(cbResultOne.isChecked()){
                                questionSevenTeenAnswers+= QuestionIDS[2]+",true"+"\n";
                            }else{
                                questionSevenTeenAnswers+= QuestionIDS[2]+",false"+"\n";
                            }
                            if(cbResultTwo.isChecked()){
                                questionSevenTeenAnswers+= QuestionIDS[3]+",true"+"\n";
                            }else{
                                questionSevenTeenAnswers+= QuestionIDS[3]+",false"+"\n";
                            }
                            if(cbResultThree.isChecked()){
                                questionSevenTeenAnswers+= QuestionIDS[4]+",true"+"\n";
                            }else{
                                questionSevenTeenAnswers+= QuestionIDS[4]+",false"+"\n";
                            }
                            if(cbResultFour.isChecked()){
                                questionSevenTeenAnswers+= QuestionIDS[5]+",true"+"\n";
                            }else{
                                questionSevenTeenAnswers+= QuestionIDS[5]+",false"+"\n";
                            }
                            String submit = questionSixteenAnswers+questionSevenTeenAnswers;
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                String nextScreen = 7+"";
                                fos.write(nextScreen.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                fos.write(submit.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            Fragment stageTwo = new surveyFragment();
                            FragmentManager fragManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                            fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                            fragmentTransaction.commit();
                        }
                    });
                    break;
                case 7:
                    v = inflater.inflate(R.layout.survey_fragment_part_seven, container, false);
                    final String[] Questionids = {"18","19","20","21"};
                    TextView txAcceptanceOfDiscomfortQ = (TextView) v.findViewById(R.id.acceptanceOfDiscomfortQ);
                    txAcceptanceOfDiscomfortQ.setText("I would like to accept discomfort to make more efficient energy usage.");
                    TextView txSacrificeOfEfficiencyQ = (TextView) v.findViewById(R.id.sacrificeOfEfficiencyQ);
                    txSacrificeOfEfficiencyQ.setText("I would like to sacrifice energy efficiency to experience a low discomfort.");
                    TextView txSoloEnergyEfficientQ = (TextView) v.findViewById(R.id.soloEnergyEfficientQ);
                    txSoloEnergyEfficientQ.setText("I would like to be more energy efficient if I know that others are more energy efficient as well.");
                    TextView txGroupAcceptanceOfDiscomfortQ = (TextView) v.findViewById(R.id.groupAcceptanceOfDiscomfortQ);
                    txGroupAcceptanceOfDiscomfortQ.setText("I can accept a discomfort caused by energy efficiency if others can accept it as well.");

                    final SeekBar sbAcceptanceOfDiscomfortA = (SeekBar)v.findViewById(R.id.acceptanceOfDiscomfortA);
                    final SeekBar sbSacrificeOfEfficiencyA = (SeekBar)v.findViewById(R.id.sacrificeOfEfficiencyA);
                    final SeekBar sbSoloEnergyEfficientA = (SeekBar)v.findViewById(R.id.soloEnergyEfficientA);
                    final SeekBar sbGroupAcceptanceOfDiscomfortA = (SeekBar)v.findViewById(R.id.groupAcceptanceOfDiscomfortA);


                    Button bEight = (Button) v.findViewById(R.id.toPartEight);
                    bEight.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int progressAcceptanceOfDiscomfortA= sbAcceptanceOfDiscomfortA.getProgress();
                            int progressSacrificeOfEfficiencyA = sbSacrificeOfEfficiencyA.getProgress();
                            int progressSoloEnergyEfficientA = sbSoloEnergyEfficientA.getProgress();
                            int progressGroupAcceptanceOfDiscomfortA = sbGroupAcceptanceOfDiscomfortA.getProgress();
                            String submit = Questionids[0]+","+progressAcceptanceOfDiscomfortA+"\n"+
                                    Questionids[1]+","+progressSacrificeOfEfficiencyA+"\n"+
                                    Questionids[2]+","+progressSoloEnergyEfficientA+"\n"+
                                    Questionids[3]+","+progressGroupAcceptanceOfDiscomfortA+"\n";
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                String nextScreen = 8+"";
                                fos.write(nextScreen.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                fos.write(submit.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            Fragment stageTwo = new surveyFragment();
                            FragmentManager fragManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                            fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                            fragmentTransaction.commit();
                        }
                    });
                    break;
                case 8:
                    v = inflater.inflate(R.layout.survey_fragment_part_eight, container, false);
                    final String[] questionIDS = {"22","23","24","25"};
                    TextView txNonSoloEnergyEfficientQ = (TextView)v.findViewById(R.id.nonSoloEnergyEfficientQ);
                    txNonSoloEnergyEfficientQ.setText("I would not like to be energy efficient if others are not energy efficient as well.");
                    TextView txNonSoloDiscomfortEscalationQ = (TextView)v.findViewById(R.id.nonSoloDiscomfortEscalationQ);
                    txNonSoloDiscomfortEscalationQ.setText("I would not like to experience higher discomfort by energy efficiency if others do not experience higher as well.");
                    TextView txTechnologyToScheduleForEfficiencyQ = (TextView)v.findViewById(R.id.technologyToScheduleForEfficiencyQ);
                    txTechnologyToScheduleForEfficiencyQ.setText("I would like to allow technology to schedule a more efficient energy usage of my appliances.");
                    TextView txSelfScheduleForEfficiencyQ = (TextView)v.findViewById(R.id.selfScheduleForEfficiencyQ);
                    txSelfScheduleForEfficiencyQ.setText("I am willing to schedule the use of appliances to make more efficient energy usage.");

                    final SeekBar sbNonSoloEnergyEfficientA = (SeekBar)v.findViewById(R.id.nonSoloEnergyEfficientA);
                    final SeekBar sbNonSoloDiscomfortEscalationA = (SeekBar)v.findViewById(R.id.nonSoloDiscomfortEscalationA);
                    final SeekBar sbTechnologyToScheduleForEfficiencyA = (SeekBar)v.findViewById(R.id.technologyToScheduleForEfficiencyA);
                    final SeekBar sbSelfScheduleForEfficiencyA = (SeekBar)v.findViewById(R.id.selfScheduleForEfficiencyA);


                    Button bNine = (Button) v.findViewById(R.id.toPartNine);
                    bNine.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int progressNonSoloEnergyEfficientA = sbNonSoloEnergyEfficientA.getProgress();
                            int progressNonSoloDiscomfortEscalationA = sbNonSoloDiscomfortEscalationA.getProgress();
                            int progressTechnologyToScheduleForEfficiencyA = sbTechnologyToScheduleForEfficiencyA.getProgress();
                            int progressSelfScheduleForEfficiencyA = sbSelfScheduleForEfficiencyA.getProgress();
                            String submit = questionIDS[0]+","+progressNonSoloEnergyEfficientA+"\n"+
                                    questionIDS[1]+","+progressNonSoloDiscomfortEscalationA+"\n"+
                                    questionIDS[2]+","+progressTechnologyToScheduleForEfficiencyA+"\n"+
                                    questionIDS[3]+","+progressSelfScheduleForEfficiencyA+"\n";
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                String nextScreen = 9+"";
                                fos.write(nextScreen.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                fos.write(submit.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            Fragment stageTwo = new surveyFragment();
                            FragmentManager fragManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                            fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                            fragmentTransaction.commit();
                        }
                    });

                    break;
                case 9:
                    v = inflater.inflate(R.layout.survey_fragment_part_nine, container, false);
                    final String[] questionID = {"26-1","26-2","26-3","26-4","26-5","26-6","26-7","27"};
                    TextView txWhenToScheduleQ = (TextView) v.findViewById(R.id.whenToScheduleQ);
                    txWhenToScheduleQ.setText("Scheduling of appliances to make a more efficient energy usage best works for me:");
                    TextView txTakeBackControlAtWhatLevelOfDiscomfortQ = (TextView) v.findViewById(R.id.takeBackControlAtWhatLevelOfDiscomfortQ);
                    txTakeBackControlAtWhatLevelOfDiscomfortQ.setText("For which discomfort level would you like to overtake control back over an appliance scheduled for an efficient energy usage?");

                    final SeekBar spTakeBackControlAtWhatLevelOfDiscomfortA = (SeekBar)v.findViewById(R.id.takeBackControlAtWhatLevelOfDiscomfortA);
                    final CheckBox cbOptionOne = (CheckBox)v.findViewById(R.id.option1);
                    cbOptionOne.setText("30 minutes ahead");
                    final CheckBox cbOptionTwo = (CheckBox)v.findViewById(R.id.option2);
                    cbOptionTwo.setText("1 hour ahead");
                    final CheckBox cbOptionThree = (CheckBox)v.findViewById(R.id.option3);
                    cbOptionThree.setText("3 hours ahead");
                    final CheckBox cbOptionFour = (CheckBox)v.findViewById(R.id.option4);
                    cbOptionFour.setText("6 hours ahead");
                    final CheckBox cbOptionFive = (CheckBox)v.findViewById(R.id.option5);
                    cbOptionFive.setText("12 hours ahead");
                    final CheckBox cbOptionSix = (CheckBox)v.findViewById(R.id.option6);
                    cbOptionSix.setText(" 24 hours ahead");
                    final CheckBox cbOptionSeven = (CheckBox)v.findViewById(R.id.option7);
                    cbOptionSeven.setText("1 week ahead");
                    Button bTen = (Button) v.findViewById(R.id.toPartTen);
                    bTen.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int progressTakeBackControlAtWhatLevelOfDiscomfortA = spTakeBackControlAtWhatLevelOfDiscomfortA.getProgress();
                            String submit = "";
                            if(cbOptionOne.isChecked()){
                                submit += questionID[0]+","+"true"+"\n";
                            }else{
                                submit += questionID[0]+","+"false"+"\n";
                            }
                            if(cbOptionTwo.isChecked()){
                                submit += questionID[1]+","+"true"+"\n";
                            }else{
                                submit += questionID[1]+","+"false"+"\n";
                            }
                            if(cbOptionThree.isChecked()){
                                submit += questionID[2]+","+"true"+"\n";
                            }else{
                                submit += questionID[2]+","+"false"+"\n";
                            }
                            if(cbOptionFour.isChecked()){
                                submit += questionID[3]+","+"true"+"\n";
                            }else{
                                submit += questionID[3]+","+"false"+"\n";
                            }
                            if(cbOptionFive.isChecked()){
                                submit += questionID[4]+","+"true"+"\n";
                            }else{
                                submit += questionID[4]+","+"false"+"\n";
                            }
                            if(cbOptionSix.isChecked()){
                                submit += questionID[5]+","+"true"+"\n";
                            }else{
                                submit += questionID[5]+","+"false"+"\n";
                            }
                            if(cbOptionSeven.isChecked()){
                                submit += questionID[6]+","+"true"+"\n";
                            }else{
                                submit += questionID[6]+","+"false"+"\n";
                            }
                            submit+=questionID[7]+","+progressTakeBackControlAtWhatLevelOfDiscomfortA+"\n";

                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                String nextScreen = 10+"";
                                fos.write(nextScreen.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                fos.write(submit.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            Fragment stageTwo = new surveyFragment();
                            FragmentManager fragManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                            fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                            fragmentTransaction.commit();
                        }
                    });
                    break;
                case 10:
                    v = inflater.inflate(R.layout.survey_fragment_part_ten, container, false);
                    Button bDismiss= (Button) v.findViewById(R.id.finished);
                    final String[] questionIds = {"28-1","28-2","28-3","28-4","28-5","28-6","28-7","28-8","28-9","28-10","28-11","28-12","28-13","28-14"};

                    TextView txAppliancesToAllowAutomatedControlQ = (TextView)v.findViewById(R.id.appliancesToAllowAutomatedControlQ);
                    txAppliancesToAllowAutomatedControlQ.setText("To which devices you would allow automated control for a more efficient energy usage?");

                    final CheckBox CBWashingMachine = (CheckBox)v.findViewById(R.id.washingMachine);
                    CBWashingMachine.setText("Washing Machine");
                    final CheckBox CBTumbleDryer = (CheckBox)v.findViewById(R.id.tumbleDryer);
                    CBTumbleDryer.setText("Tumble Dryer");
                    final CheckBox CBComputerLaptop = (CheckBox)v.findViewById(R.id.laptop);
                    CBComputerLaptop.setText("Computer(Laptop)");
                    final CheckBox CBComputerDesktop = (CheckBox)v.findViewById(R.id.desktop);
                    CBComputerDesktop.setText("Computer(Desktop)");
                    final CheckBox CBOven = (CheckBox)v.findViewById(R.id.oven);
                    CBOven.setText("Oven");
                    final CheckBox CBHob = (CheckBox)v.findViewById(R.id.hob);
                    CBHob.setText("Hob");
                    final CheckBox CBElectricShower = (CheckBox)v.findViewById(R.id.electricShower);
                    CBElectricShower.setText("Electric Shower");
                    final CheckBox CBDishwasher = (CheckBox)v.findViewById(R.id.dishwasher);
                    CBDishwasher.setText("DishWasher");
                    final CheckBox CBElectricHeater = (CheckBox)v.findViewById(R.id.electricHeater);
                    CBElectricHeater.setText("Electric Heater");
                    final CheckBox CBAirConditioner = (CheckBox)v.findViewById(R.id.airConditioner);
                    CBAirConditioner.setText("Air Conditioner");
                    final CheckBox CBKettle = (CheckBox)v.findViewById(R.id.kettle);
                    CBKettle.setText("Kettle");
                    final CheckBox CBMicrowave = (CheckBox)v.findViewById(R.id.microwave);
                    CBMicrowave.setText("Microwave");
                    final CheckBox CBFreezer = (CheckBox)v.findViewById(R.id.freezer);
                    CBFreezer.setText("Freezer");
                    final CheckBox CBFridge = (CheckBox)v.findViewById(R.id.fridge);
                    CBFridge.setText("Fridge");
                    bDismiss.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String washingMachine,tumbleDryer,computerLaptop,computerDesktop,oven,hob,electricShower,dishWasher,electricHeater,airConditioner,kettle,microwave,freezer,fridge;
                            if(CBWashingMachine.isChecked()){
                                washingMachine = "true";
                            }else{
                                washingMachine = "false";
                            }
                            if(CBTumbleDryer.isChecked()){
                                tumbleDryer = "true";
                            }else{
                                tumbleDryer = "false";
                            }
                            if(CBComputerLaptop.isChecked()){
                                computerLaptop = "true";
                            }else{
                                computerLaptop = "false";
                            }
                            if(CBComputerDesktop.isChecked()){
                                computerDesktop = "true";
                            }else{
                                computerDesktop = "false";
                            }
                            if(CBOven.isChecked()){
                                oven = "true";
                            }else{
                                oven = "false";
                            }
                            if(CBHob.isChecked()){
                                hob = "true";
                            }else{
                                hob = "false";
                            }
                            if(CBElectricShower.isChecked()){
                                electricShower = "true";
                            }else{
                                electricShower = "false";
                            }
                            if(CBDishwasher.isChecked()){
                                dishWasher = "true";
                            }else{
                                dishWasher = "false";
                            }
                            if(CBElectricHeater.isChecked()){
                                electricHeater = "true";
                            }else{
                                electricHeater = "false";
                            }
                            if(CBAirConditioner.isChecked()){
                                airConditioner = "true";
                            }else{
                                airConditioner = "false";
                            }
                            if(CBKettle.isChecked()){
                                kettle = "true";
                            }else{
                                kettle = "false";
                            }
                            if(CBMicrowave.isChecked()){
                                microwave = "true";
                            }else{
                                microwave = "false";
                            }
                            if(CBFreezer.isChecked()){
                                freezer = "true";
                            }else{
                                freezer = "false";
                            }
                            if(CBFridge.isChecked()){
                                fridge = "true";
                            }else{
                                fridge = "false";
                            }
                            String submit =
                                    questionIds[0]+","+washingMachine+"\n"+
                                            questionIds[1]+","+tumbleDryer+"\n"+
                                            questionIds[2]+","+computerLaptop+"\n"+
                                            questionIds[3]+","+computerDesktop+"\n"+
                                            questionIds[4]+","+oven+"\n"+
                                            questionIds[5]+","+hob+"\n"+
                                            questionIds[6]+","+electricShower+"\n"+
                                            questionIds[7]+","+dishWasher+"\n"+
                                            questionIds[8]+","+electricHeater+"\n"+
                                            questionIds[9]+","+airConditioner+"\n"+
                                            questionIds[10]+","+kettle+"\n"+
                                            questionIds[11]+","+microwave+"\n"+
                                            questionIds[12]+","+freezer+"\n"+
                                            questionIds[13]+","+fridge;
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_APPEND);
                                fos.write(submit.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            String results = "";
                            try{
                                FileInputStream fis = getActivity().openFileInput("surveyResults.txt");
                                int chr;
                                StringBuilder builder = new StringBuilder();
                                while ((chr = fis.read()) != -1) {
                                    builder.append((char) chr);
                                }
                                results = builder.toString();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-HH_mm_ss");
                            String date = simpleDateFormat.format(new Date());
                            String android_id;
                            android_id = Settings.Secure.getString(getActivity().getContentResolver(), Settings.Secure.ANDROID_ID);
                            String fileName = android_id+"-initial-Survey-results-on-"+date+".txt";

                            ((MainActivity)getActivity()).sendMail(results,fileName);
//                            String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
//                            FileOutputStream fOut;
//                            File file1 = new File(root+ File.separator + fileName);
//                            if(!file1.exists()) {
//                                try {
//                                    file1.createNewFile();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                            try {
//                                fOut = new FileOutputStream(file1);
//                                fOut.write(results.getBytes());
//                                fOut.close();
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
                            try{
                                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                String nextScreen = 0+"";
                                fos.write(nextScreen.getBytes());
                                fos.close();
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            settings.edit().putBoolean("show_survey",false).commit();
                            ((MainActivity)getActivity()).setMainLayoutViewInvisible(false);
                            ((MainActivity)getActivity()).setFabRevealFabsVisibility(true);
                            getActivity().getSupportFragmentManager().beginTransaction().remove(thisFrag).commit();
                        }
                    });
                    break;
                default:
                    v = inflater.inflate(R.layout.survey_fragment_part_one, container, false);

                    final String[] QIDs = {"1","2","3","4"};
                    TextView txBirthYearQ = (TextView) v.findViewById(R.id.birthYearQ);
                    final Spinner spBirthYearA = (Spinner) v.findViewById(R.id.birthYearA);
                    TextView txCountryOfOriginQ = (TextView)v.findViewById(R.id.countryOfOriginQ);
                    final Spinner spCountryOfOriginA = (Spinner)v.findViewById(R.id.countryOfOriginA);
                    TextView txGenderQ = (TextView)v.findViewById(R.id.genderQ);
                    final Spinner spGenderA = (Spinner)v.findViewById(R.id.genderA);
                    TextView txCountryOfResidenceQ = (TextView)v.findViewById(R.id.countryOfResidenceQ);
                    final Spinner spCountryOfResidenceA = (Spinner)v.findViewById(R.id.countryOfResidenceA);

                    txBirthYearQ.setText("What year were you born?");
                    txCountryOfOriginQ.setText("In which country were you born?");
                    txGenderQ.setText("What is your gender?");
                    txCountryOfResidenceQ.setText("In which country have you lived the longest?");

                    String[] birthYears = new String[101];
                    birthYears[0] = "-";
                    for(int i = 99;i>-1;i--){
                        if(i>=10){
                            birthYears[100-i] = "19"+i;
                        }else{
                            birthYears[100-i] = "190"+i;
                        }

                    }
                    String[] genders = {"-","male","female","other"};
                    String[] countries = {
                            "-",
                            "Afghanistan",
                            "Albania",
                            "Algeria",
                            "American Samoa",
                            "Andorra",
                            "Angola",
                            "Anguilla",
                            "Antarctica",
                            "Antigua and Barbuda",
                            "Argentina",
                            "Armenia",
                            "Aruba",
                            "Australia",
                            "Austria",
                            "Azerbaijan",
                            "Bahrain",
                            "Bangladesh",
                            "Barbados",
                            "Belarus",
                            "Belgium",
                            "Belize",
                            "Benin",
                            "Bermuda",
                            "Bhutan",
                            "Bolivia",
                            "Bosnia and Herzegovina",
                            "Botswana",
                            "Bouvet Island",
                            "Brazil",
                            "British Indian Ocean Territory",
                            "British Virgin Islands",
                            "Brunei",
                            "Bulgaria",
                            "Burkina Faso",
                            "Burundi",
                            "Cambodia",
                            "Cameroon",
                            "Canada",
                            "Cape Verde",
                            "Cayman Islands",
                            "Central African Republic",
                            "Chad",
                            "Chile",
                            "China",
                            "Christmas Island",
                            "Cocos (Keeling) Islands",
                            "Colombia",
                            "Comoros",
                            "Congo",
                            "Cook Islands",
                            "Costa Rica",
                            "Cote d'Ivoire",
                            "Croatia",
                            "Cuba",
                            "Cyprus",
                            "Czech Republic",
                            "Democratic Republic of the Congo",
                            "Denmark",
                            "Djibouti",
                            "Dominica",
                            "Dominican Republic",
                            "East Timor",
                            "Ecuador",
                            "Egypt",
                            "El Salvador",
                            "Equatorial Guinea",
                            "Eritrea",
                            "Estonia",
                            "Ethiopia",
                            "Faeroe Islands",
                            "Falkland Islands",
                            "Fiji",
                            "Finland",
                            "Former Yugoslav Republic of Macedonia",
                            "France",
                            "French Guiana",
                            "French Polynesia",
                            "French Southern Territories",
                            "Gabon",
                            "Georgia",
                            "Germany",
                            "Ghana",
                            "Gibraltar",
                            "Greece",
                            "Greenland",
                            "Grenada",
                            "Guadeloupe",
                            "Guam",
                            "Guatemala",
                            "Guinea",
                            "Guinea-Bissau",
                            "Guyana",
                            "Haiti",
                            "Heard Island and McDonald Islands",
                            "Honduras",
                            "Hong Kong",
                            "Hungary",
                            "Iceland",
                            "India",
                            "Indonesia",
                            "Iran",
                            "Iraq",
                            "Ireland",
                            "Israel",
                            "Italy",
                            "Jamaica",
                            "Japan",
                            "Jordan",
                            "Kazakhstan",
                            "Kenya",
                            "Kiribati",
                            "Kuwait",
                            "Kyrgyzstan",
                            "Laos",
                            "Latvia",
                            "Lebanon",
                            "Lesotho",
                            "Liberia",
                            "Libya",
                            "Liechtenstein",
                            "Lithuania",
                            "Luxembourg",
                            "Macau",
                            "Madagascar",
                            "Malawi",
                            "Malaysia",
                            "Maldives",
                            "Mali",
                            "Malta",
                            "Marshall Islands",
                            "Martinique",
                            "Mauritania",
                            "Mauritius",
                            "Mayotte",
                            "Mexico",
                            "Micronesia",
                            "Moldova",
                            "Monaco",
                            "Mongolia",
                            "Montenegro",
                            "Montserrat",
                            "Morocco",
                            "Mozambique",
                            "Myanmar",
                            "Namibia",
                            "Nauru",
                            "Nepal",
                            "Netherlands",
                            "Netherlands Antilles",
                            "New Caledonia",
                            "New Zealand",
                            "Nicaragua",
                            "Niger",
                            "Nigeria",
                            "Niue",
                            "Norfolk Island",
                            "North Korea",
                            "Northern Marianas",
                            "Norway",
                            "Oman",
                            "Pakistan",
                            "Palau",
                            "Panama",
                            "Papua New Guinea",
                            "Paraguay",
                            "Peru",
                            "Philippines",
                            "Pitcairn Islands",
                            "Poland",
                            "Portugal",
                            "Puerto Rico",
                            "Qatar",
                            "Reunion",
                            "Romania",
                            "Russia",
                            "Rwanda",
                            "Sqo Tome and Principe",
                            "Saint Helena",
                            "Saint Kitts and Nevis",
                            "Saint Lucia",
                            "Saint Pierre and Miquelon",
                            "Saint Vincent and the Grenadines",
                            "Samoa",
                            "San Marino",
                            "Saudi Arabia",
                            "Senegal",
                            "Serbia",
                            "Seychelles",
                            "Sierra Leone",
                            "Singapore",
                            "Slovakia",
                            "Slovenia",
                            "Solomon Islands",
                            "Somalia",
                            "South Africa",
                            "South Georgia and the South Sandwich Islands",
                            "South Korea",
                            "South Sudan",
                            "Spain",
                            "Sri Lanka",
                            "Sudan",
                            "Suriname",
                            "Svalbard and Jan Mayen",
                            "Swaziland",
                            "Sweden",
                            "Switzerland",
                            "Syria",
                            "Taiwan",
                            "Tajikistan",
                            "Tanzania",
                            "Thailand",
                            "The Bahamas",
                            "The Gambia",
                            "Togo",
                            "Tokelau",
                            "Tonga",
                            "Trinidad and Tobago",
                            "Tunisia",
                            "Turkey",
                            "Turkmenistan",
                            "Turks and Caicos Islands",
                            "Tuvalu",
                            "Virgin Islands",
                            "Uganda",
                            "Ukraine",
                            "United Arab Emirates",
                            "United Kingdom",
                            "United States",
                            "United States Minor Outlying Islands",
                            "Uruguay",
                            "Uzbekistan",
                            "Vanuatu",
                            "Vatican City",
                            "Venezuela",
                            "Vietnam",
                            "Wallis and Futuna",
                            "Western Sahara",
                            "Yemen",
                            "Yugoslavia",
                            "Zambia",
                            "Zimbabwe"
                    };

                    ArrayAdapter<String> birthYearQ;
                    ArrayAdapter<String> countryOfOriginQ;
                    ArrayAdapter<String> genderQ;
                    ArrayAdapter<String> countryOfResidenceQ;




                    birthYearQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,birthYears);
                    birthYearQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spBirthYearA.setAdapter(birthYearQ);


                    countryOfOriginQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,countries);
                    countryOfOriginQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCountryOfOriginA.setAdapter(countryOfOriginQ);


                    genderQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,genders);
                    genderQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spGenderA.setAdapter(genderQ);


                    countryOfResidenceQ = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,countries);
                    countryOfResidenceQ.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spCountryOfResidenceA.setAdapter(countryOfResidenceQ);

                    Button b  =(Button)v.findViewById(R.id.toPartTwo);
                    b.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String selectedBirthYear = spBirthYearA.getSelectedItem().toString();
                            String selectedCountryOfOrigin= spCountryOfOriginA.getSelectedItem().toString();
                            String selectedGender= spGenderA.getSelectedItem().toString();
                            String selectedCountryOfResidence= spCountryOfResidenceA.getSelectedItem().toString();


                            if(selectedBirthYear.equals("-")||selectedCountryOfOrigin.equals("-")||selectedGender.equals("-")||selectedCountryOfResidence.equals("-")){
                                String toastString = "Please answer all questions.";
                                int durationOfToast = Toast.LENGTH_SHORT;
                                Context context = getActivity();
                                Toast toast = Toast.makeText(context, toastString, durationOfToast);
                                toast.show();
                            }else{
                                String submitAnswers  = QIDs[0]+","+selectedBirthYear+"\n"
                                        +QIDs[1]+","+selectedCountryOfOrigin+"\n"
                                        +QIDs[2]+","+selectedGender+"\n"
                                        +QIDs[3]+","+selectedCountryOfResidence+"\n";
                                try{
                                    FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",Context.MODE_PRIVATE);
                                    String nextScreen = 2+"";
                                    fos.write(nextScreen.getBytes());
                                    fos.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                try{
                                    FileOutputStream fos = getActivity().openFileOutput("surveyResults.txt",Context.MODE_PRIVATE);
                                    fos.write(submitAnswers.getBytes());
                                    fos.close();
                                }catch(Exception e){
                                    e.printStackTrace();
                                }
                                Fragment stageTwo = new surveyFragment();
                                FragmentManager fragManager = getActivity().getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragManager.beginTransaction();
                                fragmentTransaction.replace(((ViewGroup)getView().getParent()).getId(), stageTwo);
                                fragmentTransaction.commit();
                            }
                        }
                    });
                    break;
            }

            try{
                String next = ""+nextScreen;
                FileOutputStream fos = getActivity().openFileOutput("surveyProgress.txt",context.MODE_PRIVATE);
                fos.write(next.getBytes());
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return v;
    }
    //if this fragment is dismissed, check how to proceed using the assigned close listener.
    public void DismissListner(MyDialogCloseListener closeListener) {
        this.closeListener = closeListener;
    }

}
