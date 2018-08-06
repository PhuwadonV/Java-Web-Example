package services;

import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("file/")
@Singleton
public class File {
    @GET
    @Path("url/{path: .+}")
    @RolesAllowed("employee")
    @Produces(MediaType.TEXT_PLAIN)
    public String get(@PathParam("path") String path) {
        return backend.FileStorage.SignUrl(path);
    }
}