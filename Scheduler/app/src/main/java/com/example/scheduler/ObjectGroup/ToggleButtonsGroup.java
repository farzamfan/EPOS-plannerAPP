package com.example.scheduler.ObjectGroup;

import android.widget.CompoundButton;
import android.widget.ToggleButton;
import java.util.ArrayList;

/**
 * Created by warrens on 15.08.17.
 */

//if a button is pressed, unselect the other buttons. Used in the survey for current employment status.
public class ToggleButtonsGroup implements CompoundButton.OnCheckedChangeListener {
    private boolean mDataChanged;
    private ArrayList<ToggleButton> mButtons;

    public ToggleButtonsGroup() {

        mButtons = new ArrayList<ToggleButton>();

    }

    public void addButton(ToggleButton btn) {

        btn.setOnCheckedChangeListener(this);

        mButtons.add(btn);
    }

    public ToggleButton getSelectedButton() {

        for(ToggleButton b : mButtons) {
            if(b.isChecked())
                return b;
        }

        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView,
                                 boolean isChecked) {

        if(isChecked) {
            uncheckOtherButtons(buttonView.getId());

            mDataChanged = true;

        } else if (!anyButtonChecked()){
            buttonView.setChecked(true);
        }

    }

    private boolean anyButtonChecked() {

        for(ToggleButton b : mButtons) {
            if(b.isChecked())
                return true;
        }

        return false;
    }

    private void uncheckOtherButtons(int current_button_id) {

        for(ToggleButton b : mButtons) {
            if(b.getId() != current_button_id)
                b.setChecked(false);
        }
    }

}