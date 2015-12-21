package eu.europeana.validation.edm.validation;



import java.util.List;
import java.util.concurrent.*;

/**
 * Created by gmamakis on 18-12-15.
 */
public class ValidationExecutionService {
    private static final ExecutorService es = Executors.newFixedThreadPool(10);
    private static final ExecutorCompletionService cs = new ExecutorCompletionService(es);

    public void singleValidation(final String schema, final String document) throws Exception{
        cs.submit(new Validator(schema,document));
        cs.take().get();
    }

    public void batchValidation(final String schema, List<String> documents) throws Exception{
        for(final String document : documents) {
          singleValidation(schema,document);
        }
    }

}
