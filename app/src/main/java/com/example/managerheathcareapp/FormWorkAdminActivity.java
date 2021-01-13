package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.managerheathcareapp.Model.Admin;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FormWorkAdminActivity extends AppCompatActivity {
    ImageButton imageButtonBackSpace, imageButtonGetImage, imageButtonCamera;
    TextInputEditText txt_fullName, txt_password, txt_email, txt_phone;
    Button btn_submit;

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth mAuthRegister;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference adminRef = database.getReference("Admins");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_work_admin);
        setControl();
        mAuth = FirebaseAuth.getInstance();
        setEvent();

    }

    private void setEvent() {

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txt_email.getText().toString().trim();
                String password = txt_password.getText().toString().trim();
                String phone = txt_phone.getText().toString().trim();
                String fullName = txt_fullName.getText().toString().trim();
                registerAdmin(email, password, phone, fullName);
                mAuth.signOut();
                if (mAuth.getCurrentUser() == null){
                    startActivity(new Intent(FormWorkAdminActivity.this, LoginActivity.class));
                }
            }
        });
        imageButtonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void registerAdmin(String email, String password, String phone, String fullName) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(FormWorkAdminActivity.this, "Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();
                            String id_admin = mAuth.getUid();
                            Admin admin = new Admin(id_admin, fullName, phone, email, "0");
                            adminRef.child(id_admin).setValue(admin);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(FormWorkAdminActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void setControl() {
        imageButtonBackSpace = findViewById(R.id.icon_backSpace_list_admin);
        imageButtonCamera = findViewById(R.id.btn_getCamera_form_admin);
        imageButtonGetImage = findViewById(R.id.btn_getImage_form_admin);
        btn_submit = findViewById(R.id.btn_submit_form_admin);
        txt_email = findViewById(R.id.txt_email_form_admin);
        txt_fullName = findViewById(R.id.txt_fullName_form_admin);
        txt_password = findViewById(R.id.txt_password_form_admin);
        txt_phone = findViewById(R.id.txt_phone_form_admin);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
}