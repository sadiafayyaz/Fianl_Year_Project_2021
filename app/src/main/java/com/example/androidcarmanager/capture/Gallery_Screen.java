package com.example.androidcarmanager.capture;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;

import com.example.androidcarmanager.Database.Gallery_DB;
import com.example.androidcarmanager.Galery_Views.Gallery_Adatpter;
import com.example.androidcarmanager.Galery_Views.Gallery_Model;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Gallery_Screen extends AppCompatActivity {
    GridView gridview;
    ArrayList<String> urls = new ArrayList<String>();

    Gallery_Model modelForGallery;
    ArrayList<Gallery_Model> modelList=new ArrayList<Gallery_Model>();
    Gallery_Adatpter adapterForGallery;
    ImageButton addImage;

    private DatabaseReference databaseReference1;
    private FirebaseAuth firebaseAuth;
    private int READ_PERMISSION=787;
    private int CAMERA_PERMISSION=789;
    private int WRITE_PERMISSION=790;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Gallery</font>"));
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Gallery_Screen.this, Login_Screen.class));
        }
        final FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference1= FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/gallery");

        gridview=(GridView) findViewById(R.id.gridview);
        addImage=(ImageButton) findViewById(R.id.fabbutton);

        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Gallery_Screen.this, Capture_image_Screen.class);

//              ask for permission if haven't
                if (ContextCompat.checkSelfPermission(Gallery_Screen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Gallery_Screen.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);

                }else
                if (ContextCompat.checkSelfPermission(Gallery_Screen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Gallery_Screen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
                }else{
                    if (ContextCompat.checkSelfPermission(Gallery_Screen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Gallery_Screen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
                    }else{
                        startActivity(i);
                    }
                }
            }
        });
        databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {

                String key;
                for(DataSnapshot ds:dataSnapshot.getChildren()){
//                    Map<String,Object> myVal=(HashMap<String,Object>) ds.getValue();
//                    urls.add(String.valueOf(myVal.get(ds.getKey())));
                    Gallery_DB myGallery=ds.getValue(Gallery_DB.class);
                    Log.d("myGallery", myGallery.getUrl());
                    urls.add(myGallery.getUrl());
                }
                setArrayValue();
            }
        });


    }


    public void setArrayValue(){
        for (int i=0; i<urls.size();i++){
            modelForGallery=new Gallery_Model (urls.get(i));
            modelList.add(modelForGallery);
        }
        adapterForGallery=new Gallery_Adatpter(Gallery_Screen.this, modelList);
        gridview.setAdapter(adapterForGallery);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i=new Intent(Gallery_Screen.this, Image_View_Screen.class);
                i.putExtra ( "imageurl", urls.get(position) );
                startActivity(i);
            }
        });
    }
    }