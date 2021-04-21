package com.example.pocket_chef_application.util;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.pocket_chef_application.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.UUID;

public class Register extends AppCompatActivity implements View.OnClickListener{

    private TextView registerUser;
    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextPasswordConfirm;
    private ProgressBar progressBar;
    private TextView mDisplayDate;
    private DatePickerDialog.OnDateSetListener mDateSetListener;

    private FirebaseFirestore mFireStore;
    private FirebaseAuth mAuth;
    private UUID uuid;

    private static final String TAG = "UserInfo";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        mFireStore = FirebaseFirestore.getInstance();

        registerUser = (Button) findViewById(R.id.registerUser);
        registerUser.setOnClickListener(this);

        editTextFirstName = (EditText) findViewById(R.id.firstName);
        editTextLastName = (EditText) findViewById(R.id.lastName);
        editTextEmail = (EditText) findViewById(R.id.email);
        editTextPassword = (EditText) findViewById(R.id.password);
        editTextPasswordConfirm = (EditText) findViewById(R.id.confirmPassword);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        mDisplayDate = (TextView) findViewById(R.id.DOB);
        mDisplayDate.setOnClickListener(v -> {
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
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String age = mDisplayDate.getText().toString().trim();

        if(firstName.isEmpty()){
            editTextFirstName.setError("First name is required!");
            editTextFirstName.requestFocus();
            return;
        }

        if(lastName.isEmpty()){
            editTextLastName.setError("Last name is required!");
            editTextLastName.requestFocus();
        }

        if(age.isEmpty()){
            mDisplayDate.setError("Age is required!");
            mDisplayDate.requestFocus();
            return;
        }

        if(email.isEmpty()){
            editTextEmail.setError("Email is required!");
            editTextEmail.requestFocus();
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
        Log.d(TAG, "Info: "+ firstName + " " + lastName + " " + email + " " + age);
        progressBar.setVisibility(View.VISIBLE);
        Registry(email, password, firstName, lastName, age);
        progressBar.setVisibility(View.GONE);
    }

    private void Registry(String email, String password, String firstName, String lastName,  String age){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this, "User has been created", Toast.LENGTH_SHORT).show();
                        updateFireStore(firstName, lastName, age, email);
                    }
                    else{
                        Toast.makeText(Register.this, "Fail to register! Try again!", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void updateFireStore(String firstName, String lastName, String age, String email) {
        Map<String, Object> userMap = new HashMap<>();

        userMap.put("first_name", firstName);
        userMap.put("last_name", lastName);
        userMap.put("DOB", age);
        userMap.put("email", email);
        userMap.put("strikes", 0);

        String doc_id = uuid.randomUUID().toString();
        Log.d(TAG, "Document ID: " + doc_id);

        mFireStore.collection("user_profiles")
                .document(doc_id)
                .set(userMap)
                .addOnSuccessListener(documentReference -> {
/*                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());*/
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