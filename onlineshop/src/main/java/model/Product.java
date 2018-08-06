package model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class Product implements java.io.Serializable {
    private String id;
    private String name;
    private String tags;
    private String description;
    private String imgUrl;
    private double price;
    private double amount;
    private double minimumStock;
    private double sold;
    private long date;
    private long updatedTime;

    public Product() {}

    public Product(String id, String name, String tags, String description, String imgUrl, double price, double amount, double minimumStock, double sold, long date, long updatedTime) {
        this.id = id;
        this.name = name;
        this.tags = tags;
        this.description = description;
        this.imgUrl = imgUrl;
        this.price = price;
        this.amount = amount;
        this.minimumStock = minimumStock;
        this.sold = sold;
        this.date = date;
        this.updatedTime = updatedTime;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Product get() {
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
    @Path("tags")
    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    @GET
    @Path("description")
    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
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
    @Path("minimumStock")
    public double getMinimumStock() {
        return minimumStock;
    }

    public void seMinimumStock(double minimumStock) {
        this.minimumStock = minimumStock;
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

    @GET
    @Path("updatedTime")
    public long getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(long updatedTime) {
        this.updatedTime = updatedTime;
    }
}
