package ui;

import Model.User;
import service.storedata;

import javax.swing.*;
import java.awt.*;

public class Loginframe extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public Loginframe() {
        setTitle("Login System");
        setSize(350, 220);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(3, 1));

        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Username:"));
        usernameField = new JTextField(15);
        userPanel.add(usernameField);

        JPanel passPanel = new JPanel();
        passPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField(15);
        passPanel.add(passwordField);

        JPanel btnPanel = new JPanel();

        JButton loginBtn = new JButton("Login");
        JButton registerBtn = new JButton("Sign Up");

        loginBtn.addActionListener(e -> performLogin());

        registerBtn.addActionListener(e -> {
            this.dispose();
            new RegisterFrame();
        });

        btnPanel.add(loginBtn);
        btnPanel.add(registerBtn);

        add(userPanel);
        add(passPanel);
        add(btnPanel);

        setVisible(true);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        System.out.println("Attempting login for: " + username);

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields!", "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        User user = storedata.login(username, password);

        if (user != null) {
            System.out.println("Login Successful! Role: " + user.getRole());

            new Mainframe(user);

            this.dispose();
        } else {
            System.out.println("Login Failed.");
            JOptionPane.showMessageDialog(this, "Invalid Username or Password!", "Login Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
