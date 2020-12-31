package com.example.managerheathcareapp.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerheathcareapp.Model.Message;
import com.example.managerheathcareapp.R;

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

    public MessageAdapter(Context context, ArrayList<Message> dataMessage, String imageURL) {
        this.context = context;
        this.dataMessage = dataMessage;
        this.imageURL = imageURL;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == MES_TYPE_LEFT){
            view = LayoutInflater.from(context).inflate(R.layout.message_left, parent, false);
        }else {
            view = LayoutInflater.from(context).inflate(R.layout.message_right, parent, false);
        }
        return new MessageViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        String message = dataMessage.get(position).getMessage();

        String timestamp = dataMessage.get(position).getTimestamp();
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTimeMes = DateFormat.format("dd/MM/yyyy hh:mm a", calendar).toString();

        holder.tv_message.setText(message);
        holder.tv_timestamp.setText(dateTimeMes);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (visibleTimestamp == true){
                    holder.tv_timestamp.setVisibility(View.VISIBLE);
                    visibleTimestamp = false;
                }else {
                    holder.tv_timestamp.setVisibility(View.GONE);
                    visibleTimestamp = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (dataMessage!=null){
            return dataMessage.size();
        }
        return 0;
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        ImageView imageViewUser;
        TextView tv_message, tv_timestamp, tv_idSeen;
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewUser = itemView.findViewById(R.id.img_user_message);
            tv_message = itemView.findViewById(R.id.tv_message);
            tv_timestamp = itemView.findViewById(R.id.tv_timestamp_message);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (dataMessage.get(position).getSender().equals("124")){
            return MES_TYPE_RIGHT;
        }else {
            return MES_TYPE_LEFT;
        }

        //return super.getItemViewType(position);
    }
}
