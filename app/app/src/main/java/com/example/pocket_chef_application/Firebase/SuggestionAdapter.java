package com.example.pocket_chef_application.Firebase;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.UploadActivity;

import java.util.List;

public class SuggestionAdapter extends ArrayAdapter {
    private List<String> strings;
    private Context context;
    final static String TAG = SuggestionAdapter.class.getSimpleName();

    public SuggestionAdapter(@NonNull Context context, int resource, List<String> strings) {
        super(context, resource, strings);
        this.strings = strings;
        this.context = context;
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
        if(suggestions.size() > 5){
            strings.addAll(suggestions.subList(0,4));
        }else{
            strings.addAll(suggestions);
        }
        Toast.makeText(context, "Set has been updated", Toast.LENGTH_LONG).show();

        notifyDataSetChanged();
    }
}
