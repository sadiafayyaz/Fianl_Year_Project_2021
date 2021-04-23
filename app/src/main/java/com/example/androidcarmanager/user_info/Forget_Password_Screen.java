package com.example.androidcarmanager.user_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.androidcarmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class Forget_Password_Screen extends AppCompatActivity {
//RelativeLayout resetlayout,questionlayout;
//EditText  pass1,pass2,ans1;
EditText etresetemail;
    Button resetBtn;
    private FirebaseAuth mAuth;
    ProgressBar progressBar3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget__password__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Reset Password</font>"));
        etresetemail=(EditText)findViewById(R.id.emailreset);
        resetBtn=(Button)findViewById(R.id.btnresetpass);
        progressBar3=(ProgressBar)findViewById(R.id.progressBar3);
        mAuth = FirebaseAuth.getInstance();


        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etresetemail.getText().toString().trim();
                progressBar3.setVisibility(View.VISIBLE);
                mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Forget_Password_Screen.this, "We have sent you instructions to reset your password on your Email!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(Forget_Password_Screen.this, "Failed to send reset email!", Toast.LENGTH_SHORT).show();
                        }
                        progressBar3.setVisibility(View.GONE);
                    }
                });
            }
        });

    }


       // ans1=(EditText) findViewById(R.id.ans1);
        //pass1=(EditText) findViewById(R.id.pass1);
        //pass2=(EditText) findViewById(R.id.pass2);

        //questionlayout=(RelativeLayout) findViewById(R.id.questionlayout);
        //resetlayout=(RelativeLayout) findViewById(R.id.resetlayout);

  //  }

  //  public  void submitAns(View v){
       // String ans=ans1.getText().toString();
        //if(ans.equals("parrot")){
            //Log.d("answer",ans1.getText().toString());
           // questionlayout.setVisibility(View.GONE);
         //   resetlayout.setVisibility(View.VISIBLE);
       // }else{
         //   Toast.makeText(Forget_Password_Screen.this,"You gave wrong Ans, Try Again.",Toast.LENGTH_SHORT).show();
       //     Log.d("answer",ans1.getText().toString());
     //   }
   // }


 //   public  void resetPass(View v){
   //     String pass = pass1.getText().toString();
     //   String confPass = pass2.getText().toString();

   //     if(pass.equals(confPass)){
     //       Intent i= new Intent(Forget_Password_Screen.this, Login_Screen.class);
       //     startActivity(i);
         //   Toast.makeText(Forget_Password_Screen.this,"Your Password Successfully Reset.",Toast.LENGTH_SHORT).show();
        //}else{
      //      Toast.makeText(Forget_Password_Screen.this,"Password Mismatch",Toast.LENGTH_SHORT).show();
    //    }
  //  }

    //private class FirebaseAuth {
   // }
}
