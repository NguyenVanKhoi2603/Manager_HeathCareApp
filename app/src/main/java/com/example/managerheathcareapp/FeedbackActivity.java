package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.managerheathcareapp.Adapter.FeedbackAdapter;
import com.example.managerheathcareapp.Model.Admin;
import com.example.managerheathcareapp.Model.Feedback;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedbackActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    RecyclerView rcy_list_feedback;
    RatingBar ratingBarAverageRatting;
    TextView tv_total_feedback;
    LinearLayout lnl_feedback;
    ArrayList<Feedback> dataFeedback = new ArrayList<>();
    FeedbackAdapter feedbackAdapter;
    String role = "";
    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference feedbackRef = database.getReference("Feedback");
    DatabaseReference adminRef = database.getReference("Admins");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setControl();
        setEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void setEvent() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String ID_USER = currentUser.getUid().toString();
        String user_id = currentUser.getUid();
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Admin admin = ds.getValue(Admin.class);
                    if (admin.getId_admin().equals(user_id)) {
                        role = "admin";

                    }
                }
                if (role.equals("admin")) {
                    lnl_feedback.setVisibility(View.GONE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(FeedbackActivity.this);
                    builder.setTitle("Notification");
                    builder.setMessage("Only counselor has access!");
                    builder.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    });
                    builder.show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        showDataFeedback(ID_USER);
        bottomNavigationView.setSelectedItemId(R.id.Feedback);
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
                        Intent intent1 = new Intent(getApplicationContext(), NewsAndNutritionActivity.class);
                        startActivity(intent1);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.Feedback:
                        return true;
                    case R.id.Settings:
                        Intent intent2 = new Intent(getApplicationContext(), SettingsActivity.class);
                        startActivity(intent2);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        overridePendingTransition(0, 0);
                        return true;
                }
                return true;
            }
        });
    }

    private void showDataFeedback(String id_user) {
        feedbackRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataFeedback.clear();
                int countFeedback = 0;
                float totalRatting = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Feedback feedback = ds.getValue(Feedback.class);
                    if (feedback.getCounselor_id().equals(id_user)) {
                        dataFeedback.add(feedback);
                        countFeedback += 1;
                        totalRatting += feedback.getRatting();
                    }
                }
                if (dataFeedback != null) {
                    feedbackAdapter = new FeedbackAdapter(FeedbackActivity.this, dataFeedback);
                    rcy_list_feedback.setAdapter(feedbackAdapter);
                    rcy_list_feedback.setLayoutManager(new LinearLayoutManager(FeedbackActivity.this));
                    tv_total_feedback.setText(countFeedback + "");
                    ratingBarAverageRatting.setRating(totalRatting / countFeedback);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setControl() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        ratingBarAverageRatting = findViewById(R.id._ratting_bar_average_rating);
        rcy_list_feedback = findViewById(R.id.rcy_list_feedback);
        tv_total_feedback = findViewById(R.id.tv_total_feedback);
        lnl_feedback = findViewById(R.id.lnl_feedback_info_feedback);

    }
}