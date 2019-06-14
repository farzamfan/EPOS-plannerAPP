package com.example.scheduler.fragment;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.scheduler.R;

/**
 * Created by warrens on 19.09.17.
 */

public class No_Network_Fragment extends DialogFragment {

    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final View layoutView = inflater.inflate(R.layout.no_network_fragment_layout, container, false);

        Button b = (Button) layoutView.findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return layoutView;
    }
}
