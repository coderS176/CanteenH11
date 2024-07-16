package com.example.canteenh11;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class OrderDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        if (getIntent().getExtras() != null && getIntent().getExtras().containsKey("orderId")) {
            String orderId = getIntent().getStringExtra("orderId");
            displayOrderDetails(orderId);
        }
    }

    private void displayOrderDetails(String orderId) {

    }

}