package com.example.androidcarmanager.Expences;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.androidcarmanager.Database.Expence_DB;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

public class Expense_Detail_Screen extends AppCompatActivity {

//
   String selectedExpenses;
    String[] expences={"Maintenance","Fuel","Purchase spare patrs","cleaning","engine tuning"};
    //  data to put
    String title;
    Long date, time;
    Double meterreading, cost,liter;
    ProgressDialog progressDialog;
    EditText ettitle, etdate,ettime, etmeterReading,etprice,etliter;
    Button buttonsave;
    LinearLayout imagesContainer;
    TextView selectImage;
//    ImageView  addImgGallery;
    //    addImgCamera
    private int PICK_IMAGE=796;
    private int CAMERA_REQUEST=799;
    private int READ_PERMISSION=797;
    private int WRITE_PERMISSION=800;
    private int CAMERA_PERMISSION=799;
    private Uri imgPath;
    ArrayList<Uri> imagesPathList=new ArrayList<Uri>();
    ArrayList<String> imagesDbPathList=new ArrayList<String>();

    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    private StorageReference storageReference;
    private FirebaseStorage storage;

    private DatabaseReference databaseReference1, databaseReference2;
    private FirebaseAuth Auth;

    String key;
    int expensesIndex;
    ArrayList<String> keys = new ArrayList<String>();

    int mYear;
    int mMonth;
    int mDay;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense__detail__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Add Expense</font>"));

        Auth = FirebaseAuth.getInstance();
        if (Auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Expense_Detail_Screen.this, Login_Screen.class));
        }
        final FirebaseUser user=Auth.getCurrentUser();
        key= getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key","-1");
//      initializing database
        databaseReference1= FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/vehicles/");
        databaseReference2=FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/expenses/"+key);
//      initializing storage
        storage = FirebaseStorage.getInstance();
        storageReference=storage.getReference("expenses/"+user.getUid());

        ettitle=(EditText) findViewById(R.id.expencetitle);
        etdate=(EditText) findViewById(R.id.expenseDate);
        ettime=(EditText) findViewById(R.id.expenseTime);
        etmeterReading=(EditText) findViewById(R.id.meterreading);
        etprice=(EditText) findViewById(R.id.expenceprice);
        buttonsave=(Button) findViewById(R.id.expencesave);
        selectImage =(TextView)findViewById(R.id.selectimage);
//        addImgCamera=(ImageView) findViewById(R.id.addImgCamera);
//        addImgGallery=(ImageView) findViewById(R.id.addImgGallery);
        imagesContainer= (LinearLayout) findViewById(R.id.imagesContainer);

        Bundle bundle=getIntent().getExtras();
        selectedExpenses=bundle.getString("type");

//        addImgCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (ContextCompat.checkSelfPermission(Expense_Detail_Screen.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(Expense_Detail_Screen.this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
//
//                }else if (ContextCompat.checkSelfPermission(Expense_Detail_Screen.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(Expense_Detail_Screen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
//
//                }else if (ContextCompat.checkSelfPermission(Expense_Detail_Screen.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(Expense_Detail_Screen.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
//
//                }else{
//                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
//                }
//            }
//        });
//
        selectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ActivityCompat.checkSelfPermission(Expense_Detail_Screen.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)) {
                    Intent i=new Intent();
                    i.setType("image/*");
                    i.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(i, "Select an Picture"), PICK_IMAGE);
                } else {
                    ActivityCompat.requestPermissions((Activity) Expense_Detail_Screen.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PERMISSION);
                }
            }
        });


        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

//              setting date picker
                datePickerDialog = new DatePickerDialog(Expense_Detail_Screen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        calendar.set(year, month, dayOfMonth);
                        date = calendar.getTimeInMillis();
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });

        ettime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int mHour=calendar.get(Calendar.HOUR_OF_DAY);
                int mMinute=calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(Expense_Detail_Screen.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        ettime.setText(hourOfDay+" : "+minute);
                        time=calendar.getTimeInMillis();
                    }
                },mHour, mMinute, true);
                timePickerDialog.show();
            }
        });



        databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for(DataSnapshot ds:dataSnapshot.getChildren()){
                    keys.add(ds.getKey());
                }
            }
        });

