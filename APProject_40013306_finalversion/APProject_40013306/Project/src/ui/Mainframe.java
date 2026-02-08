package ui;

import Model.Order;
import Model.Product;
import Model.User;
import service.storedata;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Mainframe extends JFrame {
    private User currentUser;
    private DefaultTableModel tableModel;
    private JTable productTable;
    private JLabel balanceLabel;

    private JTextField searchField;
    private JComboBox<String> sortComboBox;

    public Mainframe(User user) {
        if (user == null) {
            JOptionPane.showMessageDialog(null, "Login Error: No user data.");
            this.dispose();
            new Loginframe();
            return;
        }
        this.currentUser = user;
        setTitle("Store Management System - " + currentUser.getRole());
        setSize(900, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
        setVisible(true);
    }
    private java.util.List<Product> currentDisplayedProducts = new java.util.ArrayList<>();
    private void initUI() {
        setLayout(new BorderLayout());

        JPanel topContainer = new JPanel(new GridLayout(2, 1));

        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        JLabel welcomeLabel = new JLabel("User: " + currentUser.getUsername() + " (" + currentUser.getRole() + ") ");
        userInfoPanel.add(welcomeLabel);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            this.dispose();
            new Loginframe();
        });

        if ("CUSTOMER".equals(currentUser.getRole())) {
            balanceLabel = new JLabel("| Balance: $" + currentUser.getBalance());
            userInfoPanel.add(balanceLabel);

            JButton addBalanceBtn = new JButton("Add Balance (+)");
            addBalanceBtn.setBackground(new Color(144, 238, 144));
            addBalanceBtn.addActionListener(e -> {
                String input = JOptionPane.showInputDialog(this, "Amount to add:");
                if (input != null && !input.isEmpty()) {
                    try {
                        double amt = Double.parseDouble(input);
                        if (amt > 0) {
                            currentUser.setBalance(currentUser.getBalance() + amt);
                            storedata.saveAllData();
                            refreshBalance();
                            JOptionPane.showMessageDialog(this, "Balance updated!");
                        }
                    } catch (Exception ex) {
                    }
                }
            });
            userInfoPanel.add(addBalanceBtn);
        }
        userInfoPanel.add(logoutBtn);

        JPanel toolsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toolsPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, Color.LIGHT_GRAY));

        JLabel searchLbl = new JLabel("Search:");
        searchField = new JTextField(15);
        JButton searchBtn = new JButton("Go");

        JLabel sortLbl = new JLabel(" | Sort By:");
        String[] sortOptions = {"Default (ID)", "Name (A-Z)", "Price (Low-High)", "Price (High-Low)"};
        sortComboBox = new JComboBox<>(sortOptions);

        searchBtn.addActionListener(e -> loadProductsToTable());
        sortComboBox.addActionListener(e -> loadProductsToTable());

        toolsPanel.add(searchLbl);
        toolsPanel.add(searchField);
        toolsPanel.add(searchBtn);
        toolsPanel.add(sortLbl);
        toolsPanel.add(sortComboBox);

        topContainer.add(userInfoPanel);
        topContainer.add(toolsPanel);
        add(topContainer, BorderLayout.NORTH);

        String[] columns = {"ID", "Name", "Category", "Price", "Stock"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        productTable = new JTable(tableModel);
        loadProductsToTable();
        add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();

        if ("ADMIN".equals(currentUser.getRole())) {
            JButton addProdBtn = new JButton("Add New Product");
            JButton editProdBtn = new JButton("Edit / Restock Selected");
            JButton removeProdBtn = new JButton("Remove Selected");
            JButton processOrderBtn = new JButton("Process Orders");

            addProdBtn.addActionListener(e -> {
                String name = JOptionPane.showInputDialog("Product Name:");
                if (name != null && !name.isEmpty()) {
                    String cat = JOptionPane.showInputDialog("Category:", "General");
                    String priceStr = JOptionPane.showInputDialog("Price:", "100");
                    String stockStr = JOptionPane.showInputDialog("Stock:", "10");
                    try {
                        double price = Double.parseDouble(priceStr);
                        int stock = Integer.parseInt(stockStr);
                        Product p = new Product(String.valueOf(System.currentTimeMillis()), name, cat, price, stock);
                        storedata.addProduct(p);
                        loadProductsToTable();
                        storedata.saveAllData();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Invalid Number format");
                    }
                }
            });

            editProdBtn.addActionListener(e -> {
                int row = productTable.getSelectedRow();
                if (row != -1) {
                    String id = (String) tableModel.getValueAt(row, 0);
                    Product productToEdit = findProductById(id);

                    if (productToEdit != null) {
                        JTextField nameField = new JTextField(productToEdit.getName());
                        JTextField priceField = new JTextField(String.valueOf(productToEdit.getPrice()));
                        JTextField stockField = new JTextField(String.valueOf(productToEdit.getStockQuantity()));

                        Object[] message = {
                                "Name:", nameField,
                                "Price:", priceField,
                                "Stock (Update Quantity):", stockField
                        };

                        int option = JOptionPane.showConfirmDialog(this, message, "Edit Product", JOptionPane.OK_CANCEL_OPTION);
                        if (option == JOptionPane.OK_OPTION) {
                            try {
                                productToEdit.setName(nameField.getText());
                                productToEdit.setPrice(Double.parseDouble(priceField.getText()));
                                productToEdit.setStockQuantity(Integer.parseInt(stockField.getText()));

                                storedata.saveAllData();
                                loadProductsToTable();
                                JOptionPane.showMessageDialog(this, "Product Updated!");
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(this, "Invalid input for price or stock.");
                            }
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a product row to edit.");
                }
            });

            removeProdBtn.addActionListener(e -> {
                int row = productTable.getSelectedRow();
                if (row != -1) {
                    String id = (String) tableModel.getValueAt(row, 0);
                    storedata.removeProduct(id);
                    loadProductsToTable();
                    storedata.saveAllData();
                } else {
                    JOptionPane.showMessageDialog(this, "Select a product to remove.");
                }
            });

            processOrderBtn.addActionListener(e -> processNextOrder());

            bottomPanel.add(addProdBtn);
            bottomPanel.add(editProdBtn);
            bottomPanel.add(removeProdBtn);
            bottomPanel.add(processOrderBtn);

        } else {
            JButton addToCartBtn = new JButton("Add to Cart");
            JButton viewCartBtn = new JButton("View Cart / Checkout");

            addToCartBtn.addActionListener(e -> {
                boolean hasPendingOrder = false;
                for (Order order : storedata.getOrders()) {
                    if (order.getUsername().equals(currentUser.getUsername())) {
                        hasPendingOrder = true;
                        break;
                    }
                }

                if (hasPendingOrder) {
                    JOptionPane.showMessageDialog(this,
                            "You have a pending order waiting for Admin approval.\n" +
                                    "You cannot add new items until your previous order is processed.",
                            "Action Blocked",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }


                int row = productTable.getSelectedRow();
                if (row != -1) {
                    Product productToAdd = currentDisplayedProducts.get(row);

                    if (productToAdd.getStockQuantity() <= 0) {
                        JOptionPane.showMessageDialog(this, "This product is out of stock!", "Out of Stock", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int currentInCartCount = 0;
                    for (Product p : currentUser.getCart().getProductsStack() ) {
                        if (p.getId().equals(productToAdd.getId())) {
                            currentInCartCount++;
                        }
                    }

                    if (currentInCartCount >= productToAdd.getStockQuantity()) {
                        JOptionPane.showMessageDialog(this,
                                "You cannot add more! Stock limit reached (" + productToAdd.getStockQuantity() + ").",
                                "Limit Reached",
                                JOptionPane.WARNING_MESSAGE);
                    } else {
                        currentUser.getCart().addProduct(productToAdd);
                        JOptionPane.showMessageDialog(this, productToAdd.getName() + " added to cart.");
                    }

                } else {
                    JOptionPane.showMessageDialog(this, "Select a product first.");
                }
            });



            viewCartBtn.addActionListener(e -> new CartFrame(currentUser, this));
            bottomPanel.add(addToCartBtn);
            bottomPanel.add(viewCartBtn);
        }
        add(bottomPanel, BorderLayout.SOUTH);
    }


    private void loadProductsToTable() {
        String query = searchField.getText().toLowerCase();

        List<Product> allProducts = storedata.getAllProducts();
        currentDisplayedProducts.clear();

        boolean isAdmin = currentUser.getRole().equals("ADMIN");

        int rowIndex = 1;

        for (Product p : allProducts) {
            boolean match = false;

            if (p.getName().toLowerCase().contains(query) ||
                    p.getCategory().toLowerCase().contains(query)) {
                match = true;
            }
            else if (isAdmin && p.getId().toLowerCase().contains(query)) {
                match = true;
            }
            else if (!isAdmin && String.valueOf(rowIndex).contains(query)) {
                match = true;
            }

            if (match) {
                currentDisplayedProducts.add(p);
            }

            rowIndex++;
        }

        String sortType = (String) sortComboBox.getSelectedItem();
        if (sortType != null) {
            switch (sortType) {
                case "Name (A-Z)":
                    currentDisplayedProducts.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                    break;
                case "Price (Low-High)":
                    currentDisplayedProducts.sort((p1, p2) -> Double.compare(p1.getPrice(), p2.getPrice()));
                    break;
                case "Price (High-Low)":
                    currentDisplayedProducts.sort((p1, p2) -> Double.compare(p2.getPrice(), p1.getPrice()));
                    break;
                default:
                    currentDisplayedProducts.sort((p1, p2) -> {
                        try {
                            return Long.compare(Long.parseLong(p1.getId()), Long.parseLong(p2.getId()));
                        } catch (NumberFormatException e) {
                            return p1.getId().compareTo(p2.getId());
                        }
                    });
            }
        }

        String[] columns;
        if (isAdmin) {
            columns = new String[]{"ID", "Name", "Category", "Price", "Stock"};
        } else {
            columns = new String[]{"#", "Name", "Category", "Price", "Stock"};
        }

        tableModel.setColumnIdentifiers(columns);
        tableModel.setRowCount(0);

        int displayRowNumber = 1;

        for (Product p : currentDisplayedProducts) {
            Object firstColumnValue;

            if (isAdmin) {
                firstColumnValue = p.getId();
            } else {
                firstColumnValue = displayRowNumber++;
            }

            tableModel.addRow(new Object[]{
                    firstColumnValue,
                    p.getName(),
                    p.getCategory(),
                    p.getPrice(),
                    p.getStockQuantity()
            });
        }
    }




    private Product findProductById(String id) {
        for (Product p : storedata.getAllProducts()) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    public void refreshBalance() {
        if (balanceLabel != null && currentUser != null) {
            balanceLabel.setText("| Balance: $" + currentUser.getBalance());
        }
    }

    private void processNextOrder() {
        Order order = storedata.orderQueue.peek();
        if (order == null) {
            JOptionPane.showMessageDialog(this, "No orders in queue.");
            return;
        }

        User customer = storedata.getUser(order.getUsername());
        if (customer == null) {
            JOptionPane.showMessageDialog(this, "Error: Customer not found!");
            storedata.orderQueue.poll();
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "User: " + order.getUsername() + "\n" +
                        "Current Balance: $" + customer.getBalance() + "\n" +
                        "Order Total: $" + order.getTotalPrice() + "\n" +
                        "Approve and Deduct Balance?",
                "Process Order", JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            if (customer.getBalance() < order.getTotalPrice()) {
                JOptionPane.showMessageDialog(this, "User does not have enough balance anymore!\nOrder Rejected.");
                storedata.orderQueue.poll();
                return;
            }

            customer.setBalance(customer.getBalance() - order.getTotalPrice());

            for (Product orderItem : order.getItems()) {
                for (Product warehouseItem : storedata.getAllProducts()) {
                    if (warehouseItem.getId().equals(orderItem.getId())) {
                        int newStock = warehouseItem.getStockQuantity() - 1;
                        warehouseItem.setStockQuantity(Math.max(0, newStock));
                    }
                }
            }

            storedata.orderQueue.poll();
            storedata.saveAllData();
            loadProductsToTable();
            JOptionPane.showMessageDialog(this, "Order Approved & Processed.");
        } else {
            storedata.orderQueue.poll();
            JOptionPane.showMessageDialog(this, "Order Rejected.");
        }
    }
}
