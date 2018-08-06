package model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class ProductSearch implements java.io.Serializable {
    private String id;
    private String name;
    private String imgUrl;
    private double price;
    private double amount;
    private double sold;
    private long date;

    public ProductSearch() {}

    public ProductSearch(String id, String name, String imgUrl, double price, double amount, double sold, long date) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
        this.price = price;
        this.amount = amount;
        this.sold = sold;
        this.date = date;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ProductSearch get() {
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
    @Path("imgUrl")
    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    @GET
    @Path("price")
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
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
    @Path("sold")
    public double getSold() {
        return sold;
    }

    public void setSold(double sold) {
        this.sold = sold;
    }

    @GET
    @Path("date")
    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
