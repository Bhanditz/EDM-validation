package eu.europeana.validation.edm;


import eu.europeana.validation.edm.model.ValidationResultList;
import eu.europeana.validation.edm.validation.ValidationExecutionService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * REST API Implementation of the Validation Service
 */
@Path("/")
public class UploadResource {


    private final ValidationExecutionService validator = new ValidationExecutionService();


    /**
     * Single Record validation class. The target schema is supplied as a path parameter (/validate/EDM-{INTERNAL,EXTERNAL})
     * and the record via POST as a form-data parameter
     * @param targetSchema The schema to validate against
     * @param record The record to validate
     * @return A serialized ValidationResult. The result is always an OK response unless an Exception is thrown (500)
     */
    @POST
    @Path("/{schema}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@PathParam("schema") String targetSchema, @FormParam("record") String record) {
        try {

            return Response.ok().entity(validator.singleValidation(targetSchema, record)).build();
        } catch (Exception e) {
            return Response.serverError().entity(e.getMessage()).build();
        }

    }

    /**
     * Batch Validation REST API implementation. It is exposed via /validate/batch/EDM-{EXTERNAL,INTERNAL}. The parameters are
     * a zip file with records (folders are not currently supported so records need to be at the root of the file)
     * @param targetSchema The schema to validate against
     * @param zipFile A zip file
     * @param fileDisposition The zip file parameters
     * @return A Validation result List. If the validation result is empty we assume that the success field is true
     */
    @POST
    @Path("/batch/{schema}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response batchValidate(@PathParam("schema") String targetSchema,
                                  @FormDataParam("file") InputStream zipFile,
                                  @FormDataParam("file") FormDataContentDisposition fileDisposition) {


        try {
            String fileName = "/tmp/" + fileDisposition.getName() + "/" + new Date().getTime();
            FileUtils.copyInputStreamToFile(zipFile, new File(fileName + ".zip"));

            ZipFile file = new ZipFile(fileName + ".zip");
            file.extractAll(fileName);
            FileUtils.deleteQuietly(new File(fileName + ".zip"));
            File[] files = new File(fileName).listFiles();
            List<String> xmls = new ArrayList<>();
            for (File input : files) {
                xmls.add(IOUtils.toString(new FileInputStream(input)));
            }
            ValidationResultList list = validator.batchValidation(targetSchema, xmls);
            if(list.getResultList()!=null||list.getResultList().size()==0){
                list.setSuccess(true);
            }
            FileUtils.forceDelete(new File(fileName));
            return Response.ok().entity(list).build();

        } catch (IOException e) {
            return Response.serverError().entity(e.getMessage()).build();
        } catch (InterruptedException e) {
            return Response.serverError().entity(e.getMessage()).build();
        } catch (ExecutionException e) {
            return Response.serverError().entity(e.getMessage()).build();
        } catch (ZipException e) {
            return Response.serverError().entity(e.getMessage()).build();
        }
    }

}
