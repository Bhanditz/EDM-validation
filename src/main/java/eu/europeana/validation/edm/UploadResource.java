package eu.europeana.validation.edm;


import eu.europeana.validation.edm.validation.ValidationExecutionService;
import net.lingala.zip4j.core.ZipFile;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;


import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Path("/")
public class UploadResource {


    final ValidationExecutionService validator = new ValidationExecutionService();

    @GET
    @Path("/hello")
    @Produces(MediaType.APPLICATION_JSON)
    public Response sayHello(){
        return Response.ok("Hello").build();
    }

    @POST
    @Path("/{schema}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response validate(@PathParam("schema") String targetSchema, @FormParam("record") String record){
        try {
            validator.singleValidation(targetSchema, record);
        } catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

    @POST
    @Path("/batch/{schema}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public Response batchValidate(@PathParam("schema") String targetSchema,
                                  @FormDataParam("file") InputStream zipFile,
                                  @FormDataParam("file") FormDataContentDisposition fileDisposition){
        String fileName = "/tmp/"+fileDisposition.getName()+"/"+new Date().getTime();
        try {

            FileUtils.copyInputStreamToFile(zipFile,new File(fileName + ".zip"));
            ZipFile file = new ZipFile(fileName+".zip");
            file.extractAll(fileName);
            FileUtils.deleteQuietly(new File(fileName+".zip"));
            File[] files = new File(fileName).listFiles();
            List<String> xmls = new ArrayList<>();
            for(File input:files){
              xmls.add(IOUtils.toString(new FileInputStream(input)));
            }
            validator.batchValidation(targetSchema,xmls);
            FileUtils.forceDelete(new File(fileName));
        }catch (Exception e){
            return Response.serverError().entity(e.getMessage()).build();
        }
        return Response.ok().build();
    }

}
