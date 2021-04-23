package com.example.androidcarmanager.Expences;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import com.example.androidcarmanager.add.Add_Files_Screen;
import com.example.androidcarmanager.copmute.Compute_Screen;
import com.example.androidcarmanager.capture.Gallery_Screen;
import com.example.androidcarmanager.R;

public class Add_Expenses_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__expenses__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Add Expenses</font>"));}
        public void moveToNextActivity(View view){
            Intent intent;
            switch (view.getId()){
                case R.id.card1:{
                    Intent i = new Intent(Add_Expenses_Screen.this, Expense_Detail_Screen.class);
                    i.putExtra("type", "purchases spare parts");
                    startActivity(i);
                }break;
                case R.id.card2:{
                    Intent i = new Intent(Add_Expenses_Screen.this, Expense_Detail_Screen.class);
                    i.putExtra("type", "maintance");
                    startActivity(i);
                }break;
                case R.id.card3:{
                    Intent  i= new Intent(Add_Expenses_Screen.this, Fuel_Screen.class);
                    i.putExtra("type", "fuel");
                    startActivity(i);
                }break;
                case R.id.card4:{
                    Intent  i= new Intent(Add_Expenses_Screen.this, Expense_Detail_Screen.class);
                    i.putExtra("type", "cleaning");
                    startActivity(i);
                }break;
                case R.id.card5:{
                    Intent i = new Intent(Add_Expenses_Screen.this, Expense_Detail_Screen.class);
                    i.putExtra("type", "engine tuning");
                    startActivity(i);
                }break;
                default:{
                    Log.d("error: ","Next Activity not Specified");
                }
            }


    }
}
