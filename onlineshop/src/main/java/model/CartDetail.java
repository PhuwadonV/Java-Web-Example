package model;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class CartDetail implements java.io.Serializable {
    private String id;
    private String accountId;
    private String email;
    private double totalPrice;
    private String address;
    private List<ProductCartOut> products;
    private double confirm;
    private String billImgUrl;
    private long date;

    public CartDetail() {}

    public CartDetail(String id, String accountId, String email, String address, double totalPrice, List<ProductCartOut> products, double confirm, String billImgUrl, long date) {
        this.id = id;
        this.accountId = accountId;
        this.email = email;
        this.totalPrice = totalPrice;
        this.products = products;
        this.address = address;
        this.confirm = confirm;
        this.billImgUrl = billImgUrl;
        this.date = date;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CartDetail get() {
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
    @Path("accountId")
    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }


    @GET
    @Path("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @GET
    @Path("address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @GET
    @Path("productCartsOuts")
    public List<ProductCartOut> getProducts() {
        return products;
    }

    @GET
    @Path("productCartsOut/{index : \\d+}")
    public ProductCartOut getProduct(@PathParam("index") int index) {
        return products.get(index);
    }

    public void setProduct(List<ProductCartOut> products) {
        this.products = products;
    }

    @GET
    @Path("confirm")
    public double getConfirm() {
        return confirm;
    }

    public void setConfirm(double confirm) {
        this.confirm = confirm;
    }

    @GET
    @Path("billImgUrl")
    public String getBillImgUrl() {
        return billImgUrl;
    }

    public void setBillImgUrl(String billImgUrl) {
        this.billImgUrl = billImgUrl;
    }

    @GET
    @Path("totalPrice")
    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
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
