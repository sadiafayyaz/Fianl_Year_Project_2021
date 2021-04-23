package com.example.androidcarmanager.copmute;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidcarmanager.Database.Expence_DB;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

public class Compute_Screen extends AppCompatActivity {
    TextView etOdometer, etDistance,  costKmEt, kmRouteEt, etConsumedFuel, avgKmLtrEt;

    Double odometer=0.0, distance=0.0, avgrkm=0.0, avgkmcost=0.0, consumedFuel=0.0, avgkmltr=0.0,fuelcost =0.0;
    int fillups=0;

    private DatabaseReference databaseReference1,databaseReference2;
    private FirebaseAuth firebaseAuth;
    String key;
    ArrayList<String> keys = new ArrayList<String>();

    ArrayList<Double> costList=new ArrayList<Double>();
    ArrayList<Double> distanceList=new ArrayList<Double>();
    ArrayList<Double> fuellist=new ArrayList<Double>();


    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compute__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Fuel Consumption(Monthly)</font>"));
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Compute_Screen.this, Login_Screen.class));
        }
        progressDialog = ProgressDialog.show(Compute_Screen.this, "", "Please Wait, Loading...", true);

        final FirebaseUser user = firebaseAuth.getCurrentUser();
        key = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key", "-1");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/expenses/" + key);
        databaseReference2 = FirebaseDatabase.getInstance().getReference("users/" + user.getUid());
//      initialize all textviews

        etOdometer = (TextView) findViewById(R.id.odometerreading);
        etDistance = (TextView) findViewById(R.id.distance);
        kmRouteEt = (TextView) findViewById(R.id.route);
        costKmEt = (TextView) findViewById(R.id.fuelkmcost);
        etConsumedFuel = (TextView) findViewById(R.id.fuelconsumption);
        avgKmLtrEt = (TextView) findViewById(R.id.kmliter);
        databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Date date = new Date();
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTimeInMillis(date.getTime());
                    calendar.add(Calendar.MONTH, -1);
                    Long duration = calendar.getTimeInMillis();

                    for (DataSnapshot ds : dataSnapshot.child("fuel").getChildren()) {
//                        Expence_DB expensesDB= ds.getValue(Expence_DB.class);
                        if(ds.child("date").getValue(Long.class) >= duration){
                            costList.add(ds.child("price").getValue(Double.class));
                            distanceList.add(ds.child("odometer").getValue(Double.class));
                            fuellist.add(ds.child("liter").getValue(Double.class));
//                            costList.add(expensesDB.getPrice());
//                            distanceList.add(expensesDB.getOdometer());
//                            fuellist.add(expensesDB.getLiter());
//                        if (ds.child("date").getValue(Long.class) >= duration) {
//                            costList.add(ds.child("price").getValue(Double.class));
//                            distanceList.add(ds.child("odometer").getValue(Double.class));
//                            fuellist.add(ds.child("liter").getValue(Double.class));
                        }
                    }

                    if (dataSnapshot.child("oddometer").exists()) {
                        for (DataSnapshot ds : dataSnapshot.child("oddometer").getChildren()) {
                            if (ds.child("date").getValue(Long.class) >= duration) {
                                distanceList.add(ds.child("liter").getValue(Double.class));
                            }
                        }
                    }

                    if (!costList.isEmpty() && !distanceList.isEmpty() && !fuellist.isEmpty()) {
                        calculateDbValues();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(Compute_Screen.this, "No Records Found for last Month!!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(Compute_Screen.this, "Please add some data Before checking Consumptions.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
        public void calculateDbValues(){
            databaseReference2.child("vehicles").child(key).get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    Collections.sort(distanceList);
                    Double lastVal=0.0, newVal=0.0;;
                    Boolean isFirst=true;
                    for (Double val:distanceList){
                        if(isFirst){
                            isFirst=false;
                            lastVal=val;
                        }
                        newVal=val;
                    }
                    odometer=newVal;
                    distance=-1*(lastVal-newVal);

                    Double fuelVal=0.0;
                    Collections.sort(fuellist);
                    for (Double val:fuellist){
                        fuelVal=fuelVal+val;
                        Log.d("fuellist", String.valueOf(val));
                    }
                    fillups= fuellist.size();
                    consumedFuel=fuelVal;

                    Collections.sort(costList);
                    for (Double val:costList){
                        fuelcost = val+fuelcost;

                    }

                    avgrkm=(distance/distanceList.size());
                    avgkmltr=(distance);
                    avgkmcost=(consumedFuel)/avgkmltr;

                    setValues();
                }
            });

        }

        public void setValues(){

            etOdometer.setText("Meter Reading: "+new DecimalFormat("##.##").format(odometer)+" km");
            etDistance.setText("Total Distance: "+new DecimalFormat("##.##").format(distance)+" km");
            kmRouteEt.setText("Average R/Distance: "+new DecimalFormat("##.##").format(avgrkm)+" km");
            costKmEt.setText("Cost/KM Rs: "+new DecimalFormat("##.##").format(avgkmcost));
            etConsumedFuel.setText("Total Consumption: "+new DecimalFormat("##.##").format(consumedFuel)+" Ltr");
            avgKmLtrEt.setText("Ltr/KM: "+new DecimalFormat("##.##").format(avgkmltr)+" KM");

            progressDialog.dismiss();

    }

    }

