package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerheathcareapp.Model.Admin;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    CardView cv_Admin, cv_Counselors, cv_logOut;
    ImageButton imageButtonLogOut;
    TextView tv_fullNameAdmin, tv_position;

    private ArrayList<Admin> mDataAdmin = new ArrayList<>();
    // Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dataRefAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setControl();
        mAuth = FirebaseAuth.getInstance();
        dataRefAdmin = firebaseDatabase.getReference("Admins");
        setEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        } else {
            Query query = dataRefAdmin.orderByChild("id_admin").equalTo(currentUser.getUid());
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(currentUser.getUid())) {
                        dataRefAdmin.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                mDataAdmin.clear();
                                for (DataSnapshot ds : snapshot.getChildren()) {
                                    Admin admin = ds.getValue(Admin.class);
                                    if (admin.getId_admin().equals(currentUser.getUid())) {
                                        mDataAdmin.add(admin);
                                    }
                                }
                                try {
                                    if (mDataAdmin != null) {
                                        Toast.makeText(SettingsActivity.this, mDataAdmin.get(0).toString(), Toast.LENGTH_SHORT).show();
                                        tv_fullNameAdmin.setText(mDataAdmin.get(0).getFullName());
                                        if (mDataAdmin.get(0).getRole().equals("1")) {
                                            tv_position.setText("Manager");

                                        } else {
                                            tv_position.setText("Admin");
                                            cv_Admin.setVisibility(View.GONE);
                                        }
                                    }
                                } catch (Exception ex) {
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    } else {

                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
    }

    private void setEvent() {


        bottomNavigationView.setSelectedItemId(R.id.Settings);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.Message:
                        Intent intent = new Intent(getApplicationContext(), MessageActivity.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.News:
                        Intent intent2 = new Intent(getApplicationContext(), NewsActivity.class);
                        startActivity(intent2);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Feedback:
                        Intent intent1 = new Intent(getApplicationContext(), FeedbackActivity.class);
                        startActivity(intent1);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Settings:

                        return true;
                }
                return true;
            }
        });
        cv_Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, ManagerAdminActivity.class));
            }
        });

        cv_Counselors.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SettingsActivity.this, Manager_counselorsActivity.class));
            }
        });

        cv_logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle(R.string.ask_logout);
                builder.setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

                builder.setNegativeButton("LogOut", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        mAuth.signOut();
                        startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                    }
                });
                builder.show();
            }
        });
    }

    private void showInfoAdmin() {
        FirebaseUser user = mAuth.getCurrentUser();

    }

    private void setControl() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cv_Admin = findViewById(R.id.cv_admin_settings);
        cv_Counselors = findViewById(R.id.cv_counselors_settings);
        cv_logOut = findViewById(R.id.cv_logout_settings);
        imageButtonLogOut = findViewById(R.id.icon_exit_app_settings);
        tv_fullNameAdmin = findViewById(R.id.tv_fullName_admin_settings);
        tv_position = findViewById(R.id.tv_position_admin_settings);

    }
}