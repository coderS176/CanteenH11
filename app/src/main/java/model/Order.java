package model;

import java.util.List;

public class Order {
    private String orderId;
    private List<MenuItem> items;
    private String customerId;
    private String dateTime;
    private String EstimatedTime;
    private int totalAmount;
    private String status;

    public Order() {
        // Required empty public constructor for Firestore serialization
    }

    public Order(String orderId, List<MenuItem> items, String customerId, String dateTime, String status, int totalAmount, String estimatedTime) {
        this.orderId = orderId;
        this.items = items;
        this.customerId = customerId;
        this.dateTime = dateTime;
        this.status = status;
        this.EstimatedTime = estimatedTime;
        this.totalAmount = totalAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<MenuItem> getItems() {
        return items;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getEstimatedTime() {
        return EstimatedTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public String getStatus() {
        return status;
    }

}
