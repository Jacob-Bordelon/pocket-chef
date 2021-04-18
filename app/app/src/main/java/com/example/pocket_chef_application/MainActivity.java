package com.example.pocket_chef_application;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.pocket_chef_application.util.LogIn;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SearchView;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static FragmentManager manager;
    private static Dialog panel_switch;
    private Button logout;
    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        logout = (Button) findViewById(R.id.signOut);
        mFirebaseAuth = FirebaseAuth.getInstance();

        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

        manager = getSupportFragmentManager();

        // setup fab
        Log.d(TAG, "onCreate: SetupFab");
        panel_switch = new Dialog(this);
        panel_switch.setContentView(R.layout.tab_manager);
        panel_switch.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Window window = panel_switch.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);

        // Lambda handler of fab.
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(v -> panel_switch.show());



        ImageButton button1 = panel_switch.findViewById(R.id.fab_generate_recipes_btn);
        ImageButton button3 = panel_switch.findViewById(R.id.fab_pantry_btn);
        ImageButton button4 = panel_switch.findViewById(R.id.fab_upload_recipe_btn);



        button1.setOnClickListener(v -> {
            switch_fragment(new Generate_Recipes());
            button1.setVisibility(View.GONE);
            button3.setVisibility(View.VISIBLE);
            button4.setVisibility(View.VISIBLE);

        });

        button3.setOnClickListener(v -> {
            switch_fragment(new Pantry());
            button1.setVisibility(View.VISIBLE);
            button3.setVisibility(View.GONE);
            button4.setVisibility(View.VISIBLE);

        });

        button4.setOnClickListener(this::launchUpload);


        Log.d(TAG, "onCreate: Setup Buttons");

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if(mFirebaseUser!=null){
            //logged in
            return;
        }else{
            //not logged in
            startActivity(new Intent(this, LogIn.class));
            finish();
        }

    }

    public static void switch_fragment(Fragment fragment){
        manager.beginTransaction()
                .replace(R.id.nav_host_fragment,fragment)
                .addToBackStack(null)
                .commit();
        panel_switch.dismiss();
        Log.d(TAG, "switch_fragment: "+fragment.getClass().getName());
    }

    public void launchUpload(View v){
        //launch a new activity for uploading

        Intent i = new Intent(v.getContext(), UploadActivity.class);
        startActivity(i);
    }


}