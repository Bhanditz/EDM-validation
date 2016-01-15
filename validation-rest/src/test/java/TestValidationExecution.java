import eu.europeana.validation.edm.model.ValidationResult;
import eu.europeana.validation.edm.model.ValidationResultList;
import eu.europeana.validation.edm.validation.ValidationExecutionService;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by gmamakis on 18-12-15.
 */

public class TestValidationExecution {


    @Test
    public void testSingleValidationSuccess() throws IOException, ExecutionException, InterruptedException {
        String fileToValidate = IOUtils.toString(new FileInputStream("src/test/resources/Item_35834473.xml"));
        ValidationExecutionService service = new ValidationExecutionService();
        ValidationResult result = service.singleValidation("EDM-INTERNAL",fileToValidate);
        Assert.assertEquals(true,result.isSuccess());
        Assert.assertNull(result.getRecordId());
        Assert.assertNull(result.getMessage());
    }

    @Test
    public void testSingleValidationFailure() throws IOException, ExecutionException, InterruptedException {
        String fileToValidate = IOUtils.toString(new FileInputStream("src/test/resources/Item_35834473_wrong.xml"));
        ValidationExecutionService service = new ValidationExecutionService();
        ValidationResult result = service.singleValidation("EDM-INTERNAL",fileToValidate);
        Assert.assertEquals(false,result.isSuccess());
        Assert.assertNotNull(result.getRecordId());
        Assert.assertNotNull(result.getMessage());
    }

    @Test
    public void testSingleValidationFailureWrongSchema() throws IOException, ExecutionException, InterruptedException {
        String fileToValidate = IOUtils.toString(new FileInputStream("src/test/resources/Item_35834473.xml"));
        ValidationExecutionService service = new ValidationExecutionService();
        ValidationResult result = service.singleValidation("EDM-EXTERNAL",fileToValidate);
        Assert.assertEquals(false,result.isSuccess());
        Assert.assertNotNull(result.getRecordId());
        Assert.assertNotNull(result.getMessage());
    }

    @Test
    public void testBatchValidationSuccess() throws IOException, ExecutionException, InterruptedException, ZipException {


    String fileName = "src/test/resources/test";
        ZipFile file = new ZipFile("src/test/resources/test.zip");
        file.extractAll(fileName);

        File[] files = new File(fileName).listFiles();
        List<String> xmls = new ArrayList<>();
        for (File input : files) {
            xmls.add(IOUtils.toString(new FileInputStream(input)));
        }
        ValidationExecutionService service = new ValidationExecutionService();
        ValidationResultList result = service.batchValidation("EDM-INTERNAL",xmls);
        Assert.assertEquals(true,result.isSuccess());
        Assert.assertEquals(0, result.getResultList().size());

        FileUtils.forceDelete(new File(fileName));
    }

    @Test
    public void testBatchValidationFailure() throws IOException, ExecutionException, InterruptedException, ZipException {
        String fileName = "src/test/resources/test_wrong";
        ZipFile file = new ZipFile("src/test/resources/test_wrong.zip");
        file.extractAll(fileName);

        File[] files = new File(fileName).listFiles();
        List<String> xmls = new ArrayList<>();
        for (File input : files) {
            xmls.add(IOUtils.toString(new FileInputStream(input)));
        }
        ValidationExecutionService service = new ValidationExecutionService();
        ValidationResultList result = service.batchValidation("EDM-INTERNAL",xmls);
        Assert.assertEquals(false,result.isSuccess());
        Assert.assertEquals(1, result.getResultList().size());


        FileUtils.forceDelete(new File(fileName));
    }

    @Test
    public void testBatchValidationFailureWrongSchema() throws IOException, ExecutionException, InterruptedException, ZipException {
        String fileName = "src/test/resources/test";
        ZipFile file = new ZipFile("src/test/resources/test.zip");
        file.extractAll(fileName);

        File[] files = new File(fileName).listFiles();
        List<String> xmls = new ArrayList<>();
        for (File input : files) {
            xmls.add(IOUtils.toString(new FileInputStream(input)));
        }
        ValidationExecutionService service = new ValidationExecutionService();
        ValidationResultList result = service.batchValidation("EDM-EXTERNAL",xmls);
        Assert.assertEquals(false,result.isSuccess());
        Assert.assertEquals(1506, result.getResultList().size());


        FileUtils.forceDelete(new File(fileName));
    }
}
