package services;

import com.google.appengine.api.users.User;

import java.net.URISyntaxException;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.inject.Singleton;
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Path("auth")
@PermitAll
@Singleton
public class Auth {

	@GET
	@Path("isLoggedIn")
	@Produces(MediaType.APPLICATION_JSON)
    public boolean isLoggedIn() {
		return backend.Auth.IsLoggedIn();
    }
	
	@GET
	@Path("login/{path : .+}")
    public Response loginPath(@PathParam("path") String path) {
		try {
			return Response.seeOther(backend.Auth.GetLoginUri(path)).build();
		}
		catch (URISyntaxException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
    }
	
	@GET
	@Path("loginUrl/{path : .+}")
	@Produces(MediaType.TEXT_PLAIN)
    public String loginPathUrl(@PathParam("path") String path) {
		return backend.Auth.GetLoginUrl(path);
    }
	
	@GET
	@Path("login")
    public Response login() throws URISyntaxException {
		return Response.seeOther(backend.Auth.GetLoginUri()).build();
    }
	
	@GET
	@Path("loginUrl")
	@RolesAllowed("logged-in")
	@Produces(MediaType.TEXT_PLAIN)
    public String loginUrl() {
		return backend.Auth.GetLoginUrl();
    }

	@GET
	@Path("logout/{path : .+}")
	@RolesAllowed("logged-in")
	public Response logoutPath(@PathParam("path") String path) {
		try {
			return Response.seeOther(backend.Auth.GetLogoutUri(path)).build();
		}
		catch (URISyntaxException e) {
			return Response.status(Response.Status.BAD_REQUEST).build();
		}
	}

	@GET
	@Path("logoutUrl/{path : .+}")
	@RolesAllowed("logged-in")
	@Produces(MediaType.TEXT_PLAIN)
	public String logoutPathUrl(@PathParam("path") String path) {
		return backend.Auth.GetLogoutUrl(path);
	}

	@GET
	@Path("logout")
	@RolesAllowed("logged-in")
	public Response logout() throws URISyntaxException {
		return Response.seeOther(backend.Auth.GetLogoutUri()).build();
	}

	@GET
	@Path("logoutUrl")
	@RolesAllowed("logged-in")
	@Produces(MediaType.TEXT_PLAIN)
	public String logoutUrl() {
		return backend.Auth.GetLogoutUrl();
	}

	@GET
	@Path("info")
	@RolesAllowed("logged-in")
	@Produces(MediaType.APPLICATION_JSON)
	public User info() {
		return backend.Auth.GetUser();
	}

	@GET
	@Path("isAdmin")
	@RolesAllowed("logged-in")
	@Produces(MediaType.APPLICATION_JSON)
	public boolean isAdmin() {
		return backend.Auth.IsAdmin();
	}

	@GET
	@Path("permission")
	@Produces(MediaType.TEXT_PLAIN)
	public String getPermission() {
		return backend.Auth.GetPermission();
	}

	@GET
	@Path("permissionLevel")
	@Produces(MediaType.APPLICATION_JSON)
	public int getPermissionLevel() {
		return backend.Auth.GetPermissionLevel();
	}
}