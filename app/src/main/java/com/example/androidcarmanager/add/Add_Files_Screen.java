package com.example.androidcarmanager.add;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.androidcarmanager.Database.Add_Notes_DB;
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

public class Add_Files_Screen extends AppCompatActivity {
    List_Adapter adapter;
    ImageButton btnimage;
    ListView listView;

    ArrayList<String> title= new ArrayList<String>();
    ArrayList<Long> date= new ArrayList<Long>();
    ArrayList<String> message = new ArrayList<String>();
    ArrayList<List_Model> listModel = new ArrayList<List_Model>();

    private DatabaseReference databaseReference;
    private FirebaseAuth Auth;

    String key = "";
    ArrayList<String> keys = new ArrayList<String>();

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__files__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Add Notes</font>"));
//
        Auth = FirebaseAuth.getInstance();
        if (Auth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Add_Files_Screen.this, Login_Screen.class));
        }
        progressDialog= ProgressDialog.show(Add_Files_Screen.this, "","Please Wait, Loading...",true);

        final FirebaseUser user=Auth.getCurrentUser();
        key= getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key","-1");
        databaseReference= FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/notes/"+key);


        btnimage=(ImageButton)findViewById(R.id.fabbutton);
        listView=(ListView)findViewById(R.id.notelist);

        btnimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(Add_Files_Screen.this, Add_Notes_Screen.class);
                i.putExtra("type","add");
                startActivity(i);
            }
        });

        databaseReference.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot ds:dataSnapshot.getChildren()){
                        Add_Notes_DB notesDB=ds.getValue(Add_Notes_DB.class);
                        keys.add(ds.getKey());
                        title.add(notesDB.getTitle());
                        message.add(notesDB.getMessage());
                        date.add(notesDB.getDate());
                    }
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(Add_Files_Screen.this, "You havent added any note yet.",Toast.LENGTH_SHORT).show();
                }

                if (!title.isEmpty() && !date.isEmpty() && !message.isEmpty()){
                    for (int i=0;i<=title.size()-1;i++){
                        List_Model model=new List_Model(title.get(i),date.get(i));
                        listModel.add(model);
                    }
                    adapter = new List_Adapter(Add_Files_Screen.this, listModel);
                    listView.setAdapter(adapter);
                    progressDialog.dismiss();
                }else{
                    progressDialog.dismiss();
                    Log.d("listmodel","failed to get values");
                }
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Add_Files_Screen.this);
                builder.setTitle("Choose an option");
                String[] options = {"View", "Edit", "Delete"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: {
                                try {

                                    Calendar c = Calendar.getInstance();
                                    c.setTimeInMillis(date.get(position));
                                    c.add(Calendar.MONTH,1);
                                    String dateString= c.get(Calendar.DAY_OF_MONTH)+"/"+c.get(Calendar.MONTH)+"/"+c.get(Calendar.YEAR);
                                    new AlertDialog.Builder(Add_Files_Screen.this)
                                            .setTitle(title.get(position))
                                            .setMessage(
                                                    "Date: "+dateString+"\n\n"
                                                            +"----------------------------------\n\n"
                                                            +"Message: "+message.get(position)+" \n"
                                            )
                                            .setCancelable(false)
                                            .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {
                                                }
                                            }).show();
                                } catch (Exception e) {
                                    Log.d("Notifications: ", e.getMessage());
                                }
                            }break;
                            case 1: {
                                Intent i = new Intent(Add_Files_Screen.this, Add_Notes_Screen.class);
                                i.putExtra("type", "edit");
                                i.putExtra("key", keys.get(position));
                                i.putExtra("title", title.get(position));
                                i.putExtra("message", message.get(position));
                                startActivity(i);
                            }
                            break;
                            case 2: {
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
                                        removeItem(Integer.parseInt(index.get(position)), position);
                                    }
                                });
                            }break;
                        }
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

    }

    public boolean removeItem(final int dbPosition, final int listPosition){
        Log.d("position", String.valueOf(dbPosition));
        boolean status;
        if(dbPosition<0){
            status=false;
        }else{
            status=true;
            databaseReference.child(String.valueOf(dbPosition)).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    listModel.remove(listPosition);
                    title.remove(listPosition);
                    date.remove(listPosition);
                    message.remove(listPosition);
                    adapter.notifyDataSetChanged();
                }
            });
        }
        return status;
    }
}



//    }
//    public void addNotes(View v){
//        Intent i = new Intent(Add_Files_Screen.this, Add_Notes_Screen.class);
//        startActivity(i);
//    }
//    public void moveToNextActivity(View view) {
//        Intent intent;
//        switch (view.getId()) {
//            case R.id.card1: {
//                intent = new Intent(Add_Files_Screen.this, Add_Notes_Screen.class);
//                startActivity(intent);
//            }
//            break;
//            case R.id.card2: {
//                intent = new Intent(Add_Files_Screen.this, Add_Notes_Screen.class);
//                startActivity(intent);
//            }
//            break;
//            case R.id.card3: {
//                intent = new Intent(Add_Files_Screen.this, Add_Notes_Screen.class);
//                startActivity(intent);
//            }
//            break;
//
//            default: {
//                Log.d("error: ", "Next Activity not Specified");
//            }
//        }
//    }
//}
