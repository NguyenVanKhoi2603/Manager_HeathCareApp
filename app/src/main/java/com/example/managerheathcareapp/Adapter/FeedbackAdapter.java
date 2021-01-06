package com.example.managerheathcareapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerheathcareapp.Functions.TimeFunc;
import com.example.managerheathcareapp.Model.Feedback;
import com.example.managerheathcareapp.Model.User;
import com.example.managerheathcareapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    Context context;
    ArrayList<Feedback> dataFeedback = new ArrayList<>();
    ArrayList<User> dataUser = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users");

    public FeedbackAdapter(Context context, ArrayList<Feedback> dataFeedback) {
        this.context = context;
        this.dataFeedback = dataFeedback;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = dataFeedback.get(position);
        if (dataFeedback == null) {
            return;
        }
        try {
            String time = feedback.getTimestamp();
            holder.tv_timestamp.setText(TimeFunc.getTimestampToString(Long.parseLong(time)));
        } catch (Exception exception) {

        }


        holder.tv_comment.setText(feedback.getComment());
        holder.ratingBar.setRating(feedback.getRatting());
        String user_id = feedback.getUser_id();
        userRef.orderByChild(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                dataUser.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    User user = ds.getValue(User.class);
                    if (user.getUser_id().equals(user_id)) {
                        dataUser.add(user);
                    }

                }
                if (dataUser != null) {
                    holder.tv_name.setText(dataUser.get(0).getFirst_name() + " " + dataUser.get(0).getLast_name());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        if (dataFeedback != null) {
            return dataFeedback.size();
        }
        return 0;
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_timestamp, tv_comment;
        RatingBar ratingBar;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_comment = itemView.findViewById(R.id.tv_comment_item_feedback);
            tv_name = itemView.findViewById(R.id.tv_name_user_name_item_feedback);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp_item_feedback);
            ratingBar = itemView.findViewById(R.id.ratting_bar_item_feedback);
        }
    }
}
