package eu.europeana.validation.edm.config;

import eu.europeana.validation.edm.UploadResource;
import eu.europeana.validation.edm.model.ValidationResult;
import eu.europeana.validation.edm.model.ValidationResultList;

import io.swagger.jaxrs.config.BeanConfig;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;

/**
 * Configuration file for Jersey
 */

@ApplicationPath("/rest")
public class Application extends ResourceConfig {
    public Application(){
        super();
        register(UploadResource.class);
        register(MultiPartFeature.class);

        register(ValidationResult.class);
        register(ValidationResultList.class);
        register(io.swagger.jaxrs.listing.ApiListingResource.class);
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8080");
        beanConfig.setBasePath("/rest");
        beanConfig.setResourcePackage("eu.europeana.validation.edm");
        beanConfig.setScan(true);
    }
}
