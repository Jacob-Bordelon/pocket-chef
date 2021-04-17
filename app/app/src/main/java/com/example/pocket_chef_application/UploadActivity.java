package com.example.pocket_chef_application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;

import com.example.pocket_chef_application.Firebase.SuggestionAdapter;
import com.example.pocket_chef_application.Model.Ingredient;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class UploadActivity extends Activity implements View.OnClickListener {

    final static String TAG = UploadActivity.class.getSimpleName();
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private TextView cancel, save, upload;
    private EditText name, prep_time, cook_time, desc;
    private ImageView image;
    private LinearLayout ingredients, instructions;
    private Spinner diff;

    private ArrayList<Ingredient> ingredientsList;
    private String[] unitsList;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);
        setupViews();
        setupOnClickListeners();
        setupListViews();

    }


    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.slide_out_top);
    }

    private void setupViews(){
        name = findViewById(R.id.rec_name);
        prep_time = findViewById(R.id.rec_prep);
        cook_time = findViewById(R.id.rec_cook);
        desc = findViewById(R.id.rec_desc);
        ingredients = findViewById(R.id.rec_ingredients);
        image = findViewById(R.id.imageView);
        diff =  findViewById(R.id.rec_difficulty);
        cancel = findViewById(R.id.cancel);
        save =  findViewById(R.id.savebtn);
        upload = findViewById(R.id.uploadbtn);

        unitsList = this.getResources().getStringArray(R.array.units);
        ingredientsList = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    private void setupOnClickListeners(){
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        upload.setOnClickListener(this);
        image.setOnClickListener(this);
    }

    private void setupListViews(){
        newIngredient();

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void newIngredient(){
        View ingredientView = getLayoutInflater().inflate(R.layout.ingredient_view, null, false);
        EditText amount =  ingredientView.findViewById(R.id.amount);
        AutoCompleteTextView prompt =  ingredientView.findViewById(R.id.prompt);
        Spinner units = ingredientView.findViewById(R.id.units);
        Button add = ingredientView.findViewById(R.id.button5);

        List<String> keyList = new ArrayList<>();
        SuggestionAdapter adapter = new SuggestionAdapter(this,android.R.layout.simple_dropdown_item_1line,keyList);
        prompt.setAdapter(adapter);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, unitsList);
        units.setAdapter(arrayAdapter);

        add.setOnClickListener(v -> {
            if (!amount.getText().toString().matches("") && !prompt.getText().toString().matches("") && !units.getSelectedItem().toString().matches("")) {
                int amnt = Integer.parseInt(amount.getText().toString());
                String name = prompt.getText().toString();
                String unit = units.getSelectedItem().toString();
                ingredientsList.add(new Ingredient(amnt, name, unit));
                add.setOnClickListener(v1 -> { removeIngredient(ingredientView); });
                add.setBackground(getDrawable(R.drawable.ic_baseline_delete_24));
                newIngredient();
            }else {
                Toast.makeText(this,"Values have been left empty", Toast.LENGTH_SHORT).show();
            }

        });

        ingredients.addView(ingredientView);

    }

    private void removeIngredient(View view){
        ingredients.removeView(view);
    }

    private void grabAllValues(){
        // Basic string arguments
        //TODO - grab all edittext values

        // Complex string arguments
        //TODO - grab all listview items

        // Image
        //TODO - grab image resource
    }

    private void toJsonFormat(){
        //TODO take the aquired values and convert to the json/recipe format
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                break;
            case R.id.savebtn:
                //TODO - grab all values from each pane
                //TODO - save those values to a json file type structure
                //TODO - convert json file to a pretty print file type
                //TODO - store that file locally on the device
                break;
            case R.id.uploadbtn:
                String uniqueID = UUID.randomUUID().toString();
                //TODO - grab all values from each pane
                //TODO - save those values to a json file type structure
                //TODO - send the file to firebase (send the image to storage)
                uploadImageToFirebase(uniqueID);
                break;
            case R.id.imageView:
                //TODO - show option of take photo or select from existing
                openGallery();


                break;
        }
    }



    private void uploadImageToFirebase(String name){
        if(imageUri != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("/recipes/" + name);

            ref.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(UploadActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                            })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(UploadActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })
                    .addOnProgressListener(taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int)progress + "%");
                            });
        }
    }

    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE && data != null && data.getData() != null){
            imageUri = data.getData();
            image.setImageURI(imageUri);
        }
    }
}

