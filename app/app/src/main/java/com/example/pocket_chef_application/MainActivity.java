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

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout layout = findViewById(R.id.Sidebar);
        layout.setVisibility(View.GONE);


        // Lambda handler of fab.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(layout.getVisibility() != View.VISIBLE){
                    layout.setVisibility(View.VISIBLE);
                    //toolbar.setVisibility(View.VISIBLE);
                }else{
                    layout.setVisibility(View.GONE);
                    //toolbar.setVisibility(View.GONE);
                }
            }
        });

        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;




        ImageButton button1 = findViewById(R.id.imageButton1);
        button1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                SecondFragment fragment = new SecondFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        

        ImageButton button2 = findViewById(R.id.imageButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FirstFragment fragment = new FirstFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        ImageButton button3 = findViewById(R.id.imageButton3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                ThirdFragment fragment = new ThirdFragment();
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.nav_host_fragment,fragment)
                        .addToBackStack(null)
                        .commit();
            }
        });


    }





    public void launchUpload(View v){
        //launch a new activity for uploading

        Intent i = new Intent(v.getContext(), UploadActivity.class);
        startActivity(i);
    }


}
