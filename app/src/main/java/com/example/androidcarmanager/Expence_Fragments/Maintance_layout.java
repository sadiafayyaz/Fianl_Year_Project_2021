package com.example.androidcarmanager.Expence_Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.androidcarmanager.Database.Expence_DB;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Maintance_layout extends Fragment {

    BarChart barChart;
    BarDataSet barDataSet1, barDataSet2, barDataSet3, barDataSet4, barDataSet5, barDataSet6, barDataSet7, barDataSet8, barDataSet9, barDataSet10, barDataSet11, barDataSet12;
    BarData barData;
    ArrayList<BarEntry> barEntries1, barEntries2, barEntries3, barEntries4, barEntries5, barEntries6, barEntries7,
            barEntries8, barEntries9, barEntries10, barEntries11, barEntries12;

    //  graph values from firebase
//    ArrayList<Double> Cleaning = new ArrayList<Double>();
//    ArrayList<Long> cleaningDate = new ArrayList<Long>();
    //    ArrayList<Double> fuel = new ArrayList<Double>();
//    ArrayList<Long> fuelDate = new ArrayList<Long>();
    ArrayList<Double> maintenance = new ArrayList<Double>();
    ArrayList<Long> maintenanceDate = new ArrayList<Long>();
//    ArrayList<Double> purchase = new ArrayList<Double>();
//    ArrayList<Long> purchaseDate = new ArrayList<Long>();
//    ArrayList<Double> enginetuning = new ArrayList<Double>();
//    ArrayList<Long> enginetuningdate = new ArrayList<Long>();
    Long firstDate, secondDate;
    String vehicleId;
    Double cost1 = 0.01, cost2 = 0.01, cost3 = 0.01, cost4 = 0.01, cost5 = 0.01, cost6 = 0.01,
            cost7 = 0.01, cost8 = 0.01, cost9 = 0.01, cost10 = 0.01, cost11 = 0.01, cost12 = 0.01;


    private DatabaseReference databaseReference1;
    private Query databaseReference2;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;


    ProgressDialog progressDialog;
    public Maintance_layout() {
        // Required empty public constructor
    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maintance_layout, container, false);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        if (firebaseAuth.getCurrentUser() == null) {
            startActivity(new Intent(getContext(), Login_Screen.class));
        }
        vehicleId = getActivity().getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key", "-1");


//View view1= inflater.inflate(R.layout.fragment_cleaning_layout, container);
        barChart = view.findViewById(R.id.maintancechart);
        barChart.getDescription().setText("Maintancr");


//        barChart.getXAxis().setEnabled(true);
        YAxis rightYAxis = barChart.getAxisRight();
//        rightYAxis.setEnabled(false);
        XAxis topYAxis = barChart.getXAxis();
//        topYAxis.setEnabled(false);
        barChart.animateXY(2000, 2000);

        barEntries1 = new ArrayList<BarEntry>();
        barEntries2 = new ArrayList<BarEntry>();
        barEntries3 = new ArrayList<BarEntry>();
        barEntries4 = new ArrayList<BarEntry>();
        barEntries5 = new ArrayList<BarEntry>();
        barEntries6 = new ArrayList<BarEntry>();
        barEntries7 = new ArrayList<BarEntry>();
        barEntries8 = new ArrayList<BarEntry>();
        barEntries9 = new ArrayList<BarEntry>();
        barEntries10 = new ArrayList<BarEntry>();
        barEntries11 = new ArrayList<BarEntry>();
        barEntries12 = new ArrayList<BarEntry>();

        barEntries1.add(new BarEntry(1, cost1.floatValue()));
        barEntries2.add(new BarEntry(2f, cost2.floatValue()));
        barEntries3.add(new BarEntry(3, cost3.floatValue()));
        barEntries4.add(new BarEntry(4f, cost4.floatValue()));
        barEntries5.add(new BarEntry(5, cost5.floatValue()));
        barEntries6.add(new BarEntry(6f, cost6.floatValue()));
        barEntries7.add(new BarEntry(7, cost7.floatValue()));
        barEntries8.add(new BarEntry(8f, cost8.floatValue()));
        barEntries9.add(new BarEntry(9, cost9.floatValue()));
        barEntries10.add(new BarEntry(10f, cost10.floatValue()));
        barEntries11.add(new BarEntry(11, cost11.floatValue()));
        barEntries12.add(new BarEntry(12f, cost12.floatValue()));
        initializeBarGraph(false);
//        initializeBarGraph(true);

//        progressDialog = ProgressDialog.show(Cleaning_layout.this, "", "Please Wait, Loading...", true);
        getDataFromFirebase();


        return view;
    }
    public void initializeBarGraph(Boolean value) {

        // setting labels
        if (value) {
            barDataSet1.removeFirst();
            barDataSet2.removeFirst();
            barDataSet3.removeFirst();
            barDataSet4.removeFirst();
            barDataSet5.removeFirst();
            barDataSet6.removeFirst();
            barDataSet7.removeFirst();
            barDataSet8.removeFirst();
            barDataSet9.removeFirst();
            barDataSet10.removeFirst();
            barDataSet11.removeFirst();
            barDataSet12.removeFirst();

            barChart.notifyDataSetChanged();
            barChart.invalidate();
        }else {
            barDataSet1 = new BarDataSet(barEntries1, "");
            barDataSet2 = new BarDataSet(barEntries2, "");
            barDataSet3 = new BarDataSet(barEntries3, "");
            barDataSet4 = new BarDataSet(barEntries4, "");
            barDataSet5 = new BarDataSet(barEntries5, "");
            barDataSet6 = new BarDataSet(barEntries6, "");
            barDataSet7 = new BarDataSet(barEntries7, "");
            barDataSet8 = new BarDataSet(barEntries8, "");
            barDataSet9 = new BarDataSet(barEntries9, "");
            barDataSet10 = new BarDataSet(barEntries10, "");
            barDataSet11 = new BarDataSet(barEntries11, "");
            barDataSet12 = new BarDataSet(barEntries12, "");

//      setting colours
            barDataSet1.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSet2.setColors(R.color.bar2);
            barDataSet3.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet4.setColors(ColorTemplate.LIBERTY_COLORS);
            barDataSet5.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet6.setColors(ColorTemplate.PASTEL_COLORS);
            barDataSet7.setColors(ColorTemplate.COLORFUL_COLORS);
            barDataSet8.setColors(R.color.bar2);
            barDataSet9.setColors(ColorTemplate.JOYFUL_COLORS);
            barDataSet10.setColors(ColorTemplate.LIBERTY_COLORS);
            barDataSet11.setColors(ColorTemplate.VORDIPLOM_COLORS);
            barDataSet12.setColors(ColorTemplate.PASTEL_COLORS);

//      setting text colour
            barDataSet1.setValueTextColor(Color.BLUE);
            barDataSet2.setValueTextColor(Color.BLUE);
            barDataSet3.setValueTextColor(Color.BLUE);
            barDataSet4.setValueTextColor(Color.BLUE);
            barDataSet5.setValueTextColor(Color.BLUE);
            barDataSet6.setValueTextColor(Color.BLUE);
            barDataSet7.setValueTextColor(Color.BLUE);
            barDataSet8.setValueTextColor(Color.BLUE);
            barDataSet9.setValueTextColor(Color.BLUE);
            barDataSet10.setValueTextColor(Color.BLUE);
            barDataSet11.setValueTextColor(Color.BLUE);
            barDataSet12.setValueTextColor(Color.BLUE);

//      setting value textsize
            barDataSet1.setValueTextSize(8);
            barDataSet2.setValueTextSize(8);
            barDataSet3.setValueTextSize(8);
            barDataSet4.setValueTextSize(8);
            barDataSet5.setValueTextSize(8);
            barDataSet6.setValueTextSize(8);
            barDataSet7.setValueTextSize(8);
            barDataSet8.setValueTextSize(8);
            barDataSet9.setValueTextSize(8);
            barDataSet10.setValueTextSize(8);
            barDataSet11.setValueTextSize(8);
            barDataSet12.setValueTextSize(8);

            barData = new BarData(barDataSet1, barDataSet2, barDataSet3, barDataSet4, barDataSet5, barDataSet6, barDataSet7, barDataSet8, barDataSet9, barDataSet10, barDataSet11, barDataSet12);
            float groupSpace = 0.04f;
            float barSpace = 0.4f; // x2 dataset
            float barWidth = 0.5f; // x2 dataset
            barData.setBarWidth(barWidth);
            barChart.setData(barData);
            barChart.groupBars(0.45f, groupSpace, barSpace);

//                barChart.setFitBars(true);
            barChart.notifyDataSetChanged();
//                barChart.invalidate();
        }
    }




    public void getDataFromFirebase() {
        databaseReference1 = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/expenses/" + vehicleId);

        databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    Log.d("databaseReference1", "Called successfully");
                    if (dataSnapshot.child("maintance").exists()) {
                        databaseReference2 = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/expenses/" + vehicleId + "/maintance");
                        databaseReference2.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.exists()){
                                    Log.d("databaseReference2", "Called successfully");
                                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                        maintenance.add(ds.child("price").getValue(Double.class));
                                        maintenanceDate.add(ds.child("date").getValue(Long.class));
                                    }
                                    setCostValues();
                                }else{
                                    Log.d("databaseReference2  m", "Does not exist");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("databaseReference2  m", "Failed to get value"+e.toString());
                            }
                        });
                    } else {
                        Log.d("databaseReference1  m", "Does not exist");
                    }
                }
            }
        });

    }

    public Double getData(int val){
        Double cost=0.0;

        final Date currentDate = new Date();
        Calendar calendar1 = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();

        calendar1.setTime(currentDate);
        if(val==0){
            calendar1.add(Calendar.MONTH, 0);
        }else {
            calendar1.add(Calendar.MONTH, -val);
        }
        firstDate = calendar1.getTimeInMillis();

        calendar2.setTime(currentDate);
        if(val==0){
            calendar2.add(Calendar.MONTH, -1);
        }else{
            calendar2.add(Calendar.MONTH, -(val+1));
        }
        secondDate = calendar2.getTimeInMillis();

        if(!maintenance.isEmpty() && !maintenanceDate.isEmpty()){
            for (int i=0; i < maintenance.size(); i++){
                if( (maintenanceDate.get(i) <= firstDate) && (maintenanceDate.get(i) >= secondDate)){
                    cost=cost+maintenance.get(i);
                }
            }
        }else{
            cost=0.1;
            Log.d("cost:2 "+val,cost.toString());
        }
        return cost;
    }

    public void setCostValues(){

        cost1=getData(0);
        barEntries1.add(new BarEntry(1, cost1.floatValue()));
        Log.d("costt: ", cost1.toString());

        cost2=getData(1);
        barEntries2.add(new BarEntry(2, cost2.floatValue()));
        Log.d("costt: ", cost2.toString());

        cost3=getData(2);
        barEntries3.add(new BarEntry(3, cost3.floatValue()));
        Log.d("costt: ", cost3.toString());

        cost4=getData(3);
        barEntries4.add(new BarEntry(4, cost4.floatValue()));
        Log.d("costt: ", cost4.toString());

        cost5=getData(4);
        barEntries5.add(new BarEntry(5, cost5.floatValue()));
        Log.d("costt: ", cost5.toString());

        cost6=getData(5);
        barEntries6.add(new BarEntry(6, cost6.floatValue()));
        Log.d("costt: ", cost6.toString());

        cost7=getData(6);
        barEntries7.add(new BarEntry(7, cost7.floatValue()));
        Log.d("costt: ", cost7.toString());

        cost8=getData(7);
        barEntries8.add(new BarEntry(8, cost8.floatValue()));
        Log.d("costt: ", cost8.toString());

        cost9=getData(8);
        barEntries9.add(new BarEntry(9, cost9.floatValue()));
        Log.d("costt: ", cost9.toString());

        cost10=getData(9);
        barEntries10.add(new BarEntry(10, cost10.floatValue()));
        Log.d("costt: ", cost10.toString());

        cost11=getData(10);
        barEntries11.add(new BarEntry(11, cost11.floatValue()));
        Log.d("costt: ", cost11.toString());

        cost12=getData(11);
        barEntries12.add(new BarEntry(12, cost12.floatValue()));
        Log.d("costt: ", cost12.toString());

        setBarChartValues();
    }

    private void setBarChartValues() {
        initializeBarGraph(true);
        initializeBarGraph(false);
    }
}
