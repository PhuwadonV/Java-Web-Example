package filter;

import backend.Auth;
import backend.Employee;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.PreMatching;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.Provider;
import java.security.Principal;

@Provider
@PreMatching
public class Permission implements ContainerRequestFilter {

	@Override
	public void filter(final ContainerRequestContext requestContext) {
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return new Principal() {
                    @Override
                    public String getName() {
                        try {
                            return Auth.GetID();
                        }
                        catch(Exception e) {
                            return null;
                        }
                    }
                };
            }

            @Override
            public boolean isUserInRole(final String role) {
                try {
                    if(!Auth.IsLoggedIn()) return false;
                    else if(role.equals("logged-in")) return true;
                    else if(Auth.IsAdmin()) return true;
                    else if(role.equals("employee")) return Employee.IsEmployee();
                    return false;
                }
                catch(Exception e) {
                    return false;
                }
            }

            @Override
            public boolean isSecure() {
                return false;
            }

            @Override
            public String getAuthenticationScheme() {
                return null;
            }
        });
	}
}