package model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class MinimumAmount implements java.io.Serializable {
    private String id;
    private String name;
    private double amount;
    private double minimumStock;

    public MinimumAmount() {}

    public MinimumAmount(String id, String name, double amount, double minimumStock) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.minimumStock = minimumStock;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public MinimumAmount get() {
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
    @Path("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
    @Path("minimumStock")
    public double getMinimumStock() {
        return minimumStock;
    }

    public void seMinimumStock(double minimumStock) {
        this.minimumStock = minimumStock;
    }
}
