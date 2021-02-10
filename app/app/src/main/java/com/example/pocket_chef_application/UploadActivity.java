package com.example.pocket_chef_application;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class UploadActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        Button savebutton = findViewById(R.id.save);
        savebutton.setOnClickListener(v->saveAction());
    }

    protected void saveAction(){
        EditText recipeName = findViewById(R.id.recipeName);
        EditText ingredientsList = findViewById(R.id.ingredientsList);
        EditText instructionsText = findViewById(R.id.instructions);

        String name = recipeName.getText().toString();
        String ingredients = ingredientsList.getText().toString();
        String instructions = instructionsText.getText().toString();

        int failcheck=0;

        if(name.matches("")){
            Log.d("UploadActivityError","missing recipe name");
            Toast.makeText(this, "Missing Recipe Name", Toast.LENGTH_LONG).show();
            failcheck=1;
        }

        if(ingredients.matches("")){
            Log.d("UploadActivity","missing ingredients");
            Toast.makeText(this, "Missing Ingredients", Toast.LENGTH_LONG).show();
            failcheck=1;
        }

        if(instructions.matches("")){
            Log.d("UploadActivity","missing instructions");
            Toast.makeText(this, "Missing Instructions", Toast.LENGTH_LONG).show();
            failcheck=1;
        }


        if(failcheck==0){
            Log.d("Success Save", name);
            Toast.makeText(this, "Recipe Saved", Toast.LENGTH_LONG).show();
        }





    }
}