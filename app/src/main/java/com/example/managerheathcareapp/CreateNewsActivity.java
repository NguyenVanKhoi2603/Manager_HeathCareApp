package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.managerheathcareapp.Model.New;
import com.example.managerheathcareapp.Model.NewAndNutrition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class CreateNewsActivity extends AppCompatActivity {
    ImageButton imageButtonBackSpace, imageButtonImage;
    Button btn_submit;
    RadioButton rb_news, rb_nutrition;
    ImageView imageViewCreate;
    TextInputEditText txt_title, txt_des, txt_url;

    // Date
    public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";
    public static final String DATE_FORMATCreateAt = "yyyy-MM-dd-HH:mm:ss";
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference postAndNutritionRef = database.getReference("PostsNewAndNutrition");
    private StorageReference storageRef = FirebaseStorage.getInstance().getReference();
    private Uri imgUri;
    private StorageTask uploadTask;

    private final int PICK_IMAGE_REQUEST = 71;

    String id_image = "";
    String url_image = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        setControl();
        setEvent();

        storageRef = FirebaseStorage.getInstance().getReference("images/news");
    }


    private void setEvent() {
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id_image = System.currentTimeMillis() + "." + getExtension(imgUri);
                String ID = System.currentTimeMillis() + "";
                String title = txt_title.getText().toString();
                String des = txt_des.getText().toString();
                String url = txt_url.getText().toString();
                String category = "news";
                if (rb_news.isChecked() == true) {
                    category = "news";
                } else {
                    category = "nutrition";
                }
                NewAndNutrition newAndNutrition = new NewAndNutrition(ID, title, des, url, id_image, category, ID);
//                postAndNutritionRef.child(ID).setValue(newAndNutrition);
//                if (uploadTask != null && uploadTask.isInProgress()) {
//                    Toast.makeText(CreateNewsActivity.this, "In progress upload!", Toast.LENGTH_SHORT).show();
//                } else {
//                    uploadFile(id_image);
//                    onBackPressed();
//                }
                db.collection("PostsNewAndNutrition").document(ID).set(newAndNutrition)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                uploadFile(id_image);
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (uploadTask != null && uploadTask.isInProgress()) {
                                    Toast.makeText(CreateNewsActivity.this, "In progress upload!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(CreateNewsActivity.this, "In progress upload!", Toast.LENGTH_SHORT).show();
                                    onBackPressed();
                                }
                            }
                        });
            }
        });
        imageButtonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        imageButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    private void uploadFile(String id_image) {
        StorageReference ref = storageRef.child(id_image);
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
                imageViewCreate.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//
//        if (requestCode == 1 && requestCode == RESULT_OK && data != null && data.getData() != null) {
//            imgUri = data.getData();
//            imageViewCreate.setImageURI(imgUri);
//
//        }
//        super.onActivityResult(requestCode, resultCode, data);
//    }

    private void setControl() {
        imageButtonBackSpace = findViewById(R.id.icon_backSpace_create_news);
        btn_submit = findViewById(R.id.btn_submit_create_news);
        txt_title = findViewById(R.id.txt_title_news);
        txt_des = findViewById(R.id.txt_des_news);
        txt_url = findViewById(R.id.txt_url_news);
        imageButtonImage = findViewById(R.id.img_choose_create_news);
        imageViewCreate = findViewById(R.id.image_view_news_create);
        rb_news = findViewById(R.id.rd_news_create_post);
        rb_nutrition = findViewById(R.id.rd_nutrition_create_post);
    }


    public static String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date today = Calendar.getInstance().getTime();
        return dateFormat.format(today);
    }
}