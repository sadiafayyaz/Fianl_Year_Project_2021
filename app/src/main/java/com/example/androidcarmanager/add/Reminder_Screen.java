package com.example.androidcarmanager.add;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

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

import com.example.androidcarmanager.Database.Reminder_DB;
import com.example.androidcarmanager.Galery_Views.List_Adapter;
import com.example.androidcarmanager.Galery_Views.List_Model;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;

public class Reminder_Screen extends AppCompatActivity {
    ImageButton imageButtonReminder;
    ListView listView;
    List_Adapter adapterReminder;

    ArrayList<String> title= new ArrayList<String>();
    ArrayList<Long> date= new ArrayList<Long>();
    ArrayList<Long> time= new ArrayList<Long>();
    ArrayList<String> description= new ArrayList<String>();
    ArrayList<List_Model> list=new ArrayList<List_Model>();

    private DatabaseReference databaseReference1;
    private FirebaseAuth firebaseAuth;

    String key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Reminders</font>"));
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Reminder_Screen.this, Login_Screen.class));
        }
        final FirebaseUser user=firebaseAuth.getCurrentUser();
        key= getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key","-1");
        databaseReference1 = FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/reminders/");


        imageButtonReminder=(ImageButton)findViewById(R.id.fabbutton);
        listView=(ListView)findViewById(R.id.remindlist);
        imageButtonReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Reminder_Screen.this, Add_Reminder_Screen.class);
                i.putExtra("type", "add");
                startActivity(i);
            }
        });

        databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds: dataSnapshot.getChildren()){
                        Reminder_DB remindersDB = ds.getValue(Reminder_DB.class);
                        title.add(remindersDB.getTitle());
                        date.add(remindersDB.getDate());
                        time.add(remindersDB.getTime());
                        description.add(remindersDB.getDescription());
                    }
                    if(!title.isEmpty() && !date.isEmpty()){
                        for (int i=0; i < title.size(); i++){
                            List_Model modelForList=new List_Model(title.get(i), date.get(i));
                            list.add(modelForList);
                        }

                        adapterReminder = new List_Adapter(Reminder_Screen.this, list);
                        listView.setAdapter(adapterReminder);
                    }else{
                        Toast.makeText(Reminder_Screen.this, "Failed to fetch data, try again", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(Reminder_Screen.this, "You havent added any Reminder yet.",Toast.LENGTH_SHORT).show();
                }
            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                try {
                    Calendar cDate = Calendar.getInstance();
                    Calendar cTime = Calendar.getInstance();
                    cDate.setTimeInMillis(date.get(position));
                    cTime.setTimeInMillis(time.get(position));
                    cDate.add(Calendar.MONTH, 1);
                    String dateString = cDate.get(Calendar.DAY_OF_MONTH) + "/" + cDate.get(Calendar.MONTH) + "/" + cDate.get(Calendar.YEAR);
                    String timeString = cTime.get(Calendar.HOUR_OF_DAY) + " : " + cTime.get(Calendar.MINUTE) + " : " + cTime.get(Calendar.SECOND)  ;
                    new AlertDialog.Builder(Reminder_Screen.this)
                            .setTitle("Reminder Details")
//                           display message
                            .setMessage("----------------------------------\n\n"
                                    + "Title: " + title.get(position) + "\n\n"
                                    + "Date: " + dateString + "\n\n"
                                    + "Time: " + timeString + "\n\n"
                                     + "Description: " + description + "\n\n")
                            .setCancelable(false)
                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                } catch (Exception e) {
                    Log.d("Notifications: ", e.getMessage());
                }
            }
        });
    }
}
