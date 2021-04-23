package com.example.androidcarmanager.add;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.androidcarmanager.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Reminder extends BroadcastReceiver {

    private DatabaseReference databaseReference1;
    private FirebaseAuth firebaseAuth;

    @Override
    public void onReceive(final Context context, Intent intent) {
        firebaseAuth = FirebaseAuth.getInstance();
        final FirebaseUser user=firebaseAuth.getCurrentUser();
        databaseReference1 = FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/reminders/");

        Bundle bundle = intent.getExtras();
        final String Title = bundle.getString("Title");
        final String Description = bundle.getString("Description");
        String index = bundle.getString("index");

        databaseReference1.child(index).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                NotificationCompat.Builder builder=new NotificationCompat.Builder(context, "Android_Car_Manager")
                        .setSmallIcon(R.drawable.car)
                        .setContentTitle(Title)
                        .setContentText(Description)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
                notificationManagerCompat.notify(0, builder.build());
            }
        });
    }
}
