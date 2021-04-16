package com.example.pocket_chef_application;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import com.example.pocket_chef_application.GroceryList.GroceryList;
import com.example.pocket_chef_application.Pantry_utils.AddItemsToPantry;
import com.example.pocket_chef_application.util.MasterPageAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.SearchView;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private int lastFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = (ViewPager)findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);

        ArrayList<Fragment> fragmentsArray = new ArrayList<>();
        fragmentsArray.add(new AddItemsToPantry());
        fragmentsArray.add(new Generate_Recipes());
        fragmentsArray.add(new Pantry());
        fragmentsArray.add(new GroceryList());




        MasterPageAdapter adapter = new MasterPageAdapter(getSupportFragmentManager(),fragmentsArray);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setCurrentItem(2);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            switch (position){
                case 0:
                    bottomNavigationView.setSelectedItemId(R.id.search);
                    break;
                case 1:
                    bottomNavigationView.setSelectedItemId(R.id.recipes);
                    break;
                case 2:
                    bottomNavigationView.setSelectedItemId(R.id.pantry);
                    break;
                case 3:
                    bottomNavigationView.setSelectedItemId(R.id.groceryList);
                    break;
            }
            lastFragment=position;


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.search:
                    viewPager.setCurrentItem(0);
                    break;
                case R.id.recipes:
                    viewPager.setCurrentItem(1);
                    break;
                case R.id.pantry:
                    viewPager.setCurrentItem(2);
                    break;
                case R.id.groceryList:
                    viewPager.setCurrentItem(3);
                    break;
                default: return false;

            }

            return true;

        }
    };

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        viewPager.setCurrentItem(lastFragment);
    }

    public void launchUpload(View v){
        //launch a new activity for uploading

        Intent i = new Intent(v.getContext(), UploadActivity.class);
        startActivity(i);
    }


}