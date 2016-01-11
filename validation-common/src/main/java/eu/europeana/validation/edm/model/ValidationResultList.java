package eu.europeana.validation.edm.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Wrapper class for the batch validation
 * Created by ymamakis on 12/22/15.
 */
@XmlRootElement
public class ValidationResultList {

    /**
     * List of validation results. If the list is empty then we assume success == true
     */
    @XmlElement
    private List<ValidationResult> resultList;

    /**
     * The result of the batch validation
     */
    @XmlElement
    private boolean success;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<ValidationResult> getResultList() {
        return resultList;
    }

    public void setResultList(List<ValidationResult> resultList) {
        this.resultList = resultList;
    }
}
