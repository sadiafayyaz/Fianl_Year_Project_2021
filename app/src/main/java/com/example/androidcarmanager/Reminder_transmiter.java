package com.example.androidcarmanager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Reminder_transmiter extends BroadcastReceiver {
    String key;
    ArrayList<String> keys = new ArrayList<String>();
    private DatabaseReference databaseReference1;
    private FirebaseAuth Auth;
    @Override
    public void onReceive(final Context context, Intent intent) {
        Auth = FirebaseAuth.getInstance();
        final FirebaseUser user=Auth.getCurrentUser();

        databaseReference1 = FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/reminders/");

        SharedPreferences preferences = context.getSharedPreferences("PREFERENCE", context.MODE_PRIVATE);
        key = preferences.getString("key","-1");

        Bundle bundle = intent.getExtras();
        final String Title = bundle.getString("Title");
        final String Description = bundle.getString("Description");
        String index = bundle.getString("index");

        databaseReference1.child(index).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                NotificationCompat.Builder builder=new NotificationCompat.Builder(context, "AndroidCarManager")
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
