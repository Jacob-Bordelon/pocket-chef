package com.example.pocket_chef_application.Model;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.pocket_chef_application.Generate_Recipes;
import com.example.pocket_chef_application.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the  factory method to
 * create an instance of this fragment.
 */
public class Camera extends Fragment {
    public static final String EXTRA_INFO = "default";
    private static final int RESULT_OK = 0;
    private static final int RESULT_CANCELED = 1;
    private Button btnCapture;
    private ImageView imgCapture;
    private static final int Image_Capture_Code = 1;

    public static Camera newInstance(String text){
        Camera fragment = new Camera();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        btnCapture =(Button) view.findViewById(R.id.btnTakePicture);
        imgCapture = (ImageView) view.findViewById(R.id.capturedImage);
        btnCapture.setOnClickListener(v -> {
            Intent cInt = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cInt,Image_Capture_Code);
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Image_Capture_Code) {
            if (resultCode == RESULT_OK) {
                Bitmap bp = (Bitmap) data.getExtras().get("data");
                imgCapture.setImageBitmap(bp);
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_LONG).show();
            }
        }
    }}