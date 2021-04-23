package com.example.androidcarmanager.user_info;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.androidcarmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.util.Objects;

public class SignUp_Screen extends AppCompatActivity {

    private FirebaseAuth mAuth;
    ProgressBar progressBar2;
    Button btnSignUp;
    EditText etemail,etpass,confermpass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up__screen);
//        getSupportActionBar().hide();
        setTitle(Html.fromHtml("<font color='#3477e3'>Sign Up</font>"));


       // Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
//      initialization
        progressBar2=(ProgressBar) findViewById(R.id.progressBar2);
        btnSignUp=(Button) findViewById(R.id.btnsigup);
        etemail=(EditText) findViewById(R.id.edemail);
        etpass=(EditText) findViewById(R.id.pass1);
        confermpass=(EditText) findViewById(R.id.cnfrmpass);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etemail.getText().toString().trim();
                String password = etpass.getText().toString().trim();
                String confirmpassword = confermpass.getText().toString().trim();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter Email address!", Toast.LENGTH_SHORT).show();
                    return;
                }else  if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter Password!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (password.length() < 8) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 8 characters!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (TextUtils.isEmpty(confirmpassword)) {
                    Toast.makeText(getApplicationContext(), "please confirm password!", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!Objects.equals(password, confirmpassword)) {
                    Toast.makeText(getApplicationContext(), "Password does not match!", Toast.LENGTH_SHORT).show();
                    return;
                } else{
                    progressBar2.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar2.setVisibility(View.GONE);
                            Toast.makeText(SignUp_Screen.this,"User Successfully Registered.",Toast.LENGTH_SHORT).show();
                            if(!task.isSuccessful()){
                                Toast.makeText(SignUp_Screen.this,"Registration failed.",Toast.LENGTH_SHORT).show();
                            }else{
//                            Intent i = new Intent(Signup.this,Signin.class);
//                            startActivity(i);
                                finish();
                            }

                        }
                    });


                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar2.setVisibility(View.GONE);
    }

}
