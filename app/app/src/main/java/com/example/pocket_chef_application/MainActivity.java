package com.example.pocket_chef_application;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import com.example.pocket_chef_application.GroceryList.GroceryList;
import com.example.pocket_chef_application.Pantry_utils.SearchFoodDB;
import com.example.pocket_chef_application.util.LogIn;
import com.example.pocket_chef_application.util.MasterPageAdapter;
import com.example.pocket_chef_application.util.ProfilesPage;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.MenuItem;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    public static FirebaseDatabase realtimedb = FirebaseDatabase.getInstance("https://pocketchef-be978-default-rtdb.firebaseio.com/");
    public static FirebaseDatabase fooddb = FirebaseDatabase.getInstance("https://pocketchef-be978-food-rtdb.firebaseio.com/");
    public static final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    public static final FirebaseStorage firebaseStorage = FirebaseStorage.getInstance("gs://pocketchef-be978-recipes");
    private BottomNavigationView bottomNavigationView;
    private ViewPager viewPager;
    private int lastFragment;
    public static boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        viewPager = (ViewPager)findViewById(R.id.viewpager);
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);



        ArrayList<Fragment> fragmentsArray = new ArrayList<>();
        fragmentsArray.add(new SearchFoodDB());
        fragmentsArray.add(new Generate_Recipes());
        fragmentsArray.add(new Pantry());
        fragmentsArray.add(new GroceryList());
        fragmentsArray.add(new ProfilesPage());

        MasterPageAdapter adapter = new MasterPageAdapter(getSupportFragmentManager(),fragmentsArray);

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPager.setCurrentItem(2);

        bottomNavigationView.setBackground(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
    }

    private final ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
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
                case 4:
                    bottomNavigationView.setSelectedItemId(R.id.settings);
                    break;
            }
            lastFragment=position;


        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    private final BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @SuppressLint("NonConstantResourceId")
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
                case R.id.settings:
                    viewPager.setCurrentItem(4);
                    break;
                default: return false;

            }

            return true;

        }
    };

    public static boolean isNetworkAvailable(Context con) {
        try {
            ConnectivityManager cm = (ConnectivityManager) con
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isConnected = isNetworkAvailable(this);

        if(isConnected){
            FirebaseUser mFirebaseUser = firebaseAuth.getCurrentUser();
            if(mFirebaseUser!=null){
                SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                String check_value = preferences.getString("remember", "");
                if(!check_value.equals("true")){
                    Log.d(TAG, "User signed out");
                    FirebaseAuth.getInstance().signOut();
                }
            }else{
                Log.d(TAG, "User logged in");
                startActivity(new Intent(this, LogIn.class));
                finish();
            }
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: ");
        viewPager.setCurrentItem(lastFragment);
    }








}