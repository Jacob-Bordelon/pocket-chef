package com.example.pocket_chef_application;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.util.TypedValue;
import android.view.OrientationEventListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.example.pocket_chef_application.Firebase.FirebaseFoodDatabase_Helper;
import com.example.pocket_chef_application.Model.Food;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


public class Item_Recognition_Activity extends AppCompatActivity {
    private final int REQUEST_CODE_PERMISSIONS = 10;
    private final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};
    private static final String TAG = "Image_Item_Rec";

    private PreviewView previewView;
    private TextView output;
    private Button scan_button;
    private ExecutorService cameraExecutor;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private FirebaseFoodDatabase_Helper helper = new FirebaseFoodDatabase_Helper();
    private ProcessCameraProvider cameraProvider;
    private List<Food> foodList;
    private HashMap<String,String> namesMap;
    private DatePickerDialog expdate;

    private Dialog mDialog2;
    private EditText amount, exp;
    private TextView submit;
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

            this.model = new LocalModel.Builder()
                    .setAssetFilePath("FoodModel.tflite")
                    .build();


            CustomObjectDetectorOptions customObjectDetectorOptions = new CustomObjectDetectorOptions.Builder(model)
                    .setDetectorMode(CustomObjectDetectorOptions.STREAM_MODE)
                    .enableClassification()
                    .setClassificationConfidenceThreshold(0.7f)
                    .setMaxPerObjectLabelCount(3)
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
                        .addOnSuccessListener(detectedObjects -> {
                            for(DetectedObject obj : detectedObjects){
                                if(!obj.getLabels().isEmpty() && !mDialog.isShowing()){
                                    output.setText(Integer.toString(obj.getLabels().size()));
                                    mDialog.setContentView(R.layout.pantry_imgrec_dialog);
                                    LinearLayout ll = (LinearLayout)mDialog.findViewById(R.id.btn_layout);

                                    int highestOccurrence = 0;
                                    List<String> labels = obj.getLabels().stream().map(p->p.getText().toString()).collect(Collectors.toList());
                                    List<String> matches =  new ArrayList<>();
                                    for(String label:labels){
                                        Log.d(TAG, "analyze: "+label);
                                        int indexOfDash = label.indexOf("-");
                                        if (indexOfDash != -1) {
                                            label = label.substring(0, indexOfDash - 1).replace(",", "");
                                        }

                                        String[] keywords = label.split(" ");
                                        for(String key : namesMap.keySet()){
                                            int occurrences = searchWords(keywords, key);

                                            if (highestOccurrence < occurrences) {
                                                highestOccurrence = occurrences;
                                                matches.clear();
                                                matches.add(key);
                                                Log.d(TAG, "key: "+key+" Occurrences: "+occurrences+" sizeOfList: "+matches.size());
                                            } else if (highestOccurrence == occurrences && highestOccurrence != 0 && !matches.contains(key)) {
                                                matches.add(key);
                                                Log.d(TAG, "ADDED key: "+key+" Occurrences: "+occurrences+" sizeOfList: "+matches.size());
                                            }
                                        }
                                    }

                                    for(String match : matches){
                                        Log.d(TAG, "CREATING button for: "+match+" sizeOfList: "+matches.size());
                                        Button currentButton = new Button(mDialog.getContext());
                                        currentButton.setTextColor(Color.WHITE);
                                        currentButton.setTextSize(TypedValue.COMPLEX_UNIT_SP,18);
                                        currentButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                                        currentButton.setText(match);
                                        ll.addView(currentButton);

                                        currentButton.setOnClickListener(v -> {

                                            helper.getFoodItem(namesMap.get(match), Item_Recognition_Activity.this::EditOperation);

                                            objectDetector.close();
                                        });
                                    }

                                    mDialog.findViewById(R.id.closebtn).setOnClickListener(v -> mDialog.dismiss());
                                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                    mDialog.show();
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
        img = mDialog2.findViewById(R.id.imageView);
        TextView exp_preview = mDialog2.findViewById(R.id.exp_preview);
        TextView exp_label = mDialog2.findViewById(R.id.exp_label);


        TextView cancel = mDialog2.findViewById(R.id.closebtn);
        cancel.setOnClickListener(n-> mDialog2.onBackPressed());
        Calendar mCalender = Calendar.getInstance();
        int year = mCalender.get(Calendar.YEAR);
        int month = mCalender.get(Calendar.MONTH);
        int dayOfMonth = mCalender.get(Calendar.DAY_OF_MONTH);

        exp_label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_edit_calendar_24, 0, 0, 0);

        DatePickerDialog expdate = new DatePickerDialog(Item_Recognition_Activity.this, (view, year1, month1, dayOfMonth1) -> {
            String date = month1+"/"+dayOfMonth1+"/"+year1;
            exp_preview.setText(date);
        }, year, month, dayOfMonth);

        exp_label.setOnClickListener(v -> expdate.show());

    }

    private void EditOperation(Food i){

        mDialog2 = new Dialog(this);
        mDialog2.setContentView(R.layout.dialog_add_new_item);

        amount = mDialog2.findViewById(R.id.edititem_amount);
        exp = mDialog2.findViewById(R.id.edititem_exp);
        name = mDialog2.findViewById(R.id.item_name);
        submit = mDialog2.findViewById(R.id.submitbtn);
        img = mDialog2.findViewById(R.id.imageView);
        TextView exp_preview = mDialog2.findViewById(R.id.exp_preview);
        TextView exp_label = mDialog2.findViewById(R.id.exp_label);


        TextView cancel = mDialog2.findViewById(R.id.closebtn);
        cancel.setOnClickListener(n-> mDialog2.onBackPressed());
        Calendar mCalender = Calendar.getInstance();
        int year = mCalender.get(Calendar.YEAR);
        int month = mCalender.get(Calendar.MONTH);
        int dayOfMonth = mCalender.get(Calendar.DAY_OF_MONTH);

        exp_label.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_edit_calendar_24, 0, 0, 0);

        DatePickerDialog expdate = new DatePickerDialog(Item_Recognition_Activity.this, (view, year1, month1, dayOfMonth1) -> {
            String date = month1+"/"+dayOfMonth1+"/"+year1;
            exp_preview.setText(date);
        }, year, month, dayOfMonth);

        exp_label.setOnClickListener(v -> expdate.show());

        name.setText(i.getName());
        if(i.getImage() != null && !i.getImage().equals("")){
            Picasso.get()
                    .load(i.getImage())
                    .fit()
                    .centerCrop()
                    .into(img);
        }

        submit.setOnClickListener(v -> {
            DatePicker picker = expdate.getDatePicker();
            Pantry.AddItem(i, getDateFromDatePicker(picker), Integer.parseInt(amount.getText().toString()));
            mDialog2.dismiss();
            onBackPressed();
        });

        mDialog2.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog2.show();


    }

    public java.util.Date getDateFromDatePicker(DatePicker datePicker){
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year =  datePicker.getYear();

        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);

        return calendar.getTime();
    }

    public int searchWords(String[] keywords, String lin2) {

        Trie trie = Trie.builder().ignoreCase().addKeywords(keywords).onlyWholeWords().build();
        Collection<Emit> emits = trie.parseText(lin2);

        return emits.size();
    }

    public void fetchFood() {
        helper.readFood(names -> namesMap=names);


    }


}