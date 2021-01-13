package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.managerheathcareapp.Model.Counselor;
import com.example.managerheathcareapp.Model.Feedback;
import com.example.managerheathcareapp.Model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.hbb20.CountryCodePicker;

import java.io.IOException;
import java.util.ArrayList;

public class FormWorkCounselorsMainActivity extends AppCompatActivity {
    ImageButton imageViewBackSpace, imageViewGetImageGallery, imageViewGetImageCamera, imageButton_Delete;
    Button btn_submit;
    TextInputEditText txt_firstName, txt_lastName, txt_email, txt_password, txt_introduce, txt_position, txt_phone;
    RadioButton radioButton_man, radioButton_women, radioButton_other, radioButton_fitness, radioButton_health, radioButton_nutrition;
    ImageView imageView;
    CardView cv_birthDate, cv_image_avatar;
    TextView tv_birthDate;
    CountryCodePicker countryCodePicker;
    DatePicker datePicker;
    String id_image = "";
    String url_image = "";
    Boolean CheckImageSend = false;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imgUri;
    private StorageTask uploadTask;
    private Boolean checkHide = true;
    String ID_COUNSELOR_UPDATE = "";
    private ArrayList<Counselor> dataCounselor = new ArrayList<>();
    private ArrayList<User> dataUser = new ArrayList<>();
    // Firebase
    private FirebaseAuth mAuth;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference CounselorRef = database.getReference("Counselors");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference ImageSendStorageRef = FirebaseStorage.getInstance().getReference("images/user/");

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
        try {
            ID_COUNSELOR_UPDATE = getIntent().getExtras().getString("ID_COUNSELOR");
        } catch (Exception e) {
        }

        if (!ID_COUNSELOR_UPDATE.equals("")){
            Toast.makeText(FormWorkCounselorsMainActivity.this, "Update",
                    Toast.LENGTH_SHORT).show();
        }
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
        imageViewGetImageGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        imageButton_Delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                url_image = "";
                id_image = "";
                CheckImageSend = false;
                imageButton_Delete.setVisibility(View.GONE);
                cv_image_avatar.setVisibility(View.GONE);
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
                if (CheckImageSend == true) {
                    id_image = System.currentTimeMillis() + "." + getExtension(imgUri);
                } else {
                    id_image = "";
                }

                if (uploadTask != null && uploadTask.isInProgress()) {
                    Toast.makeText(FormWorkCounselorsMainActivity.this, "In progress upload!", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        uploadFile(id_image);
                    } catch (Exception exception) {

                    }
                }
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
                    registerCounselor(email, password, phone, firstName, lastName, Category, id_image, introduce, finalGender, age, location, position);
                    Toast.makeText(FormWorkCounselorsMainActivity.this, "Login again", Toast.LENGTH_SHORT).show();
                    mAuth.signOut();
                    if (mAuth.getCurrentUser() == null) {
                        startActivity(new Intent(FormWorkCounselorsMainActivity.this, LoginActivity.class));
                    }
                } catch (Exception exception) {
                    Toast.makeText(FormWorkCounselorsMainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void registerCounselor(String email, String password, String phone, String firstName, String lastName, String category, String id_image, String introduce, String gender, String age, String location, String position) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            String id_user = mAuth.getUid();
                            User userCounselor = new User(System.currentTimeMillis() + "", age, gender, id_user, firstName, lastName, id_image, email, phone, location);
                            userRef.child(id_user).setValue(userCounselor);
                            Counselor counselor = new Counselor(id_user, id_user, category, introduce, position, 0);
                            CounselorRef.child(id_user).setValue(counselor);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(FormWorkCounselorsMainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void uploadFile(String id_image) {
        StorageReference ref = ImageSendStorageRef.child(id_image);
        uploadTask = ref.putFile(imgUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        url_image = taskSnapshot.getUploadSessionUri().toString();
                        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

    }

    private String getExtension(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(cr.getType(uri));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imgUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imgUri);
                imageView.setImageBitmap(bitmap);
                CheckImageSend = true;
                cv_image_avatar.setVisibility(View.VISIBLE);
                imageButton_Delete.setVisibility(View.VISIBLE);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
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
        cv_image_avatar = findViewById(R.id.cv_image_avatar);
        imageButton_Delete = findViewById(R.id.delete_image_form_counselor);
    }
}