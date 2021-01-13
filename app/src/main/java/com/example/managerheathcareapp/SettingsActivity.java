package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerheathcareapp.Adapter.FeedbackAdapter;
import com.example.managerheathcareapp.Model.Admin;
import com.example.managerheathcareapp.Model.Counselor;
import com.example.managerheathcareapp.Model.Feedback;
import com.example.managerheathcareapp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SettingsActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    CardView cv_Admin, cv_Counselors, cv_logOut;
    ImageButton imageButtonLogOut;
    ImageView imageViewAvatarCounselor;

    RatingBar ratingBar_counselor;
    TextView tv_fullNameAdmin, tv_position, tv_name_counselor, tv_position_counselor, tv_introduce_counselor, tv_total_feedback_counselor, tv_edit_counselor;
    LinearLayout lnl_option_settings, lnl_info_admin, lnl_info_counselor;
    ArrayList<Feedback> dataFeedback = new ArrayList<>();
    private ArrayList<Admin> mDataAdmin = new ArrayList<>();
    private ArrayList<Counselor> dataCounselor = new ArrayList<>();
    private ArrayList<User> dataUser = new ArrayList<>();
    // Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    DatabaseReference dataRefAdmin;
    DatabaseReference counselorRef = firebaseDatabase.getReference("Counselors");
    DatabaseReference userRef = firebaseDatabase.getReference("Users");
    DatabaseReference feedbackRef = firebaseDatabase.getReference("Feedback");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

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
        bottomNavigationView.setSelectedItemId(R.id.Settings);
        if (currentUser == null) {
            startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
        } else {
            counselorRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    dataCounselor.clear();
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Counselor counselor = ds.getValue(Counselor.class);
                        if (counselor.getId_counselor().equals(currentUser.getUid())) {
                            dataCounselor.add(counselor);
                        }
                    }
                    if (dataCounselor != null) {
                        try {
                            lnl_info_counselor.setVisibility(View.VISIBLE);
                            tv_position_counselor.setText("#" + dataCounselor.get(0).getPosition_counselor());
                            tv_introduce_counselor.setText(dataCounselor.get(0).getIntroduce());
                            userRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    dataUser.clear();
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        User user = ds.getValue(User.class);
                                        if (user.getUser_id().equals(currentUser.getUid())) {
                                            dataUser.add(user);
                                        }
                                    }
                                    if (dataUser != null) {
                                        try {
                                            tv_name_counselor.setText(dataUser.get(0).getFirst_name() + " " + dataUser.get(0).getLast_name());
                                            String imgUser = dataUser.get(0).getImage_id();
                                            try {
                                                StorageReference islandRef = storageRef.child("images/user/" + imgUser);
                                                final long ONE_MEGABYTE = 1024 * 1024;
                                                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                                    @Override
                                                    public void onSuccess(byte[] bytes) {
                                                        // Data for "images/island.jpg" is returns, use this as needed
                                                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                                        imageViewAvatarCounselor.setImageBitmap(bitmap);
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception exception) {
                                                        // Handle any errors
                                                    }
                                                });

                                            } catch (Exception ex) {

                                            }
                                        } catch (Exception exception) {

                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                            feedbackRef.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    dataFeedback.clear();
                                    int countFeedback = 0;
                                    float totalRatting = 0;
                                    for (DataSnapshot ds : snapshot.getChildren()) {
                                        Feedback feedback = ds.getValue(Feedback.class);
                                        if (feedback.getCounselor_id().equals(currentUser.getUid())) {
                                            dataFeedback.add(feedback);
                                            countFeedback += 1;
                                            totalRatting += feedback.getRatting();
                                        }
                                    }
                                    if (dataFeedback != null) {
                                        tv_total_feedback_counselor.setText(countFeedback + "");
                                        ratingBar_counselor.setRating(totalRatting / countFeedback);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        } catch (Exception exception) {

                        }

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            //
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
                                        lnl_info_admin.setVisibility(View.VISIBLE);
                                        lnl_option_settings.setVisibility(View.VISIBLE);
                                        lnl_info_counselor.setVisibility(View.GONE);
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
                        Intent intent2 = new Intent(getApplicationContext(), NewsAndNutritionActivity.class);
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
        tv_edit_counselor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FormWorkCounselorsMainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("ID_COUNSELOR", mAuth.getCurrentUser().getUid());
                intent.putExtras(bundle);
                startActivity(intent);
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


    private void setControl() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        cv_Admin = findViewById(R.id.cv_admin_settings);
        cv_Counselors = findViewById(R.id.cv_counselors_settings);
        cv_logOut = findViewById(R.id.cv_logout_settings);
        imageButtonLogOut = findViewById(R.id.icon_exit_app_settings);
        tv_fullNameAdmin = findViewById(R.id.tv_fullName_admin_settings);
        tv_position = findViewById(R.id.tv_position_admin_settings);
        lnl_option_settings = findViewById(R.id.lnl_option_settings);
        imageViewAvatarCounselor = findViewById(R.id.img_avatar_counselor_profile_settings);

        tv_edit_counselor = findViewById(R.id.tv_edit_counselor_settings);
        ratingBar_counselor = findViewById(R.id._ratting_bar_average_rating_counselor_Settings);
        tv_name_counselor = findViewById(R.id.tv_fullName_counselor_settings);
        tv_position_counselor = findViewById(R.id.tv_position_counselor_settings);
        tv_introduce_counselor = findViewById(R.id.tv_introduce_counselor_settings);
        tv_total_feedback_counselor = findViewById(R.id.tv_total_feedback_counselor_settings);
        lnl_info_admin = findViewById(R.id.lnl_info_admin);
        lnl_info_counselor = findViewById(R.id.lnl_info_counselor);

    }
}