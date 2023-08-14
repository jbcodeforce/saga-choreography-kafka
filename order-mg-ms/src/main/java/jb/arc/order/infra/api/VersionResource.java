package jb.arc.order.infra.api;

import java.util.logging.Logger;

import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/version")
@ApplicationScoped
public class VersionResource {
    private static final Logger logger = Logger.getLogger(VersionResource.class.getName());
    @Inject
    @ConfigProperty(name="app.version")
    public String version;

    @GET
    public String getVersion(){
        return "{ \"version\": \"" + version + "\"}";
    }

    void onStart(@Observes StartupEvent ev){
		logger.info(getVersion());
	}
}
