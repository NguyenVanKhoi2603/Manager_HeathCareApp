package com.example.managerheathcareapp.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.example.managerheathcareapp.Model.New;
import com.example.managerheathcareapp.Model.NewAndNutrition;
import com.example.managerheathcareapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class NewsAndNutritionAdapter extends RecyclerView.Adapter<NewsAndNutritionAdapter.NewsViewHolder> {
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ArrayList<NewAndNutrition> mNews;
    Context context;
    String _id_news = "";
    private String strImg = "";

    public NewsAndNutritionAdapter(ArrayList<NewAndNutrition> mNews, Context context) {
        this.mNews = mNews;
        this.context = context;
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {

        NewAndNutrition news = mNews.get(position);
        strImg = news.getImage_id();
        if (news == null) {
            return;
        }
        try {
            StorageReference islandRef = storageRef.child("images/news/" + strImg);
            final long ONE_MEGABYTE = 1024 * 1024;
            islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    // Data for "images/island.jpg" is returns, use this as needed
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    holder.imageViewNews.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });

        } catch (Exception ex) {

        }

        holder.tv_category_post.setText("#"+news.getCategory());
        holder.tv_title_news.setText(news.getTitle());
        holder.tv_content_news.setText(news.getDescription());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(context, DetailNewsActivity.class);
//                ((Activity) context).startActivity(intent);
                Toast.makeText(context, strImg, Toast.LENGTH_SHORT).show();
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                _id_news = news.getId_post();
                return false;
            }
        });
    }


    @Override
    public int getItemCount() {
        if (mNews != null) {
            return mNews.size();
        }
        return 0;
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        ImageView imageViewNews;
        TextView tv_title_news, tv_content_news, tv_category_post;
        CardView cardView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewNews = itemView.findViewById(R.id.img_item_news);
            tv_content_news = itemView.findViewById(R.id.tv_content_item_news);
            tv_title_news = itemView.findViewById(R.id.tv_title_item_news);
            cardView = itemView.findViewById(R.id.item_news_card);
            tv_category_post = itemView.findViewById(R.id.tv_category_item_post);
            cardView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Option");
            contextMenu.add(this.getAdapterPosition(), 120, 1, _id_news);
            contextMenu.add(this.getAdapterPosition(), 121, 2, "Delete-" + _id_news);
            contextMenu.add(this.getAdapterPosition(), 122, 3, "Update-" + _id_news);
        }
    }

}
