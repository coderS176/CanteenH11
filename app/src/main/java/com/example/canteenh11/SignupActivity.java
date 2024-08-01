package com.example.canteenh11;

import static androidx.core.content.ContextCompat.startActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignupActivity extends AppCompatActivity {
    private static final String TAG = "SignupActivity";
    private EditText editTextScholarNo, editTextMobileNum, editTextRoomNum, editTextName, editTextPassword, editTextEmail;
    private Button buttonSignup;
    private ProgressBar progressBar2;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        editTextScholarNo = findViewById(R.id.editTextScholarNo);
        editTextMobileNum = findViewById(R.id.editTextMobileNum);
        editTextRoomNum = findViewById(R.id.editTextRoomNum);
        editTextName = findViewById(R.id.editTextName);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextEmail = findViewById(R.id.editTextEmail);
        buttonSignup = findViewById(R.id.buttonSignup);
        progressBar2 = findViewById(R.id.progressBar2);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        if (fAuth.getCurrentUser() != null) {
            openMenuActivity();
        }

        buttonSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                progressBar2.setVisibility(View.GONE);
                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Email is Required.");
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Password is Required.");
                    return;
                }
                if (password.length() < 6) {
                    editTextPassword.setError("Password must have 6 characters.");
                    return;
                }

                // Get FCM token and proceed with signup
                FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        // Get new FCM registration token
                        String token = task.getResult();

                        // Proceed with creating the user in Firebase Auth
                        fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(SignupActivity.this, "Registration Done.", Toast.LENGTH_SHORT).show();
                                    userId = fAuth.getCurrentUser().getUid();
                                    DocumentReference documentReference = fStore.collection("users").document(userId);
                                    String full_name = editTextName.getText().toString().trim();
                                    String room_no = editTextRoomNum.getText().toString().trim();
                                    String scholar_no = editTextScholarNo.getText().toString().trim();
                                    String mobile_no = editTextMobileNum.getText().toString().trim();
                                    Map<String, Object> user = new HashMap<>();
                                    user.put("full_name", full_name);
                                    user.put("room_no", room_no);
                                    user.put("mobile_no", mobile_no);
                                    user.put("email", email);
                                    user.put("scholar_no", scholar_no);
                                    user.put("fcm_token", token);  // Add FCM token to the user data

                                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "User profile created successfully for user:" + userId);
                                        }
                                    });

                                    openMenuActivity();
                                } else {
                                    Toast.makeText(SignupActivity.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        progressBar2.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }

    private void openMenuActivity() {
        Intent intent = new Intent(SignupActivity.this, MenuActivity.class);
        startActivity(intent);
        finish();
    }
}
