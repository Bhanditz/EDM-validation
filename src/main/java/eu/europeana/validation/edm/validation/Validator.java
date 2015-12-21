package eu.europeana.validation.edm.validation;

import eu.europeana.validation.edm.Constants;
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

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gmamakis on 18-12-15.
 */
public class Validator implements Callable {


    public Validator(String schema, String document) {
        this.schema = schema;
        this.document = document;
    }

    private String schema;
    private String document;




    private void validate () throws Exception{
        Logger.getLogger("test").log(Level.SEVERE, "Validating");
        InputSource source = new InputSource();
        source.setByteStream(new ByteArrayInputStream(document.getBytes()));
        Document doc =EDMParser.getInstance().getEdmParser().parse(source);
        EDMParser.getInstance().getEdmValidator(schema).validate(new DOMSource(doc));
    }

    @Override
    public Object call() throws Exception {
        validate();
        return null;
    }
}

class EDMParser{
    private final static Logger LOGGER = Logger.getLogger(EDMParser.class.getName());



    private static DocumentBuilder edmParser;

    private static javax.xml.validation.Validator edmInternalValidator;
    private static javax.xml.validation.Validator edmExternalValidator;
    private static EDMParser p;
    private EDMParser(){
        try {
            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            Schema edmExternalSchema = factory
                    .newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(Constants.EDM_EXTERNAL_SCHEMA_LOCATION)));
            Schema edmInternalSchema = factory
                    .newSchema(new StreamSource(this.getClass().getClassLoader().getResourceAsStream(Constants.EDM_INTERNAL_SCHEMA_LOCATION))
                    );
            edmExternalValidator = edmExternalSchema.newValidator();
            edmInternalValidator = edmInternalSchema.newValidator();
            DocumentBuilderFactory parseFactory = DocumentBuilderFactory.newInstance();
            parseFactory.setNamespaceAware(true);
            edmParser = parseFactory.newDocumentBuilder();
        } catch (SAXException e) {
            LOGGER.log(Level.SEVERE,e.getMessage());
        } catch (ParserConfigurationException e) {
            LOGGER.log(Level.SEVERE, e.getMessage());
        }


    }

    public  DocumentBuilder getEdmParser() {
            return edmParser;
    }
    public  javax.xml.validation.Validator getEdmValidator(String schema) throws Exception {
        if(StringUtils.equals(schema,Constants.EDM_INTERNAL_SCHEMA)){
            return edmInternalValidator;
        } else  if(StringUtils.equals(schema,Constants.EDM_EXTERNAL_SCHEMA)){
            return edmExternalValidator;
        } else{
            throw new Exception("Schema not found");
        }
    }

    public static EDMParser getInstance() {
       if(p==null){
           p =new EDMParser();

       }
        return p;
    }
}
