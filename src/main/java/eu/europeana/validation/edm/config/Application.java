package eu.europeana.validation.edm.config;

import eu.europeana.validation.edm.UploadResource;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Created by gmamakis on 18-12-15.
 */

@ApplicationPath("/")
public class Application extends ResourceConfig {
    public Application(){
        super();
        register(UploadResource.class);
        register(MultiPartFeature.class);
    }
}
