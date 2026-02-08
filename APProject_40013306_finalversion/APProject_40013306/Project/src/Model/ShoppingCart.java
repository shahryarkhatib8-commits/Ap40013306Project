package Model;

import java.util.Stack;

public class ShoppingCart {
    private Stack<Product> products;

    public ShoppingCart() {
        this.products = new Stack<>();
    }

    public Stack<Product> getProductsStack() {
        return products;
    }

    public void addProduct(Product product) {
        products.push(product);
    }

    public Product removeLastProduct() {
        if (!products.isEmpty()) {
            return products.pop();
        }
        return null;
    }

    public double calculateTotalPrice() {
        double total = 0;
        for (Product p : products) {
            total += p.getPrice();
        }
        return total;
    }
}

