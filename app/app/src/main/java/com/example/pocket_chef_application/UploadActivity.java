package com.example.pocket_chef_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
    }

    protected void saveAction(View v){
        EditText a = findViewById(R.id.recipeName);
        EditText b = findViewById(R.id.ingredientsList);
        EditText c = findViewById(R.id.instructions);

        String recipeName = a.getText().toString();
        String ingredientsList = b.getText().toString();
        String instructions = c.getText().toString();
        Log.d("Success Save", recipeName);
        Toast.makeText(this, "Recipe Saved", Toast.LENGTH_LONG).show();
    }
}