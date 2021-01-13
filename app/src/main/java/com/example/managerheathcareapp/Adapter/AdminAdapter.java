package com.example.managerheathcareapp.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.managerheathcareapp.Model.Admin;
import com.example.managerheathcareapp.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {
    Context context;
    ArrayList<Admin> admins = new ArrayList<>();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference UsersRef = database.getReference("Users");
    DatabaseReference adminRef = database.getReference("Admins");

    public AdminAdapter(Context context, ArrayList<Admin> admins) {
        this.context = context;
        this.admins = admins;
    }
    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        View view = LayoutInflater.from(context).inflate(R.layout.item_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        Admin admin = admins.get(position);
        if (admin == null){
            return;
        }
        holder.tv_fullName.setText(admin.getFullName());
        holder.tv_email.setText(admin.getEmail());
        holder.tv_phone.setText(admin.getPhone());
        String ID_ADMIN = admin.getId_admin();
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Notification");
                builder.setMessage("Do you want delete: " + admin.getFullName());
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        UsersRef.child(ID_ADMIN).removeValue();
                        adminRef.child(ID_ADMIN).removeValue();
                        Toast.makeText(context, "Deleted!", Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        if (admins != null){
            return admins.size();
        }
        return 0;
    }

    public class AdminViewHolder extends RecyclerView.ViewHolder {
        ImageView imageAvatarAdmin;
        TextView tv_fullName, tv_email, tv_phone;
        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            imageAvatarAdmin = itemView.findViewById(R.id.img_item_admin);
            tv_email = itemView.findViewById(R.id.email_item_admin);
            tv_fullName = itemView.findViewById(R.id.fullName_item_admin);
            tv_phone = itemView.findViewById(R.id.phone_item_admin);
        }
    }
}
