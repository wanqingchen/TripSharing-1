
package com.example.tainingzhang.tripsharing_v0;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegitrationActivity extends AppCompatActivity {

    private EditText txtEmailAddress;
    private EditText txtPassword;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regitration);
        txtEmailAddress = (EditText) findViewById(R.id.txtemailregistration);
        txtPassword = (EditText) findViewById(R.id.txtpasswordregistration);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public void btnRegistrationUser_Click(View v) {

        //final ProgressDialog progressDialog = ProgressDialog.show(RegitrationActivity.this, "Please wait...", "Processing...", true);
        (firebaseAuth.createUserWithEmailAndPassword(txtEmailAddress.getText().toString(), txtPassword.getText().toString()))
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //progressDialog.dismiss();

                        if (task.isSuccessful()) {
                            Toast.makeText(RegitrationActivity.this, "Registration successful", Toast.LENGTH_LONG).show();
                            Intent i = new Intent(RegitrationActivity.this, userinfoActivity.class);
                            startActivity(i);
                        }
                        else
                        {
                            Log.e("ERROR", task.getException().toString());
                            Toast.makeText(RegitrationActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
