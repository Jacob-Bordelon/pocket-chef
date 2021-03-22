package com.example.pocket_chef_application;

import android.content.Intent;
import android.os.Bundle;

import com.example.pocket_chef_application.Pantry.Pantry;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.View;

import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static LinearLayout layout;
    private static FragmentManager manager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layout = findViewById(R.id.Sidebar);
        layout.setVisibility(View.GONE);

        manager = getSupportFragmentManager();
        switch_fragment(new Pantry());




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

        ImageButton button1 = findViewById(R.id.fab_generate_recipes_btn);
        ImageButton button2 = findViewById(R.id.fab_homepage_btn);
        ImageButton button3 = findViewById(R.id.fab_pantry_btn);
        ImageButton button4 = findViewById(R.id.fab_upload_recipe_btn);



        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_fragment(new Generate_Recipes());
                button1.setVisibility(View.GONE);
                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.VISIBLE);
                button4.setVisibility(View.VISIBLE);

            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_fragment(new Homepage());
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.GONE);
                button3.setVisibility(View.VISIBLE);
                button4.setVisibility(View.VISIBLE);

            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_fragment(new Pantry());
                button1.setVisibility(View.VISIBLE);
                button2.setVisibility(View.VISIBLE);
                button3.setVisibility(View.GONE);
                button4.setVisibility(View.VISIBLE);

            }
        });

        button4.setOnClickListener(this::launchUpload);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


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