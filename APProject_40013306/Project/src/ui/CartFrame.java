package ui;

import Model.Product;
import Model.User;
import Model.Order;
import service.storedata;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class CartFrame extends JFrame {
    private User currentUser;
    private Mainframe parentFrame;
    private DefaultTableModel tableModel;
    private JLabel totalPriceLabel;

    public CartFrame(User user, Mainframe parentFrame) {
        this.currentUser = user;
        this.parentFrame = parentFrame;

        setTitle("Your Shopping Cart");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        String[] columnNames = {"Name", "Price"};
        tableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(tableModel);
        add(new JScrollPane(table), BorderLayout.CENTER);

        totalPriceLabel = new JLabel("Total Price: $0.0");
        totalPriceLabel.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel bottomPanel = new JPanel(new GridLayout(2, 1));
        JPanel pricePanel = new JPanel();
        pricePanel.add(totalPriceLabel);
        JPanel btnPanel = new JPanel();
        JButton undoBtn = new JButton("Remove Last Item (Undo)");
        JButton checkoutBtn = new JButton("Checkout (Finalize)");

        undoBtn.addActionListener(e -> removeLastItem());
        checkoutBtn.addActionListener(e -> checkout());

        btnPanel.add(undoBtn);
        btnPanel.add(checkoutBtn);
        bottomPanel.add(pricePanel);
        bottomPanel.add(btnPanel);
        add(bottomPanel, BorderLayout.SOUTH);

        loadCartData();
        setVisible(true);
    }

    private void loadCartData() {
        tableModel.setRowCount(0);
        List<Product> items = currentUser.getCart().getProductsStack();
        for (Product p : items) {
            tableModel.addRow(new Object[]{p.getName(), p.getPrice()});
        }
        totalPriceLabel.setText("Total Price: $" + currentUser.getCart().calculateTotalPrice());
    }

    private void removeLastItem() {
        if (!currentUser.getCart().getProductsStack().isEmpty()) {
            currentUser.getCart().removeLastProduct();
            JOptionPane.showMessageDialog(this, "Item removed.");
            loadCartData();
        } else {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
        }
    }

    private void checkout() {
        Stack<Product> cartStack = currentUser.getCart().getProductsStack();

        if (cartStack.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        double total = currentUser.getCart().calculateTotalPrice();

        // 1. فقط چک می‌کنیم پول دارد یا نه (اما کسر نمی‌کنیم)
        if (currentUser.getBalance() < total) {
            JOptionPane.showMessageDialog(this,
                    "Insufficient Balance!\nYour Balance: $" + currentUser.getBalance() + "\nTotal Cost: $" + total,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Product> orderItems = new ArrayList<>(cartStack);
        Order newOrder = new Order(currentUser.getUsername(), orderItems, total);

        storedata.orderQueue.add(newOrder);

        cartStack.clear();
        storedata.saveAllData();

        JOptionPane.showMessageDialog(this,
                "Order placed successfully!\nAmount: $" + total +
                        "\nWait for Admin approval to deduct balance.");

        this.dispose();
    }
}
