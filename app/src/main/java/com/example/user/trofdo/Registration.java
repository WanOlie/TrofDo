package com.example.user.trofdo;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Registration extends AppCompatActivity implements View.OnClickListener{

    EditText editTextEnterusername, editTextEnterIDno, editTextEnteremai, editTextEntertellephone, editTextEnterpassword, editTextConfirmpassword;
    TextView textViewUserCategory;
    Spinner spinnerUsercategory;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    DatabaseReference databaseUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        databaseUsers = FirebaseDatabase.getInstance().getReference().child("Users");

        editTextEnterusername = findViewById(R.id.Enterusername);
        editTextEnterIDno = findViewById(R.id.EnterIDno);
        editTextEnteremai = findViewById(R.id.Enteremail);
        editTextEntertellephone = findViewById(R.id.Entertellephone);
        textViewUserCategory = findViewById(R.id.Selectcategory);
        editTextEnterpassword = findViewById(R.id.Enterpassword);
        editTextConfirmpassword = findViewById(R.id.Confirmpassword);
        progressBar = findViewById(R.id.Progressbar);
        spinnerUsercategory = findViewById(R.id.Usercategory);

        mAuth = FirebaseAuth.getInstance();
        findViewById(R.id.Submit).setOnClickListener(this);
    }

    private void registerUser(){
         String username = editTextEnterusername.getText().toString().trim();
         String IDno = editTextEnterIDno.getText().toString().trim();
         String email =editTextEnteremai.getText().toString().trim();
         String phone = editTextEntertellephone.getText().toString().trim();
         String usercategory = spinnerUsercategory.getSelectedItem().toString();
         String Passsword = editTextEnterpassword.getText().toString().trim();
         String confirmpassword = editTextConfirmpassword.getText().toString().trim();

        if(username.isEmpty()){
            editTextEnterusername.setError("Enter username");
            editTextEnterusername.requestFocus();
            return;
        }
        if(IDno.isEmpty()) {
            editTextEnterIDno.setError("Enter ID");
            editTextEnterIDno.requestFocus();
            return;
        }
        if(email.isEmpty()) {
            editTextEnteremai.setError("Enter Email");
            editTextEnteremai.requestFocus();
            return;
        }
        if(phone.isEmpty()) {
            editTextEntertellephone.setError("Enter Phone number");
            editTextEntertellephone.requestFocus();
            return;
        }
        if(usercategory.isEmpty()) {
            textViewUserCategory.setError("Select user category");
           spinnerUsercategory.requestFocus();
            return;
        }
        if(Passsword.isEmpty()) {
            editTextEnterpassword.setError("Enter password");
            editTextEnterpassword.requestFocus();
            return;
        }else if (Passsword.length()<6){
                editTextEnterpassword.setError("At least 6 characters");
                editTextEnterpassword.requestFocus();
                return;
        }
        if(confirmpassword.isEmpty()) {
            editTextConfirmpassword.setError("Confirm your Password");
            editTextConfirmpassword.requestFocus();

        }

        String Name = editTextEnterusername.getText().toString().trim();
        String ID_number = editTextEnterIDno.getText().toString().trim();
        String Email =editTextEnteremai.getText().toString().trim();
        String Phone_number = editTextEntertellephone.getText().toString().trim();
        String User_category = spinnerUsercategory.getSelectedItem().toString();

        progressBar.setVisibility(View.VISIBLE);

        HashMap data = new HashMap();
        data.put("name",Name);
        data.put("idNumber",ID_number);
        data.put("email",Email);
        data.put("phone",Phone_number);
        data.put("category",User_category);

        databaseUsers.updateChildren(data).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                if (task.isSuccessful()){
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, "Data has been successfully saved", Toast.LENGTH_SHORT).show();
                }else {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(Registration.this, "Data not saved", Toast.LENGTH_SHORT).show();
                }

            }
        });

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, Passsword).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    finish();
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Registration Successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(Registration.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);

                }else {
                    if (task.getException()instanceof FirebaseAuthUserCollisionException){
                        Toast.makeText(Registration.this, "Alredy registered", Toast.LENGTH_SHORT).show();
                    }else {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Toast.makeText(getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.Submit:
                registerUser();
                break;
        }
    }
}
