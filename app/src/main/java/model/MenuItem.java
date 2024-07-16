package model;

public class MenuItem {
    private String productName;
    private int price;
    private String description;
    private String type;
    private int cookingTime;
    private boolean isAvailable;
    private boolean status;
    private String imageUrl;
    private int capacity;

    private int quantity = 0;

    public MenuItem() {

    }

    public MenuItem(String productName, int price, String type, String description, String imageUrl, int cookingTime, int capacity) {
        this.productName = productName;
        this.price = price;
        this.description = description;
        this.imageUrl = imageUrl;
        this.cookingTime = cookingTime;
        this.capacity = capacity;
        this.type = type;
    }

    public String getItemName() {
        return productName;
    }

    public int getPrice() {
        return price;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setCookingTime(int cookingTime) {
        this.cookingTime = cookingTime;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public String getDescription() {
        return description;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getType() {
        return type;
    }

    public String getImageResource() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void increaseQuantity() {
        quantity++;
    }

    public void decreaseQuantity() {
        quantity--;
    }
}