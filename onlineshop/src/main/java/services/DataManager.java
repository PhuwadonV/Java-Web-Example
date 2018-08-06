package services;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("dataManager")
@PermitAll
@Singleton
public class DataManager {

    @GET
    @Path("delete/{indexName : .+}/{id : .+}")
    @RolesAllowed("employee")
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(@PathParam("indexName") String indexName, @PathParam("id") String id) {
        backend.DataManager.Delete(indexName, id);
        return Response.noContent().build();
    }
}
