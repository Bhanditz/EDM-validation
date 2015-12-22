package eu.europeana.validation.edm.validation;

import eu.europeana.validation.edm.Constants;
import eu.europeana.validation.edm.model.ValidationResult;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;

import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * EDM Validator class
 * Created by gmamakis on 18-12-15.
 */
public class Validator implements Callable<ValidationResult> {


    /**
     * Constructor specifying the schema to validate against and the document
     * @param schema
     * @param document
     */
    public Validator(String schema, String document) {
        this.schema = schema;
        this.document = document;
    }

    private String schema;
    private String document;


    /**
     * Validate method using JAXP
     * @return The outcome of the Validation
     */
    private ValidationResult validate () {
        Logger.getLogger("test").log(Level.SEVERE, "Validating");
        InputSource source = new InputSource();
        source.setByteStream(new ByteArrayInputStream(document.getBytes()));
        try {
            Document doc = EDMParser.getInstance().getEdmParser().parse(source);

            EDMParser.getInstance().getEdmValidator(schema).validate(new DOMSource(doc));


        } catch(Exception e){
            return constructValidationError(document, e);
        }
        return constructOk();
    }

    private ValidationResult constructValidationError(String document, Exception e) {
        ValidationResult res = new ValidationResult();
        res.setMessage(e.getMessage());
        res.setRecordId(StringUtils.substringBetween(document,"ProvidedCHO",">"));
        if(StringUtils.isEmpty(res.getRecordId())){
            res.setRecordId("Missing record identifier for EDM record");
        }

        res.setSuccess(false);
        return res;
    }

    private ValidationResult constructOk() {
        ValidationResult res = new ValidationResult();

        res.setSuccess(true);
        return res;
    }

    @Override
    public ValidationResult call() {

        return validate();
    }
}

/**
 * Helper class for EDM validation exposing two validator and a DOMParser
 */
class EDMParser{
    private final static Logger LOGGER = Logger.getLogger(EDMParser.class.getName());



    private static DocumentBuilder edmParser;

    private static javax.xml.validation.Validator edmInternalValidator;
    private static javax.xml.validation.Validator edmExternalValidator;
    private static EDMParser p;
    private EDMParser(){
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            ClasspathResourceResolver resolver = new ClasspathResourceResolver();
            //Set the prefix as schema since this is the folder where the schemas exist in the classpath
            resolver.setPrefix("schema");
            factory.setResourceResolver(resolver);
            //Allow for classpath related schema file locations
            factory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
            factory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);

            //Read the EDM-External and EDM-Internal schemas
            Schema edmExternalSchema = factory
                    .newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(Constants.EDM_EXTERNAL_SCHEMA_LOCATION)));
            Schema edmInternalSchema = factory
                    .newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(Constants.EDM_INTERNAL_SCHEMA_LOCATION))
                    );
            edmExternalValidator = edmExternalSchema.newValidator();
            edmInternalValidator = edmInternalSchema.newValidator();
            //Create a DOM Parser
            DocumentBuilderFactory parseFactory = DocumentBuilderFactory.newInstance();
            parseFactory.setNamespaceAware(true);
            parseFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
            parseFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);
            edmParser = parseFactory.newDocumentBuilder();
        } catch (SAXException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE,e.getMessage());
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, e.getMessage());
        }


    }

    /**
     * Get an EDM Parser using DOM
     * @return
     */
    public  DocumentBuilder getEdmParser() {
            return edmParser;
    }

    /**
     * Get a JAXP schema validator (singleton)
     * @param schema The schema to validate against (EDM-INTERNAL or EDM-EXTERNAL)
     * @return
     */
    public  javax.xml.validation.Validator getEdmValidator(String schema) {
        if(StringUtils.equals(schema,Constants.EDM_INTERNAL_SCHEMA)) {
            return edmInternalValidator;
        }
            return edmExternalValidator;
    }

    /**
     * Get a parser instance as a singleton
     * @return
     */
    public static EDMParser getInstance() {
       if(p==null){
           p =new EDMParser();

       }
        return p;
    }
}
