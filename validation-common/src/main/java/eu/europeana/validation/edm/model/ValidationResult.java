package eu.europeana.validation.edm.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Validation result bean
 * Created by ymamakis on 12/22/15.
 */

@XmlRootElement
public class ValidationResult {

    /**
     * The record id that generated the issue. Null if success
     */
    @XmlElement
    private String recordId;

    /**
     * The error code. Null if success
     */
    @XmlElement
    private String message;

    /**
     * The validation result. true if success, false if failure
     */
    @XmlElement
    private boolean success;

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
