package com.example.pocket_chef_application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraInfoUnavailableException;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.pocket_chef_application.ObjectDetection.CameraXViewModel;
import com.example.pocket_chef_application.ObjectDetection.GraphicOverlay;
import com.example.pocket_chef_application.ObjectDetection.VisionImageProcessor;
import com.example.pocket_chef_application.ObjectDetection.barcodescanner.BarcodeScannerProcessor;
import com.example.pocket_chef_application.ObjectDetection.labeldetector.LabelDetectorProcessor;
import com.example.pocket_chef_application.ObjectDetection.objectdetector.ObjectDetectorProcessor;
import com.example.pocket_chef_application.ObjectDetection.preference.PreferenceUtils;
import com.example.pocket_chef_application.ObjectDetection.textdetector.TextRecognitionProcessor;
import com.google.mlkit.common.MlKitException;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;

import java.util.ArrayList;
import java.util.List;

public class ImageRecog extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener,
                    CompoundButton.OnCheckedChangeListener
{
    private static final String TAG = "Image Recognition";
    private static final int PERMISSION_REQUESTS =1;

    private static final String TEXT_RECOGNITION = "Text Recognition";
    private static final String OBJECT_DETECTION = "Object Detection";
    private static final String BARCODE_SCANNING = "Barcode Scanning";
    private static final String IMAGE_LABELING = "Image Labeling";
    private static final String OBJECT_DETECTION_CUSTOM = "Custom Object Detection (Bird)";
    private static final String IMAGE_LABELING_CUSTOM = "Custom Image Labeling (Bird)";
    private static final String STATE_SELECTED_MODEL = "selected_model";
    private static final String STATE_LENS_FACING = "lens_facing";

    @Nullable private ProcessCameraProvider cameraProvider;
    @Nullable private Preview previewUseCase;
    @Nullable private ImageAnalysis analysisUseCase;
    @Nullable private VisionImageProcessor imageProcessor;
    private boolean needUpdateGraphicOverlayImageSourceInfo;

    private PreviewView previewView;
    private GraphicOverlay graphicOverlay;

    private String selectedModel = OBJECT_DETECTION;
    private int lensFacing = CameraSelector.LENS_FACING_BACK;
    private CameraSelector cameraSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recog);
        Log.d(TAG, "onCreate");

        // If there is no saved state
        if (savedInstanceState != null) {
            selectedModel = savedInstanceState.getString(STATE_SELECTED_MODEL, OBJECT_DETECTION);
            lensFacing = savedInstanceState.getInt(STATE_LENS_FACING, CameraSelector.LENS_FACING_BACK);
        }

        // create camera selector
        cameraSelector = new CameraSelector.Builder()
                    .requireLensFacing(lensFacing)
                    .build();

        // Set content view
        setContentView(R.layout.activity_image_recog);


        previewView = findViewById(R.id.preview_view);
        if(previewView == null){
            Log.d(TAG, "previewView is null");

        }

        graphicOverlay = findViewById(R.id.graphic_overlay);
        if (graphicOverlay == null) {
            Log.d(TAG, "graphicOverlay is null");
        }

        Spinner options = findViewById(R.id.spinner);
        ArrayAdapter<CharSequence> optionsAdapter = ArrayAdapter.createFromResource(this, R.array.image_recog_types, android.R.layout.simple_spinner_item);
        optionsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        options.setAdapter(optionsAdapter);
        options.setOnItemSelectedListener(this);

        new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
            .get(CameraXViewModel.class)
                .getProcessCameraProvider()
                .observe(this, provider -> {
                    cameraProvider = provider;
                    if(allPermissionsGranted()){
                        bindAllCameraUseCases();
                    }
                });

        if(!allPermissionsGranted()){
            getRuntimePermissions();
        }



    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putString(STATE_SELECTED_MODEL, selectedModel);
        bundle.putInt(STATE_LENS_FACING, lensFacing);
    }

    // Additional methods

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }

        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private void bindAllCameraUseCases() {
        if (cameraProvider != null) {
            // As required by CameraX API, unbinds all use cases before trying to re-bind any of them.
            cameraProvider.unbindAll();
            bindPreviewUseCase();
            bindAnalysisUseCase();
        }
    }

    private void bindPreviewUseCase() {
        if (!PreferenceUtils.isCameraLiveViewportEnabled(this)) {
            return;
        }
        if (cameraProvider == null) {
            return;
        }
        if (previewUseCase != null) {
            cameraProvider.unbind(previewUseCase);
        }

        Preview.Builder builder = new Preview.Builder();
        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution);
        }
        previewUseCase = builder.build();
        previewUseCase.setSurfaceProvider(previewView.getSurfaceProvider());
        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, previewUseCase);
    }

    private void bindAnalysisUseCase() {
        if (cameraProvider == null) {
            return;
        }
        if (analysisUseCase != null) {
            cameraProvider.unbind(analysisUseCase);
        }
        if (imageProcessor != null) {
            imageProcessor.stop();
        }

        try {
            switch (selectedModel) {
                case OBJECT_DETECTION:
                    Log.i(TAG, "Using Object Detector Processor");
                    ObjectDetectorOptions objectDetectorOptions =
                            PreferenceUtils.getObjectDetectorOptionsForLivePreview(this);
                    imageProcessor = new ObjectDetectorProcessor(this, objectDetectorOptions);
                    break;
                case OBJECT_DETECTION_CUSTOM:
                    Log.i(TAG, "Using Custom Object Detector (Bird) Processor");
                    LocalModel localModel =
                            new LocalModel.Builder()
                                    .setAssetFilePath("custom_models/bird_classifier.tflite")
                                    .build();
                    CustomObjectDetectorOptions customObjectDetectorOptions =
                            PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(this, localModel);
                    imageProcessor = new ObjectDetectorProcessor(this, customObjectDetectorOptions);
                    break;
                case TEXT_RECOGNITION:
                    Log.i(TAG, "Using on-device Text recognition Processor");
                    imageProcessor = new TextRecognitionProcessor(this);
                    break;
                case BARCODE_SCANNING:
                    Log.i(TAG, "Using Barcode Detector Processor");
                    imageProcessor = new BarcodeScannerProcessor(this);
                    break;
                case IMAGE_LABELING:
                    Log.i(TAG, "Using Image Label Detector Processor");
                    imageProcessor = new LabelDetectorProcessor(this, ImageLabelerOptions.DEFAULT_OPTIONS);
                    break;
                case IMAGE_LABELING_CUSTOM:
                    Log.i(TAG, "Using Custom Image Label (Bird) Detector Processor");
                    LocalModel localClassifier =
                            new LocalModel.Builder()
                                    .setAssetFilePath("custom_models/bird_classifier.tflite")
                                    .build();
                    CustomImageLabelerOptions customImageLabelerOptions =
                            new CustomImageLabelerOptions.Builder(localClassifier).build();
                    imageProcessor = new LabelDetectorProcessor(this, customImageLabelerOptions);
                    break;
                default:
                    throw new IllegalStateException("Invalid model name");
            }
        } catch (Exception e) {
            Log.e(TAG, "Can not create image processor: " + selectedModel, e);
            Toast.makeText(
                    getApplicationContext(),
                    "Can not create image processor: " + e.getLocalizedMessage(),
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        ImageAnalysis.Builder builder = new ImageAnalysis.Builder();
        Size targetResolution = PreferenceUtils.getCameraXTargetResolution(this, lensFacing);
        if (targetResolution != null) {
            builder.setTargetResolution(targetResolution);
        }
        analysisUseCase = builder.build();

        needUpdateGraphicOverlayImageSourceInfo = true;
        analysisUseCase.setAnalyzer(
                // imageProcessor.processImageProxy will use another thread to run the detection underneath,
                // thus we can just runs the analyzer itself on main thread.
                ContextCompat.getMainExecutor(this),
                imageProxy -> {
                    if (needUpdateGraphicOverlayImageSourceInfo) {
                        boolean isImageFlipped = lensFacing == CameraSelector.LENS_FACING_FRONT;
                        int rotationDegrees = imageProxy.getImageInfo().getRotationDegrees();
                        if (rotationDegrees == 0 || rotationDegrees == 180) {
                            graphicOverlay.setImageSourceInfo(
                                    imageProxy.getWidth(), imageProxy.getHeight(), isImageFlipped);
                        } else {
                            graphicOverlay.setImageSourceInfo(
                                    imageProxy.getHeight(), imageProxy.getWidth(), isImageFlipped);
                        }
                        needUpdateGraphicOverlayImageSourceInfo = false;
                    }
                    try {
                        imageProcessor.processImageProxy(imageProxy, graphicOverlay);
                    } catch (MlKitException e) {
                        Log.e(TAG, "Failed to process image. Error: " + e.getLocalizedMessage());
                        Toast.makeText(getApplicationContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                                .show();
                    }
                });

        cameraProvider.bindToLifecycle(/* lifecycleOwner= */ this, cameraSelector, analysisUseCase);
    }


    @Override
    public void onResume() {
        super.onResume();
        bindAllCameraUseCases();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (imageProcessor != null) {
            imageProcessor.stop();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Log.d(TAG, "Set facing");
        if (cameraProvider == null) {
            return;
        }

        int newLensFacing =
                lensFacing == CameraSelector.LENS_FACING_FRONT
                        ? CameraSelector.LENS_FACING_BACK
                        : CameraSelector.LENS_FACING_FRONT;
        CameraSelector newCameraSelector =
                new CameraSelector.Builder().requireLensFacing(newLensFacing).build();
        try {
            if (cameraProvider.hasCamera(newCameraSelector)) {
                lensFacing = newLensFacing;
                cameraSelector = newCameraSelector;
                bindAllCameraUseCases();
                return;
            }
        } catch (CameraInfoUnavailableException e) {
            // Falls through
        }
        Toast.makeText(
                getApplicationContext(),
                "This device does not have lens with facing: " + newLensFacing,
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Log.i(TAG, "Permission granted!");
        if (allPermissionsGranted()) {
            bindAllCameraUseCases();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private boolean allPermissionsGranted() {
        for(String permission: getRequiredPermissions()){
            if(!isPermissionGranted(this, permission)){
                return false;
            }
        }
        return true;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if(ps != null && ps.length > 0){
                return ps;
            }else{
                return new String[0];
            }

        } catch (Exception e) {
            return new String[0];
        }
    }

    private static boolean isPermissionGranted(Context context, String permission){
        if(ContextCompat.checkSelfPermission(context, permission)== PackageManager.PERMISSION_GRANTED){
            Log.i(TAG, "isPermissionGranted -- True: "+permission);
            return true;
        }
        Log.i(TAG, "isPermissionGranted -- False: "+permission);
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}