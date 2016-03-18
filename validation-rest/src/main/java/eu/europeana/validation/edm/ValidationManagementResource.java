package eu.europeana.validation.edm;

import eu.europeana.validation.edm.validation.ValidationManagementService;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created by ymamakis on 3/14/16.
 */
@Path("/schemas")
public class ValidationManagementResource {

    private ValidationManagementService service = new ValidationManagementService();
    @GET
    @Path("/schema/download/{name}")
    @Produces(MediaType.APPLICATION_OCTET_STREAM)
    public Response getZip(@PathParam("name") String name,@QueryParam("version")@DefaultValue("undefined")String version ){
        return Response.ok(new ByteArrayInputStream(service.getZip(name, version)),MediaType.APPLICATION_OCTET_STREAM).
                header("Content-Disposition", "attachment; filename=\"" + name +"-"+version + ".zip\"" ).build();
    }

    @POST
    @Path("/schema/{name}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createSchema(@PathParam("name") String name, @QueryParam("schemaPath") String schemaPath,
                                 @QueryParam("schematronPath")String schematronPath,
                                 @QueryParam("version")@DefaultValue("undefined")String version,
                                 @FormDataParam("file") InputStream zipFile,
                                 @FormDataParam("file") FormDataContentDisposition fileDisposition) throws IOException{
        service.createSchema(name,schemaPath,schematronPath,version,zipFile);
        return Response.created(URI.create("/schema/download/"+name)).build();
    }

    @PUT
    @Path("/schema/{name}")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response updateSchema(@PathParam("name") String name, @QueryParam("schemaPath") String schemaPath,
                                 @QueryParam("schematronPath")String schematronPath, @QueryParam("version")@DefaultValue("undefined")String version,
                                 @FormDataParam("file") InputStream zipFile,
                                 @FormDataParam("file") FormDataContentDisposition fileDisposition) throws IOException{

        service.updateSchema(name,schemaPath,schematronPath,version, zipFile);
        return Response.ok().build();
    }

    @DELETE
    @Path("/schema/{name}")
    public Response deleteSchema(@PathParam("name") String name, @QueryParam("version")@DefaultValue("undefined") String version){

        service.deleteSchema(name,version);
        return Response.ok().build();
    }

    @GET
    @Path("/schema/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getSchema(@PathParam("name") String name, @QueryParam("version")@DefaultValue("undefined") String version){
        return Response.ok(service.getSchemaByName(name,version)).build();
    }
    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllSchema(){
        return Response.ok(service.getAll()).build();
    }
}
