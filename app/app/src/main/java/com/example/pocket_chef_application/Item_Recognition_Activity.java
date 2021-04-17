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

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.OrientationEventListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;


import java.util.Arrays;
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
                    .setClassificationConfidenceThreshold(0.9f)
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
                        .addOnFailureListener( e -> Log.e(TAG, "analyze: Failed"+e))
                        .addOnSuccessListener(detectedObjects -> {
                            for(DetectedObject obj : detectedObjects){
                                if(!obj.getLabels().isEmpty() && !mDialog.isShowing()){
                                    Log.d(TAG, "analyze: "+obj.getTrackingId());



                                    output.setText(Integer.toString(obj.getLabels().size()));


                                    mDialog.setContentView(R.layout.pantry_imgrec_dialog);
                                    TextView option1, option2, option3;
                                    option1 = mDialog.findViewById(R.id.option1);
                                    option2 = mDialog.findViewById(R.id.option2);
                                    option3 = mDialog.findViewById(R.id.option3);

                                    List<TextView> textViews = Arrays.asList(option1,option2,option3);
                                    for(int i = 0; i<obj.getLabels().size(); i++){
                                        textViews.get(i).setText(obj.getLabels().get(i).getText());
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



}