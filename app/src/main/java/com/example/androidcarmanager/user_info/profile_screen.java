package com.example.androidcarmanager.user_info;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.androidcarmanager.Database.User_information_DB;
import com.example.androidcarmanager.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class profile_screen extends AppCompatActivity {
//    private DatabaseReference databaseReference;
//    private StorageReference storageReference;
//    private FirebaseStorage storage;
//    private FirebaseAuth auth;
//
//    EditText namefirstEt,phoneEt,genderEt,namelastEt;
//    TextView emailTv;
//    Button profileUpdatebtn;
//    ImageView imageofUser;
//
//    private int PICK_IMAGE=786;
//    private int READ_PERMISSION=787;
//    private Uri filePath;
//    private String downloadPath;
//ProgressDialog progressDialog;
private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    EditText namefirstEt,phoneEt,genderEt,namelastEt;
    TextView emailTv;
    Button profilebtn;
    public CircleImageView imageofUser;
    public Uri imguri;
    private StorageReference storageReference;
 private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Profile</font>"));
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null) {
            finish();
            startActivity(new Intent(profile_screen.this,Login_Screen.class));

        }
        databaseReference= FirebaseDatabase.getInstance().getReference("users");
        FirebaseUser user=auth.getCurrentUser();
        storage =FirebaseStorage.getInstance();
        storageReference=storage.getReference();

//        initialize views
        namefirstEt=(EditText)findViewById(R.id.etfirstname);
        namelastEt=(EditText)findViewById(R.id.etlastname);
        phoneEt=(EditText)findViewById(R.id.etphone);
        genderEt=(EditText)findViewById(R.id.etgender);
        emailTv=(TextView)findViewById(R.id.userEmailTv);
        imageofUser =(CircleImageView) findViewById(R.id.userimage);
//        set Email from user object of Firebase
        emailTv.setText(user.getEmail());
        profilebtn=(Button)findViewById(R.id.btnprofile);


        imageofUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooserpic();

            }
        });


        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String firstname=namefirstEt.getText().toString().trim();
                String lastname=namelastEt.getText().toString().trim();
                String phone=phoneEt.getText().toString().trim();
                String gender=genderEt.getText().toString().trim();

                if(TextUtils.isEmpty(firstname)){
                    Toast.makeText(profile_screen.this,"Please Enter Your First Name!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(lastname)){
                    Toast.makeText(profile_screen.this,"Please Enter Your Last Name!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(phone)){
                    Toast.makeText(profile_screen.this,"Please Enter Phone!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(gender)){
                    Toast.makeText(profile_screen.this,"Please Enter Gender!",Toast.LENGTH_SHORT).show();
                }else{
                    User_information_DB userinformationDB =new User_information_DB(firstname,lastname,gender,phone);
                    FirebaseUser user = auth.getCurrentUser();
                    databaseReference.child(user.getUid()).child("profile").setValue(userinformationDB);
                    Toast.makeText(getApplicationContext(),"User information updated", Toast.LENGTH_LONG).show();
                }
            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FirebaseUser user = auth.getCurrentUser();

                if(snapshot.child(user.getUid()).exists()){
                    if(snapshot.child(user.getUid()).child("profile").exists()){
                    User_information_DB userProfile = snapshot.child(user.getUid()).child("profile").getValue(User_information_DB.class);
                    namefirstEt .setText(userProfile.getfirstname());
                    namelastEt .setText(userProfile.getlastname());
                    phoneEt.setText(userProfile.getPhone());
                    genderEt.setText(userProfile.getGender());
                    }
                }else{
                    Log.d("snapshot","not exists");
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("snapshot",error.toString());
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            imguri =data.getData();
            imageofUser.setImageURI(imguri);
            uploadpicture();

        }

    }

    private void uploadpicture() {
        final ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading image");
        progressDialog.show();
        //        Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        final String randomkey = UUID.randomUUID().toString();
        StorageReference riversRef = storageReference.child("images/" + randomkey);

        riversRef.putFile(imguri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Snackbar.make(findViewById(android.R.id.content),"image uploaded", Snackbar.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {

                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),"Failed to Upload",Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        double progreePercent =(100.00 * snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                        progressDialog.setMessage("percentage" + (int) progreePercent + "%");
                    }
                });
    }

    private void chooserpic() {
       {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, 1);

        }


    }


    public void editTextBoxs(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText input = new EditText(this);
        alert.setView(input);

        switch (view.getId()){
            case R.id.etfirstname:{
                alert.setTitle("Enter Name");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        namefirstEt.setText(input.getText());
                    }
                });
            }break;
            case R.id.etlastname:{
                alert.setTitle("Enter Your last Name");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        namelastEt.setText(input.getText());
                    }
                });
            }break;

            case R.id.etphone:{
                alert.setTitle("Enter Phone");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        phoneEt.setText(input.getText());
                    }
                });
            }break;
            case R.id.etgender:{
                alert.setTitle("Enter Gender");
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        genderEt.setText(input.getText());
                    }
                });
            }break;
        }

//      alert.setMessage("Message");
//      Set an EditText view to get user input

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
}