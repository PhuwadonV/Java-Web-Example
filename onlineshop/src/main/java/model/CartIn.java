package model;

import java.util.List;

public class CartIn implements java.io.Serializable {
    private String address;
    private double totalPrice;
    private List<ProductCartIn> products;

    public CartIn() {}

    public CartIn(String address, double totalPrice, List<ProductCartIn> products) {
        this.address = address;
        this.totalPrice = totalPrice;
        this.products = products;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public List<ProductCartIn> getProducts() {
        return products;
    }

    public void setProducts(List<ProductCartIn> products) {
        this.products = products;
    }
}
