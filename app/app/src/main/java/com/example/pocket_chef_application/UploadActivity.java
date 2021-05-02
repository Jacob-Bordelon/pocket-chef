package com.example.pocket_chef_application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Ingredient;
import com.example.pocket_chef_application.Model.Recipe;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.concurrent.CompletableFuture.completedFuture;


public class UploadActivity extends Activity implements View.OnClickListener {

    final static String TAG = UploadActivity.class.getSimpleName();
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private TextView cancel, save, upload, doubletapclue1,doubletapclue2;
    private EditText name, prep_time, cook_time, desc;
    private ImageView image;
    private LinearLayout ingredientsLayout, instructionsLayout;
    private Spinner diff;
    private FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();


    private static List<Food> foodsList;

    private HashMap<String,Ingredient> ingredientsList;
    private HashMap<String,String> instructionsList;

    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);
        setupViews();
        setupOnClickListeners();

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.nothing, R.anim.slide_out_top);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViews(){
        name = findViewById(R.id.rec_name);
        prep_time = findViewById(R.id.rec_prep);
        cook_time = findViewById(R.id.rec_cook);
        desc = findViewById(R.id.rec_desc);
        ingredientsLayout = findViewById(R.id.rec_ingredients);
        image = findViewById(R.id.imageView);
        diff =  findViewById(R.id.rec_difficulty);
        cancel = findViewById(R.id.cancel);
        save =  findViewById(R.id.savebtn);
        upload = findViewById(R.id.uploadbtn);
        instructionsLayout = findViewById(R.id.rec_instructions);
        doubletapclue1 = findViewById(R.id.double_tap_clue1);
        doubletapclue2 = findViewById(R.id.double_tap_clue2);


        name.setOnKeyListener(moveFocusTo(prep_time));
        prep_time.setOnKeyListener(moveFocusTo(cook_time));
        cook_time.setOnKeyListener(moveFocusTo(desc));

        desc.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    newIngredient();
                    return true;
                }
            }
            return false;
        });

        GestureDetector ingredientGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                newIngredient();
                return super.onDoubleTap(e);
            }
        });

        GestureDetector instructionGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                newStep();
                return super.onDoubleTap(e);
            }
        });
        
        ingredientsLayout.setOnTouchListener((v, event) -> { ingredientGestureDetector.onTouchEvent(event);return true; });
        instructionsLayout.setOnTouchListener((v, event) -> { instructionGestureDetector.onTouchEvent(event);return true; });


        String[] unitsList = this.getResources().getStringArray(R.array.units);
        ingredientsList = new HashMap<>();
        instructionsList =  new HashMap<>();

        storageReference = MainActivity.firebaseStorage.getReference();
        databaseReference = MainActivity.realtimedb.getReference("/bufferLayer/");
    }

    private void setupOnClickListeners(){
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        upload.setOnClickListener(this);
        image.setOnClickListener(this);


    }

    private View.OnKeyListener moveFocusTo(View toView){
        return (v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    toView.requestFocus();
                    return true;
                }
            }
            return false;
        };
    }


    // --------------------- Ingredient Stuff ------------------------

    @SuppressLint("UseCompatLoadingForDrawables")
    private void newIngredient(){
        @SuppressLint("InflateParams") View ingredientView = getLayoutInflater().inflate(R.layout.ingredient_view, null, false);
        EditText amount =  ingredientView.findViewById(R.id.amount);
        TextInputLayout prompt =  ingredientView.findViewById(R.id.prompt);
        Spinner units = ingredientView.findViewById(R.id.units);
        Button add = ingredientView.findViewById(R.id.button5);

        if(doubletapclue1.getVisibility() == View.VISIBLE){
            doubletapclue1.setVisibility(View.GONE);
        }


        add.setOnClickListener(v -> removeIngredient(ingredientView));


        ingredientView.findViewById(R.id.prompt_in).setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    newIngredient();
                    return true;
                }
            }
            return false;
        });
        ingredientsLayout.addView(ingredientView);
        amount.requestFocus();
    }

    private void removeIngredient(View view){
        ingredientsLayout.removeView(view);
        if(ingredientsLayout.getChildCount()==0){
            doubletapclue1.setVisibility(View.VISIBLE);
        }
    }


    //TODO - Compare these ingredients to the food database and obtain their collective FDCId values for accurate food results
    private Pair<TextInputLayout, Ingredient> getIngredientValues(View view){
        EditText amount =  view.findViewById(R.id.amount);
        TextInputLayout prompt = view.findViewById(R.id.prompt);
        Spinner units = view.findViewById(R.id.units);

        int amnt = Integer.parseInt(amount.getText().toString());
        String item = prompt.getEditText().getText().toString();
        String unit = units.getSelectedItem().toString();
        Log.d(TAG, "getIngredientValues: "+item);

        if(item.isEmpty()){
            prompt.setError("No Value Specified");
        }

        Ingredient ingredient = new Ingredient(amnt,item, unit);
        return new Pair<>(prompt, ingredient);

    }

    private HashMap<String,Ingredient> getAllIngredients(){

        HashMap<String,Ingredient> returnVal = new HashMap<>();
        for(int i = 0; i< ingredientsLayout.getChildCount(); i++){
            Pair<TextInputLayout, Ingredient> entry = getIngredientValues(ingredientsLayout.getChildAt(i));
            returnVal.put("00001",entry.second);
            /*returnVal.put(Integer.toString("00001",new Ingredient());*/
            /*helper.searchFor(entry.second.getName(), (FirebaseFoodDatabase_Helper.Container) foods -> {
                if(foods.size() < 1){
                    entry.first.setError("Value not recignized");
                }else if(foods.size() == 1){
                    returnVal.put(Integer.toString(foods.get(0).getFdcId()),entry.second);
                }else{
                    Toast.makeText(this, "Multiple entries found", Toast.LENGTH_SHORT).show();
                }
            });*/
        }
        return returnVal;
    }

    private HashMap<String,Ingredient> testIngredients(){
        HashMap<String,Ingredient> returnVal = new HashMap<>();
        for(int i = 0; i< 5; i++){
            Pair<String, Ingredient> entry = new Pair<>(Integer.toBinaryString(i), new Ingredient(1,"test ingredient","cups"));
            returnVal.put(entry.first,entry.second);
        }
        return returnVal;
    }

    // ------------------ Steps Stuff ----------------------------
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void newStep(){
        @SuppressLint("InflateParams") View instructionView = getLayoutInflater().inflate(R.layout.instruction_view, null, false);
        TextView steptext = instructionView.findViewById(R.id.steptext);
        EditText instruct = instructionView.findViewById(R.id.instruction);
        Button addButton = instructionView.findViewById(R.id.addbtn);

        if(doubletapclue2.getVisibility() == View.VISIBLE){
            doubletapclue2.setVisibility(View.GONE);
        }

        int stepInt = instructionsLayout.getChildCount()+1;
        steptext.setText("Step "+stepInt+": ");

        addButton.setOnClickListener(v->removeStep(instructionView));
        instruct.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN)
            {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    newStep();
                    return true;
                }
            }
            return false;
        });
        instructionsLayout.addView(instructionView);
        instruct.requestFocus();
    }

    @SuppressLint("SetTextI18n")
    private void removeStep(View view){
        int start = instructionsLayout.indexOfChild(view);
        instructionsLayout.removeView(view);
        for(int i = start; i< instructionsLayout.getChildCount(); i++){
            View v = instructionsLayout.getChildAt(i);
            TextView stepText = v.findViewById(R.id.steptext);
            stepText.setText("Step "+(i+1)+": ");
        }
        if(instructionsLayout.getChildCount()==0){
            doubletapclue2.setVisibility(View.VISIBLE);
        }
    }

    private Pair<String, String> getInstructionValues(View view){
        TextView steptext = view.findViewById(R.id.steptext);
        EditText instruct = view.findViewById(R.id.instruction);
        String stepNum = steptext.getText().toString();
        stepNum = stepNum.replace(" ","");
        stepNum = stepNum.replace(":","");

        String action = instruct.getText().toString();

        return new Pair<>(stepNum, action);

    }

    private HashMap<String,String> getAllInstructions(){
        HashMap<String,String> returnVal = new HashMap<>();
        for (int i = 0; i< instructionsLayout.getChildCount(); i++){
            Pair<String, String> entry = getInstructionValues(instructionsLayout.getChildAt(i));
            returnVal.put(entry.first, entry.second);
        }
        return returnVal;
    }

    private HashMap<String,String> testInstructions(){
        HashMap<String,String> returnVal = new HashMap<>();
        for (int i = 0; i< 5; i++){
            Pair<String, String> entry = new Pair<>("Step"+(i+1), "Test step "+(i+1));
            returnVal.put(entry.first, entry.second);
        }
        return returnVal;
    }

    // ---------------- Recipe Related stuff ----------------------

    private boolean checkAllViews(){
        List<View> views = Arrays.asList(name,desc,cook_time,prep_time, diff);

        for(View v:views){
            if(v instanceof EditText){
                if(((EditText) v).getText().toString().matches("")){
                    return false;
                }
            }
        }
        return true;
    }

    private Recipe formatDataToRecipe(String uniqueID){
        String title        = name.getText().toString();
        String author       = Objects.requireNonNull(MainActivity.firebaseAuth.getCurrentUser()).getDisplayName();
        String description  = desc.getText().toString();
        String id           = uniqueID;
        int cook            = Integer.parseInt(cook_time.getText().toString());
        int prep            = Integer.parseInt(prep_time.getText().toString());
        int rating          = 0;
        String difficulty   = diff.getSelectedItem().toString();
        ingredientsList     = getAllIngredients();
        instructionsList    = getAllInstructions();
        String image        = "recipes/"+uniqueID;

        return new Recipe(
                title,
                id,
                author,
                description,
                cook,
                prep,
                rating,
                difficulty,
                image,
                instructionsList,
                ingredientsList
        );
    }

    private Recipe testRecipe(String uniqueID){
        String title        = "Test";
        String author       = "Author";
        String description  = "Test";
        String id           = uniqueID;
        int cook            = 15;
        int prep            = 15;
        int rating          = 0;
        String difficulty   = "Master";
        ingredientsList     = getAllIngredients();
        instructionsList    = testInstructions();
        String image        = "recipes/"+uniqueID;

        return new Recipe(
                title,
                id,
                author,
                description,
                cook,
                prep,
                rating,
                difficulty,
                image,
                instructionsList,
                ingredientsList
        );
    }



    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cancel:
                finish();
                break;
            case R.id.savebtn:
                //TODO - grab all values from each pane
                //TODO - save those values to a json file type structure
                //TODO - convert json file to a pretty print file type
                //TODO - store that file locally on the device
                break;
            case R.id.uploadbtn:
                String uniqueID = UUID.randomUUID().toString();
                Recipe recipe = testRecipe(uniqueID);
                uploadImageToFirebase(uniqueID, recipe);
                /*if(checkAllViews()){
                    Recipe recipe = testRecipe(uniqueID);
                    uploadImageToFirebase(uniqueID, recipe);
                }*/
                break;
            case R.id.imageView:
                openGallery();
                break;
        }
    }

    // Image related functions
    private void uploadRecipeToFirebase(Recipe recipe){
        Log.d(TAG, "uploadRecipeToFirebase: ");
        databaseReference.child(recipe.getId()).setValue(recipe)
        .addOnCompleteListener(task -> {
            Log.d(TAG, "Process to complete");
            if(task.isSuccessful()){
                Toast.makeText(UploadActivity.this, "Recipe Was Uploaded", Toast.LENGTH_SHORT).show();
                finish();
            }else{
                Toast.makeText(UploadActivity.this, "There was an error during upload", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadImageToFirebase(String name, Recipe recipe){
        if(imageUri != null){
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading Image...");
            progressDialog.show();
            String filePath = "/recipes/" + name +".jpg";

            StorageReference ref = storageReference.child(filePath);
            UploadTask uploadTask = ref.putFile(imageUri);
            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            });

            uploadTask.addOnCompleteListener(UploadActivity.this, task -> {
                progressDialog.dismiss();
                if(!task.isSuccessful()){
                    Toast.makeText(UploadActivity.this, "Error Occurred during upload", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "uploadImageToFirebase: "+task.getException().toString());
                }else{
                    Toast.makeText(UploadActivity.this, "Image was successfully uploaded", Toast.LENGTH_SHORT).show();

                }
            });


            Task<Uri> getDownloadUriTask = uploadTask.continueWithTask(task -> {
                if(!task.isSuccessful()){
                    throw task.getException();
                }
                return ref.getDownloadUrl();
            });
            getDownloadUriTask.addOnCompleteListener(UploadActivity.this, task -> {
                if (task.isSuccessful()) {
                    Uri downloadedUri = task.getResult();
                    Log.d(TAG, "uploadImageToFirebase: "+downloadedUri.toString());
                    recipe.setImage(downloadedUri.toString());
                    uploadRecipeToFirebase(recipe);
                }else{
                    Toast.makeText(UploadActivity.this, "Activity failed to upload image", Toast.LENGTH_SHORT ).show();
                }
            });
        }
        else {
            new AlertDialog.Builder(UploadActivity.this)
                    .setTitle("Warning")
                    .setMessage("You have not selected an Image for this Recipe. No image will be displayed when other users view your content. \n Is this okay?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        recipe.setImage("none");
                        uploadRecipeToFirebase(recipe);
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
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

