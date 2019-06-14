package com.example.scheduler.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.scheduler.MainActivity;
import com.example.scheduler.R;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by warrens on 07.08.17.
 */

//display the current wattages of the appliances and allow the user to change them.

public class editApplianceSettings extends Fragment {
    public ArrayList textViewIds = new ArrayList();
    public ArrayList editTextIds = new ArrayList();
    @Override
    public void onCreate(Bundle savedInstaceState) {
        super.onCreate(savedInstaceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((MainActivity)getActivity()).setMainLayoutViewInvisible(true);
        final Context context = getActivity();
        final View v = inflater.inflate(R.layout.fragment_edit_appliance_settings, container, false);
        String appliancesEnabledDataFile = "appliancesEnabledDataFile.txt";
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
        String[][] enableTable = new String[appliancesEnabledArray.length][2];
        for(int i = 0 ; i<enableTable.length;i++){
            enableTable[i] = appliancesEnabledArray[i].split(",");
        }

        String wattages = "";
        //create a display for all enabled appliances.
        try{
            FileInputStream fis = context.openFileInput("wattagesFile.txt");
            int chr;
            StringBuilder builder = new StringBuilder();
            while ((chr = fis.read()) != -1) {
                builder.append((char) chr);
            }
            wattages = builder.toString();
            //  0,              1,          2,          3,              4,    5,      6,        7,              8
            //House Number, Computer, Cooker (Hob),Cooker (Oven),Dishwasher,Kettle,Shower,Tumble Dryer, Washing Machine

            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        final String[] wattagesArray = wattages.split(",");
        LinearLayout lin = (LinearLayout)v.findViewById(R.id.editTextLayout);
        LinearLayout tempLayoutView;
        LinearLayout tempLayoutViewText;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(100,0,50,50);
        LinearLayout.LayoutParams paramsText = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsText.setMargins(50,25,50,0);
        LinearLayout.LayoutParams paramsLayoutText =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLayoutText.setMargins(15,5,15,0);
        LinearLayout.LayoutParams paramsLayout = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsLayout.setMargins(15,0,15,5);
        for(int i =0; i<enableTable.length;i++){
            String currentText;
            if(enableTable[i][1].equals("true")){
                int index = i+1;

                String applianceIWattage = wattagesArray[index];
                currentText = enableTable[i][0]+": "+applianceIWattage+" watts";
                EditText etTemp = new EditText(context);
                etTemp.setInputType(InputType.TYPE_CLASS_NUMBER);
                etTemp.setText(applianceIWattage);
                etTemp.setId(v.generateViewId());
                etTemp.setTextColor(getResources().getColor(R.color.white));
                etTemp.setShadowLayer(1.5f, -1, 1, getResources().getColor(R.color.black));
                etTemp.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                TextView textTemp = new TextView(context);
                textTemp.setText(currentText);
                textTemp.setTextSize(15);
                textTemp.setTextColor(getResources().getColor(R.color.white));
                textTemp.setShadowLayer(1.5f, -1, 1, getResources().getColor(R.color.black));
                textTemp.setTypeface(null, Typeface.BOLD);
                textTemp.setId(v.generateViewId());


                tempLayoutView = new LinearLayout(getActivity());
                tempLayoutView.setId(v.generateViewId());
                tempLayoutView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tempLayoutView.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                tempLayoutViewText = new LinearLayout(getActivity());
                tempLayoutViewText.setId(v.generateViewId());
                tempLayoutViewText.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                tempLayoutViewText.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

                try{
                    tempLayoutViewText.addView(textTemp,paramsText);
                    lin.addView(tempLayoutViewText,paramsLayoutText);
                    tempLayoutView.addView(etTemp,params);
                    lin.addView(tempLayoutView,paramsLayout);
                    editTextIds.add(etTemp.getId());
                    textViewIds.add(textTemp.getId());
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        final String[][] enableTableFinal = enableTable;
        final Fragment thisFrag = this;
        Button confirm = (Button) v.findViewById(R.id.button2);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View g) {
                String wattFile = "";
                int ind = 0;
                for(int i = 1; i<wattagesArray.length;i++){
                    if(enableTableFinal[i-1][1].equals("true")){
                        TextView textTemp = (TextView)v.findViewById((int)textViewIds.get(ind));
                        EditText temp = (EditText)v.findViewById((int)editTextIds.get(ind));
                        String title = textTemp.getText().toString();
                        String t = temp.getText().toString();
                        ind++;
                        String[] parts = title.split(":");
                        wattagesArray[i] = t;
                    }
                }
                for(int i = 0; i<wattagesArray.length;i++) {
                    if(i<wattagesArray.length-1){
                        wattFile += wattagesArray[i] + ",";
                    }else{
                        wattFile+=wattagesArray[i];
                    }
                }
                try{
                    FileOutputStream fos = getActivity().openFileOutput("wattagesFile.txt", Context.MODE_PRIVATE);
                    fos.write(wattFile.getBytes());
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
        return v;
    }

}