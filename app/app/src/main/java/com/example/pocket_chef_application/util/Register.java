package com.example.pocket_chef_application.util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocket_chef_application.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private TextView registerUser;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword, editTextPasswordConfirm;
    private ProgressBar progressBar;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

/*    private FirebaseDatabase database;*/
    private FirebaseFirestore mFireStore;
    private FirebaseAuth mAuth;

    private static final String TAG = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFullName = (EditText) findViewById(R.id.fullName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextPasswordConfirm = (EditText) findViewById(R.id.confirmPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mDisplayDate = (TextView) findViewById(R.id.DOB);
        mDisplayDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        Register.this,
                        android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        mDateSetListener,
                        year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mDateSetListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;

            String date = month + "/" + dayOfMonth + "/" + year;
            mDisplayDate.setText(date);
        };
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.registerUser:
                RegisterUser();
                break;
        }
    }

    private void RegisterUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String confirmation = editTextPasswordConfirm.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = mDisplayDate.getText().toString().trim();

        if(fullName.isEmpty()){
            editTextFullName.setError("Full name is required!");
            editTextFullName.requestFocus();
            return;
        }

        if(age.isEmpty()){
            editTextAge.setError("Age is required!");
            editTextFullName.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextAge.setError("Email is required!");
            editTextFullName.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            editTextEmail.setError("Please provide valid email!");
            editTextEmail.requestFocus();
            return;
        }

        if(password.isEmpty()){
            editTextPassword.setError("Password is required");
            editTextPassword.requestFocus();
            return;
        }

        if(password.length() < 6){
            editTextPassword.setError("Min password length should be 6 characters!");
            editTextPassword.requestFocus();
            return;
        }

        if(!password.equals(confirmation)){
            editTextPassword.setError("The password confirmation must match the password!");
            editTextPassword.requestFocus();
            return;
        }
        Log.d(TAG, "Info: "+ fullName + email + age);
        progressBar.setVisibility(View.VISIBLE);
        Registry(email, password, fullName, age);
        progressBar.setVisibility(View.GONE);
    }

    private void Registry(String email, String password, String fullName, String age){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this, "User has been created", Toast.LENGTH_SHORT).show();
                        updateFireStore(fullName, age, email);
                    }
                    else{
                        Toast.makeText(Register.this, "Fail to register! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateFireStore(String fullName, String age, String email) {
        Map<String, String> userMap = new HashMap<>();

        userMap.put("name", fullName);
        userMap.put("DOB", age);
        userMap.put("email", email);

        mFireStore.collection("user_profiles")
                .add(userMap)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    Toast.makeText(Register.this, "User Profile added to FireStore", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    String error = e.getMessage();
                    Log.w(TAG, "Error adding document", e);
                    Toast.makeText(Register.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                });
    }
}