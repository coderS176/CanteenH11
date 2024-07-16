package com.example.canteenh11;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class PendingOrderAdapter extends RecyclerView.Adapter<PendingOrderAdapter.PendingOrderViewHolder> {

    private Context context;
    private List<Map.Entry<String, List<String>>> orderList;

    public PendingOrderAdapter(Context context, List<Map.Entry<String, List<String>>> orderList) {
        this.context = context;
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public PendingOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_order, parent, false);
        return new PendingOrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PendingOrderViewHolder holder, int position) {
        Map.Entry<String, List<String>> entry = orderList.get(position);
        holder.bind(entry);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ProfileActivity) context).startOrderTrackingActivity(entry.getValue().get(1));
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public class PendingOrderViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewOrderTime;
        private TextView textViewOrderAmount;
        private TextView textViewEstimatedTime;
        private TextView textViewOrderId;
        private CardView cardView;

        public PendingOrderViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderTime = itemView.findViewById(R.id.textViewOrderTime);
            textViewOrderAmount = itemView.findViewById(R.id.textViewOrderAmount);
            textViewEstimatedTime = itemView.findViewById(R.id.textViewEstimatedTime);
            textViewOrderId = itemView.findViewById(R.id.textViewOrderId);
            cardView = itemView.findViewById(R.id.cardView);
        }

        public void bind(@NonNull Map.Entry<String, List<String>> entry) {
            String orderTime = entry.getKey();
            List<String> orderDetails = entry.getValue();
            String orderAmount = orderDetails.get(0);
            String estimatedTime = orderDetails.get(1);
            //Toast.makeText(ProfileActivity.this,orderAmount,Toast.LENGTH_SHORT).show();
            textViewOrderTime.setText("Order Time: " + orderTime);
            textViewOrderAmount.setText("Amount: " + orderAmount);
            textViewEstimatedTime.setText("Estimated Time: " + estimatedTime);
            textViewOrderId.setText("OrderId: #124");
        }

    }
}
