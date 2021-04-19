package com.example.pocket_chef_application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.example.pocket_chef_application.Firebase.SuggestionAdapter;
import com.example.pocket_chef_application.Model.Ingredient;
import com.example.pocket_chef_application.Model.Recipe;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;


public class UploadActivity extends Activity implements View.OnClickListener {

    final static String TAG = UploadActivity.class.getSimpleName();
    private static final int PICK_IMAGE = 100;
    private Uri imageUri;
    private TextView cancel, save, upload, doubletapclue1,doubletapclue2;
    private EditText name, prep_time, cook_time, desc;
    private ImageView image;
    private LinearLayout ingredientsLayout, instructionsLayout;
    private Spinner diff;

    private HashMap<String,Ingredient> ingredientsList;
    private HashMap<String,String> instructionsList;
    private String[] unitsList;

    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseStorage storage;
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


        unitsList = this.getResources().getStringArray(R.array.units);
        ingredientsList = new HashMap<>();
        instructionsList =  new HashMap<>();

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        database = MainActivity.realtimedb;
        databaseReference = database.getReference("/recipeBook/");
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
        AutoCompleteTextView prompt =  ingredientView.findViewById(R.id.prompt);
        Spinner units = ingredientView.findViewById(R.id.units);
        Button add = ingredientView.findViewById(R.id.button5);

        if(doubletapclue1.getVisibility() == View.VISIBLE){
            doubletapclue1.setVisibility(View.GONE);
        }


        List<String> keyList = new ArrayList<>();
        SuggestionAdapter promptAdapter = new SuggestionAdapter(this,android.R.layout.simple_dropdown_item_1line, keyList);
        prompt.setAdapter(promptAdapter);

        add.setOnClickListener(v -> removeIngredient(ingredientView));
        amount.setOnKeyListener(moveFocusTo(prompt));
        prompt.setOnKeyListener((v, keyCode, event) -> {
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
    private Pair<String, Ingredient> getIngredientValues(View view){
        EditText amount =  view.findViewById(R.id.amount);
        AutoCompleteTextView prompt =  view.findViewById(R.id.prompt);
        Spinner units = view.findViewById(R.id.units);

        int amnt = Integer.parseInt(amount.getText().toString());
        String item = prompt.getText().toString();
        String unit = units.getSelectedItem().toString();

        Ingredient ingredient = new Ingredient(amnt,item, unit);
        String itemLoc = Integer.toBinaryString(amnt);

        return new Pair<>(itemLoc, ingredient);

    }

    private HashMap<String,Ingredient> getAllIngredients(){
        HashMap<String,Ingredient> returnVal = new HashMap<>();
        for(int i = 0; i< ingredientsLayout.getChildCount(); i++){
            Pair<String, Ingredient> entry = getIngredientValues(ingredientsLayout.getChildAt(i));
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
                switch (keyCode)
                {
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_ENTER:
                        newStep();
                        return true;
                    default:
                        break;
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
            System.out.println(entry.first+" "+entry.second);
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
        String author       = "Anonymous";
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

    private Recipe testRecipe(String id){
        StorageReference ref = storageReference.child("/recipes/" + "blah");

        Log.d(TAG, "uploadImageToFirebase: "+ref);

        instructionsList.put("step1","blah blah blah");
        ingredientsList.put("0000101",new Ingredient(2,"blah","cups"));
        return new Recipe(
                "My Recipe",
                id,
                "Test Approval",
                "Test",
                4,
                4,
                0,
                "Beginner",
                " ",
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
                if(checkAllViews()){
                    Recipe recipe = formatDataToRecipe(uniqueID);
                    uploadImageToFirebase(uniqueID, recipe);
                }
                break;
            case R.id.imageView:
                openGallery();
                break;
        }
    }

    private void uploadRecipeToFirebase(Recipe recipe){
        databaseReference.child(recipe.getId()).setValue(recipe)
        .addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(UploadActivity.this, "Recipe Was Uploaded", Toast.LENGTH_SHORT).show();
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

            Log.d(TAG, "uploadImageToFirebase: "+ref);

            UploadTask uploadTask = ref.putFile(imageUri);

            uploadTask.addOnProgressListener(taskSnapshot -> {
                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                progressDialog.setMessage("Uploaded " + (int)progress + "%");
            });


            uploadTask.addOnCompleteListener(UploadActivity.this, task -> {
                progressDialog.dismiss();
                Toast.makeText(UploadActivity.this, "Task has been completed", Toast.LENGTH_SHORT).show();
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


                    recipe.setImage(downloadedUri.toString());
                    uploadRecipeToFirebase(recipe);
                }else{
                    Toast.makeText(UploadActivity.this, "Activity failed to upload image", Toast.LENGTH_SHORT ).show();
                }
            });
                     /*
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
*/

        }
        else {
            new AlertDialog.Builder(UploadActivity.this)
                    .setTitle("Warning")
                    .setMessage("You have not selected an Image for this Recipe. No image will be displayed when other users view your content. \n Is this okay?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            uploadRecipeToFirebase(recipe);
                        }
                    })

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton("No", null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }

        //https://firebasestorage.googleapis.com/v0/b/pocketchef-9fb14.appspot.com/o/recipes%2Fracy.jpg?alt=media&token=71fc154b-b2b8-4551-b048-6474a2f9cf80
        //https://firebasestorage.googleapis.com/v0/b/pocketchef-9fb14.appspot.com/o/blurred%2Frecipes%2Fracy.jpg?alt=media&token=71fc154b-b2b8-4551-b048-6474a2f9cf80




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

