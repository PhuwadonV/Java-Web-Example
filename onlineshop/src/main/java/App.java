import javax.ws.rs.ApplicationPath;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.filter.RolesAllowedDynamicFeature;

@ApplicationPath("api")
public class App extends ResourceConfig {
    public App() {
        register(RolesAllowedDynamicFeature.class);
        register(JacksonFeature.class);
        register(MultiPartFeature.class);
        register(filter.Permission.class);
        packages("services");
    }
}