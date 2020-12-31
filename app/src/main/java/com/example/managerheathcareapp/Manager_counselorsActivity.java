package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.example.managerheathcareapp.Adapter.CounselorAdapter;
import com.example.managerheathcareapp.Model.Counselor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Manager_counselorsActivity extends AppCompatActivity {
    ImageButton imageButtonBackSpace, imageButtonAddNewCounselors;
    RecyclerView recyclerVListCounselor;
    ArrayList<Counselor> mCounselors = new ArrayList<>();
    CounselorAdapter counselorAdapter;
    // Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference CounselorRef = database.getReference("Counselors");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manager_counselors);
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
        showData();
        imageButtonAddNewCounselors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Manager_counselorsActivity.this, FormWorkCounselorsMainActivity.class));
            }
        });

        imageButtonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showData() {
        CounselorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCounselors.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Counselor counselor = ds.getValue(Counselor.class);
                    mCounselors.add(counselor);
                }
                if (mCounselors != null) {
                    counselorAdapter = new CounselorAdapter(Manager_counselorsActivity.this, mCounselors);
                    recyclerVListCounselor.setAdapter(counselorAdapter);
                    recyclerVListCounselor.setLayoutManager(new LinearLayoutManager(Manager_counselorsActivity.this));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setControl() {
        imageButtonBackSpace = findViewById(R.id.icon_backSpace_manager_counselors);
        imageButtonAddNewCounselors = findViewById(R.id.icon_add_news_counselors);
        recyclerVListCounselor = findViewById(R.id.rcy_list_counselor);
    }
}