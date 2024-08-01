package com.example.canteenh11;
import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import model.MenuItem;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<MenuItem> cartItems;
    private Context context;

    public CartAdapter(Context context, ArrayList<MenuItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MenuItem item = cartItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView itemTextView;
        TextView quantityTextView;
        Button addButton;
        Button subtractButton;
        TextView priceTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            itemTextView = itemView.findViewById(R.id.itemTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            addButton = itemView.findViewById(R.id.addButton);
            subtractButton = itemView.findViewById(R.id.subtractButton);
            priceTextView = itemView.findViewById(R.id.priceTextView);
            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        MenuItem item = cartItems.get(adapterPosition);
                        item.increaseQuantity();
                        ((CheckoutActivity) context).updateTotalPrice_and_WaitTime();

                        // Notify adapter about the quantity change
                        notifyItemChanged(adapterPosition);
                    }
                }
            });

            subtractButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int adapterPosition = getAdapterPosition();
                    if (adapterPosition != RecyclerView.NO_POSITION) {
                        MenuItem item = cartItems.get(adapterPosition);
                        item.decreaseQuantity();
                        if (item.getQuantity() == 0) {
                            cartItems.remove(adapterPosition);
                            notifyItemRemoved(adapterPosition);
                            notifyItemRangeChanged(adapterPosition, cartItems.size() - adapterPosition);
                        } else {
                            notifyItemChanged(adapterPosition);
                        }
                        ((CheckoutActivity) context).updateTotalPrice_and_WaitTime();
                    }
                }
            });
        }

        public void bind(MenuItem item) {
            itemTextView.setText(item.getItemName());
            quantityTextView.setText(String.valueOf(item.getQuantity()));
            int price = item.getPrice();
            priceTextView.setText("Rs:" + price * item.getQuantity());
        }
    }
}


//just to check git is connected or not