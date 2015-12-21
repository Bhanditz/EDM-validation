package eu.europeana.validation.edm.validation;

import com.sun.org.apache.xerces.internal.impl.xs.util.LSInputListImpl;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by ymamakis on 12/21/15.
 */
public class ClasspathResourceResolver implements LSResourceResolver {
    private String prefix;
    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        try {
            LSInput input = new ClasspathLSInput();
            InputStream stream =null;
            String newSystemId = prefix+"/"+systemId;
            if(!newSystemId.startsWith("http")) {
                stream = getClass().getClassLoader().getResourceAsStream(newSystemId);
            }else {
                stream = new URL(newSystemId).openStream();
            }
            Logger.getLogger(this.getClass().getName()).log(Level.INFO, "reading "+systemId);
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

