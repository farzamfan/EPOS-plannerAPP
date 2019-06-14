package com.example.scheduler.fragment;

/**
 * Created by warrens on 07.09.17.
 */

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.example.scheduler.MainActivity;
import com.example.scheduler.R;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created by warrens on 06.09.17.
 */
//a shell dialog fragment to hold the pages of options of possible plans.
// if the button is pressed, put the tempChosenPlan into chosen plan and dismiss the dialog.
public class tabsFragment extends DialogFragment {

    private SectionsPagerAdapter sectionsPagerAdapter;
    private ViewPager viewPager;
    public int pagesCount;


    @Override
    public Dialog onCreateDialog(final Bundle savedInstanceState)
    {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.YELLOW));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        return dialog;
    }
    public void onCreate(Bundle savedInstaceState){
        super.onCreate(savedInstaceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Rect displayRectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
        View view = inflater.inflate(R.layout.fragment_dialog, null);
        view.setMinimumWidth((int)(displayRectangle.width() * 0.9f));

        // tab slider
        sectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());
        sectionsPagerAdapter.setReference(getThis());
        String[] separate;
        String submit = " ";
        try{
            FileInputStream fis = getActivity().openFileInput("TomorrowSchedule.txt");
            int ch;
            StringBuilder builder = new StringBuilder();
            while((ch=fis.read())!=-1){
                builder.append((char)ch);
            }
            submit = builder.toString();
        }catch(Exception e){
            e.printStackTrace();
        }
        separate = submit.split("\n");
        pagesCount = separate.length;
        // Set up the ViewPager with the sections adapter.
        viewPager = (ViewPager)view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(pagesCount-1);
        viewPager.setAdapter(sectionsPagerAdapter);
        return view;
    }
    public DialogFragment getThis(){
        return this;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        public ArrayList<Fragment> pages = new ArrayList<>();
        private DialogFragment reference;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            String submit = " ";
            String[] separate;
            try{
                FileInputStream fis = getActivity().openFileInput("TomorrowSchedule.txt");
                int ch;
                StringBuilder builder = new StringBuilder();
                while((ch=fis.read())!=-1){
                    builder.append((char)ch);
                }
                submit = builder.toString();
            }catch(Exception e){
                e.printStackTrace();
            }
            separate = submit.split("\n");
            submit = separate[position];
            fragment_page page = fragment_page.newInstance(submit);
            page.setReference(reference);
            pages.add(position,page);
            return pages.get(position);
        }

        public DialogFragment getReference(){
            return reference;
        }
        @Override
        public int getCount() {
            return pagesCount;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            int title = position+1;
            return "Option "+title;
        }

        public void setReference(DialogFragment r){
            reference = r;
        }
    }
}

