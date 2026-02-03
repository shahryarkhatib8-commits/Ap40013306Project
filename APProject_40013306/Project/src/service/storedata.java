package service;

import Model.Product;
import Model.User;
import Model.Order;

import java.io.*;
import java.util.*;

public class storedata {
    public static Map<String, User> usersMap = new HashMap<>();

    private static List<Product> productsList = new ArrayList<>();

    public static Queue<Order> orderQueue = new LinkedList<>();

    private static final String USERS_FILE = "users.csv";
    private static final String PRODUCTS_FILE = "products.csv";

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
    public static Queue<Order> getOrders() {
        return orderQueue;
    }

    public static List<Product> getAllProducts() {
        return productsList;
    }

    public static void addProduct(Product product) {
        productsList.add(product);
        saveProducts();
    }

    public static void removeProduct(String id) {
        productsList.removeIf(p -> p.getId().equals(id));
        saveProducts();
    }


    public static void saveAllData() {
        saveUsers();
        saveProducts();
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

    public static void loadData() {
        usersMap.clear();
        productsList.clear();
        loadUsers();
        loadProducts();
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
}
