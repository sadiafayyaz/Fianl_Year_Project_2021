package com.example.androidcarmanager.vehical;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidcarmanager.Database.Vehical_info_DB;
import com.example.androidcarmanager.Expences.Expense_Detail_Screen;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Add_Vehical_screen extends AppCompatActivity {
    String vName, vmanufactur, vModel, plateNo, odoreading;
    Long purchasdate;
    Double mileage, fuellimit;
    EditText vNameEt, odoreadingEt, vmanufecturEt, vModelEt, plateNoEt, purchasedateEt, milageEt, fuelLimitEt;
    Button btnsavevehicl;
    DatePickerDialog datePickerDialog;
    EditText date;


    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    int vehicleId;
    boolean status=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__vehical_screen);
        setTitle(Html.fromHtml("<font color='#3466e3'>Add Vehical</font>"));
//        date=(EditText) findViewById(R.id.purchasedate);
        auth=FirebaseAuth.getInstance();
        if(auth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(Add_Vehical_screen.this, Login_Screen.class));
        }
        final FirebaseUser user=auth.getCurrentUser();
        databaseReference= FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/vehicles");

//        odometerUnitSpin=(Spinner)findViewById(R.id.odometerUnit);
//        odometerUnitSpin.setOnItemSelectedListener(this);

        vNameEt=(EditText)findViewById(R.id.vehicalname);
        vmanufecturEt=(EditText)findViewById(R.id.manufectur);
        vModelEt=(EditText)findViewById(R.id.vmodel);
        plateNoEt=(EditText)findViewById(R.id.plateno);
        purchasedateEt=(EditText)findViewById(R.id.purchasedate);
        milageEt=(EditText)findViewById(R.id.milagerange);
        fuelLimitEt=(EditText)findViewById(R.id.fulelimit);
        odoreadingEt=(EditText)findViewById(R.id.odometerreading);

        btnsavevehicl=(Button)findViewById(R.id.btnsave);
//       // seting date picker
//        date.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                final Calendar calendar=Calendar.getInstance();
//                int mYear=calendar.get(Calendar.YEAR);
//                int mMonth=calendar.get(Calendar.MONTH);
//                int mDay=calendar.get(Calendar.DAY_OF_MONTH);
//
////                setting date picker
//                datePickerDialog = new DatePickerDialog(Add_Vehical_screen.this, new DatePickerDialog.OnDateSetListener() {
//                    @Override
//                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        date.setText(dayOfMonth+"/"+(month+1)+"/"+year);
//                    }
//                }, mYear,mMonth,mDay);
//                datePickerDialog.show();
//            }
//        });



//      check if user want to edit
        Bundle bundle=getIntent().getExtras();
        final String type=bundle.getString("type");
        if(type.equals("edit")){
            setTitle(Html.fromHtml("<font color='#3477e3'>Update Details</font>"));
            String key=bundle.getString("key");
            String title=bundle.getString("title");
            String meterReading=bundle.getString("meterReading");
            String model=bundle.getString("model");
            String manufacturer=bundle.getString("manufacturer");
            String purchaseDate=bundle.getString("purchaseDate");
            String milage=bundle.getString("milage");
            String plateNum=bundle.getString("plateNum");
            String fuelLimit=bundle.getString("fuelLimit");


            vNameEt.setText(title);
            odoreadingEt.setText(meterReading);
            vmanufecturEt.setText(manufacturer);
            vModelEt.setText(model);
            plateNoEt.setText(plateNum);
            purchasedateEt.setText(purchaseDate);
            milageEt.setText(milage);
            fuelLimitEt.setText(fuelLimit);

        }else if(type.equals("add")){
        }else if(type.equals("add")){
            setTitle(Html.fromHtml("<font color='#3477e3'>Add Vehical</font>"));
        }
        purchasedateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar=Calendar.getInstance();
                int mYear=calendar.get(Calendar.YEAR);
                int mMonth=calendar.get(Calendar.MONTH);
                int mDay=calendar.get(Calendar.DAY_OF_MONTH);

