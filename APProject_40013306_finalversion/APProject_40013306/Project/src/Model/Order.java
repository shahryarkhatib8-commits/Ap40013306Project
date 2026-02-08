package Model;

import java.util.List;
import java.util.ArrayList;
import service.storedata;

public class Order {
    private String username;
    private List<Product> items;
    private double totalPrice;
    private String status;

    public Order(String username, List<Product> items, double totalPrice) {
        this.username = username;
        this.items = new ArrayList<>(items);
        this.totalPrice = totalPrice;
        this.status = "PENDING";
    }

    public String getUsername() {
        return username;
    }

    public List<Product> getItems() {
        return items;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User: " + username + " | Items: " + items.size() + " | Total: $" + totalPrice + " | Status: " + status;
    }

    public String toCSV() {
        StringBuilder productIds = new StringBuilder();
        for (Product p : items) {
            productIds.append(p.getId()).append(";");
        }

        return String.format("%s,%s,%.2f,%s",
                username, status, totalPrice, productIds.toString());
    }

    public static Order fromCSV(String csvLine) {
        String[] parts = csvLine.split(",");

        if (parts.length < 3) {
            return null;
        }

        String username = parts[0];
        String status = parts[1];
        double totalPrice = Double.parseDouble(parts[2]);

        List<Product> loadedItems = new ArrayList<>();
        if (parts.length > 3) {
            String[] ids = parts[3].split(";");
            for (String idStr : ids) {
                if (!idStr.isEmpty()) {
                    Product p = storedata.getProductById(idStr);
                    if (p != null) {
                        loadedItems.add(p);
                    }
                }
            }
        }

        Order order = new Order(username, loadedItems, totalPrice);
        order.setStatus(status);
        return order;
    }
}
