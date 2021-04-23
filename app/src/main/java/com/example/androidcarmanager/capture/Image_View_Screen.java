package com.example.androidcarmanager.capture;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.androidcarmanager.R;

public class Image_View_Screen extends AppCompatActivity {
    ImageView myimageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image__view__screen);

        myimageView =(ImageView) findViewById(R.id.imageview);

        Bundle intent=getIntent().getExtras();
        String url=intent.getString("imageurl");
        Glide.with(Image_View_Screen.this).load(url).into(myimageView);
    }
}
