package com.example.androidcarmanager.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.androidcarmanager.add.Add_Files_Screen;
import com.example.androidcarmanager.add.Add_Notes_Screen;
import com.example.androidcarmanager.copmute.Compute_Screen;
import com.example.androidcarmanager.Expences.Add_Expenses_Screen;
import com.example.androidcarmanager.capture.Gallery_Screen;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.add.Reminder_Screen;
import com.example.androidcarmanager.View_EXPENCES.View_Expences_Screen;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.example.androidcarmanager.user_info.profile_screen;
import com.example.androidcarmanager.vehical.vehicals;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    //    int vehicleId;
    Boolean isrunningfirst;
    ImageButton btnsavereading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        //show vehicls list only first time
        isrunningfirst = getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getBoolean("isrunningfirst", true);

        if (isrunningfirst) {
            setTitle(Html.fromHtml("<font color='#3477e3'>Dashboard</font>"));
            startActivity(new Intent(MainActivity.this, vehicals.class));
        } else {
//          vehicle name will be dynamic
            setTitle(Html.fromHtml("<font color='#3477e3'>Dashboard" + "(" +
                    getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                            .getString("vehicle", "Nothing Selected")
                    + ")</font>"));

            btnsavereading=(ImageButton)findViewById(R.id.fabbuttonodometer);
            btnsavereading.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(MainActivity.this, Odometer_Screen.class);
                    i.putExtra("type","add");
                    startActivity(i);
                }
            });


        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard_menu, menu);

        return true;
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()) {
            case R.id.profile: {
                Intent i = new Intent(MainActivity.this, profile_screen.class);
                i.putExtra("type","create");
                i.putExtra("type","update");
                startActivity(i);
            }break;
            case R.id.vehicle:{
                Intent i = new Intent(MainActivity.this, vehicals.class);
                startActivity(i);
            }break;
            case R.id.logout:{
                mAuth.signOut();
                getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                        .edit()
                        .putString("vehicle", "Nothing Selected")
                        .putString("key", "null")
                        .commit();
                startActivity(new Intent(MainActivity.this, Login_Screen.class));
                finish();
            }break;
        }
        return(super.onOptionsItemSelected(item));
    }
    public void moveToNextActivity(View view){
        Intent intent;
        switch (view.getId()){
            case R.id.card1:{
                intent = new Intent(MainActivity.this, Add_Expenses_Screen.class);
                startActivity(intent);
            }break;
            case R.id.card2:{
                intent = new Intent(MainActivity.this, Gallery_Screen.class);
                startActivity(intent);
            }break;
            case R.id.card3:{
                intent = new Intent(MainActivity.this, View_Expences_Screen.class);
                startActivity(intent);
            }break;
            case R.id.card4:{
                intent = new Intent(MainActivity.this, Compute_Screen.class);
                startActivity(intent);
            }break;
            case R.id.card5:{
                intent = new Intent(MainActivity.this, Add_Files_Screen.class);
                startActivity(intent);
            }break;
            case R.id.card6:{
                intent = new Intent(MainActivity.this, Reminder_Screen.class);
                startActivity(intent);
            }break;
            default:{
                Log.d("error: ","Next Activity not Specified");
            }
        }
    }
}
