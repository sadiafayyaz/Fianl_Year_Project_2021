package com.example.androidcarmanager.add;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.androidcarmanager.Database.Reminder_DB;
import com.example.androidcarmanager.R;
import com.example.androidcarmanager.Reminder_transmiter;
import com.example.androidcarmanager.user_info.Login_Screen;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class Add_Reminder_Screen extends AppCompatActivity {
    EditText etTitle, etDate, etTime,etDescription;
    DatePickerDialog datePiker;
    TimePickerDialog timePiker;
    Button btnAddReminder;

    String key;
    ArrayList<String> keys = new ArrayList<String>();
    private DatabaseReference databaseReference1;
    private FirebaseAuth reminderAuth;
    int mYear;
    int mMonth;
    int mDay;

    Long date, time;
    String title,description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add__reminder__screen);
        setTitle(Html.fromHtml("<font color='#3477e3'>Add Reminder</font>"));
        reminderAuth = FirebaseAuth.getInstance();
        if (reminderAuth.getCurrentUser() == null) {
            finish();
            startActivity(new Intent(Add_Reminder_Screen.this, Login_Screen.class));
        }
        final FirebaseUser user=reminderAuth.getCurrentUser();
        key= getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                .getString("key","-1");

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "Android_Car_Manager";
            String description = "Channel for Android Car Manager";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Android_Car_Manager",name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        databaseReference1 = FirebaseDatabase.getInstance().getReference("users/"+user.getUid()+"/reminders/");

        etTitle=(EditText)findViewById(R.id.Remindertitle);
        etDate=(EditText)findViewById(R.id.Alarmdate);
        etTime=(EditText)findViewById(R.id.Alarmtime);
        etDescription=(EditText)findViewById(R.id.reminderDescription);
        btnAddReminder=(Button)findViewById(R.id.Alarmbutton);

//      setting empty value by default
        etTitle.setText("");
        etDate.setText("");
        etTime.setText("");
        etDescription.setText("");

        Bundle i = getIntent().getExtras();
//      check type
        if(i.getString("type").equals("edit")){
            etTitle.setText(i.getString("title"));
            etDate.setText(i.getString("date"));
            etTime.setText(i.getString("time"));
            etDescription.setText(i.getString("Description"));
        }

//      datepicker
        etDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);

//                setting date picker
                datePiker = new DatePickerDialog(Add_Reminder_Screen.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        etDate.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
                        calendar.set(year, month, dayOfMonth);
                        date = calendar.getTimeInMillis();
                    }
                }, mYear,mMonth,mDay);
                datePiker.show();
            }
        });
//      timepicker
        etTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Date dateObjForTime = new Date();

                final Calendar calendar=Calendar.getInstance();
                calendar.setTimeInMillis(System.currentTimeMillis());
                int mHour=calendar.get(Calendar.HOUR_OF_DAY);
                final int mMinute=calendar.get(Calendar.MINUTE);

                timePiker = new TimePickerDialog(Add_Reminder_Screen.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        etTime.setText(hourOfDay+" : "+minute);
                        if(hourOfDay != calendar.get(Calendar.HOUR_OF_DAY)){
                            calendar.add(Calendar.HOUR_OF_DAY, (calendar.get(Calendar.HOUR_OF_DAY) - hourOfDay));
                        }
                        if(minute != calendar.get(Calendar.MINUTE)){
                            calendar.add(Calendar.MINUTE, (minute-calendar.get(Calendar.MINUTE)));
                        }
                        time=calendar.getTimeInMillis();
                    }
                },mHour, mMinute, false);
                timePiker.show();
            }
        });


        databaseReference1.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for(DataSnapshot ds:dataSnapshot.getChildren()){
                        keys.add(ds.getKey());
                    }
                }
            }
        });




        btnAddReminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String index;

                if(keys.size() > 0){
                    index = String.valueOf(Integer.parseInt(keys.get(keys.size()-1)) + 1);
                }else{
                    index = String.valueOf(0);
                }

                getValuesFromEt();
                Reminder_DB remindersDB=new Reminder_DB(title, date, time,description);
                Date dateObj = new Date();
                Calendar cal_now = Calendar.getInstance();
                cal_now.setTime(dateObj);
                if (date > cal_now.getTimeInMillis() || time > cal_now.getTimeInMillis()){
                    final String in=index;
                    databaseReference1.child(in).setValue(remindersDB).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Calendar cal_set = Calendar.getInstance();
                            Calendar cal_trigger_date = Calendar.getInstance();
                            Calendar cal_trigger_time = Calendar.getInstance();

                            int day, month, year, hours, minutes;
                            cal_trigger_date.setTimeInMillis(date);
                            cal_trigger_time.setTimeInMillis(time);

                            day = cal_trigger_date.get(Calendar.DAY_OF_MONTH);
                            month = cal_trigger_date.get(Calendar.MONTH);
                            year = cal_trigger_date.get(Calendar.YEAR);

                            hours = cal_trigger_time.get(Calendar.HOUR_OF_DAY);
                            minutes = (cal_trigger_time.get(Calendar.MINUTE) ) ;

                            cal_set.set(year, month, day, hours, minutes);

                            AlarmManager manager = (AlarmManager) getSystemService(Add_Reminder_Screen.ALARM_SERVICE);
                            Intent myIntent = new Intent(Add_Reminder_Screen.this, Reminder.class);
                            myIntent.putExtra("Title", title);
                            myIntent.putExtra("Description", description);
                            myIntent.putExtra("index", in);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(Add_Reminder_Screen.this, 0, myIntent, Intent.FILL_IN_DATA);

                            manager.set(AlarmManager.RTC_WAKEUP, cal_set.getTimeInMillis(), pendingIntent);
                            Toast.makeText(Add_Reminder_Screen.this, "Reminder Added", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(Add_Reminder_Screen.this, "Please Add correct duration", Toast.LENGTH_SHORT).show();
                }
            }






//                AlarmManager manager = (AlarmManager) getSystemService(AddReminders.ALARM_SERVICE);
//
//                Date dat = new Date();
//                Calendar cal_alarm = Calendar.getInstance();
//                Calendar cal_now = Calendar.getInstance();
//
//                cal_now.setTime(dat);
//                cal_alarm.setTime(dat);
//
////                cal_alarm.set(Calendar.HOUR_OF_DAY,12);
//                cal_alarm.set(Calendar.MINUTE,0);
//                cal_alarm.set(Calendar.SECOND,30);
//                if(cal_alarm.before(cal_now)){
//                    cal_alarm.add(Calendar.DATE,1);
//                }
//
//                Intent myIntent = new Intent(AddReminders.this, ReminderBroadcast.class);
//                PendingIntent pendingIntent = PendingIntent.getBroadcast(AddReminders.this, 0, myIntent, 0);
//
//                manager.set(AlarmManager.RTC_WAKEUP, cal_now.getTimeInMillis() + (1000*10), pendingIntent);
//                Toast.makeText(AddReminders.this,"Alarm setuped",Toast.LENGTH_SHORT).show();
//            }


        });

    }
    public void getValuesFromEt() {
        title=etTitle.getText().toString().trim();
        description=etDescription.getText().toString().trim();
    }

    }
