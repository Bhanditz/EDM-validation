package eu.europeana.validation.edm.validation;

import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import eu.europeana.validation.edm.model.Schema;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by ymamakis on 3/14/16.
 */
public class Configuration {

    private static Configuration INSTANCE;
    private static SchemaDao dao;

    private Configuration() {
        Properties properties = new Properties();
        try {
            properties.load(Configuration.class.getClassLoader().getResourceAsStream("validation.properties"));
            ServerAddress address = new ServerAddress(properties.getProperty("mongo.host"),
                    Integer.parseInt(properties.getProperty("mongo.port")));
            MongoClient client = new MongoClient(address);
            Morphia morphia = new Morphia();
            morphia.map(Schema.class);
            Datastore datastore= morphia.createDatastore(client,properties.getProperty("mongo.db"));
            datastore.ensureIndexes();
            dao = new SchemaDao(datastore,properties.getProperty("root.path"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Configuration getInstance(){
        if (INSTANCE==null){
            INSTANCE = new Configuration();
        }
        return INSTANCE;
    }

    public SchemaDao getDao(){
        return dao;
    }

}
