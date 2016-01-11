package eu.europeana.validation.edm.rest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Created by ymamakis on 12/24/15.
 */
public class Config {


    private static String validationPath;

    public Config(){
        Properties props = new Properties();
        try {
            props.load(new FileInputStream("/home/ymamakis/git/EDM-validation/validation-client/src/main/resources/validation.properties"));
            validationPath= props.getProperty("validation.path");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public  String getValidationPath() {

        return validationPath;
    }


}
