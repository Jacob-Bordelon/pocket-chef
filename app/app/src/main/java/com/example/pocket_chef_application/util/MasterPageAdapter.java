package com.example.pocket_chef_application.util;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.pocket_chef_application.Generate_Recipes;
import com.example.pocket_chef_application.GroceryList.GroceryList;
import com.example.pocket_chef_application.Pantry;

import java.util.ArrayList;
import java.util.List;

public class MasterPageAdapter extends FragmentPagerAdapter {

    private ArrayList<Fragment> fragments;

    public MasterPageAdapter(@NonNull FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm,FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.fragments = fragments;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
       return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }
}
