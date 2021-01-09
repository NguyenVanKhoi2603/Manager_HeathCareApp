package com.example.managerheathcareapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.util.ArrayList;

public class ConversationDetailActivity extends AppCompatActivity {
    ImageButton imageButtonBackSpace, imageButtonSendMessage, imageButtonImage, imageButtonCloseImageSend;
    EditText txt_message;
    RecyclerView rcy_ListMessage;
    TextView tv_userName;
    LinearLayout lnl_showImage_send;
    ImageView imageViewUserReceiver, imageSend;
    String user_id = "";
    String id_image = "";
    String url_image = "";
    Boolean CheckImageSend = false;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri imgUri;
    private StorageTask uploadTask;
    KeyBoardApp keyBoardApp = new KeyBoardApp();
    // Firebase sender, receiver
    private FirebaseAuth mAuth;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference ref = database.getReference();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference chatListRef = database.getReference("ChatList");
    DatabaseReference dbMessageRef = database.getReference("Messages");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    StorageReference ImageSendStorageRef = FirebaseStorage.getInstance().getReference("images/message");
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

                    try {
                        String message = txt_message.getText().toString().trim();
                        sendMessage(message);
                        keyBoardApp.closeKeyBoard(ConversationDetailActivity.this);
                        lnl_showImage_send.setVisibility(View.GONE);
                        id_image = "";
                        CheckImageSend = false;
                    } catch (Exception exception) {

                    }


            }
        });
        imageButtonCloseImageSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lnl_showImage_send.setVisibility(View.GONE);
                url_image = "";
                id_image = "";
                CheckImageSend = false;
            }
        });
        imageButtonImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        imageButtonBackSpace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSeenMessage(id_receiver, id_sender);
                onBackPressed();
            }
        });
    }

    private void setSeenMessage(String id_receiver, String id_sender) {
        dbMessageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Message mes = ds.getValue(Message.class);
                    String ID_Mess = ds.getKey();
                    if (mes.getReceiver().equals(id_sender) && mes.getSender().equals(id_receiver)) {
                        Message message = new Message(mes.getSender(), mes.getReceiver(), mes.getMessage(), mes.getImage(), mes.getTimestamp(), true);
                        dbMessageRef.child(ID_Mess).setValue(message);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
                    if (!mUsers.get(0).getImage_id().equals("")) {
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
                messageAdapter.notifyDataSetChanged();
                rcy_ListMessage.setAdapter(messageAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendMessage(String message) {
        if (CheckImageSend == true) {
            id_image = System.currentTimeMillis() + "." + getExtension(imgUri);
        } else {
            id_image = "";
        }

        if (uploadTask != null && uploadTask.isInProgress()) {
            Toast.makeText(ConversationDetailActivity.this, "In progress upload!", Toast.LENGTH_SHORT).show();
        } else {
            try {
                uploadFile(id_image);
            } catch (Exception exception) {

            }
        }
        String timestamp = String.valueOf(System.currentTimeMillis());
        DatabaseReference mesRef = ref.child("Messages");
        Message mes1 = new Message(id_sender, id_receiver, message, id_image, timestamp, false);
        mesRef.push().setValue(mes1);
        txt_message.setText("");
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
                imageSend.setImageBitmap(bitmap);
                lnl_showImage_send.setVisibility(View.VISIBLE);
                CheckImageSend = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void setControl() {
        imageButtonBackSpace = findViewById(R.id.icon_backspace_conversation_detail);
        imageButtonImage = findViewById(R.id.btn_image_message_conversation_detail);
        imageButtonSendMessage = findViewById(R.id.btn_send_message_conversation_detail);
        txt_message = findViewById(R.id.editText_send_message_conversation_detail);
        rcy_ListMessage = findViewById(R.id.rcy_conversation_detail);
        tv_userName = findViewById(R.id.title_toolbar_conversation_detail);
        imageViewUserReceiver = findViewById(R.id.imgRows_conversation_detail);
        lnl_showImage_send = findViewById(R.id.lnl_showImageSend_conversation_detail);
        imageSend = findViewById(R.id.image_send_conversation_detail);
        imageButtonCloseImageSend = findViewById(R.id.btn_delete_image_send);
    }
}