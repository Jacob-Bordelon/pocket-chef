package com.example.pocket_chef_application.Firebase;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SearchView;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Model.Food;

import java.util.List;

public class SuggestionAdapter extends ArrayAdapter {
    private List<String> strings;
    private Context context;
    private int resource;
    final static String TAG = SuggestionAdapter.class.getSimpleName();

    public SuggestionAdapter(@NonNull Context context, int resource, List<String> strings) {
        super(context, resource, strings);
        this.strings = strings;
    }

    public List<String> getStrings() {
        return strings;
    }

    @Override
    public int getCount() {
        return strings.size();
    }

    @Override
    public Object getItem(int position) {
        return strings.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }

    public void updateDataSet(List<String> suggestions){
        strings.clear();
        strings.addAll(suggestions);
        this.notifyDataSetChanged();
    }
}
