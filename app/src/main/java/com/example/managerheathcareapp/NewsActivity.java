package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.managerheathcareapp.Adapter.NewsAdapter;
import com.example.managerheathcareapp.Model.New;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {
    ImageButton imageButtonCreateNew;
    BottomNavigationView bottomNavigationView;
    NewsAdapter newsAdapter;
    ArrayList<New> dataNews = new ArrayList<>();
    RecyclerView recyclerViewNews;
    // Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth;
    private static final String ID_NEWS = "id_new";
    private static final String TITLE_NEWS = "title_news";
    private static final String DES_NEWS = "description_news";
    private static final String URL_NEWS = "url_news";

    private DocumentReference cateRef = db.document("News/NewsTest");
    private CollectionReference categoryRef = db.collection("News");
    private ListenerRegistration listenerCate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        setControl();
        mAuth = FirebaseAuth.getInstance();
        setEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            startActivity(new Intent(NewsActivity.this, LoginActivity.class));
        }else {

        }
        categoryRef.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    return;
                }
                dataNews.clear();
                try {
                    for (QueryDocumentSnapshot documentSnapshot : value) {
                        New news = documentSnapshot.toObject(New.class);
                        news.setId_new(documentSnapshot.getId());
                        String title = news.getTitle_new();
                        String id = news.getId_new();
                        String des = news.getContent_new();
                        String url = news.getUrl_new();
                        String imageId = news.getImg_new();
                        dataNews.add(new New(id, title, des, url, imageId));
                    }
                    newsAdapter = new NewsAdapter(dataNews, NewsActivity.this);
                    recyclerViewNews.setAdapter(newsAdapter);
                }catch (Exception ex){

                }

            }
        });
    }

    private void setEvent() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Toast.makeText(NewsActivity.this, currentUser.getUid()+"", Toast.LENGTH_SHORT).show();
        try {
            newsAdapter = new NewsAdapter(dataNews, NewsActivity.this);
            recyclerViewNews.setAdapter(newsAdapter);

            recyclerViewNews.setLayoutManager(new LinearLayoutManager(NewsActivity.this));
        }catch (Exception ex){

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
                startActivity(new Intent(NewsActivity.this, CreateNewsActivity.class));
            }
        });
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case 121:
                String[] str = item.getTitle().toString().split("-");
                item.setTitle(str[0]);
                Toast.makeText(getApplicationContext(),  str[1], Toast.LENGTH_SHORT).show();
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
    }
}