package eu.europeana.validation.edm.validation;

import eu.europeana.validation.edm.Constants;
import eu.europeana.validation.edm.model.ValidationResult;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.concurrent.*;

/**
 * EDM Validator class
 * Created by gmamakis on 18-12-15.
 */
public class Validator implements Callable<ValidationResult> {

    private static final Logger logger = Logger.getRootLogger();

    /**
     * Constructor specifying the schema to validate against and the document
     *
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
     *
     * @return The outcome of the Validation
     */
    private ValidationResult validate() {
        logger.info("Validation started");

        InputSource source = new InputSource();
        source.setByteStream(new ByteArrayInputStream(document.getBytes()));
        try {
            Document doc = EDMParser.getInstance().getEdmParser().parse(source);

            EDMParser.getInstance().getEdmValidator(schema).validate(new DOMSource(doc));
            StringReader reader = null;
            if (StringUtils.equals(schema, "EDM-EXTERNAL")) {
                reader = new StringReader(IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(Constants.SCHEMATRON_FILE)));
            } else {
                reader = new StringReader(IOUtils.toString(this.getClass().getClassLoader().getResourceAsStream(Constants.SCHEMATRON_FILE_INTERNAL)));
            }
            DOMResult result = new DOMResult();
            Transformer transformer = TransformerFactory.newInstance().newTemplates(new StreamSource(reader)).newTransformer();
            transformer.transform(new DOMSource(doc), result);

            NodeList nresults = result.getNode().getFirstChild().getChildNodes();
            for (int i = 0; i < nresults.getLength(); i++) {
                Node nresult = nresults.item(i);
                if ("failed-assert".equals(nresult.getLocalName())) {
                    System.out.println(nresult.getTextContent());
                    return constructValidationError(document, "Schematron error: " + nresult.getTextContent());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return constructValidationError(document, e);
        }
        return constructOk();
    }

    private ValidationResult constructValidationError(String document, Exception e) {
        ValidationResult res = new ValidationResult();
        res.setMessage(e.getMessage());
        res.setRecordId(StringUtils.substringBetween(document, "ProvidedCHO", ">"));
        if (StringUtils.isEmpty(res.getRecordId())) {
            res.setRecordId("Missing record identifier for EDM record");
        }

        res.setSuccess(false);
        return res;
    }

    private ValidationResult constructValidationError(String document, String message) {
        ValidationResult res = new ValidationResult();
        res.setMessage(message);
        res.setRecordId(StringUtils.substringBetween(document, "ProvidedCHO", ">"));
        if (StringUtils.isEmpty(res.getRecordId())) {
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
class EDMParser {
    private static EDMParser p;

    private EDMParser() {



    }

    /**
     * Get an EDM Parser using DOM
     *
     * @return
     */
    public DocumentBuilder getEdmParser() {
        try {
            DocumentBuilderFactory parseFactory = DocumentBuilderFactory.newInstance();

            parseFactory.setNamespaceAware(true);
            parseFactory.setFeature("http://apache.org/xml/features/validation/schema-full-checking", false);
            parseFactory.setFeature("http://apache.org/xml/features/honour-all-schemaLocations", true);

            return parseFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a JAXP schema validator (singleton)
     *
     * @param schema The schema to validate against (EDM-INTERNAL or EDM-EXTERNAL)
     * @return
     */
    public javax.xml.validation.Validator getEdmValidator(String schema) {
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


            if (StringUtils.equals(schema, Constants.EDM_INTERNAL_SCHEMA)) {
                Schema edmInternalSchema = factory
                        .newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(Constants.EDM_INTERNAL_SCHEMA_LOCATION)));


                return edmInternalSchema.newValidator();
            } else {
                Schema edmExternalSchema = factory
                        .newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(Constants.EDM_EXTERNAL_SCHEMA_LOCATION)));
                return edmExternalSchema.newValidator();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a parser instance as a singleton
     *
     * @return
     */
    public static EDMParser getInstance() {
        if (p == null) {
            p = new EDMParser();

        }
        return p;
    }


}
