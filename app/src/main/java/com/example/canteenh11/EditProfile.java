package com.example.canteenh11;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class EditProfile extends AppCompatActivity {

    private EditText editTextScholarNo,editTextMobileNum,editTextRoomNum,editTextName,editTextEmail;
    private Button buttonSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextScholarNo = findViewById(R.id.editTextScholarNo);
        editTextName = findViewById(R.id.editTextName);
        editTextMobileNum = findViewById(R.id.editTextMobileNum);
        editTextRoomNum = findViewById(R.id.editTextRoomNum);
        buttonSignup = findViewById(R.id.buttonSignup);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            Map<String, Object> data = bundleToMap(bundle);
            String fname = (String) data.get("full_name");
            String m_no = (String) data.get("Mobile_no");
            String scholar_no = (String) data.get("scholar_no");
            String Room_no = (String) data.get("Room_no");
            String email = (String) data.get("email");
            editTextEmail.setText(email);
            editTextRoomNum.setText(Room_no);
            editTextName.setText(fname);
            editTextScholarNo.setText(scholar_no);
            editTextMobileNum.setText(m_no);
        }

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null) {
                    String userId = currentUser.getUid();
                   String email = editTextEmail.getText().toString().trim();
                   String Room_no = editTextRoomNum.getText().toString().trim();
                   String full_name =  editTextName.getText().toString().trim();
                   String scholar_no = editTextScholarNo.getText().toString().trim();
                   String Mobile_no = editTextMobileNum.getText().toString().trim();

                    Map<String, Object> updatedData = new HashMap<>();
                    updatedData.put("full_name",full_name);
                    updatedData.put("Room_no",Room_no);
                    updatedData.put("scholar_no",scholar_no);
                    updatedData.put("Mobile_no",Mobile_no);
                    updatedData.put("email",email);
                    updateUserProfile(userId, updatedData);
                    Intent i = new Intent(EditProfile.this, ProfileActivity.class);
                    startActivity(i);
                }
            }
        });
    }
    public Map<String, Object> bundleToMap(Bundle bundle) {
        Map<String, Object> map = new HashMap<>();
        for (String key : bundle.keySet()) {
            map.put(key, bundle.get(key));
        }
        return map;
    }
    public void updateUserProfile(String userId, Map<String, Object> updatedData) {
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        fStore.collection("users").document(userId).update(updatedData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditProfile.this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditProfile.this, "Failed to Update Profile", Toast.LENGTH_SHORT).show();
                        Log.e("updateUserProfile", "Error updating document", e);
                    }
                });
    }
}