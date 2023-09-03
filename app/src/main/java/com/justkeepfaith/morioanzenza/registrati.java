package com.justkeepfaith.morioanzenza;

import static java.util.Calendar.*;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class registrati extends AppCompatActivity {

    EditText rgsterfirst, rgstermiddle, rgsterlast, rgsteremail, rgsterphone, rgsterpin, rgsterconfirm;
    Button rgsterdob, rgsterbutton;
    DatePickerDialog datePickerDialog;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrati);

        rgsterfirst = findViewById(R.id.rgsterfirst);
        rgstermiddle = findViewById(R.id.rgstermiddle);
        rgsterlast = findViewById(R.id.rgsterlast);
        rgsteremail = findViewById(R.id.rgsteremail);
        rgsterphone = findViewById(R.id.rgsterphone);
        rgsterpin = findViewById(R.id.rgsterpin);
        rgsterconfirm = findViewById(R.id.rgsterconfirm);
        rgsterdob = findViewById(R.id.rgsterdob);
        rgsterbutton = findViewById(R.id.rgsterbutton);


        initDatePicker();
        rgsterdob.setText(getTodaysDate());

        if (!Conectivity.isConnectingToInternet(registrati.this)) {
            Toast.makeText(registrati.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);

        rgsterbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String first = rgsterfirst.getText().toString();
                String middle = rgstermiddle.getText().toString();
                String last = rgsterlast.getText().toString();
                String email = rgsteremail.getText().toString();
                String phone = rgsterphone.getText().toString();
                String pin = rgsterpin.getText().toString();
                String confirm = rgsterconfirm.getText().toString();

                if (first.isEmpty()){
                    rgsterfirst.setError("Enter Name");
                    return;
                }
                if (middle.isEmpty()){
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.remove("middle_name");
                    editor.commit();
                }
                if (last.isEmpty()){
                    rgsterlast.setError("Enter Name");
                    return;
                }
                if (email.isEmpty()){
                    rgsteremail.setError("Enter Email");
                    Toast.makeText(registrati.this, "Enter Email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    rgsteremail.setError("Invalid Email");
                    rgsteremail.requestFocus();
                    return;
                }
                if (phone.isEmpty()){
                    rgsterphone.setError("Enter Phone number");
                    Toast.makeText(registrati.this, "Enter Phone number", Toast.LENGTH_SHORT).show();
                }
                if (phone.length() < 8){
                    rgsterphone.setError("Too short");
                    Toast.makeText(registrati.this, "Too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (phone.length() > 13){
                    rgsterphone.setError("Too long");
                    Toast.makeText(registrati.this, "Too long", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pin.isEmpty()){
                    rgsterpin.setError("Enter PIN");
                    Toast.makeText(registrati.this, "Enter PIN", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pin.length() < 4){
                    rgsterpin.setError("Too short");
                    Toast.makeText(registrati.this, "Too short", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (confirm.isEmpty()){
                    rgsterconfirm.setError("Confirm PIN");
                    Toast.makeText(registrati.this, "Confirm PIN", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!confirm.equals(pin)){
                    rgsterpin.setError("Must be equal");
                    rgsterconfirm.setError("Must be equal");
                    Toast.makeText(registrati.this, "Must be equal", Toast.LENGTH_SHORT).show();
                    return;
                }

                AlertDialog.Builder dialog = new AlertDialog.Builder(registrati.this);
                dialog.setTitle("Are you sure?");
                dialog.setMessage("The details you have entered cannot be changed after account creation.");
                dialog.setPositiveButton("PROCEED", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("first_name", first);
                        editor.putString("middle_name", middle);
                        editor.putString("last_name", last);
                        editor.putString("Email", email);
                        editor.putString("phone_number", phone);
                        editor.putString("PIN", pin);

                        Integer logged_in = 2;
                        editor.putInt("logged", logged_in);

                        editor.commit();

                        progressDialog = new ProgressDialog(registrati.this);
                        progressDialog.setTitle("Please wait");
                        progressDialog.setMessage("Creating Account...");
                        progressDialog.setProgressStyle(0);
                        progressDialog.setMax(100);
                        progressDialog.show();
                        progressDialog.setCancelable(false);
                        new Thread(new Runnable() {
                            public void run() {
                                try {
                                    Thread.sleep(6000);
                                    Intent intent = new Intent(registrati.this, accesso.class);
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                progressDialog.dismiss();
                            }
                        }).start();

                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });

                AlertDialog alertDialog = dialog.create();
                alertDialog.show();
            }
        });
    }
    private String getTodaysDate()
    {
        Calendar cal = getInstance();

        int current = cal.get(YEAR);
        int age = 16;

        int year = current - age;
        int month = cal.get(MONTH);
        month = month + 1;
        int day = cal.get(DAY_OF_MONTH);
        return makeDateString(day, month, year);
    }

    private void initDatePicker()
    {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener()
        {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day)
            {
                month = month + 1;
                String date = makeDateString(day, month, year);
                rgsterdob.setText(date);
            }
        };

        Calendar cal = getInstance();

        int current = cal.get(YEAR);
        int age = 16;

        int year = current - age;
        int month = cal.get(MONTH);
        month = month + 1;
        int day = cal.get(DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_DARK;

        datePickerDialog = new DatePickerDialog(this, style, dateSetListener, year, month, day);
        datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() );

    }
    private String makeDateString(int day, int month, int year)
    {
        return getMonthFormat(month) + " " + day + ", " + year;
    }

    private String getMonthFormat(int month)
    {
        if(month == 1)
            return "JAN";
        if(month == 2)
            return "FEB";
        if(month == 3)
            return "MAR";
        if(month == 4)
            return "APR";
        if(month == 5)
            return "MAY";
        if(month == 6)
            return "JUN";
        if(month == 7)
            return "JUL";
        if(month == 8)
            return "AUG";
        if(month == 9)
            return "SEP";
        if(month == 10)
            return "OCT";
        if(month == 11)
            return "NOV";
        if(month == 12)
            return "DEC";

        //default should never happen
        return "AUG";
    }

    public void openDatePicker(View view)
    {
        datePickerDialog.show();
    }
}