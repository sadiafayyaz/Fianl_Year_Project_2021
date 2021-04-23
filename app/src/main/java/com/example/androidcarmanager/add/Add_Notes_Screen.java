package com.example.androidcarmanager.add;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.example.androidcarmanager.Database.Add_Notes_DB;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Add_Notes_Screen extends AppCompatActivity {
    EditText ettitle, etdate,etmessage;
    Button btnsave;

    String title="",message="";
    Long date;
    private DatabaseReference databaseReference, databaseReference2;
    private FirebaseAuth auth;
    String key="";
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__notes__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Notes Details</font>"));
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Add_Notes_Screen.this, Login_Screen.class));
        }
        final FirebaseUser user = auth
                .getCurrentUser();
        key = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key", "-1");
        databaseReference = FirebaseDatabase.getInstance().getReference("users/" + user.getUid() + "/notes/" + key);
//        databaseReference2=FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/notes/"+key);

        ettitle = (EditText) findViewById(R.id.titlenote);
        etdate = (EditText) findViewById(R.id.notedate);
        etmessage = (EditText) findViewById(R.id.description);
        btnsave = (Button) findViewById(R.id.notesave);

        etdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH);
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(Add_Notes_Screen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etdate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        calendar.set(year, month, dayOfMonth);
                        date = calendar.getTimeInMillis();
                    }
                }, mYear, mMonth, mDay).show();
            }
        });


        Bundle i = getIntent().getExtras();
        final String type = i.getString("type");
        if (type.equals("edit")) {
            ettitle.setText(i.getString("title"));
            etmessage.setText(i.getString("message"));
        }

        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                title = ettitle.getText().toString().trim();
                message = etmessage.getText().toString().trim();
                if (key.equals("-1")) {
                    Toast.makeText(Add_Notes_Screen.this, "Please Select a Car Before Adding Note!", Toast.LENGTH_SHORT).show();
                } else if (title.isEmpty()) {
                    Toast.makeText(Add_Notes_Screen.this, "Enter Title!", Toast.LENGTH_SHORT).show();
                } else if (date == null) {
                    Toast.makeText(Add_Notes_Screen.this, "Select Date!", Toast.LENGTH_SHORT).show();
                } else if (message.isEmpty()) {
                    Toast.makeText(Add_Notes_Screen.this, "Enter Message!", Toast.LENGTH_SHORT).show();
                } else {
                    addNoteIntoFirebase(type);
                }
            }
        });

    }

//    public void getPosition() {
//        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
//            @Override
//            public void onSuccess(DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
//                        position= Integer.parseInt(ds.getKey());
//                    }
//                }else{
//                    position=0;
//                }
//            }
//        });
//    }
    public void addNoteIntoFirebase(final String type){
        final Add_Notes_DB notesDB = new Add_Notes_DB(title,message,date);
        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        position= Integer.parseInt(ds.getKey());
                    }
                    position++;
                }else{
                    position=0;
                }

                if(type.equals("add")){
                    databaseReference.child(String.valueOf(position)).setValue(notesDB);
                    clearEtValue();
                    Toast.makeText(Add_Notes_Screen.this, "Note Added!", Toast.LENGTH_SHORT).show();
                }else if(type.equals("edit")) {
                    Bundle bundle=getIntent().getExtras();
                    String noteKey=bundle.getString("key");
                    databaseReference.child(noteKey).setValue(notesDB);
                    clearEtValue();
                    Toast.makeText(Add_Notes_Screen.this, "Note Updated!", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void clearEtValue(){
        ettitle.setText("");
        etmessage.setText("");
        etdate.setText("");

    }
}