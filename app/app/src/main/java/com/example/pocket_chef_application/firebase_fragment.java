package com.example.pocket_chef_application;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;


public class firebase_fragment extends Fragment {
    private Button button;
    private TextView text;
    private FirebaseAnalytics mFirebaseAnalytics;
    private int count;

    public static firebase_fragment newInstance() {
        firebase_fragment fragment = new firebase_fragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_firebase_fragment, container, false);
        button = view.findViewById(R.id.fb_event);
        text = view.findViewById(R.id.fb_output);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(view.getContext());
        count = 0;

        button.setOnClickListener(v -> FirebaseClickEvent());

        return view;
    }


    public void FirebaseClickEvent(){
        mFirebaseAnalytics.logEvent("button_clicked", null);
        count++;
        update_text();
    }

    public void update_text(){
        String t = count +" Events have been sent";
        text.setText(t);
    }


}