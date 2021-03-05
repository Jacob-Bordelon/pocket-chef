package com.example.pocket_chef_application;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class PantryItem_Activity extends Activity {
    private TextView title,expdate, amount;
    private ImageView img;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry_item_);

        title = (TextView) findViewById(R.id.pantry_item_activity_title);
        expdate = (TextView) findViewById(R.id.pantry_item_activity_expdate);
        amount = (TextView) findViewById(R.id.pantry_item_activity_amount);
        img = (ImageView) findViewById(R.id.pantry_item_activity_image);

        // get values from card
        Intent intent = getIntent();
        String Name = intent.getExtras().getString("Name");
        String Exp = intent.getExtras().getString("Exp_Date");
        int Amount = intent.getExtras().getInt("Amount");
        int image = intent.getExtras().getInt("Image");

        // set values in display
        title.setText(Name);
        expdate.setText(Exp);
        amount.setText(Integer.toString(Amount));
        img.setImageResource(image);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.6), (int)(height*.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
    }
}