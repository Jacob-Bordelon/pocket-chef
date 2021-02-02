package com.example.pocket_chef_application;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
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
        FrameLayout layout = findViewById(R.id.TestFrame);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set default value for toolbar and layout
        toolbar.setVisibility(View.GONE);
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
                Fragment fragment = FirstFragment.newInstance(null);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_container,fragment, "FirstFragment");
                transaction.addToBackStack(null);
                transaction.commit();

            }
        });


        ImageButton button2 = findViewById(R.id.imageButton2);
        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = SecondFragment.newInstance(null);
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.nav_host_fragment_container,fragment, "SecondFragment");
                //transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        ImageButton button3 = findViewById(R.id.imageButton3);
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                CharSequence text = "Button 3 has been pressed";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        });




    }

    public void openFragment(){
        FirstFragment fragment = FirstFragment.newInstance("Hello");
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_fade_enter,R.anim.fragment_fade_exit);
        transaction.addToBackStack(null);
        transaction.add(R.id.nav_host_fragment,fragment,"BLANK_FRAGMENT").commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}