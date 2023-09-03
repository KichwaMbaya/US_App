package com.justkeepfaith.morioanzenza;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class finitura extends AppCompatActivity {

    EditText finoccup, finpow, finincome, finidno, finkeen, finrelation, finphone;
    Button finsubmit;
    SharedPreferences sharedPreferences;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finitura);

        finoccup = findViewById(R.id.finoccup);
        finpow = findViewById(R.id.finpow);
        finincome = findViewById(R.id.finincome);
        finidno = findViewById(R.id.finidno);
        finkeen = findViewById(R.id.finkeen);
        finrelation = findViewById(R.id.finrelation);
        finphone = findViewById(R.id.finphone);
        finsubmit = findViewById(R.id.finsubmit);

        sharedPreferences = getApplicationContext().getSharedPreferences("user_info", Context.MODE_PRIVATE);
        String proce = sharedPreferences.getString("workplace", "");

        if (!proce.isEmpty()){

            Intent intent = new Intent(finitura.this, principale2.class);
            startActivity(intent);
            finish();
        }

        if (!Conectivity.isConnectingToInternet(finitura.this)) {
            Toast.makeText(finitura.this, "Please check your internet connection.", Toast.LENGTH_SHORT).show();
        }

        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Classified").document("Limits");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                DocumentSnapshot dsnap = task.getResult();

                String Upper = dsnap.getString("Upper");
                String Lower = dsnap.getString("Lower");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("Upper", Upper);
                editor.putString("Lower", Lower);
                editor.commit();
            }
        });

        finsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                verifyer();
            }
        });
    }
    private void verifyer(){
        String finoccu = finoccup.getText().toString();
        String finpo = finpow.getText().toString();
        String finincom = finincome.getText().toString();
        String finidn = finidno.getText().toString();
        String finkee = finkeen.getText().toString();
        String finrelatio = finrelation.getText().toString();
        String phon = finphone.getText().toString();

        if (finoccu.isEmpty()){
            finoccup.setError("Enter occupation");
            Toast.makeText(finitura.this, "Enter occupation", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finpo.isEmpty()){
            finpow.setError("Enter workplace");
            Toast.makeText(finitura.this, "Enter workplace", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finincom.isEmpty()){
            finincome.setError("Enter income");
            Toast.makeText(finitura.this, "Enter income", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finidn.isEmpty()){
            finidno.setError("Enter NI number");
            Toast.makeText(finitura.this, "Enter NI number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finidn.length() != 4 ){
            finidno.setError("Last 4 digits only");
            Toast.makeText(finitura.this, "Last 4 digits only", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finkee.isEmpty()){
            finkeen.setError("Enter Name");
            Toast.makeText(finitura.this, "Enter Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (finrelatio.isEmpty()){
            finrelation.setError("Relationship");
            Toast.makeText(finitura.this, "You must be related with your guarantor", Toast.LENGTH_LONG).show();
            return;
        }
        if (phon.isEmpty()){
            finphone.setError("Enter phone number");
            Toast.makeText(finitura.this, "Enter phone number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phon.length() < 8 ){
            finphone. setError("Too short");
            Toast.makeText(finitura.this, "Too short", Toast.LENGTH_SHORT).show();
            return;
        }
        if (phon.length() > 13 ){
            finphone.setError("Too long");
            Toast.makeText(finitura.this, "Too long", Toast.LENGTH_SHORT).show();
            return;
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("occupation", finoccu);
        editor.putString("workplace", finpo);
        editor.putString("income", finincom);
        editor.putString("IDno", finidn);
        editor.putString("keen_name", finkee);
        editor.putString("relationship", finrelatio);
        editor.putString("keen_phone", phon);
        editor.commit();


        progressDialog = new ProgressDialog(finitura.this);
        progressDialog.setMessage("Saving details...");
        progressDialog.setProgressStyle(0);
        progressDialog.setMax(100);
        progressDialog.show();
        progressDialog.setCancelable(false);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(4000);
                    Intent intent = new Intent(finitura.this, grenze_witz.class);
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                progressDialog.dismiss();
            }
        }).start();
    }
}