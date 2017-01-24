package au.edu.unsw.cse.data.api.infrastructure;

import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;

import au.edu.unsw.cse.data.api.security.AuthenticationFilter;

public class DataApiApplication extends ResourceConfig {
	public DataApiApplication() {
		property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
		register(AuthenticationFilter.class);
		register(EntityResourceDynamicFeature.class);
		register(new DataApiBinder());
		packages(true, "au.edu.unsw.cse.data.api.resources");
	}
}
