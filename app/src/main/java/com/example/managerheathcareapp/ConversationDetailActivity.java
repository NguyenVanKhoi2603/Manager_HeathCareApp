package com.example.managerheathcareapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerheathcareapp.Adapter.MessageAdapter;
import com.example.managerheathcareapp.Functions.KeyBoardApp;
import com.example.managerheathcareapp.Model.ChatList;
import com.example.managerheathcareapp.Model.Message;
import com.example.managerheathcareapp.Model.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ConversationDetailActivity extends AppCompatActivity {
    ImageButton imageButtonBackSpace, imageButtonSendMessage, imageButtonImage;
    EditText txt_message;
    RecyclerView rcy_ListMessage;
    TextView tv_userName;
    ImageView imageViewUserReceiver;
    String user_id = "";
    KeyBoardApp keyBoardApp = new KeyBoardApp();
    // Firebase sender, receiver
    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference chatListRef = database.getReference("ChatList");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    String id_sender = "";
    String id_receiver = "";
    //
    ArrayList<User> mUsers = new ArrayList<>();
    ArrayList<Message> dataMessage = new ArrayList<>();
    MessageAdapter messageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_detail);

        setControl();
        //rcy_ListMessage.isAlwaysDrawnWithCacheEnabled() =
        mAuth = FirebaseAuth.getInstance();
        SetEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            id_sender = currentUser.getUid();
        }
//        rcy_ListMessage.post(new Runnable() {
//            @Override
//            public void run() {
//                rcy_ListMessage.scrollToPosition(messageAdapter.getItemCount());
//            }
//        });

    }
    private void scrollToBottom(final RecyclerView recyclerView) {
        // scroll to last item to get the view of last item
        final LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        final RecyclerView.Adapter adapter = recyclerView.getAdapter();
        final int lastItemPosition = adapter.getItemCount() - 1;

        layoutManager.scrollToPositionWithOffset(lastItemPosition, 0);
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                // then scroll to specific offset
                View target = layoutManager.findViewByPosition(lastItemPosition);
                if (target != null) {
                    int offset = recyclerView.getMeasuredHeight() - target.getMeasuredHeight();
                    layoutManager.scrollToPositionWithOffset(lastItemPosition, offset);
                }
            }
        });
    }

    private void SetEvent() {
        id_receiver = getIntent().getExtras().getString("user_id");
        showInfo(id_receiver);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//        linearLayoutManager.onSaveInstanceState();
//        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        //rcy_ListMessage.setHasFixedSize(true);
        messageAdapter = new MessageAdapter(ConversationDetailActivity.this, dataMessage, "");
        rcy_ListMessage.setLayoutManager(linearLayoutManager);

        try {
            readMessage();
            scrollToBottom(rcy_ListMessage);
            Toast.makeText(this, dataMessage.size() + "", Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {

        }

        imageButtonSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (txt_message.getText().length() > 0) {
                    try {
                        String message = txt_message.getText().toString().trim();
                        sendMessage(message);
                        keyBoardApp.closeKeyBoard(ConversationDetailActivity.this);
                    }catch (Exception exception){

                    }

                }
            }
        });
//        txt_message.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                if (charSequence.length() >= 1) {
//                    imageButtonSendMessage.setBackgroundResource(R.drawable.ic_send);
//                } else {
//                    imageButtonSendMessage.setBackgroundResource(R.drawable.ic_send_enable);
//                }
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//
//            }
//        });
        imageButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Error 404", Toast.LENGTH_SHORT).show();
            }
        });
        imageButtonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void showInfo(String receiver) {
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                if (snapshot.hasChild(receiver)) {
                    for (DataSnapshot ds : snapshot.getChildren()) {
                        User user = ds.getValue(User.class);
                        if (user.getUser_id().equals(receiver)) {
                            mUsers.add(user);
                        }
                    }
                    tv_userName.setText(mUsers.get(0).getFirst_name() + " " + mUsers.get(0).getLast_name());
                    String imgUser = mUsers.get(0).getImage_id();
                    if (!mUsers.get(0).getImage_id().equals("")){
                        try {
                            StorageReference islandRef = storageRef.child("images/user/" + imgUser);
                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    imageViewUserReceiver.setImageBitmap(bitmap);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle any errors
                                }
                            });

                        } catch (Exception ex) {

                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readMessage() {
        DatabaseReference dbMessageRef = database.getReference("Messages");
        dbMessageRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataMessage.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Message mes = ds.getValue(Message.class);

                    if (mes.getReceiver().equals(id_receiver) && mes.getSender().equals(id_sender) || mes.getReceiver().equals(id_sender) && mes.getSender().equals(id_receiver)) {
                        dataMessage.add(mes);
                    }
                }
                //messageAdapter = new MessageAdapter(ConversationDetailActivity.this, dataMessage, "");
                messageAdapter.notifyDataSetChanged();
                rcy_ListMessage.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference mesRef = ref.child("Messages");
        Message mes1 = new Message(id_sender, id_receiver, message, "1", timestamp, false);
        mesRef.push().setValue(mes1);
        txt_message.setText("");
//        ChatList chatList = new ChatList(id_receiver);
//        chatListRef.child(id_sender).child(id_receiver).setValue(chatList);
//        ChatList chatList2 = new ChatList(id_sender);
//        chatListRef.child(id_receiver).child(id_sender).setValue(chatList2);
    }

    private void setControl() {
        imageButtonBackSpace = findViewById(R.id.icon_backspace_conversation_detail);
        imageButtonImage = findViewById(R.id.btn_image_message_conversation_detail);
        imageButtonSendMessage = findViewById(R.id.btn_send_message_conversation_detail);
        txt_message = findViewById(R.id.editText_send_message_conversation_detail);
        rcy_ListMessage = findViewById(R.id.rcy_conversation_detail);
        tv_userName = findViewById(R.id.title_toolbar_conversation_detail);
        imageViewUserReceiver = findViewById(R.id.imgRows_conversation_detail);
    }
}