package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.managerheathcareapp.Adapter.NewsAndNutritionAdapter;
import com.example.managerheathcareapp.Model.Admin;
import com.example.managerheathcareapp.Model.New;
import com.example.managerheathcareapp.Model.NewAndNutrition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewsAndNutritionActivity extends AppCompatActivity {
    ImageButton imageButtonCreateNew;
    BottomNavigationView bottomNavigationView;
    NewsAndNutritionAdapter newsAndNutritionAdapter;
    ArrayList<New> dataNews = new ArrayList<>();
    ArrayList<NewAndNutrition> dataPost = new ArrayList<>();
    RecyclerView recyclerViewNews;
    Spinner spinnerCategory;
    EditText txt_search_post;
    String role = "counselor";
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private CollectionReference categoryRef = db.collection("News");
    DatabaseReference adminRef = database.getReference("Admins");
    private CollectionReference postsNewAndNutritionRef = db.collection("PostsNewAndNutrition");
    private ListenerRegistration listenerCate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_nutrition);
        setControl();
        mAuth = FirebaseAuth.getInstance();
        setEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            startActivity(new Intent(NewsAndNutritionActivity.this, LoginActivity.class));
        } else {

        }
        bottomNavigationView.setSelectedItemId(R.id.News);
        getAllData();
    }

    private void getAllData() {
        postsNewAndNutritionRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                dataPost.clear();
                try {
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        NewAndNutrition post = documentSnapshot.toObject(NewAndNutrition.class);
                        post.setId_post(documentSnapshot.getId());
                        String TITLE = post.getTitle();
                        String ID = post.getId_post();
                        String DES = post.getDescription();
                        String URL = post.getUrl();
                        String CATEGORY = post.getCategory();
                        String IMAGE_ID = post.getImage_id();
                        String TIMESTAMP = post.getTimestamp();
                        dataPost.add(new NewAndNutrition(ID, TITLE, DES, URL, IMAGE_ID, CATEGORY, TIMESTAMP));
                    }
                    newsAndNutritionAdapter = new NewsAndNutritionAdapter(dataPost, NewsAndNutritionActivity.this);
                    recyclerViewNews.setAdapter(newsAndNutritionAdapter);
                } catch (Exception ex) {

                }

            }
        });
    }

    private void setEvent() {

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user_id = currentUser.getUid();
        adminRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Admin admin = ds.getValue(Admin.class);
                    if (admin.getId_admin().equals(user_id)) {
                        role = "";
                    }
                }
                if (role.equals("counselor")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(NewsAndNutritionActivity.this);
                    builder.setTitle("Notification");
                    builder.setMessage("Only Admin has access!");
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
        ArrayList<String> Category = new ArrayList<>();
        Category.add("All");
        Category.add("News");
        Category.add("Nutrition");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, Category);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(arrayAdapter);
        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String tutorialsName = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(adapterView.getContext(), "Selected: " + tutorialsName, Toast.LENGTH_SHORT).show();
                String cate = String.valueOf(i - 1);
                if (i == 0) {
                    getAllData();
                } else {
                    if (spinnerCategory.getSelectedItem().toString().equals("News")) {
                        searchPostByCategory("news");
                    } else {
                        if (spinnerCategory.getSelectedItem().toString().equals("Nutrition")) {
                            searchPostByCategory("nutrition");
                        } else {
                            getAllData();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        txt_search_post.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    if (spinnerCategory.getSelectedItem().toString().equals("News")) {
                        searchPostByKeyAndCategory(charSequence.toString(), "news");
                    } else {
                        if (spinnerCategory.getSelectedItem().toString().equals("Nutrition")) {
                            searchPostByKeyAndCategory(charSequence.toString(), "nutrition");
                        } else {
                            searchPostByKey(charSequence.toString());
                        }
                    }
                } else {
                    if (spinnerCategory.getSelectedItem().toString().equals("News")) {
                        searchPostByCategory("news");
                    } else {
                        if (spinnerCategory.getSelectedItem().toString().equals("Nutrition")) {
                            searchPostByCategory("nutrition");
                        } else {
                            getAllData();
                        }
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        try {
            newsAndNutritionAdapter = new NewsAndNutritionAdapter(dataPost, NewsAndNutritionActivity.this);
            recyclerViewNews.setAdapter(newsAndNutritionAdapter);
            recyclerViewNews.setLayoutManager(new LinearLayoutManager(NewsAndNutritionActivity.this));
        } catch (Exception ex) {

        }

        bottomNavigationView.setSelectedItemId(R.id.News);
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
                        return true;
                    case R.id.Feedback:
                        Intent intent1 = new Intent(getApplicationContext(), FeedbackActivity.class);
                        startActivity(intent1);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        overridePendingTransition(0, 0);
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

        imageButtonCreateNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(NewsAndNutritionActivity.this, CreateNewsActivity.class));
            }
        });
    }

    private void searchPostByKey(String key) {
        postsNewAndNutritionRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dataPost.clear();
                for (DocumentSnapshot ds : task.getResult()) {
                    NewAndNutrition newAndNutrition = ds.toObject(NewAndNutrition.class);
                    if (newAndNutrition.getTitle().toLowerCase().contains(key.toLowerCase()) || newAndNutrition.getDescription().toLowerCase().contains(key.toLowerCase())) {
                        dataPost.add(newAndNutrition);
                    }

                }
                newsAndNutritionAdapter = new NewsAndNutritionAdapter(dataPost, NewsAndNutritionActivity.this);
                recyclerViewNews.setAdapter(newsAndNutritionAdapter);
            }
        });
    }

    private void searchPostByKeyAndCategory(String key, String category) {
        postsNewAndNutritionRef.whereEqualTo("category", category).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dataPost.clear();
                for (DocumentSnapshot ds : task.getResult()) {
                    NewAndNutrition newAndNutrition = ds.toObject(NewAndNutrition.class);
                    if (newAndNutrition.getTitle().toLowerCase().contains(key.toLowerCase()) || newAndNutrition.getDescription().toLowerCase().contains(key.toLowerCase())) {
                        dataPost.add(newAndNutrition);
                    }

                }
                newsAndNutritionAdapter = new NewsAndNutritionAdapter(dataPost, NewsAndNutritionActivity.this);
                recyclerViewNews.setAdapter(newsAndNutritionAdapter);
            }
        });
    }

    private void searchPostByCategory(String category) {
        postsNewAndNutritionRef.whereEqualTo("category", category).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dataPost.clear();
                for (DocumentSnapshot ds : task.getResult()) {
                    NewAndNutrition newAndNutrition = ds.toObject(NewAndNutrition.class);
                    dataPost.add(newAndNutrition);

                }
                newsAndNutritionAdapter = new NewsAndNutritionAdapter(dataPost, NewsAndNutritionActivity.this);
                recyclerViewNews.setAdapter(newsAndNutritionAdapter);
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case 121:
                String[] str = item.getTitle().toString().split("-");
                item.setTitle(str[0]);
                categoryRef.document(str[1]).delete();
                return true;
            case 122:

                Toast.makeText(getApplicationContext(), "Update", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void setControl() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerViewNews = findViewById(R.id.rcl_news);
        imageButtonCreateNew = findViewById(R.id.icon_create_news);
        spinnerCategory = findViewById(R.id.sp_category_post);
        txt_search_post = findViewById(R.id.txt_search_post);
    }
}