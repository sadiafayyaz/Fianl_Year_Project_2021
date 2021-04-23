package com.example.androidcarmanager.capture;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.androidcarmanager.Database.Gallery_DB;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

public class Capture_image_Screen extends AppCompatActivity {
    ImageView imageView;
    Button imageCaptureBtn, imageUploadIBtn;

    private static final int CAMERA_REQUEST = 0;

    private Uri filePath;
    String downloadLink;
    String key;
    int expensesIndex;
    ArrayList<Uri> pathList = new ArrayList<Uri>();
    private DatabaseReference databaseReference1;
    private FirebaseAuth Auth;
    private StorageReference storageReference;
    private FirebaseStorage storage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_image__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Upload Image</font>"));

        Auth = FirebaseAuth.getInstance();
        if (Auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Capture_image_Screen.this, Login_Screen.class));
        }
        final FirebaseUser user=Auth.getCurrentUser();
        key= getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key","-1");
        databaseReference1= FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/gallery/");

        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference("gallery/"+user.getUid());


        imageView=(ImageView)findViewById(R.id.imgView);
        imageCaptureBtn=(Button) findViewById(R.id.reclickimage);
        imageUploadIBtn=(Button) findViewById(R.id.uploadbutton);

        imageCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    ActivityCompat.requestPermissions(Capture_image_Screen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 790);
                    Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera, CAMERA_REQUEST);
            }
        });

        imageUploadIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(key.equals("-1")) {
                    Toast.makeText(Capture_image_Screen.this, "Please Select a Car Before Adding Expense!", Toast.LENGTH_LONG).show();
                }else{
                    databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists()){
                                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                    expensesIndex = Integer.parseInt(ds.getKey());
                                    Log.d("expensesIndex1", String.valueOf(expensesIndex));
                                }
                                expensesIndex++;
                                Log.d("expensesIndex2", String.valueOf(expensesIndex));
                                addIntoFirebase(expensesIndex);
                            }else{
                                Log.d("expensesIndex3", String.valueOf(expensesIndex));
                                expensesIndex=0;
                                addIntoFirebase(expensesIndex);
                            }
                        }
                    });

                }
            }
        });

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_REQUEST);
    }

    public void addIntoFirebase(int i){
        final int index=i;
        if(pathList.get(0) != null) {
            storageReference
                    .child(String.valueOf(index))
                    .putFile(pathList.get(0))
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageReference
                                    .child(String.valueOf(index))
                                    .getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            downloadLink = String.valueOf(uri);
                                            uploadIntoDb(downloadLink, index);
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Capture_image_Screen.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }else{
            Toast.makeText(Capture_image_Screen.this, "File Path not set!!", Toast.LENGTH_LONG).show();
        }
    }

    public void uploadIntoDb(String url, int i){
        Gallery_DB myGallery=new Gallery_DB(url);
        databaseReference1.child(String.valueOf(i)).setValue(myGallery).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(Capture_image_Screen.this, "Image Uploaded", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Capture_image_Screen.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null){
            filePath= data.getData();
            pathList.add(filePath);
            Bitmap photo=(Bitmap)data.getExtras().get("data");
            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(photo);
        }else{
            Toast.makeText(Capture_image_Screen.this, "Please retake picture", Toast.LENGTH_LONG).show();
        }
    }
}
