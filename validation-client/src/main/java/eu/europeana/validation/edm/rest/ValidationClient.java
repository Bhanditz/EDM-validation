package eu.europeana.validation.edm.rest;

import eu.europeana.validation.edm.model.ValidationResult;
import eu.europeana.validation.edm.model.ValidationResultList;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.glassfish.jersey.media.multipart.MultiPartFeature;

import javax.ws.rs.FormParam;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;


/**
 * Created by ymamakis on 12/24/15.
 */
public class ValidationClient {
    private Client client =  ClientBuilder.newBuilder().build();
    private Config config = new Config();
    public ValidationResult validateSingle(String targetSchema, String xmlPath) throws Exception{
    System.out.println(config.getValidationPath());
        WebTarget target  = client.target(config.getValidationPath()).path(targetSchema);

        Form form =new Form();
        form.param("record", FileUtils.readFileToString(new File(xmlPath)));
        return target.request().post(Entity.form(form)).readEntity(ValidationResult.class);
    }

    public ValidationResultList validateBatch(String targetSchema, String zipFilePath) throws Exception{
        Client client =  ClientBuilder.newBuilder().register(MultiPartFeature.class).register(ValidationResultList.class).build();
        WebTarget target  = client.target(config.getValidationPath()).path("batch/"+targetSchema);
        FormDataMultiPart part = new FormDataMultiPart();

        part.field("file", new FileInputStream(zipFilePath), MediaType.TEXT_PLAIN_TYPE);

        return target.request().post(Entity.entity(part, MediaType.MULTIPART_FORM_DATA_TYPE)).readEntity(ValidationResultList.class);
    }
}
