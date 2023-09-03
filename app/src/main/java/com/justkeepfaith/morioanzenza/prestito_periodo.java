package com.justkeepfaith.morioanzenza;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Timer;
import java.util.TimerTask;

public class prestito_periodo extends AppCompatActivity {

    static RadioButton months3, months4, months5, months6, months7, months12;
    RadioGroup radioGroup1;
    TextView repayment, applydonkey, amount_tit;
    String undeni, months, string;
    int counternot;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prestito_periodo);

        months3 = (RadioButton) findViewById(R.id.months3);
        months4 = (RadioButton) findViewById(R.id.months4);
        months5 = (RadioButton) findViewById(R.id.months5);
        months6 = (RadioButton) findViewById(R.id.months6);
        months7 = (RadioButton) findViewById(R.id.months7);
        months12 = (RadioButton) findViewById(R.id.months12);
        radioGroup1 = (RadioGroup) findViewById(R.id.radioGroup1);
        repayment = (TextView) findViewById(R.id.repayment);
        applydonkey = (TextView) findViewById(R.id.applydonkey);
        amount_tit = findViewById(R.id.amount_tit);


        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        string = sharedPreferences.getString("loan_appliedx", "");

        amount_tit.setText("You are about to apply for a loan of $" + string + ".");
        createNotificationChannel();

        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                if (!string.isEmpty()){

                    int ienteramount = Integer.parseInt(string);

                    if (prestito_periodo.this.months3.isChecked()) {

                        double rate = 0.09;

                        undeni = String.valueOf(ienteramount * rate + ienteramount);

                        months = "3";
                    }
                    if (prestito_periodo.this.months4.isChecked()) {

                        double rate = 0.12;

                        undeni = String.valueOf(ienteramount * rate + ienteramount);

                        months = "4";
                    }
                    if (prestito_periodo.this.months5.isChecked()) {

                        double rate = 0.15;

                        undeni = String.valueOf(ienteramount * rate + ienteramount);

                        months = "5";
                    }
                    if (prestito_periodo.this.months6.isChecked()) {

                        double rate = 0.18;

                        undeni = String.valueOf(ienteramount * rate + ienteramount);

                        months = "6";
                    }
                    if (prestito_periodo.this.months7.isChecked()) {

                        double rate = 0.21;

                        undeni = String.valueOf(ienteramount * rate + ienteramount);

                        months = "8";
                    }
                    if (months12.isChecked()) {

                        double rate = 0.33;

                        undeni = String.valueOf(ienteramount * rate + ienteramount);

                        months = "12";
                    }

                    double vale = Double.parseDouble((undeni));
                    int deni = (int) (1 * Math.round(vale/1));
                    repayment.setText("You will repay: $" + deni);
                }
            }
        });
        applydonkey.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (months3.isChecked() || months4.isChecked() || months5.isChecked()
                        || months6.isChecked() || months7.isChecked() || months12.isChecked()) {

                    delaynot();
                    delaynot2();
                    notifhand();

                    SharedPreferences.Editor editor2 = sharedPreferences.edit();
                    editor2.putString("Months", months);
                    editor2.putString("loan_applied", string);
                    editor2.commit();

                    final ProgressDialog progressDialog = new ProgressDialog(prestito_periodo.this);
                    progressDialog.setTitle("Please wait");
                    progressDialog.setMessage("Submitting your loan request.");
                    progressDialog.setProgressStyle(0);
                    progressDialog.setMax(100);
                    progressDialog.show();
                    progressDialog.setCancelable(false);
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                Thread.sleep(5000);
                                prestito_periodo.this.startActivity(new Intent(prestito_periodo.this, applicato_successo.class));
                                finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            progressDialog.dismiss();
                        }
                    }).start();
                    return;

                }
                Toast.makeText(prestito_periodo.this, "Select period", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void createNotificationChannel(){

        if (Build.VERSION.SDK_INT >= 26){

            CharSequence name = "SuccessNotification";
            String description = "Channel for success notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Successful", name, importance);
            channel.setVibrationPattern(new long[]{0, 500, 200, 500});

            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    private void delaynot(){

        /*NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "Successful")
                .setPriority(NotificationCompat.DEFAULT_ALL)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Congratulations!! Your loan application has been successful.")
                .setContentText("Your loan application has been approved. Please log into the app for disbursement.")
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText("Your loan application has been approved. Please log into the app for disbursement."))
                .setAutoCancel(true);

        Intent intent = new Intent(getApplicationContext(), striscia_pagina.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("name", "Some value");

        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                0, intent, PendingIntent.FLAG_MUTABLE);

        builder.setContentIntent(pendingIntent);

        PendingIntent pendingIntent3 = PendingIntent.getBroadcast(prestito_periodo.this, 0,
                intent, PendingIntent.FLAG_MUTABLE);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();

        long tenSecondInMillis = 1000 * 157800;//570

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSecondInMillis, pendingIntent3);

        notificationManager.notify(0, builder.build());*/

        Intent intent = new Intent(prestito_periodo.this, Notification_Alarm.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(prestito_periodo.this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();

        long tenSecondInMillis = 1000 * 1080;//1080

        alarmManager.set(AlarmManager.RTC_WAKEUP,
                timeAtButtonClick + tenSecondInMillis,
                pendingIntent);
    }
    private void delaynot2(){

        Intent intent = new Intent(prestito_periodo.this, Notification_Alarm2.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(prestito_periodo.this, 0,
                intent, PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        long timeAtButtonClick = System.currentTimeMillis();

        long tenSecondInMillis = 1000 * 157800;//157800

        alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + tenSecondInMillis, pendingIntent);
    }
    private void notifhand() {
        final Timer tno = new Timer();
        TimerTask ttno = new TimerTask() {
            @Override
            public void run() {
                counternot++;

                if (counternot == 100) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("notification", "Congratulations!! Your loan request has been approved. " +
                            "You are remaining with only a few steps before you can receive your loan. " +
                            "Click on this message for more information.");
                    editor.commit();

                    tno.cancel();
                }
            }
        };
        tno.schedule(ttno, 0, 5700);
    }
}