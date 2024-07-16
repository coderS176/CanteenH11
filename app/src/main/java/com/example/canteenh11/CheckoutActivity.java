package com.example.canteenh11;

import static android.os.Build.*;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import model.MenuItem;
import model.Order;


public class CheckoutActivity extends AppCompatActivity {
    private ArrayList<MenuItem> cartItems;
    private RecyclerView recyclerView;
    private TextView totalPriceTextView;
    private TextView totalWaitTimeTextView;
    private Button buttonPlaceOrder;
    private int totalPrice;
    private String estimatedTime;
    private String TAG = "CheckOutActivity";
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        db = FirebaseFirestore.getInstance();
        // Retrieve the cart items from the Intent's extras
        String json = getIntent().getStringExtra("cartItems");
        cartItems = new Gson().fromJson(json, new TypeToken<ArrayList<MenuItem>>() {
        }.getType());
        // Initialize RecyclerView
        recyclerView = findViewById(R.id.cartRecyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // Initialize TextView for total price
        totalPriceTextView = findViewById(R.id.totalPriceTextView);
        totalWaitTimeTextView = findViewById(R.id.estimatedTimeTextView);
        updateTotalPrice_and_WaitTime();
        CartAdapter adapter = new CartAdapter(this, cartItems);
        recyclerView.setAdapter(adapter);
        buttonPlaceOrder = findViewById(R.id.buttonPlaceOrder);
        buttonPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                placeOrder();
//                Toast.makeText(CheckoutActivity.this,"Placed order!",Toast.LENGTH_SHORT).show();
                buttonPlaceOrder.setEnabled(false);
            }
        });

    }

    public void updateTotalPrice_and_WaitTime() {
        // Calculate total price based on quantities
        if (cartItems.size() == 0) {
            Intent i = new Intent(CheckoutActivity.this, MenuActivity.class);
            startActivity(i);
            return; // Exit the method if there are no cart items
        }

        ArrayList<String> items = new ArrayList<>();
        int totalAmount = calculateTotalPriceAndItems(items);

        totalPrice = totalAmount;
        totalPriceTextView.setText("Total Price: Rs " + totalAmount);

        fetchAndDisplayEstimatedTime(items);
    }

    private int calculateTotalPriceAndItems(ArrayList<String> items) {
        int totalAmount = 0;
        for (MenuItem cartItem : cartItems) {
            String item = cartItem.getItemName();
            items.add(item);
            int quantity = cartItem.getQuantity();
            int price = cartItem.getPrice();
            totalAmount += quantity * price;
        }
        return totalAmount;
    }

    private void fetchAndDisplayEstimatedTime(ArrayList<String> items) {
        fetchAndCalculateEstimatedTime(items, cartItems, new EstimatedTimeCallback() {
            @Override
            public void onEstimatedTimeCalculated(long estimatedTimeMillis) {
                if (estimatedTimeMillis != 0) {
                    String formattedDateTime = DateUtil.getFormattedDateTime(estimatedTimeMillis);
                    Log.d(TAG, "Estimated Time: " + formattedDateTime);

                    String time = DateUtil.getFormattedTime(estimatedTimeMillis);
                    estimatedTime = formattedDateTime;
                    totalWaitTimeTextView.setText("Estimated Preparation Time: " + time);
                } else {
                    Log.d(TAG, "Failed to calculate estimated time.");
                    totalWaitTimeTextView.setText("Estimated Preparation Time: Not available");
                }
            }
        });
    }


    private void placeOrder() {
        // Generate an auto-generated orderId
        String orderId = generateOrderId();

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String customerId = currentUser.getUid();

            // Create order object
            Order order = new Order(orderId, cartItems, customerId, getCurrentDateTime(), "pending", totalPrice, estimatedTime);

            // Get Firestore instance
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            // Reference to the collection where orders will be stored
            CollectionReference userOrdersRef = db.collection("orders");

            // Add the order to Firestore
            userOrdersRef.document(orderId).set(order)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            ArrayList<String> itemNames = new ArrayList<>();
                            for (MenuItem menuItem : cartItems) {
                                itemNames.add(menuItem.getItemName());
                            }
                            Toast.makeText(CheckoutActivity.this, "Order placed successfully!", Toast.LENGTH_SHORT).show();
                            // Start OrderTrackingActivity or any other activity
                            Intent intent = new Intent(CheckoutActivity.this, OrderTrackingActivity.class);
                            intent.putExtra("EstimatedTime", estimatedTime);
                            startActivity(intent);
                            finish(); // Close current activity
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Failed to place order
                            Toast.makeText(CheckoutActivity.this, "Failed to place order. Please try again.", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // User is not logged in
            Toast.makeText(CheckoutActivity.this, "User not logged in!", Toast.LENGTH_SHORT).show();
            // Redirect user to login activity or handle as needed
        }
    }

    private String generateOrderId() {
        // Implement your method to generate unique order IDs (e.g., using timestamp)
        return "ORDER_" + System.currentTimeMillis();
    }

    public String getCurrentDateTime() {
        // Get the current date and time in the system's default time zone
        LocalDateTime currentDateTime = null;
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            currentDateTime = LocalDateTime.now();
        }

        // Specify the Indian time zone
        ZoneId zoneId = null;
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            zoneId = ZoneId.of("Asia/Kolkata");
        }

        // Convert the current date and time to Indian Standard Time
        LocalDateTime currentDateTimeInIndia = null;
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            currentDateTimeInIndia = currentDateTime.atZone(zoneId).toLocalDateTime();
        }

        // Format the date and time as a string
        DateTimeFormatter formatter = null;
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        }

        String formattedDateTime = null;
        if (VERSION.SDK_INT < VERSION_CODES.O) {
            return formattedDateTime;
        }
        if (VERSION.SDK_INT >= VERSION_CODES.O) {
            formattedDateTime = currentDateTimeInIndia.format(formatter);
        }

        return formattedDateTime;
    }


    private void fetchAndCalculateEstimatedTime(List<String> itemNames, List<MenuItem> cartItems, EstimatedTimeCallback callback) {
        db.collection("orders")
                .whereEqualTo("status", "pending")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<DocumentSnapshot> pendingOrders = new ArrayList<>();
                        int maxTime = 0;

                        for (int i = 0; i < itemNames.size(); i++) {
                            String itemName = itemNames.get(i);
                            MenuItem cartItem = cartItems.get(i);
                            int currentQuantity = cartItem.getQuantity();
                            int capacity = cartItem.getCapacity();
                            int cookingTime = cartItem.getCookingTime();

                            int totalPendingQuantity = 0;
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                totalPendingQuantity += processOrderDocument(document, itemName, currentQuantity, capacity, cookingTime);
                                pendingOrders.add(document);
                            }

                            int estimatedTimeMinutes = calculateEstimatedTime(totalPendingQuantity, capacity, cookingTime);
                            maxTime = Math.max(estimatedTimeMinutes, maxTime);
                        }

                        sortOrdersByTime(pendingOrders);

                        long targetTimeMillis = System.currentTimeMillis() + (maxTime * 60 * 1000);
                        callback.onEstimatedTimeCalculated(targetTimeMillis);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    private int processOrderDocument(QueryDocumentSnapshot document, String itemName, int currentQuantity, int capacity, int cookingTime) {
        int totalPendingQuantity = 0;

        List<Map<String, Object>> items = (List<Map<String, Object>>) document.get("items");
        if (items != null) {
            for (Map<String, Object> item : items) {
                String itemDocumentName = (String) item.get("itemName");
                String itemType = (String) item.get("type");

                if (itemName.equals(itemDocumentName) && "Prepared".equals(itemType)) {
                    Number quantity = (Number) item.get("quantity");
                    totalPendingQuantity += quantity != null ? quantity.intValue() : 0;
                    totalPendingQuantity += currentQuantity;

                    Number itemCapacity = (Number) item.get("capacity");
                    if (itemCapacity != null) {
                        capacity = itemCapacity.intValue();
                    }

                    Number itemCookingTime = (Number) item.get("cookingTime");
                    if (itemCookingTime != null) {
                        cookingTime = itemCookingTime.intValue();
                    }
                }
            }
        }

        return totalPendingQuantity;
    }

    private int calculateEstimatedTime(int totalPendingQuantity, int capacity, int cookingTime) {
        return (int) Math.ceil((double) totalPendingQuantity / capacity) * cookingTime;
    }

    private void sortOrdersByTime(List<DocumentSnapshot> pendingOrders) {
        Collections.sort(pendingOrders, new Comparator<DocumentSnapshot>() {
            @Override
            public int compare(DocumentSnapshot o1, DocumentSnapshot o2) {
                String dateTime1 = o1.getString("dateTime");
                String dateTime2 = o2.getString("dateTime");
                return dateTime1.compareTo(dateTime2);
            }
        });
    }
}


