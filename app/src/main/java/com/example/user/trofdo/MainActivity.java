package com.example.user.trofdo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
        FirebaseAuth mAuth;
    EditText editTextEmail, editTextPassword;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressDialog = new ProgressDialog(this);
        editTextEmail = findViewById(R.id.TypeEmail);
        editTextPassword = findViewById(R.id.typepassword);
        findViewById(R.id.Register).setOnClickListener(this);
        findViewById(R.id.login).setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
    }


    private void userLogin(){
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        if(email.isEmpty()) {
            editTextEmail.setError("Enter Email");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty()) {
            editTextPassword.setError("Enter Password");
            editTextPassword.requestFocus();
            }
        progressDialog.show();


        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Intent intent = new Intent(MainActivity.this, Home.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    progressDialog.dismiss();

                }else {
                   Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() != null){
            finish();
            startActivity(new Intent(this, Home.class));
        }
    }
    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.Register:
               startActivity(new Intent(this, Registration.class));
               break;
            case R.id.login:
                userLogin();
                break;
        }
    }
}
