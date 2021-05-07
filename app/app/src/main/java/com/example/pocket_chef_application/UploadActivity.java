package com.example.pocket_chef_application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.BlendModeColorFilterCompat;
import androidx.core.graphics.BlendModeCompat;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Gen_Recipes.IngredientChip;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Model.Ingredient;
import com.example.pocket_chef_application.Model.Recipe;
import com.example.pocket_chef_application.util.BackgroundThread;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.atomic.AtomicInteger;


public class UploadActivity extends Activity implements View.OnClickListener {

    final static String TAG = "UploadFragment";
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private TextView cancel, save, upload;
    private EditText name, prep_time, cook_time;
    private ImageView image;
    private LinearLayout instructionsLayout;
    private AutoCompleteTextView diff;
    @SuppressLint("StaticFieldLeak")
    public static TextView helper_text;

    private ImageButton addIngredientbtn;
    private TextInputLayout name_layout,amount_layout, unit_layout;
    private MaterialAutoCompleteTextView units;
    private TextInputLayout desc;
    private ChipGroup chipGroup;

    public static HashMap<String,Ingredient> ingredientsList= new HashMap<>();
    private HashMap<String,String> instructionsList= new HashMap<>();

    private DatabaseReference databaseReference = MainActivity.realtimedb.getReference("/bufferLayer/");
    private StorageReference storageReference = MainActivity.firebaseStorage.getReference();
    private BackgroundThread handlerThread = new BackgroundThread();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handlerThread.quit();
    }
    @Override
    public void finish() {
        super.finish();
        handlerThread.quit();
        overridePendingTransition(R.anim.nothing, R.anim.slide_out_top);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String[] difficultyList = this.getResources().getStringArray(R.array.difficulties);
        ArrayAdapter<String> diffAdapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, difficultyList);
        diff.setAdapter(diffAdapter);

        String[] unitsList = this.getResources().getStringArray(R.array.units);
        ArrayAdapter<String> unitsAdapter = new ArrayAdapter<String>(this,android.R.layout.select_dialog_item, unitsList);
        units.setAdapter(unitsAdapter);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.upload_layout);
        setupViews();
        setupOnClickListeners();
        handlerThread.start();

    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupViews(){
        name = findViewById(R.id.rec_name);
        prep_time = findViewById(R.id.rec_prep);
        cook_time = findViewById(R.id.rec_cook);
        desc = findViewById(R.id.rec_desc);
        image = findViewById(R.id.imageView);
        diff =  findViewById(R.id.rec_difficulty);
        cancel = findViewById(R.id.cancel);
        save =  findViewById(R.id.savebtn);
        upload = findViewById(R.id.uploadbtn);
        instructionsLayout = findViewById(R.id.rec_instructions);
        name_layout = findViewById(R.id.name_layout);
        amount_layout = findViewById(R.id.amount_layout);
        unit_layout = findViewById(R.id.units_layout);
        units = findViewById(R.id.units);
        chipGroup = findViewById(R.id.rec_ingredients);
        addIngredientbtn = findViewById(R.id.imageButton);
        helper_text = findViewById(R.id.helper_text);
        Dialog mDialog = new Dialog(this);
        newStep();

    }

    private void setupOnClickListeners(){
        cancel.setOnClickListener(this);
        save.setOnClickListener(this);
        upload.setOnClickListener(this);
        image.setOnClickListener(this);

        addIngredientbtn.setOnClickListener(v -> {
            String textInput = name_layout.getEditText().getText().toString();
            String amountInput = amount_layout.getEditText().getText().toString();
            String unitInput = units.getText().toString();
            boolean isBlank = false;

            if(textInput.isEmpty()){
                name_layout.setError("Value needed");
                isBlank=true;
            }else{
                name_layout.setError(null);
            }

            if(amountInput.isEmpty()){
                amount_layout.setError("Value needed");
                isBlank=true;
            }else{
                amount_layout.setError(null);
            }

            if(unitInput.isEmpty()){
                unit_layout.setError("Value needed");
                isBlank=true;
            }else{
                unit_layout.setError(null);
            }

            if(!isBlank){
                addIngredient(textInput,Integer.parseInt(amountInput),unitInput);

                name_layout.getEditText().setText(null);
                amount_layout.getEditText().setText(null);
            }
        });
    }

    // --------------------- Ingredient Stuff ------------------------

    @SuppressLint({"UseCompatLoadingForDrawables", "ResourceType"})
    private void addIngredient(String name, int amount, String units){
        // make ingredient object
        // search database for food item
        // add entry to list of ingredients
        IngredientChip chip = new IngredientChip(this,name,amount, units);

        chip.setOnCloseIconClickListener(v -> {
            chipGroup.removeView(chip);
            helper_text.setText(null);
            helper_text.setVisibility(View.GONE);
        });

        chip.setOnLongClickListener(v -> {
            chip.toggleDelete();
            return true;
        });


        searchItem(chip);
        chipGroup.addView(chip);

    }


    @SuppressLint("UseCompatLoadingForDrawables")
    private void searchItem(IngredientChip chip) {
        Runnable run = new SearchRunnable(chip);
        handlerThread.getHandler().post(run);
    }

    static class SearchRunnable implements Runnable {
        private static final String TAG = "SearchRunnable";
        private FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();
        private IngredientChip chip;


        SearchRunnable(final IngredientChip chip) {
            this.chip = chip;
        }

        @Override
        public void run() {
            helper.searchFor(chip.getName(), foods -> chip.setFoodList(foods));
        }
    }

    private HashMap<String,Ingredient> getAllIngredients(){
        HashMap<String,Ingredient> returnVal = new HashMap<>();

        for(IngredientChip chip:IngredientChip.chips){
            returnVal.put(chip.getFoodId(),chip.getIngredient());
        }
        return returnVal;
    }

    private HashMap<String,Ingredient> testIngredients(){
        HashMap<String,Ingredient> returnVal = new HashMap<>();

        for(int i = 0; i<10;i++){
            returnVal.put(Integer.toBinaryString(i),new Ingredient(2,"Name","cups"));
        }
        return returnVal;
    }

    // ------------------ Steps Stuff ----------------------------------------------------------------------
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void newStep(){
        @SuppressLint("InflateParams") View instructionView = getLayoutInflater().inflate(R.layout.instruction_view, null, false);
        TextView steptext = instructionView.findViewById(R.id.steptext);
        EditText instruct = instructionView.findViewById(R.id.instruction);
        Button addButton = instructionView.findViewById(R.id.addbtn);


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
        if (instructionsLayout.getChildCount()>1){
            instruct.requestFocus();
        }

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
        String author       = "Ricardo Aranaga";
        String description  = desc.getEditText().getText().toString();
        String id           = uniqueID;
        int cook            = Integer.parseInt(cook_time.getText().toString());
        int prep            = Integer.parseInt(prep_time.getText().toString());
        int rating          = 0;
        String difficulty   = diff.getText().toString();
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
        ingredientsList     = testIngredients();
        instructionsList    = testInstructions();
        String image        = "none";

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

    private Recipe testBadRecipe(String uniqueID){
        String title        = "Test";
        String author       = "Author";
        String description  = "I AM SHOUTING";
        String id           = uniqueID;
        int cook            = 15;
        int prep            = 15;
        int rating          = 0;
        String difficulty   = "Master";
        ingredientsList     = testIngredients();
        instructionsList    = testInstructions();
        String image        = "none";

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

    public void sendGoodRecipe(Context context){
        Recipe recipe = testRecipe(UUID.randomUUID().toString());
        uploadRecipeToFirebaseFromActivity(context,recipe);
    }

    public void sendBadRecipe(Context context){
        Recipe recipe = testBadRecipe(UUID.randomUUID().toString());
        uploadRecipeToFirebaseFromActivity(context,recipe);
    }


    private boolean validate(){
        boolean allGood=true;
        if(name.getText().toString().isEmpty()){
            allGood=false;
        }

        if(prep_time.getText().toString().isEmpty()){
            allGood=false;
        }

        if(cook_time.getText().toString().isEmpty()){
            allGood=false;
        }

        if(diff.getText().toString().isEmpty()){
            allGood=false;
        }

        if(IngredientChip.chips.size() < 1){
            allGood = false;
        }

        if(instructionsLayout.getChildCount()<1){
            allGood = false;
        }

        return allGood;
    }

    /*----------------- On Click -------------------------*/
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
                if(validate()){
                    Recipe recipe = formatDataToRecipe(uniqueID);
                    uploadImageToFirebase(uniqueID, recipe);
                }
                break;
            case R.id.imageView:
                openGallery();
                break;
        }
    }


    private void uploadRecipeToFirebaseFromActivity(Context context, Recipe recipe){
        Log.d(TAG, "uploadRecipeToFirebase: ");
        databaseReference.child(recipe.getId()).setValue(recipe)
                .addOnCompleteListener(task -> {
                    Log.d(TAG, "Process to complete");
                    if(task.isSuccessful()){
                        Toast.makeText(context, "Recipe Was Uploaded", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(context, "There was an error during upload", Toast.LENGTH_SHORT).show();
                    }
                });
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

