package model;

public class ProductCartIn implements java.io.Serializable {
    private String productId;
    private double amount;

    public ProductCartIn() {}

    public ProductCartIn(String productId, String name, double amount) {
        this.productId = productId;
        this.amount = amount;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}