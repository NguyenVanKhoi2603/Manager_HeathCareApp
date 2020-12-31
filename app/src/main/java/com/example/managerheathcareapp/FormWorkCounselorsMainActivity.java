package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerheathcareapp.Model.Admin;
import com.example.managerheathcareapp.Model.Counselor;
import com.example.managerheathcareapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hbb20.CountryCodePicker;

import java.util.Locale;

public class FormWorkCounselorsMainActivity extends AppCompatActivity {
    ImageView imageViewBackSpace, imageViewGetImageGallery, imageViewGetImageCamera;
    Button btn_submit;
    TextInputEditText txt_firstName, txt_lastName, txt_email, txt_password, txt_introduce, txt_position, txt_phone;
    RadioButton radioButton_man, radioButton_women, radioButton_other, radioButton_fitness, radioButton_health, radioButton_nutrition;
    ImageView imageView;
    CardView cv_birthDate;
    TextView tv_birthDate;
    CountryCodePicker countryCodePicker;
    DatePicker datePicker;

    private Boolean checkHide = true;
    // Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference CounselorRef = database.getReference("Counselors");

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form_work_counselors_main);
        setControl();
        mAuth = FirebaseAuth.getInstance();
        setEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setEvent() {
        cv_birthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkHide) {
                    datePicker.setVisibility(View.VISIBLE);
                } else {
                    datePicker.setVisibility(View.GONE);
                }
                checkHide = !checkHide;
            }
        });
        datePicker.setOnDateChangedListener(new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                String birthDate = "";
                int day = i2;
                int month = i1 + 1;
                int year = i;
                birthDate += String.valueOf(day) + '/' + String.valueOf(month) + '/' + String.valueOf(year);
                tv_birthDate.setText(birthDate);
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txt_email.getText().toString().trim();
                String password = txt_password.getText().toString().trim();
                String phone = txt_phone.getText().toString().trim();
                String firstName = txt_firstName.getText().toString().trim();
                String lastName = txt_lastName.getText().toString().trim();
                String introduce = txt_introduce.getText().toString().trim();
                String gender = "0";
                String Category = "0";
                String age = tv_birthDate.getText().toString().trim();
                String location = countryCodePicker.getDefaultCountryName();
                String position = txt_position.getText().toString().trim();
                if (radioButton_man.isChecked()) {
                    gender = "0";
                } else {
                    if (radioButton_women.isChecked()) {
                        gender = "1";
                    } else {
                        gender = "2";
                    }
                }

                if (radioButton_health.isChecked()) {
                    Category = "0";
                } else {
                    if (radioButton_fitness.isChecked()) {
                        Category = "1";
                    } else {
                        Category = "2";
                    }
                }

                String finalGender = gender;
                try {
                    registerAdmin(email, password, phone, firstName, lastName,Category, introduce, finalGender, age, location, position);
                } catch (Exception exception) {
                    Toast.makeText(FormWorkCounselorsMainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerAdmin(String email, String password, String phone, String firstName, String lastName, String category, String introduce, String gender, String age, String location, String position) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(FormWorkCounselorsMainActivity.this, "Authentication Successful.",
                                    Toast.LENGTH_SHORT).show();
                            String id_user = mAuth.getUid();

                            User userCounselor = new User(System.currentTimeMillis() + "", age, gender, id_user, firstName, lastName, "", email, phone, location);
                            userRef.child(id_user).setValue(userCounselor);

                            Counselor counselor = new Counselor(id_user, id_user, category, introduce, position, 0);
                            CounselorRef.child(id_user).setValue(counselor);
                            onBackPressed();
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(FormWorkCounselorsMainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void setControl() {
        datePicker = findViewById(R.id.date_picker_form_counselor);
        cv_birthDate = findViewById(R.id.cv_birthDate_form_counselor);
        tv_birthDate = findViewById(R.id.tv_birthDate_form_counselor);
        countryCodePicker = findViewById(R.id.countryCodeHolder_form_counselor);
        imageViewBackSpace = findViewById(R.id.icon_backSpace_list_counselor);
        imageViewGetImageCamera = findViewById(R.id.imageCamera_form_counselor);
        imageViewGetImageGallery = findViewById(R.id.imageGallery_form_counselor);
        btn_submit = findViewById(R.id.btn_submit_form_counselor);
        txt_email = findViewById(R.id.txt_email_form_counselor);
        txt_introduce = findViewById(R.id.txt_introduce_form_counselor);
        txt_password = findViewById(R.id.txt_password_form_counselor);
        txt_phone = findViewById(R.id.txt_phone_form_counselor);
        txt_position = findViewById(R.id.txt_position_form_counselor);
        radioButton_man = findViewById(R.id.rd_man_form_counselor);
        radioButton_other = findViewById(R.id.rd_other_form_counselor);
        radioButton_women = findViewById(R.id.rd_women_form_counselor);
        imageView = findViewById(R.id.imageView_form_counselor);
        txt_firstName = findViewById(R.id.txt_firstName_form_counselor);
        txt_lastName = findViewById(R.id.txt_lastName_form_counselor);
        radioButton_fitness = findViewById(R.id.rd_fitness_form_counselor);
        radioButton_health = findViewById(R.id.rd_health_form_counselor);
        radioButton_nutrition = findViewById(R.id.rd_nutrition_form_counselor);
    }
}