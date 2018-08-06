package model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class ProductCartOut implements java.io.Serializable {
    private String id;
    private String productId;
    private String productName;
    private double amount;
    private double price;

    public ProductCartOut() {}

    public ProductCartOut(String id, String productId, String productName, double amount, double price) {
        this.id = id;
        this.productId = productId;
        this.productName = productName;
        this.amount = amount;
        this.price = price;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ProductCartOut get() {
        return this;
    }

    @GET
    @Path("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @GET
    @Path("productId")
    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    @GET
    @Path("productName")
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    @GET
    @Path("amount")
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @GET
    @Path("price")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}