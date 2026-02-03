package Model;

import java.util.List;
import java.util.ArrayList;

public class Order {
    private String username;
    private List<Product> items;
    private double totalPrice;
    private String status; // "PENDING", "APPROVED", "REJECTED"

    public Order(String username, List<Product> items, double totalPrice) {
        this.username = username;
        this.items = new ArrayList<>(items);
        this.totalPrice = totalPrice;
        this.status = "PENDING";
    }

    public String getUsername() { return username; }
    public List<Product> getItems() { return items; }
    public double getTotalPrice() { return totalPrice; }

    @Override
    public String toString() {
        return "User: " + username + " | Items: " + items.size() + " | Total: $" + totalPrice;
    }
}
