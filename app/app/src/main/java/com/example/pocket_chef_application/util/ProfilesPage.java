package com.example.pocket_chef_application.util;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.pocket_chef_application.MainActivity;
import com.example.pocket_chef_application.R;
import com.example.pocket_chef_application.SettingsActivity;
import com.example.pocket_chef_application.UploadActivity;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

import java.util.UUID;

import static android.content.Context.MODE_PRIVATE;

public class ProfilesPage extends Fragment {

    Button good, bad;


    public static ProfilesPage newInstance(String param1, String param2) {
        ProfilesPage fragment = new ProfilesPage();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_profiles_page, container, false);

        TextView logout = view.findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getContext().getSharedPreferences("checkbox", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("remember", "false");
                editor.apply();

                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), LogIn.class));
            }
        });

        TextView settings = view.findViewById(R.id.settings);
        settings.setOnClickListener(v ->{
            Intent i = new Intent(this.getContext(), SettingsActivity.class);
            startActivity(i);
            requireActivity().overridePendingTransition(R.anim.slide_in_top, R.anim.nothing);
        });

        good = view.findViewById(R.id.good_recipe);
        bad = view.findViewById(R.id.bad_recipe);

        good.setOnClickListener(v -> new UploadActivity().sendGoodRecipe(getContext()));

        bad.setOnClickListener(v -> new UploadActivity().sendBadRecipe(getContext()));



        return view;
    }
}