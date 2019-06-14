package com.example.scheduler.fragment;


import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import com.example.scheduler.MainActivity;
import com.example.scheduler.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by warrens on 07.08.17.
 */

public class addRemoveAppliance extends Fragment {
    private CheckBox[] applianceCheckBoxs;
    private String[] applianceEnableBooleans;
    private ArrayList checkBoxIdArrayList = new ArrayList();
    private ArrayList LinearLayoutIdArrayList = new ArrayList();
    private ArrayList<LinearLayout> LinearLayoutArrayList = new ArrayList<>();
    private String[] applianceNames;
    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity();
        ((MainActivity)getActivity()).setMainLayoutViewInvisible(true);

        View v = inflater.inflate(R.layout.fragment_add_remove_appliance, container, false);

        final String appliancesEnabledDataFile = "appliancesEnabledDataFile.txt";
        String appliancesEnabled="";
        try{
            FileInputStream fis = getActivity().openFileInput(appliancesEnabledDataFile);
            StringBuilder builder = new StringBuilder();
            int chr;
            while ((chr = fis.read()) != -1) {
                builder.append((char) chr);
            }
            fis.close();
            appliancesEnabled = builder.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        String[] appliancesEnabledArray = appliancesEnabled.split("\n");
        final String[][] enableTable = new String[appliancesEnabledArray.length][2];
        for(int i = 0 ; i<enableTable.length;i++){
            enableTable[i] = appliancesEnabledArray[i].split(",");
        }

        String applianceNamesFile = "applianceNames.txt";
        String appliancesNames="";
        try{
            FileInputStream fis = getActivity().openFileInput(applianceNamesFile);
            StringBuilder builder = new StringBuilder();
            int chr;
            while ((chr = fis.read()) != -1) {
                builder.append((char) chr);
            }
            fis.close();
            appliancesNames = builder.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        applianceNames = appliancesNames.split(",");
        applianceCheckBoxs = new CheckBox[applianceNames.length];
        applianceEnableBooleans = new String[applianceNames.length];

        LinearLayout tempLayoutView;

        LinearLayout lin = (LinearLayout)v.findViewById(R.id.checkBoxLayout);
        for(int i = 0; i<applianceCheckBoxs.length;i++){
            final int index = i;
            final CheckBox temp;
            tempLayoutView = new LinearLayout(getActivity());
            tempLayoutView.setId(v.generateViewId());
            tempLayoutView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            tempLayoutView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

            temp = new CheckBox(getActivity());
            temp.setId(v.generateViewId());
            temp.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            temp.setText(applianceNames[i]);
            temp.setTextColor(getResources().getColor(R.color.white));
            temp.setShadowLayer(1.5f, -1, 1, getResources().getColor(R.color.black));
            //check which appliances are currently added or not.
            if(applianceNames[i].equals(enableTable[i][0])){
                applianceEnableBooleans[i] = "false";
                if(enableTable[i][1].equals("true")){
                    if(!temp.isChecked()){
                        applianceEnableBooleans[i] = "true";
                        temp.toggle();
                    }
                }else{
                    if(temp.isChecked()){
                        applianceEnableBooleans[i] = "false";
                        temp.toggle();
                    }
                }
            }
            LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
            llp.setMargins(50,15,0,50 );

            LinearLayout.LayoutParams relParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
            relParams.setMargins(15,5,15,50);

            temp.setLayoutParams(llp);
            try{
                lin.addView(tempLayoutView,relParams);
                tempLayoutView.addView(temp);
            }catch(Exception e){
                e.printStackTrace();
            }
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View vi) {
                    if(temp.isChecked()){
                        applianceEnableBooleans[index] = "true";
                    }else{
                        applianceEnableBooleans[index] = "false";
                    }
                }
            });
            applianceCheckBoxs[i] = temp;
            checkBoxIdArrayList.add(temp.getId());
            LinearLayoutIdArrayList.add(tempLayoutView.getId());
            LinearLayoutArrayList.add(tempLayoutView);
        }
        //dismiss the fragment without saving changes.
        final Fragment thisFrag = this;
        Button cancel = (Button) v.findViewById(R.id.button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View g) {
                FloatingActionButton b =((MainActivity)getActivity()).getFabRevealFabs();
                b.setClickable(true);
                b.setVisibility(View.VISIBLE);
                ((MainActivity)getActivity()).setMainLayoutViewInvisible(false);
                getActivity().getSupportFragmentManager().beginTransaction().remove(thisFrag).commit();
            }
        });
        //dismiss the fragment and save the changes.
        Button confirm = (Button) v.findViewById(R.id.button2);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View g) {
                String submitString = "";
                StringBuilder submitBuilder = new StringBuilder();
                for(int i = 0; i <applianceEnableBooleans.length;i++){
                    if(enableTable.length == applianceEnableBooleans.length){
                        enableTable[i][1] = applianceEnableBooleans[i];
                    }

                    for(int j = 0; j<enableTable[i].length;j++){
                        if(j==enableTable[i].length-1){
                            if(enableTable[i][j].equals("false"))
                                ((MainActivity)getActivity()).removeItemWithName(enableTable[i][0]);
                            submitBuilder.append(enableTable[i][j]);
                        }else{
                            submitBuilder.append(enableTable[i][j]+",");
                        }
                    }
                    submitBuilder.append("\n");
                }
                submitString = submitBuilder.toString();
                try{
                    FileOutputStream fos = getActivity().openFileOutput(appliancesEnabledDataFile,Context.MODE_PRIVATE);
                    fos.write(submitString.getBytes());
                    fos.close();
                }catch(Exception e){
                    e.printStackTrace();
                }
                FloatingActionButton b =((MainActivity)getActivity()).getFabRevealFabs();
                b.setClickable(true);
                b.setVisibility(View.VISIBLE);
                ((MainActivity)getActivity()).setMainLayoutViewInvisible(false);
                getActivity().getSupportFragmentManager().beginTransaction().remove(thisFrag).commit();
            }
        });
        return v;
    }
}