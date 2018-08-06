package model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class Account implements java.io.Serializable {
    private String id;
    private String name;
    private String phoneNumber;
    private String address;

    public Account() {}

    public Account(String id, String name, String phoneNumber, String address) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    @GET
    @RolesAllowed("logged-in")
    @Produces(MediaType.APPLICATION_JSON)
    public Account get() {
        return this;
    }

    @GET
    @RolesAllowed("logged-in")
    @Path("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @GET
    @RolesAllowed("logged-in")
    @Path("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @GET
    @RolesAllowed("logged-in")
    @Path("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @GET
    @RolesAllowed("logged-in")
    @Path("address")
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}