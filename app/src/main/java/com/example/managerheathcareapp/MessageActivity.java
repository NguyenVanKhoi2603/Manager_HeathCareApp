package com.example.managerheathcareapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.managerheathcareapp.Adapter.ChatListAdapter;
import com.example.managerheathcareapp.Model.ChatList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class MessageActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    RecyclerView recyclerViewChatList;
    ArrayList<ChatList> dataChatList = new ArrayList<>();
    ChatListAdapter chatListAdapter;
    // Firebase
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference chatListRef = database.getReference("ChatList");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        setControl();
        setEvent();
    }

    private void showListChat(String user_id) {
        DatabaseReference listU = chatListRef.child(user_id);
        listU.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataChatList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ChatList chatList = ds.getValue(ChatList.class);
                    dataChatList.add(chatList);
                }
                if (dataChatList != null) {
                    try {
                        chatListAdapter = new ChatListAdapter(MessageActivity.this, dataChatList);
                        recyclerViewChatList.setAdapter(chatListAdapter);
                        recyclerViewChatList.setLayoutManager(new LinearLayoutManager(MessageActivity.this));
                    } catch (Exception ex) {

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setEvent() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String user_id = currentUser.getUid();
        showListChat(user_id);
        bottomNavigationView.setSelectedItemId(R.id.Message);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                item.setVisible(false);
                switch (item.getItemId()) {
                    case R.id.Message:
                        return true;
                    case R.id.News:
                        Intent intent = new Intent(getApplicationContext(), NewsAndNutritionActivity.class);
                        startActivity(intent);
                        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        overridePendingTransition(0, 0);
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
    }

//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
//        super.onPrepareOptionsMenu(menu);
//        if (menu.findItem(R.id.action_messages) != null)
//            menu.findItem(R.id.action_messages).setVisible(false);
//    }




    private void setControl() {
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        recyclerViewChatList = findViewById(R.id.rcy_chatList);
    }
}