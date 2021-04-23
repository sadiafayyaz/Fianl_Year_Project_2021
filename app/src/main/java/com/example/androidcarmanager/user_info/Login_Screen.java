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
import android.widget.TextView;
import android.widget.Toast;

import com.example.androidcarmanager.main.MainActivity;
import com.example.androidcarmanager.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login_Screen extends AppCompatActivity {
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    EditText emailEt, passwordEt;
    Button SigninBtn,signup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login__screen);
//        getSupportActionBar().hide();
        setTitle(Html.fromHtml("<font color='#3477e3'>Login to Continue</font>"));




        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(Login_Screen.this, MainActivity.class));
            finish();
        }
        progressBar=(ProgressBar)findViewById(R.id.progressBar);
        emailEt=(EditText)findViewById(R.id.email);
        passwordEt=(EditText)findViewById(R.id.pass);
        SigninBtn=(Button)findViewById(R.id.btnsignin);
        signup =(Button)findViewById(R.id.btnsigup);
        mAuth = FirebaseAuth.getInstance();

        SigninBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEt.getText().toString();
                final String password = passwordEt.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }else if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }else {
                    progressBar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressBar.setVisibility(View.GONE);
                            if (!task.isSuccessful()) {
                                // there was an error
                                if (password.length() < 8) {
                                    passwordEt.setError("Please Enter Corrent Passowrd.");
                                } else {
                                    Toast.makeText(Login_Screen.this, "Authentication Failed", Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Intent intent = new Intent(Login_Screen.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
//        Intent i = new Intent(Signin.this, MainActivity.class);
//        i.putExtra("token",currentUser);
//        startActivity(i);
    }

    public void forgotPassword(View v){
        Intent i = new Intent(Login_Screen.this,Forget_Password_Screen.class);
        startActivity(i);
    }
    public void signIn(View v){



        Intent i = new Intent(Login_Screen.this, MainActivity.class);
        startActivity(i);
        Log.i("Sign In","Signed In Successfully.");
    }
    public void signUp(View v){
        Intent i = new Intent(Login_Screen.this,SignUp_Screen.class);
        startActivity(i);
    }



}
