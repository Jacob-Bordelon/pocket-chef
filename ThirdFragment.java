package com.example.pocket_chef_application;

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

import com.example.pocket_chef_application.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ThirdFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThirdFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    public class Camera extends Fragment {
        public static final String EXTRA_INFO = "default";
        private static final int RESULT_OK = 0;
        private static final int RESULT_CANCELED = 1;
        private Button btnCapture;
        private ImageView imgCapture;
        private static final int Image_Capture_Code = 1;

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
        }



    public ThirdFragment() {
        // Required empty public constructor
        PantryDatabase.AppDatabase db = Room.databaseBuilder(getActivity(),
                PantryDatabase.AppDatabase.class, "PantryDatabase").build();

        pantryDao PantryDao = db.pantryDao();
        List<Pantry> items = PantryDao.getAll();
        System.out.println("pantry items" + items.toString());
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThirdFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThirdFragment newInstance(String param1, String param2) {
        ThirdFragment fragment = new ThirdFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_third, container, false);
    }
}