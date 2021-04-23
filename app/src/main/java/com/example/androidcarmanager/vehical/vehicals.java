package com.example.androidcarmanager.vehical;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidcarmanager.Database.Vehical_info_DB;
import com.example.androidcarmanager.Galery_Views.List_Adapter;
import com.example.androidcarmanager.Galery_Views.List_Model;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.example.androidcarmanager.main.MainActivity;

import java.util.ArrayList;
import java.util.Calendar;

public class vehicals extends AppCompatActivity {


    List_Adapter adapter;
    ImageButton btnimage;
    ListView listView;


    ArrayList<String> key=new ArrayList<String>();
    ArrayList<String> title=new ArrayList<String>();
    ArrayList<String> odometerUnit=new ArrayList<String>();
    ArrayList<String> manufacturer=new ArrayList<String>();
    ArrayList<Long> purchaseDate=new ArrayList<Long>();
    ArrayList<String> model=new ArrayList<String>();
    ArrayList<String> milage=new ArrayList<String>();
    ArrayList<String> fuelLimit=new ArrayList<String>();
    ArrayList<String> plateNum=new ArrayList<String>();
    ArrayList<List_Model> listffModel= new ArrayList<List_Model>();

    AlertDialog.Builder builder;
    AlertDialog dialog;


    private DatabaseReference databaseReference,databaseReference1;
    private FirebaseAuth Auth;

    boolean status;
    ProgressDialog progressDialog;
    ChildEventListener childEventListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicals);
        setTitle(Html.fromHtml("<font color='#3477e3'>Vehicals</font>"));


//      check user is loggedin or not

        Auth=FirebaseAuth.getInstance();
        if(Auth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(vehicals.this, Login_Screen.class));
        }


        FirebaseUser user = Auth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/vehicles");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("users/"+user.getUid());
        getSharedPreferences("PREFERENCE", MODE_PRIVATE).edit()
                .putBoolean("isrunningfirst", false).commit();

        btnimage = (ImageButton)findViewById(R.id.fabbutton);
        listView = (ListView)findViewById(R.id.vehicleslist);


//      add new vehicle details
        btnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(vehicals.this, Add_Vehical_screen.class);
                i.putExtra("type", "add");
                startActivity(i);
            }
        });
        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    progressDialog = ProgressDialog.show(vehicals.this, "","Please Wait, Loading...");
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
//                      Map<String,Object> myVal=(HashMap<String,Object>) dataSnapshot.getValue();
                        Vehical_info_DB vehicleDB=ds.getValue(Vehical_info_DB.class);
                        key.add(ds.getKey());
                        title.add(String.valueOf(vehicleDB.getVehicleName()));
                        purchaseDate.add((Long) vehicleDB.getPurchaseDate());
                        odometerUnit.add(String.valueOf(vehicleDB.getModometerReading()));
                        manufacturer.add(String.valueOf(vehicleDB.getManufacturer()));
                        model.add(String.valueOf(vehicleDB.getVehicleModel()));
                        milage.add(String.valueOf(vehicleDB.getMileageRange()));
                        fuelLimit.add(String.valueOf(vehicleDB.getFuelLimit()));
                        plateNum.add(String.valueOf(vehicleDB.getPlateNumber()));
                    }
