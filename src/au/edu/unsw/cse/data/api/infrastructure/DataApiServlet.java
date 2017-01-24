package au.edu.unsw.cse.data.api.infrastructure;

import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

@WebServlet(name = "DataLake", urlPatterns = { "/api/*" }, initParams = {
		@WebInitParam(name = "javax.ws.rs.Application", value = "au.edu.unsw.cse.data.api.infrastructure.DataApiApplication"),
		@WebInitParam(name = "jersey.config.server.provider.classnames", value = "org.glassfish.jersey.media.multipart.MultiPartFeature") })
public class DataApiServlet extends org.glassfish.jersey.servlet.ServletContainer {
	private static final long serialVersionUID = -3829703594030383951L;
}
