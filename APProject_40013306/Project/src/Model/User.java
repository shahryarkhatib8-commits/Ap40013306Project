package Model;

public class User {
    private String username;
    private String password;
    private String role; // "ADMIN" or "CUSTOMER"
    private ShoppingCart cart;
    private double balance;

    public User(String username, String password, String role, double balance) {
        this.username = username;
        this.password = password;
        this.role = role;
        this.balance = balance;
        this.cart = new ShoppingCart();
    }


    public User(String username, String password, String role) {
        this(username, password, role, 0.0);
    }

    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }

    public ShoppingCart getCart() { return cart; }

    public double getBalance() { return balance; }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return username + " (" + role + ")";
    }
}