//                setting date picker
                datePickerDialog = new DatePickerDialog(Add_Vehical_screen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        purchasedateEt.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                        purchasdate=calendar.getTimeInMillis();
                    }
                }, mYear,mMonth,mDay);
                datePickerDialog.show();
            }
        });

        btnsavevehicl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEDITTEXTValues(type);

                if(TextUtils.isEmpty(vName)){
                    Toast.makeText(Add_Vehical_screen.this,"Enter Vehicle Name!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(vmanufactur)){
                    Toast.makeText(Add_Vehical_screen.this,"Enter Manufacturer Name!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(vModel)){
                    Toast.makeText(Add_Vehical_screen.this,"Enter Vehicle Model!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(plateNo)){
                    Toast.makeText(Add_Vehical_screen.this,"Enter License Plate Number!",Toast.LENGTH_SHORT).show();
                }else if(mileage==null){
                    Toast.makeText(Add_Vehical_screen.this,"Enter Mileage Range!",Toast.LENGTH_SHORT).show();
                }else if(fuellimit==null){
                    Toast.makeText(Add_Vehical_screen.this,"Enter Fuel Limit Name!",Toast.LENGTH_SHORT).show();
                }else if(TextUtils.isEmpty(odoreading)){
                    Toast.makeText(Add_Vehical_screen.this,"Enter Odometer Reading!",Toast.LENGTH_SHORT).show();
                }else if(purchasdate==null){
                    Toast.makeText(Add_Vehical_screen.this,"Enter Purchase Date!",Toast.LENGTH_SHORT).show();
                }else{
                    addvehicaldata(type);
                }
            }
        });}
        public void addvehicaldata(String type){
            final Vehical_info_DB vehicleDB =new Vehical_info_DB(vName, odoreading, vmanufactur, vModel,
                    mileage, fuellimit, plateNo, purchasdate);

            if(type.equals("add")){
                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for (DataSnapshot ds : snapshot.getChildren()) {vehicleId = Integer.parseInt(ds.getKey());}
                            vehicleId++;
                        }else{
                            vehicleId=0;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Add_Vehical_screen.this, error.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
                databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.exists()) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                vehicleId = Integer.parseInt(ds.getKey());
                            }
                            vehicleId++;
                            databaseReference.child(String.valueOf(vehicleId)).setValue(vehicleDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    emptyValues();
                                    Toast.makeText(getApplicationContext(),"Vehicle information Added", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            vehicleId=0;
                            databaseReference.child(String.valueOf(vehicleId)).setValue(vehicleDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    emptyValues();
                                    Toast.makeText(getApplicationContext(),"Vehicle information Added", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Add_Vehical_screen.this, e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else if(type.equals("edit")){
                Bundle bundle=getIntent().getExtras();
                String key=bundle.getString("key");
                databaseReference.child(key).setValue(vehicleDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        emptyValues();
                        Toast.makeText(getApplicationContext(),"Vehicle information Updated", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
//                databaseReference.child(String.valueOf(vehicleId)).setValue(vehicleDB);
//                Toast.makeText(getApplicationContext(),"Vehicle information Added", Toast.LENGTH_LONG).show();
//            }else if(type.equals("edit")){
//                Bundle bundle=getIntent().getExtras();
//                String key=bundle.getString("key");
//                databaseReference.child(key).setValue(vehicleDB);
//                Toast.makeText(getApplicationContext(),"Vehicle information Updated", Toast.LENGTH_LONG).show();
//            }
//
//            emptyValues();
//        }

        public void emptyValues(){
            vNameEt.setText("");
            vmanufecturEt.setText("");
            vModelEt.setText("");
            plateNoEt.setText("");
            purchasedateEt.setText("");
            milageEt.setText("");
            fuelLimitEt.setText("");
            odoreadingEt.setText("");
        }

        public void getEDITTEXTValues(String type){
            vName=vNameEt.getText().toString().trim();
            vmanufactur=vmanufecturEt.getText().toString().trim();
            vModel=vModelEt.getText().toString().trim();
            plateNo=plateNoEt.getText().toString().trim();
            mileage=Double.parseDouble(milageEt.getText().toString());
            fuellimit=Double.parseDouble(fuelLimitEt.getText().toString());
            odoreading=odoreadingEt.getText().toString().trim();
    }
}

