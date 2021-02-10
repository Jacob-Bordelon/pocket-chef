package com.example.pocket_chef_application;

import android.content.Intent;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static LinearLayout layout;
    private static FragmentManager manager;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG,"One 1 Instance");
        layout = findViewById(R.id.Sidebar);
        layout.setVisibility(View.GONE);

        manager = getSupportFragmentManager();




        // Lambda handler of fab.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(layout.getVisibility() != View.VISIBLE){
                    layout.setVisibility(View.VISIBLE);

                }else{
                    layout.setVisibility(View.GONE);

                }
            }
        });

        ImageButton button1 = findViewById(R.id.imageButton1);
        ImageButton button2 = findViewById(R.id.imageButton2);
        ImageButton button3 = findViewById(R.id.imageButton3);



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_fragment(new SecondFragment());
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_fragment(new FirstFragment());
                button2.setVisibility(View.GONE);
                button1.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_fragment(new ThirdFragment());
                button3.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button1.setVisibility(View.VISIBLE);
            }
        });

    }

    private void switch_fragment(Fragment fragment){
        manager.beginTransaction()
                .replace(R.id.nav_host_fragment,fragment)
                .addToBackStack(null)
                .commit();
        layout.setVisibility(View.GONE);
    }

    public void launchUpload(View v){
        //launch a new activity for uploading

        Intent i = new Intent(v.getContext(), UploadActivity.class);
        startActivity(i);
    }


}