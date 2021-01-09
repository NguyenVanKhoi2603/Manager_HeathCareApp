package com.example.managerheathcareapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerheathcareapp.Functions.TimeFunc;
import com.example.managerheathcareapp.Model.Message;
import com.example.managerheathcareapp.Model.User;
import com.example.managerheathcareapp.R;
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
import java.util.Calendar;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private static final int MES_TYPE_LEFT = 0;
    private static final int MES_TYPE_RIGHT = 1;
    Boolean visibleTimestamp = true;
    Context context;
    ArrayList<Message> dataMessage = new ArrayList<>();
    String imageURL;

    private FirebaseUser firebaseUser;
    ArrayList<User> mDataUsers = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users");
    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference storageRef = storage.getReference();
    StorageReference ImageSendStorageRef = FirebaseStorage.getInstance().getReference("images/message");

    public MessageAdapter(Context context, ArrayList<Message> dataMessage, String imageURL) {
        this.context = context;
        this.dataMessage = dataMessage;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MES_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.message_left, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.message_right, parent, false);
        }
        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = dataMessage.get(position).getMessage();
        String timestamp = dataMessage.get(position).getTimestamp();
        holder.tv_timestamp.setText(TimeFunc.getTimeAgo(Long.parseLong(timestamp), context));
        holder.tv_message.setText(message);
        String imageMes = dataMessage.get(position).getImage();
        if (!message.equals("")) {
            holder.tv_message.setVisibility(View.VISIBLE);
        }
        if (!imageMes.equals("") && !imageMes.equals("1")) {
            holder.imageViewMes.setVisibility(View.VISIBLE);
            try {
                StorageReference islandRef = ImageSendStorageRef.child(imageMes);
                final long ONE_MEGABYTE = 1024 * 1024;
                islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        // Data for "images/island.jpg" is returns, use this as needed
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        holder.imageViewMes.setImageBitmap(bitmap);
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
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visibleTimestamp == true) {
                    holder.tv_timestamp.setVisibility(View.VISIBLE);
                    visibleTimestamp = false;
                } else {
                    holder.tv_timestamp.setVisibility(View.GONE);
                    visibleTimestamp = true;
                }
            }
        });

        String user_id = dataMessage.get(position).getSender();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mDataUsers.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user.getUser_id().equals(user_id)) {
                        mDataUsers.add(user);
                    }
                }
                if (mDataUsers != null) {
                    try {
                        String imgUser = mDataUsers.get(0).getImage_id();
                        try {
                            StorageReference islandRef = storageRef.child("images/user/" + imgUser);
                            final long ONE_MEGABYTE = 1024 * 1024;
                            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                @Override
                                public void onSuccess(byte[] bytes) {
                                    // Data for "images/island.jpg" is returns, use this as needed
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                    holder.imageViewUser.setImageBitmap(bitmap);
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
    }

    @Override
    public int getItemCount() {
        if (dataMessage != null) {
            return dataMessage.size();
        }
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageViewUser, imageViewMes;
        TextView tv_message, tv_timestamp, tv_idSeen;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUser = itemView.findViewById(R.id.img_user_message);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp_message);
            imageViewMes = itemView.findViewById(R.id.image_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (dataMessage.get(position).getSender().equals(firebaseUser.getUid())) {
            return MES_TYPE_RIGHT;
        } else {
            return MES_TYPE_LEFT;
        }

        //return super.getItemViewType(position);
    }
}