//
                    if(!title.isEmpty() && !purchaseDate.isEmpty()){
                        for(int i=0;i<title.size();i++){
                            List_Model modelAdapter=new List_Model(title.get(i), purchaseDate.get(i));
                            //bind all strings in an array
                            listffModel.add(modelAdapter);
                        }
                        setListAdapter();
                        progressDialog.dismiss();
                    }else{
                        progressDialog.dismiss();
                        Toast.makeText(vehicals.this, "Failed in Retrieving Data",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(vehicals.this,"Failed to Fetch Data try again",Toast.LENGTH_SHORT);
                Log.d("snapshot:", "snapshot does not exist");
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                builder = new AlertDialog.Builder(vehicals.this);
                builder.setTitle("Choose an option");
                String[] options={"View","Edit", "Select it","Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0:{
                                try {
                                    Calendar c = Calendar.getInstance();
                                    c.setTimeInMillis(purchaseDate.get(position));
                                    c.add(Calendar.MONTH,1);
                                    String dateString= c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.YEAR);
                                    new AlertDialog.Builder(vehicals.this)
                                            .setTitle(title.get(position))
//                                            display message
                                            .setMessage("Date: "+dateString+"\n"
                                                    +"----------------------------------\n\n"
                                                    +"Model: "+model.get(position)+"\n\n"
                                                    +"Mileage: "+milage.get(position)+"km \n\n"
                                                    +"FuelLimit: "+ fuelLimit.get(position)+" Ltr\n\n"
                                                    +"PlateNumber: "+ plateNum.get(position))
                                            .setCancelable(false)
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).show();
                                } catch (Exception e) {
                                    Log.d("Notifications: ", e.getMessage());
                                }
                            }break;
                            case 1:{
                                Intent i = new Intent(vehicals.this, Add_Vehical_screen.class);
                                i.putExtra("type", "edit");
                                i.putExtra("key",key.get(position));
                                i.putExtra("title",title.get(position));
                                i.putExtra("meterReading",odometerUnit.get(position));
                                i.putExtra("manufacturer",manufacturer.get(position));
                                i.putExtra("purchaseDate",purchaseDate.get(position));
                                i.putExtra("model",model.get(position));
                                i.putExtra("milage",milage.get(position));
                                i.putExtra("fuelLimit",fuelLimit.get(position));
                                i.putExtra("plateNum",plateNum.get(position));
                                startActivity(i);
                            }break;
                            case 2:{
//                              set vehicle name
                                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                        .edit()
                                        .putString("vehicle", title.get(position))
                                        .putString("key", key.get(position))
                                        .commit();
//                              start dashboard activity
                                Intent i=new Intent(vehicals.this, MainActivity.class);
                                startActivity(i);
                                finish();
                            }break;
                            case 3:{
                                databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                    ArrayList<String> index=new ArrayList<String>();
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
                                            String key=ds.getKey();
                                            if(!key.isEmpty()){
                                                index.add(key);
                                            }
                                        }
                                        removeItem(index.get(position), position);
                                    }
                                });
                            }
                        }
                    }
                });
                dialog = builder.create();
                dialog.show();
            }
        });
    }

    public void setListAdapter(){
        adapter = new List_Adapter(vehicals.this, listffModel);
        listView.setAdapter(adapter);
    }

    public void removeItem(String dbPosition, int listPosition){
        final String delKey = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key",null);


        final String pos=dbPosition;
        final int listPos=listPosition;

        databaseReference.child(dbPosition)
                .removeValue()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listffModel.remove(listPos);
                        key.remove(listPos);
                        title.remove(listPos);
                        purchaseDate.remove(listPos);
                        odometerUnit.remove(listPos);
                        manufacturer.remove(listPos);
                        model.remove(listPos);
                        milage.remove(listPos);
                        fuelLimit.remove(listPos);
                        plateNum.remove(listPos);
                        adapter.notifyDataSetChanged();

                        if (delKey.equals(pos)){
                            databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
                                @Override
                                public void onSuccess(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.child("expenses").exists()){
                                        databaseReference1.child("expenses").child(pos).removeValue();
                                    }
                                    if(dataSnapshot.child("notes").exists()){
                                        databaseReference1.child("notes").child(pos).removeValue();
                                    }
                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                                            .edit()
                                            .putString("vehicle", "Nothing Selected")
                                            .putString("key", "")
                                            .commit();
                                }
                            });
                        }else{
                            Toast.makeText(vehicals.this, "Failed to remove other Details",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                builder = new AlertDialog.Builder(vehicals.this);
//                builder.setTitle("Choose an option");
//                String[] options={"View","Edit", "Select it","Delete"};
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case 0:{
//                                try {
//                                    Calendar c = Calendar.getInstance();
//                                    c.setTimeInMillis(purchaseDate.get(position));
//                                    c.add(Calendar.MONTH,1);
//                                    String dateString= c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.YEAR);
//                                    new AlertDialog.Builder(vehicals.this)
//                                            .setTitle(title.get(position))
////                                            display message
//                                            .setMessage("Date: "+dateString+"\n"
//                                                    +"----------------------------------\n\n"
//                                                    +"Model: "+model.get(position)+"\n\n"
//                                                    +"Mileage: "+milage.get(position)+"km \n\n"
//                                                    +"FuelLimit: "+ fuelLimit.get(position)+" Ltr\n\n"
//                                                    +"PlateNumber: "+ plateNum.get(position))
//                                            .setCancelable(false)
//                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                }
//                                            }).show();
//                                } catch (Exception e) {
//                                    Log.d("Notifications: ", e.getMessage());
//                                }
//                            }break;
//                            case 1:{
//                                Intent i = new Intent(vehicals.this, Add_Vehical_screen.class);
//                                i.putExtra("type", "edit");
//                                i.putExtra("key",key.get(position));
//                                i.putExtra("title",title.get(position));
//                                i.putExtra("meterReading",odometerUnit.get(position));
//                                i.putExtra("manufacturer",manufacturer.get(position));
//                                i.putExtra("purchaseDate",purchaseDate.get(position));
//                                i.putExtra("model",model.get(position));
//                                i.putExtra("milage",milage.get(position));
//                                i.putExtra("plateNum",plateNum.get(position));
//                                i.putExtra("fuelLimit",fuelLimit.get(position));
//
//                                startActivity(i);
//                            }break;
//                            case 2:{
////                              set vehicle name
//                                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//                                        .edit()
//                                        .putString("vehicle", title.get(position))
//                                        .putString("key", key.get(position))
//                                        .commit();
////                              start dashboard activity
//                                Intent i=new Intent(vehicals.this, MainActivity.class);
//                                startActivity(i);
//                                finish();
//                            }break;
//                            case 3:{
//                                databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                                    ArrayList<String> index=new ArrayList<String>();
//                                    @Override
//                                    public void onSuccess(DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
//                                            String key=ds.getKey();
//                                            if(!key.isEmpty()){
//                                                index.add(key);
//                                            }
//                                        }
//                                        removeItem(index.get(position), position);
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });
//                dialog = builder.create();
//                dialog.show();
//            }
//        });
//    }
//
//    public void setListAdapter(){
//        adapter = new List_Adapter(vehicals.this, listffModel);
//        listView.setAdapter(adapter);
//    }
//
//    public void removeItem(String dbPosition, int listPosition){
//        final String delKey = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//                .getString("key",null);
//
//
//        final String pos=dbPosition;
//        final int listPos=listPosition;
//
//        databaseReference.child(dbPosition)
//                .removeValue()
//                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                    @Override
//                    public void onSuccess(Void aVoid) {
//                        listffModel.remove(listPos);
//                        key.remove(listPos);
//                        title.remove(listPos);
//                        purchaseDate.remove(listPos);
//                        odometerUnit.remove(listPos);
//                        manufacturer.remove(listPos);
//                        model.remove(listPos);
//                        milage.remove(listPos);
//                        fuelLimit.remove(listPos);
//                        plateNum.remove(listPos);
//                        adapter.notifyDataSetChanged();
//
//                        if (delKey.equals(pos)){
//                            databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                                @Override
//                                public void onSuccess(DataSnapshot dataSnapshot) {
//
//                                    if(dataSnapshot.child("expenses").exists()){
//                                        databaseReference1.child("expenses").child(pos).removeValue();
//                                    }
//                                    if(dataSnapshot.child("notes").exists()){
//                                        databaseReference1.child("notes").child(pos).removeValue();
//                                    }
//                                    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//                                            .edit()
//                                            .putString("vehicle", "Nothing Selected")
//                                            .putString("key", "")
//                                            .commit();
//                                }
//                            });
//                        }else{
//                            Toast.makeText(vehicals.this, "Failed to remove other Details",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//    }

//      delete or update item from database
//        childEventListener=new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if(snapshot.exists()){
//                    int index= Integer.parseInt(snapshot.getKey());
//                    Map<String,Object> myVal=(HashMap<String,Object>) snapshot.getValue();
//                    key.add(String.valueOf(index));
//                    title.add(String.valueOf(myVal.get("vehicleName")));
//////                   // getting date from long
////                    Calendar calendar = Calendar.getInstance();
////                    calendar.setTimeInMillis((Long) myVal.get("purchaseDate"));
////                    String dateObj = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);
////                    purchaseDate.add(dateObj);
//                    odometerUnit.add(String.valueOf(myVal.get("modometerReading")));
//                    manufacturer.add(String.valueOf(myVal.get("manufacturer")));
//                    model.add(String.valueOf(myVal.get("vehicleModel")));
//                    milage.add(String.valueOf(myVal.get("mileageRange")));
//                    plateNum.add(String.valueOf(myVal.get("plateNumber")));
//                    fuelLimit.add(String.valueOf(myVal.get("fuelLimit")));
//
//                    if(!title.isEmpty() && !purchaseDate.isEmpty()){
//                        for(int i=0;i<title.size();i++){
//                            Model_Adapter modelAdapter=new Model_Adapter(title.get(i), purchaseDate.get(i));
//                            //bind all strings in an array
//                            listofModel.add(modelAdapter);
//                        }
//                        setListAdapter();
//                    }else{
//                        Toast.makeText(vehicals.this, "Failed in Retrieving Data",Toast.LENGTH_SHORT).show();
//                    }
////                    Log.d("dateobj", purchaseDate.get(0));
//                    progressDialog.dismiss();
//                }else{
//                    progressDialog.dismiss();
//                    Toast.makeText(vehicals.this,"Failed to Fetch Data try again",Toast.LENGTH_SHORT);
//
//                    Log.d("snapshot:", "snapshot does not exist");
//                }
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                if(snapshot.exists()){
//                    int arrayIndex=key.indexOf(snapshot.getKey());
//                    if(arrayIndex != -1 ){
//                        Vehical_info_DB vehicleDB=snapshot.getValue(Vehical_info_DB.class);
//                        title.set(arrayIndex, vehicleDB.getVehicleName());
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.setTimeInMillis(vehicleDB.getPurchaseDate());
//                        String dateObj = calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.MONTH) + "/" + calendar.get(Calendar.YEAR);
//                        purchaseDate.set(arrayIndex, vehicleDB.getPurchaseDate());
//                        odometerUnit.set(arrayIndex,vehicleDB.getModometerReading());
//                        manufacturer.set(arrayIndex,vehicleDB.getManufacturer());
//                        model.set(arrayIndex,vehicleDB.getVehicleModel());
//                        milage.set(arrayIndex,String.valueOf(vehicleDB.getMileageRange()));
//                        fuelLimit.set(arrayIndex,String.valueOf(vehicleDB.getFuelLimit()));
//                        plateNum.set(arrayIndex,vehicleDB.getPlateNumber());
////                          notify the adapter
//                        adapter.notifyDataSetChanged();
//                    }
//                }
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
//                if(snapshot.exists()){
//                    int arrayIndex=key.indexOf(snapshot.getKey());
//                    if(arrayIndex != -1 ) {
//                        int index = Integer.parseInt(snapshot.getKey());
//                        Log.d("snapshotindex", String.valueOf(index));
//                        key.remove(arrayIndex);
//                        title.remove(arrayIndex);
//                        purchaseDate.remove(arrayIndex);
//                        odometerUnit.remove(arrayIndex);
//                        manufacturer.remove(arrayIndex);
//                        model.remove(arrayIndex);
//                        milage.remove(arrayIndex);
//
//                        fuelLimit.remove(arrayIndex);
//                        plateNum.remove(arrayIndex);
////                      notify the adapter
//                        adapter.notifyDataSetChanged();
//                    }
//                }else{
//                    Toast.makeText(vehicals.this,"Failed to Delete!! Please Try again.",Toast.LENGTH_LONG);
//                }
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//
//            }
//        };
////      set childEventListener
//        databaseReference.addChildEventListener(childEventListener);
//
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
//                builder = new AlertDialog.Builder(vehicals.this);
//                builder.setTitle("Choose an option");
//                String[] options={"View","Edit", "Select it","Delete"};
//                builder.setItems(options, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        switch (which){
//                            case 0:{
//                                try {
//                                    new AlertDialog.Builder(vehicals.this)
//                                            .setTitle(title.get(position))
////                                            display message
//                                            .setMessage("Date: "+purchaseDate.get(position)+"\n"
//                                                    +"----------------------------------\n\n"
//                                                    +"Model: "+model.get(position)+"\n\n"
//                                                    +"Mileage: "+milage.get(position)+"km \n\n"
//                                                    +"FuelLimit: "+ fuelLimit.get(position)+" Ltr\n\n"
//                                                    +"PlateNumber: "+ plateNum.get(position))
//                                            .setCancelable(false)
//                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
//                                                public void onClick(DialogInterface dialog, int which) {
//                                                }
//                                            }).show();
//                                } catch (Exception e) {
//                                    Log.d("Notifications: ", e.getMessage());
//                                }
//                            }break;
//                            case 1:{
//                                Intent i = new Intent(vehicals.this, Add_Vehical_screen.class);
//                                i.putExtra("type", "edit");
//                                i.putExtra("key",key.get(position));
//                                i.putExtra("title",title.get(position));
//                                i.putExtra("meterReading",odometerUnit.get(position));
//                                i.putExtra("manufacturer",manufacturer.get(position));
//                                i.putExtra("purchaseDate",purchaseDate.get(position));
//                                i.putExtra("model",model.get(position));
//                                i.putExtra("milage",milage.get(position));
//                                i.putExtra("fuelLimit",fuelLimit.get(position));
//                                i.putExtra("plateNum",plateNum.get(position));
//                                startActivity(i);
//                            }break;
//                            case 2:{
////                              set vehicle name
//                                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//                                        .edit()
//                                        .putString("vehicle", title.get(position))
//                                        .putString("key", key.get(position))
//                                        .commit();
////                              start dashboard activity
//                                Intent i=new Intent(vehicals.this, MainActivity.class);
//                                i.putExtra("vehicle",title.get(position));
////                              it should be unique from firebase
//                                i.putExtra("vehicleId","140");
//                                startActivity(i);
//                                finish();
//                            }break;
//                            case 3:{
//                                databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//                                    ArrayList<String> index=new ArrayList<String>();
//                                    @Override
//                                    public void onSuccess(DataSnapshot dataSnapshot) {
//                                        for(DataSnapshot ds:dataSnapshot.getChildren()){
//                                            String key=ds.getKey();
//                                            if(!key.isEmpty()){
//                                                index.add(key);
//                                            }
//                                        }
//                                        removeItem(Integer.parseInt(index.get(position)));
//                                    }
//                                });
//                            }
//                        }
//                    }
//                });
//                dialog = builder.create();
//                dialog.show();
//            }
//        });
//    }
//
//    public void setListAdapter(){
//        adapter = new Custom_Adapter(vehicals.this, listofModel);
//        listView.setAdapter(adapter);
//    }
//
//    public boolean removeItem(int position){
//        Log.d("position", String.valueOf(position));
//        boolean status;
//        if(position<0){
//            status=false;
//        }else{
//            status=true;
//            databaseReference.child(String.valueOf(position)).removeValue();
//            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
//                    .edit()
//                    .putString("vehicle", "Nothing Selected")
//                    .putString("key", "")
//                    .commit();
//        }
//        return status;
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        databaseReference.removeEventListener(childEventListener);
//    }

}