package com.example.managerheathcareapp.Functions;

import androidx.annotation.NonNull;

import com.example.managerheathcareapp.Model.Admin;
import com.example.managerheathcareapp.Model.Counselor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class CheckRole {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference userRef = database.getReference("Users");
    DatabaseReference counselorRef = database.getReference("Counselors");
    DatabaseReference adminRef = database.getReference("Admins");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    ArrayList<String> dataAdmin = new ArrayList<>();

    public String checkRoleOnBack(String user_id) {
        adminRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Admin admin = ds.getValue(Admin.class);
                    dataAdmin.add(admin.getId_admin());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        if (dataAdmin.contains(user_id)) {
            return "admin";
        } else {
            return "counselor";
        }

    }
}
