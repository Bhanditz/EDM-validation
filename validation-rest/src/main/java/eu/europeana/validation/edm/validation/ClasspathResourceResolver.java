package eu.europeana.validation.edm.validation;

import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class enabling classpath XSD reading for split XSDs. This is because of an issue with JAXP XSD loading
 * Created by ymamakis on 12/21/15.
 */
public class ClasspathResourceResolver implements LSResourceResolver {
    private String prefix;
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        try {
            LSInput input = new ClasspathLSInput();
            InputStream stream;
            if(!systemId.startsWith("http")) {
                Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Reading classpath stream: " + (prefix+"/"+systemId));
                stream = getClass().getClassLoader().getResourceAsStream(prefix+"/"+systemId);
            }else {
              Logger.getLogger(this.getClass().getName()).log(Level.INFO, "Reading URL stream: " + systemId);
                stream = new URL(systemId).openStream();
            }
            input.setPublicId(publicId);
            input.setSystemId(systemId);
            input.setBaseURI(baseURI);
            input.setCharacterStream(new InputStreamReader(stream));

            return input;
        } catch (Exception e){
            e.printStackTrace();
        } return null;
    }
    /**
     * @return the prefix
     */
    public String getPrefix() {
        return prefix;
    }
    /**
     * @param prefix the prefix to set
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }
}