//      change each toast text
        buttonsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getValuesFromEt();
                if(key.equals("-1")) {
                    Toast.makeText(Expense_Detail_Screen.this, "Please Select a Car Before Adding Expense!", Toast.LENGTH_LONG).show();
                }else if(title==null){
                    Toast.makeText(Expense_Detail_Screen.this,"Enter Expense Title!",Toast.LENGTH_SHORT).show();
                }else if(date==null){
                    Toast.makeText(Expense_Detail_Screen.this,"Enter Date!",Toast.LENGTH_SHORT).show();
                }else if(time==null){
                    Toast.makeText(Expense_Detail_Screen.this,"Enter Time!",Toast.LENGTH_SHORT).show();
                }else if(meterreading==null){
                    Toast.makeText(Expense_Detail_Screen.this,"Enter Meter Reading!",Toast.LENGTH_SHORT).show();
                }else if(cost==null){
                    Toast.makeText(Expense_Detail_Screen.this,"Enter Price!",Toast.LENGTH_SHORT).show();
                }else{
                    int position=keys.indexOf(key);
                    if(position==-1){
                        Toast.makeText(Expense_Detail_Screen.this, "Please Select an Existing Car!", Toast.LENGTH_LONG).show();
                    }else {
                        databaseReference2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.child(selectedExpenses).exists()){
                                    for (DataSnapshot ds : dataSnapshot.child(selectedExpenses).getChildren()) {
                                        expensesIndex = Integer.parseInt(ds.getKey());
//
                                    }
                                    expensesIndex++;
//
                                    addIntoDb(expensesIndex);
                                }else{
//
                                    expensesIndex=0;
                                    addIntoDb(expensesIndex);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK && data != null){
            imgPath=data.getData();
            imagesPathList.add(imgPath);

//            Bitmap photo=(Bitmap) data.getExtras().get("data");
//            ImageView imageView=new ImageView(Expense_Detail_Screen.this);
//            imageView.setLayoutParams(new ViewGroup.LayoutParams(250,350));
//            imageView.setMaxHeight(350);
//            imageView.setMaxWidth(250);
//            imageView.setPadding(10,0,10,0);
//            imageView.setImageBitmap(photo);
//            imagesContainer.addView(imageView);
        }else if(requestCode == PICK_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            imgPath=data.getData();
            imagesPathList.add(imgPath);

            try {
                Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), imgPath);
                ImageView imageView=new ImageView(Expense_Detail_Screen.this);

//              change image size
                imageView.setLayoutParams(new ViewGroup.LayoutParams(250,350));
                imageView.setMaxHeight(350);
                imageView.setMaxWidth(250);
                imageView.setPadding(10,0,10,0);
                imageView.setImageBitmap(bitmap);
                imagesContainer.addView(imageView);
            } catch (IOException e) {
                progressDialog.dismiss();
                Toast.makeText(Expense_Detail_Screen.this, e.toString(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    public void addIntoDb(int in){
        final int index=in;
        Expence_DB expensesDB = new Expence_DB(title, date, time, meterreading, cost);
        progressDialog= ProgressDialog.show(Expense_Detail_Screen.this, "","Adding Expense",true);

        if (imagesPathList.isEmpty()){
//
            databaseReference2.child(selectedExpenses).child(String.valueOf(index)).setValue(expensesDB)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            clearEtValue();
                            progressDialog.dismiss();
                            Toast.makeText(Expense_Detail_Screen.this, "Expense Added!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss();
                    Toast.makeText(Expense_Detail_Screen.this, "Failed to add: "+e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }else{
//
            databaseReference2.child(selectedExpenses).child(String.valueOf(index)).setValue(expensesDB)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            for (int i=0; i < imagesPathList.size(); i++){
                                final int j=i;
                                storageReference
                                        .child(selectedExpenses)
                                        .child(key)
                                        .child(String.valueOf(index))
                                        .child(String.valueOf(i))
                                        .putFile(imagesPathList.get(i))
                                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                            @Override
                                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                storageReference
                                                        .child(selectedExpenses)
                                                        .child(key)
                                                        .child(String.valueOf(index))
                                                        .child(String.valueOf(j))
                                                        .getDownloadUrl()
                                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                            @Override
                                                            public void onSuccess(Uri uri) {
                                                                imagesDbPathList.add(String.valueOf(uri));
                                                                if ( j == imagesPathList.size()-1 ){
                                                                    addImageIntoFirebase(index);
                                                                }
                                                            }
                                                        });
                                            }
                                        });
                            }

                        }
                    });
        }
    }

    public void addImageIntoFirebase(int index){
        for(int i=0; i<imagesDbPathList.size(); i++) {
            String url=imagesDbPathList.get(i);
            databaseReference2.child(selectedExpenses).child(String.valueOf(index))
                    .child("images")
                    .child(String.valueOf(i))
                    .setValue(url)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            progressDialog.dismiss();
                            Toast.makeText(Expense_Detail_Screen.this, "Expense Added!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(Expense_Detail_Screen.this, "Failed"+e.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        imagesContainer.removeAllViews();
        clearEtValue();
    }

    public void clearEtValue(){
        ettitle.setText("");
        etdate.setText("");
        ettime.setText("");
        etmeterReading.setText("");
        etprice.setText("");
    }

    public void getValuesFromEt(){
        title= ettitle.getText().toString().trim();
        meterreading= Double.valueOf(etmeterReading.getText().toString());
        cost= Double.valueOf(etprice.getText().toString().trim());
    }
}
