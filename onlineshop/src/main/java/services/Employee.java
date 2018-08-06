package services;

import com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.util.List;

@Path("employee")
@Singleton
public class Employee {

    @PUT
    @Path("put")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public boolean put(model.Employee model) {
        backend.Employee.Put(model);
        return true;
    }

    @DELETE
    @Path("delete")
    @RolesAllowed("admin")
    @Produces(MediaType.TEXT_PLAIN)
    public boolean delete(String email) {
        backend.Employee.Delete(email);
        return true;
    }

    @GET
    @Path("count")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public long count() {
        return backend.Employee.Count();
    }

    @GET
    @Path("search")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.Employee> search() {
        return backend.Employee.Search();
    }

    @POST
    @Path("search")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.Employee> search(JsonNode json) {
        return backend.Employee.Search(json);
    }
}
