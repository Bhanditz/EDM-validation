package eu.europeana.validation.edm.config;

import eu.europeana.validation.edm.UploadResource;
import eu.europeana.validation.edm.model.ValidationResult;
import eu.europeana.validation.edm.model.ValidationResultList;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Configuration file for Jersey
 */

@ApplicationPath("/")
public class Application extends ResourceConfig {
    public Application(){
        super();
        register(UploadResource.class);
        register(MultiPartFeature.class);

        register(ValidationResult.class);
        register(ValidationResultList.class);

    }
}
