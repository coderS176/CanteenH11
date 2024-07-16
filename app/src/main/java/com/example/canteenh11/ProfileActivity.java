package com.example.canteenh11;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private List<Map.Entry<String, List<String>>> dummyPendingOrders;
    private PendingOrderAdapter pendingOrderAdapter;
    private TextView textViewName,textViewEmail;
    private Button buttonEditProfile;
    private Map<String,Object>data;
    private RecyclerView recyclerViewPendingOrders;
    private FirebaseFirestore fStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        textViewName = findViewById(R.id.textViewName);
        textViewEmail = findViewById(R.id.textViewEmail);
        buttonEditProfile = findViewById(R.id.buttonEditProfile);
        // Initialize Firestore
        fStore = FirebaseFirestore.getInstance();
        data = new HashMap<>();
        fetchProfileDetails(new FirestoreCallback() {
            @Override
            public void onCallback(Map<String, Object> data) {
                if (data != null) {
//                    Toast.makeText(ProfileActivity.this, data.toString(), Toast.LENGTH_SHORT).show();
                    String fName = (String) data.get("full_name");
                    String Email = (String) data.getOrDefault("email", "Error404");
                    runOnUiThread(() -> {
                        textViewName.setText(fName);
                        textViewEmail.setText(Email);
                    });
                }
            }
        });
//        Toast.makeText(this, data.toString(),Toast.LENGTH_SHORT).show();
        String fName= (String) data.get("full_name");
        String Email = (String) data.getOrDefault("email","Error404");
        textViewName.setText(fName);
        textViewEmail.setText(Email);

        // Initialize the list
        dummyPendingOrders = new ArrayList<>();

        // Setup RecyclerView and Adapter
        recyclerViewPendingOrders = findViewById(R.id.recyclerViewPendingOrders);
        pendingOrderAdapter = new PendingOrderAdapter(this, dummyPendingOrders);
        recyclerViewPendingOrders.setAdapter(pendingOrderAdapter);
        recyclerViewPendingOrders.setLayoutManager(new LinearLayoutManager(this));

        // Fetch pending orders
        fetchPendingOrders();
        buttonEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ProfileActivity.this, EditProfile.class);
                Bundle bundle = mapToBundle(data);
                i.putExtras(bundle);
                startActivity(i);
            }
        });
    }

    private void fetchPendingOrders() {
        fStore.collection("orders")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            dummyPendingOrders.clear(); // Clear existing data
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                try {
                                    String orderTime = document.getString("dateTime");
                                    long orderAmount = document.getLong("totalAmount");
                                    String estimatedTime = document.getString("estimatedTime");
                                    dummyPendingOrders.add(new AbstractMap.SimpleEntry<>(orderTime, Arrays.asList(""+orderAmount, estimatedTime)));
                                } catch (Exception e) {
                                    Log.e("ProfileActivity", "Error parsing document: " + document.getId(), e);
                                }
                            }
//                            Toast.makeText(ProfileActivity.this,""+dummyPendingOrders.size(),Toast.LENGTH_SHORT).show();
                            pendingOrderAdapter.notifyDataSetChanged();
                        } else {
                            // Handle the error
                            Log.w("ProfileActivity", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    public void fetchProfileDetails(FirestoreCallback callback){
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
//        Toast.makeText(this, userId, Toast.LENGTH_SHORT).show();
        fStore.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
//                    Toast.makeText(ProfileActivity.this, task.getResult().getData().toString(), Toast.LENGTH_SHORT).show();
                    data  = task.getResult().getData();
                    callback.onCallback(data);
//                    Toast.makeText(ProfileActivity.this, data.toString(), Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(ProfileActivity.this, "Failed to Load ProfileData!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public Bundle mapToBundle(Map<String, Object> map) {
        Bundle bundle = new Bundle();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof String) {
                bundle.putString(key, (String) value);
            } else if (value instanceof Integer) {
                bundle.putInt(key, (Integer) value);
            } else if (value instanceof Boolean) {
                bundle.putBoolean(key, (Boolean) value);
            } else if (value instanceof Float) {
                bundle.putFloat(key, (Float) value);
            } else if (value instanceof Long) {
                bundle.putLong(key, (Long) value);
            } else if (value instanceof Double) {
                bundle.putDouble(key, (Double) value);
            } else if (value instanceof Serializable) {
                bundle.putSerializable(key, (Serializable) value);
            } else {
                throw new IllegalArgumentException("Unsupported type for key: " + key);
            }
        }
        return bundle;
    }
    public void startOrderTrackingActivity(String EstimatedTime){
        Intent i = new Intent(ProfileActivity.this, OrderTrackingActivity.class);
        i.putExtra("EstimatedTime",EstimatedTime);
        startActivity(i);
    }
    public interface FirestoreCallback {
        void onCallback(Map<String, Object> data);
    }

}
