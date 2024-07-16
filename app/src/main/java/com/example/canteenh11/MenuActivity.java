package com.example.canteenh11;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.gson.Gson;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import model.MenuItem;

public class MenuActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMenu;
    private MenuAdapter menuAdapter;
    private List<MenuItem> menuItems;
    private Button buttonCheckout;
    private FirebaseFirestore fStore;
    private String TAG = "MenuActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        // Initialize RecyclerView
        recyclerViewMenu = findViewById(R.id.recyclerViewMenu);
        menuItems = new ArrayList<>();
        fStore = FirebaseFirestore.getInstance();

        // Fetch products from Firebase Firestore
        fetchProducts();

        // Create and set the adapter
        menuAdapter = new MenuAdapter(this, menuItems);
        recyclerViewMenu.setAdapter(menuAdapter);
        recyclerViewMenu.setLayoutManager(new LinearLayoutManager(this));
        buttonCheckout = findViewById(R.id.buttonCheckout);
        buttonCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get cart items from adapter
                List<MenuItem> cartItems = menuAdapter.getCartItems();
                // Pass the cart items to the CheckoutActivity
                Gson gson = new Gson();
                String json = gson.toJson(cartItems);
                Intent intent = new Intent(MenuActivity.this, CheckoutActivity.class);
                intent.putExtra("cartItems", json);
                startActivity(intent);
            }
        });
        ImageButton profileButton = findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open profile page
                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    // Check if any item is added to cart
    public void updateCheckoutButtonVisibility() {
        if (!menuAdapter.isCartEmpty()) {
            buttonCheckout.setVisibility(View.VISIBLE); // Show checkout button if cart is not empty
        } else {
            buttonCheckout.setVisibility(View.GONE); // Hide checkout button if cart is empty
        }
    }

    private void fetchProducts() {
        fStore.collection("products")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String, Object> data = document.getData();
                            Gson gson = new Gson();
                            String json = gson.toJson(data);
                            MenuItem product = gson.fromJson(json, MenuItem.class);
                            menuItems.add(product);
                        }
                        menuAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(MenuActivity.this, "Failed to fetch products", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

