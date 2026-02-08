package ui;

import Model.User;
import service.storedata;

import javax.swing.*;
import java.awt.*;

public class RegisterFrame extends JFrame {

    public RegisterFrame() {
        setTitle("Register New User");
        setSize(400, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        JTextField userField = new JTextField();
        JPasswordField passField = new JPasswordField();

        String[] roles = {"CUSTOMER", "ADMIN"};
        JComboBox<String> roleCombo = new JComboBox<>(roles);

        JTextField balanceField = new JTextField("0.0");

        add(new JLabel(" Username:"));
        add(userField);

        add(new JLabel(" Password:"));
        add(passField);

        add(new JLabel(" Role:"));
        add(roleCombo);

        JLabel balanceLabel = new JLabel(" Initial Balance:");
        add(balanceLabel);
        add(balanceField);

        roleCombo.addActionListener(e -> {
            String selected = (String) roleCombo.getSelectedItem();
            boolean isCustomer = "CUSTOMER".equals(selected);
            balanceField.setEnabled(isCustomer);
            if (!isCustomer) balanceField.setText("0.0");
        });

        JButton registerBtn = new JButton("Register");
        JButton backBtn = new JButton("Back to Login");

        add(registerBtn);
        add(backBtn);

        registerBtn.addActionListener(e -> {
            String username = userField.getText().trim();
            String password = new String(passField.getPassword()).trim();
            String role = (String) roleCombo.getSelectedItem();
            double balance = 0.0;

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill all fields.");
                return;
            }

            try {
                if ("CUSTOMER".equals(role)) {
                    balance = Double.parseDouble(balanceField.getText());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Balance format.");
                return;
            }

            User newUser;
            if ("ADMIN".equals(role)) {
                newUser = new User(username, password, role);
            } else {
                newUser = new User(username, password, role, balance);
            }

            String result = storedata.registerUser(newUser);

            switch (result) {
                case "SUCCESS":
                    JOptionPane.showMessageDialog(this, "User registered successfully!");
                    this.dispose();
                    new Loginframe();
                    break;

                case "USERNAME_EXISTS":
                    JOptionPane.showMessageDialog(this,
                            "Username already exists! Please choose another one.",
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;

                case "PASSWORD_EXISTS":
                    JOptionPane.showMessageDialog(this,
                            "This Password is already in use by another user.\nPlease choose a unique password.",
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE);
                    break;

                default:
                    JOptionPane.showMessageDialog(this, "Unknown error occurred.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backBtn.addActionListener(e -> {
            this.dispose();
            new Loginframe();
        });

        setVisible(true);
    }
}
