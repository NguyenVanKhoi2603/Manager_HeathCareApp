package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.example.managerheathcareapp.Adapter.AdminAdapter;
import com.example.managerheathcareapp.Model.Admin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ManagerAdminActivity extends AppCompatActivity {
    ImageButton imageButtonBackSpace, imageButtonAddNewAdmin;
    RecyclerView rcy_list_admin;

    ArrayList<Admin> mAdmins = new ArrayList<>();
    AdminAdapter adminAdapter;

    // Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference AdminRef = database.getReference("Admins");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_admin);
        setControl();
        mAuth = FirebaseAuth.getInstance();
        setEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();


    }

    private void setEvent() {
        showList();
        imageButtonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        imageButtonAddNewAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ManagerAdminActivity.this, FormWorkAdminActivity.class));
            }
        });
    }

    private void showList() {
        // Read from the database
        AdminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mAdmins.clear();
                for (DataSnapshot ds : snapshot.getChildren()){
                    Admin admin = ds.getValue(Admin.class);
                    mAdmins.add(admin);
                }
                adminAdapter = new AdminAdapter(ManagerAdminActivity.this, mAdmins);
                rcy_list_admin.setAdapter(adminAdapter);
                rcy_list_admin.setLayoutManager(new LinearLayoutManager(ManagerAdminActivity.this));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setControl() {
        imageButtonAddNewAdmin = findViewById(R.id.icon_add_news_admin);
        imageButtonBackSpace = findViewById(R.id.icon_backSpace_manager_admin);
        rcy_list_admin = findViewById(R.id.rcy_admin);
    }
}