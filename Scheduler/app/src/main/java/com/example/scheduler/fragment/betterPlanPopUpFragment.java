package com.example.scheduler.fragment;

/**
 * Created by warrens on 08.08.17.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import com.example.scheduler.R;
import android.support.v4.app.DialogFragment;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by warrens on 02.08.17.
 */

//Offer a new plan and store it as the chosen plan if the user wants it.

public class betterPlanPopUpFragment extends DialogFragment {

    public String plan;

    @Override
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Context context = getActivity();
        final View v = inflater.inflate(R.layout.better_plan_pop_up_fragment, container, false);
        String display = "";
        try{
            FileInputStream fis = context.openFileInput("suggestedPlan.txt");
            StringBuilder builder = new StringBuilder();
            int chr;
            while ((chr = fis.read()) != -1) {
                builder.append((char) chr);
            }
            display = builder.toString();
            fis.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        final CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox);
        cb.setText(display);
        cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View vi) {

            }
        });
        Button b = (Button)v.findViewById(R.id.button3);
        b.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View vi){
                if(cb.isChecked()){
                    String newChosenPlan = cb.getText().toString();
                    String chosenPlanFile = "chosenPlan.txt";
                    try{
                        FileOutputStream fos = getActivity().openFileOutput(chosenPlanFile,Context.MODE_PRIVATE);
                        fos.write(newChosenPlan.getBytes());
                        fos.close();
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }
                dismiss();
            }
        });
        return v;
    }
}
