package com.example.pocket_chef_application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Model.Food;
import com.example.pocket_chef_application.Pantry_utils.AddItemsToPantry;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.squareup.picasso.Picasso;


import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;



public class Item_Recognition_Activity extends AppCompatActivity {
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final String TAG = "Image_Item_Rec";

    private PreviewView previewView;
    private TextView output;
    private Button scan_button;
    private ExecutorService cameraExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private ProcessCameraProvider cameraProvider;
    private List<Food> foodList;

    private Dialog mDialog2;
    private EditText amount, exp;
    private Button submit;
    private TextView name;
    private ImageView img;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item__recognition_);

        scan_button = findViewById(R.id.fb_event);
        output = findViewById(R.id.output);
        previewView = findViewById(R.id.viewFinder);

        checkPermissions();
        setOnClickListeners();
        cameraExecutor = Executors.newSingleThreadExecutor();
        fetchFood();
    }

    private void setOnClickListeners() {
        scan_button.setOnClickListener(v -> takePhoto());
    }

    public void checkPermissions(){
        if(allPermissionsGranted()){
            startCamera();
        }else{
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSIONS){
            if(allPermissionsGranted()){
                startCamera();
            }else{
                Toast.makeText(this, "Action cannot be performed: User has not granted permission",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void bindImageAnalysis(@NonNull ProcessCameraProvider cameraProvider) {
        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setTargetResolution(new Size(1280, 720))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalyzer(this,output));


        OrientationEventListener orientationEventListener = new OrientationEventListener(this) {
            @Override
            public void onOrientationChanged(int orientation) {
            }
        };


        orientationEventListener.enable();
        Preview preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle((LifecycleOwner)this, cameraSelector,  imageAnalysis, preview);



    }

    private void startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindImageAnalysis(cameraProvider);
            }  catch (ExecutionException | InterruptedException e){ e.printStackTrace();}
        }, ContextCompat.getMainExecutor(this));




    }

    private void takePhoto() {


    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        cameraExecutor.shutdown();
    }

    private class ImageAnalyzer implements ImageAnalysis.Analyzer {

        private LocalModel model;
        private boolean newUpdateGraphicOverlayImageSourceInfo = true;
        private Context context;
        private ObjectDetector objectDetector;
        private TextView output;
        private Dialog mDialog ;

        public ImageAnalyzer(Context context, TextView output) {
            this.context = context;
            this.output = output;
            mDialog= new Dialog(this.context);
            setupDialog();

            this.model = new LocalModel.Builder()
                    .setAssetFilePath("FoodModel.tflite")
                    .build();


            CustomObjectDetectorOptions customObjectDetectorOptions = new CustomObjectDetectorOptions.Builder(model)
                    .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                    .enableClassification()
                    .setClassificationConfidenceThreshold(0.7f)
                    .setMaxPerObjectLabelCount(5)
                    .build();

            objectDetector = ObjectDetection.getClient(customObjectDetectorOptions);

        }

        @SuppressLint("SetTextI18n")
        @Override
        public void analyze(@NonNull ImageProxy imageProxy) {
            @SuppressLint("UnsafeExperimentalUsageError") Image image = imageProxy.getImage();
            if(image != null){
                InputImage inputImage = InputImage.fromMediaImage(image, imageProxy.getImageInfo().getRotationDegrees());


                objectDetector.process(inputImage)
                        .addOnFailureListener( e -> Log.e(TAG, "analyze: Failed"+e))
                        .addOnSuccessListener(detectedObjects -> {
                            for(DetectedObject obj : detectedObjects){
                                if(!obj.getLabels().isEmpty() && !mDialog.isShowing()){
                                    Log.d(TAG, "analyze: "+obj.getTrackingId());



                                    output.setText(Integer.toString(obj.getLabels().size()));

                                    mDialog.setContentView(R.layout.pantry_imgrec_dialog);
                                    LinearLayout ll = (LinearLayout)mDialog.findViewById(R.id.btn_layout);
                                    Button option1, option2, option3, option4, option5;

                                    int highest = 0;
                                    List<Food> matches =  new ArrayList<>();
                                    for(int i = 0; i<obj.getLabels().size(); i++) {
                                        String currOption = obj.getLabels().get(i).getText();
                                        int indexOfDash = currOption.indexOf("-");
                                        if (indexOfDash != -1) {
                                            currOption = currOption.substring(0, indexOfDash - 1).replace(",", "");
                                        }
                                        String[] keywords = currOption.split(" ");
                                        for (Food food : foodList
                                        ) {
                                            int iTemp = searchWords(keywords, food.getName());
                                            if (highest < iTemp) {
                                                highest = iTemp;
                                                matches.clear();
                                                matches.add(food);
                                            } else if (highest == iTemp) {
                                                if(!matches.contains(food)){
                                                    matches.add(food);
                                                }
                                            }
                                        }
                                    }
                                    if(highest > 0) {
                                        for (int i = 0; i<matches.size(); i++) {
                                            Button currentButton = new Button(mDialog.getContext());
                                            currentButton = new Button(mDialog.getContext());
                                            currentButton.setTextColor(Color.WHITE);
                                            currentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                                            currentButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                                            currentButton.setText(matches.get(i).getName());
                                            ll.addView(currentButton);
                                            //currentButton = buttons.get(i);
                                            //currentButton.setText(matches.get(i).getName());
                                            int finalI = i;
                                            currentButton.setOnClickListener(new View.OnClickListener() {
                                                public void onClick(View v) {
                                                    EditOperation(matches.get(finalI));
                                                    objectDetector.close();
                                                }
                                            });
                                        }

                                        mDialog.findViewById(R.id.closebtn).setOnClickListener(v -> mDialog.dismiss());
                                        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                        mDialog.show();

                                    }
                                      





                                }


                            }
                        })
                        .addOnCompleteListener(task -> imageProxy.close());

            }

        }


    }

    public void setupDialog() {
        mDialog2 = new Dialog(this);
        mDialog2.setContentView(R.layout.dialog_add_new_item);
        amount = mDialog2.findViewById(R.id.edititem_amount);
        exp = mDialog2.findViewById(R.id.edititem_exp);
        name = mDialog2.findViewById(R.id.item_name);
        submit = mDialog2.findViewById(R.id.submitbtn);
        img = mDialog2.findViewById(R.id.item_image);
        TextView cancel = mDialog2.findViewById(R.id.closebtn);

        cancel.setOnClickListener(n-> mDialog2.onBackPressed());

    }

    private void EditOperation(Food i){
        name.setText(i.getName());
        if(i.getImage() != null && !i.getImage().equals("")){
            Picasso.get()
                    .load(i.getImage())
                    .fit()
                    .centerCrop()
                    .into(img);
        }

        submit.setOnClickListener(v -> {

            Pantry.AddItem(i, exp.getText().toString(), Integer.parseInt(amount.getText().toString()));
            mDialog2.dismiss();
            onBackPressed();
        });

        mDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog2.show();


    }

    public int searchWords(String[] keywords, String lin2) {

        Trie trie = Trie.builder().ignoreCase().addKeywords(keywords).build();
        Collection<Emit> emits = trie.parseText(lin2);

        return emits.size();
    }

    public void fetchFood() {
        FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();
        helper.readFood(new FirebaseFoodDatabase_Helper.Container() {
            @Override
            public void returnData(List<Food> foods) {
                foodList = foods;
            }
        });


    }


}