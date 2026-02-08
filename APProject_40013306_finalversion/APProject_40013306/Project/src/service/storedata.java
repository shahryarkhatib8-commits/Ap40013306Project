package service;

import Model.Product;
import Model.User;
import Model.Order;

import javax.swing.*;
import java.io.*;
import java.util.*;

public class storedata {
    public static Map<String, User> usersMap = new HashMap<>();
    private static List<Product> productsList = new ArrayList<>();
    public static Queue<Order> orderQueue = new LinkedList<>();

    private static final String USERS_FILE = "users.csv";
    private static final String PRODUCTS_FILE = "products.csv";
    private static final String ORDERS_FILE = "orders.csv";

    static {
        loadData();
    }

    public static User login(String username, String password) {
        if (usersMap.containsKey(username)) {
            User u = usersMap.get(username);
            if (u.getPassword().equals(password)) {
                return u;
            }
        }
        return null;
    }

    public static String registerUser(User user) {
        if (usersMap.containsKey(user.getUsername())) {
            return "USERNAME_EXISTS";
        }

        for (User existingUser : usersMap.values()) {
            if (existingUser.getPassword().equals(user.getPassword())) {
                return "PASSWORD_EXISTS";
            }
        }

        usersMap.put(user.getUsername(), user);
        saveAllData();
        return "SUCCESS";
    }

    public static User getUser(String username) {
        return usersMap.get(username);
    }

    public static List<Product> getAllProducts() {
        return productsList;
    }

    public static Product getProductById(String id) {
        for (Product p : productsList) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public static void addProduct(Product product) {
        productsList.add(product);
        saveAllData();
    }

    public static void removeProduct(String id) {
        productsList.removeIf(p -> p.getId().equals(id));
        saveAllData();
    }

    public static Queue<Order> getOrders() {
        return orderQueue;
    }

    public static void addOrder(Order order) {
        orderQueue.add(order);
        saveAllData();
    }

    public static void saveAllData() {
        saveUsers();
        saveProducts();
        saveOrders();
    }

    public static void loadData() {
        usersMap.clear();
        productsList.clear();
        orderQueue.clear();
        loadUsers();
        loadProducts();
        loadOrders();
    }

    private static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (User u : usersMap.values()) {
                writer.write(u.getUsername() + "," + u.getPassword() + "," + u.getRole() + "," + u.getBalance());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    String user = parts[0];
                    String pass = parts[1];
                    String role = parts[2];
                    double bal = (parts.length > 3) ? Double.parseDouble(parts[3]) : 0.0;

                    User u;
                    if ("ADMIN".equalsIgnoreCase(role)) {
                        u = new User(user, pass, role);
                    } else {
                        u = new User(user, pass, role, bal);
                    }
                    usersMap.put(user, u);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveProducts() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PRODUCTS_FILE))) {
            for (Product p : productsList) {
                writer.write(p.getId() + "," + p.getName() + "," + p.getCategory() + "," + p.getPrice() + "," + p.getStockQuantity());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadProducts() {
        File file = new File(PRODUCTS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    String id = parts[0];
                    String name = parts[1];
                    String cat = parts[2];
                    double price = Double.parseDouble(parts[3]);
                    int stock = Integer.parseInt(parts[4]);
                    productsList.add(new Product(id, name, cat, price, stock));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveOrders() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ORDERS_FILE, false))) {
            for (Order order : orderQueue) {
                writer.println(order.toCSV());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void loadOrders() {
        File file = new File(ORDERS_FILE);
        if (!file.exists()) return;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Order order = Order.fromCSV(line);
                    if (order != null) {
                        orderQueue.add(order);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
