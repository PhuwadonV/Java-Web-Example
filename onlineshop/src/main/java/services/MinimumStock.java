package services;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("minimumStock")
@PermitAll
@Singleton
public class MinimumStock {

    @GET
    @Path("count")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public long count() {
        return backend.MinimumStock.Count();
    }

    @GET
    @Path("hasNew")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response hasNew(@DefaultValue("-1") @CookieParam("minimumStock-last-added") long lastAdded) {
        long currLastAdded = backend.MinimumStock.GetLastAdded();
        if(lastAdded == -1) {
            if(backend.MinimumStock.GetIsSearched()) return Response.ok(false).cookie(new NewCookie("minimumStock-last-added", "" + currLastAdded)).build();
            else Response.ok(true).build();
        }
        else if(lastAdded == currLastAdded) return Response.ok(false).build();
        return Response.ok(true).cookie(new NewCookie("minimumStock-last-added", "" + currLastAdded)).build();
    }

    @GET
    @Path("search/{offset : \\d+}/{limit : \\d+}")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public List<model.MinimumAmount> search(@PathParam("offset") int offset, @PathParam("limit") int limit) {
        return backend.MinimumStock.Search( offset, limit);
    }

    @DELETE
    @Path("delete")
    @RolesAllowed("employee")
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(String id) {
        backend.MinimumStock.Delete(id);
        return Response.noContent().build();
    }
}
