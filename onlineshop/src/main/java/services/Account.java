package services;

import com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("account")
@PermitAll
@Singleton
public class Account {

    @Path("")
    public model.Account get() {
        return backend.Account.Get();
    }

    @GET
    @Path("get/{id : .+}")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("employee")
    public model.Account getId(@PathParam("id") String id) {
        return backend.Account.Get(id);
    }

    @PUT
    @Path("put")
    @RolesAllowed("logged-in")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response put(JsonNode json) {
        backend.Account.Put(json);
        return Response.noContent().build();
    }
}