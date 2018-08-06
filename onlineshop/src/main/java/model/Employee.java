package model;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

public class Employee implements java.io.Serializable {
    private String email;
    private String name;

    public Employee() {}

    public Employee(String email, String name) {
        this.email = email;
        this.name = name;
    }

    @GET
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Employee get() {
        return this;
    }

    @GET
    @RolesAllowed("admin")
    @Path("email")
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @GET
    @RolesAllowed("admin")
    @Path("name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}