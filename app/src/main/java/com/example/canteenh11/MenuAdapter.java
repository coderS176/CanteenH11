package com.example.canteenh11;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import model.MenuItem;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    private List<MenuItem> menuItems;
    private static final String TAG = "MenuAdapter";
    private List<MenuItem> cartItems; // Maintain a list of cart items
    private Context context;

    public MenuAdapter(Context context, List<MenuItem> menuItems) {
        this.context = context;
        Log.d(TAG, "MenuAdapter constructor called");
        this.menuItems = menuItems;
        this.cartItems = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder called");
        View view = LayoutInflater.from(context).inflate(R.layout.item_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called for position: " + position);
        MenuItem menuItem = menuItems.get(position);
        holder.textViewItemName.setText(menuItem.getItemName());

        // Format the price string
        String priceFormatted = "Price: Rs" + menuItem.getPrice();
        holder.textViewItemPrice.setText(priceFormatted);

        holder.textViewItemDescription.setText(menuItem.getDescription());
        String imageUrl = menuItem.getImageResource();
        Glide.with(holder.itemView.getContext())
                .load(imageUrl)
                .into(holder.imageMenuItem);



        // Check if the item is added to the cart and update visibility of buttons accordingly
        if (menuItem.getQuantity() > 0) {
            holder.buttonAddToCart.setVisibility(View.GONE);
            holder.buttonDecreaseQuantity.setVisibility(View.VISIBLE);
            holder.buttonIncreaseQuantity.setVisibility(View.VISIBLE);
            holder.textQuantity.setVisibility(View.VISIBLE);
            holder.textQuantity.setText(String.valueOf(menuItem.getQuantity()));
        } else {
            holder.buttonAddToCart.setVisibility(View.VISIBLE);
            holder.buttonDecreaseQuantity.setVisibility(View.GONE);
            holder.buttonIncreaseQuantity.setVisibility(View.GONE);
            holder.textQuantity.setVisibility(View.GONE);
        }

        holder.buttonAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Add to cart button clicked for item: " + menuItem.getItemName());
                menuItem.increaseQuantity();
                cartItems.add(menuItem);
                notifyDataSetChanged();
                ((MenuActivity) context).updateCheckoutButtonVisibility();
            }
        });

        holder.buttonDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuItem.decreaseQuantity();
                if (menuItem.getQuantity() == 0) {
                    cartItems.remove(menuItem);
                    ((MenuActivity) context).updateCheckoutButtonVisibility();
                }
                notifyItemChanged(holder.getAdapterPosition());
            }
        });


        holder.buttonIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menuItem.increaseQuantity();
                notifyDataSetChanged();
            }
        });
    }


    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return menuItems.size();
    }

    public boolean isCartEmpty() {
        return cartItems.isEmpty();
    }

    public List<MenuItem> getCartItems() {
        Log.d(TAG,"getCartItems");
        return cartItems;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageMenuItem;
        TextView textViewItemName;
        TextView textViewItemPrice;
        TextView textViewItemDescription;
        Button buttonAddToCart;
        TextView textQuantity;
        Button buttonDecreaseQuantity;
        TextView textViewQuantity;
        Button buttonIncreaseQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMenuItem = itemView.findViewById(R.id.imageMenuItem);
            textViewItemName = itemView.findViewById(R.id.textViewItemName);
            textViewItemPrice = itemView.findViewById(R.id.textViewItemPrice);
            textViewItemDescription = itemView.findViewById(R.id.textViewItemDescription);
            buttonAddToCart = itemView.findViewById(R.id.buttonAddToCart);
            textQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonDecreaseQuantity = itemView.findViewById(R.id.buttonDecreaseQuantity);
            textViewQuantity = itemView.findViewById(R.id.textViewQuantity);
            buttonIncreaseQuantity = itemView.findViewById(R.id.buttonIncreaseQuantity);
        }

    }
}
