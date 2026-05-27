package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Email Log model class for tracking email notifications sent to suppliers.
 * Required for Module 6: Supplier Email Notification.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class EmailLog implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int logId;
    private int tenderId;
    private String recipientEmail;
    private String subject;
    private String outcome;
    private Timestamp sentAt;
    private String status;
    
    // Constants for outcomes
    public static final String OUTCOME_WON = "WON";
    public static final String OUTCOME_NOT_WON = "NOT_WON";
    
    // Constants for statuses
    public static final String STATUS_SENT = "SENT";
    public static final String STATUS_FAILED = "FAILED";
    
    // Additional fields for display
    private String tenderReference;
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public EmailLog() {
        this.status = STATUS_SENT;
    }
    
    /**
     * Constructs a new Email Log entry.
     * 
     * @param tenderId the tender ID
     * @param recipientEmail the recipient's email address
     * @param subject the email subject
     * @param outcome the outcome (WON or NOT_WON)
     */
    public EmailLog(int tenderId, String recipientEmail, String subject, String outcome) {
        this.tenderId = tenderId;
        this.recipientEmail = recipientEmail;
        this.subject = subject;
        this.outcome = outcome;
        this.status = STATUS_SENT;
    }
    
    /**
     * Gets the log ID.
     * 
     * @return the unique identifier for this log entry
     */
    public int getLogId() {
        return logId;
    }
    
    /**
     * Sets the log ID.
     * 
     * @param logId the unique identifier to set
     */
    public void setLogId(int logId) {
        this.logId = logId;
    }
    
    /**
     * Gets the tender ID.
     * 
     * @return the tender ID
     */
    public int getTenderId() {
        return tenderId;
    }
    
    /**
     * Sets the tender ID.
     * 
     * @param tenderId the tender ID to set
     */
    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }
    
    /**
     * Gets the recipient email address.
     * 
     * @return the recipient's email
     */
    public String getRecipientEmail() {
        return recipientEmail;
    }
    
    /**
     * Sets the recipient email address.
     * 
     * @param recipientEmail the email to set
     */
    public void setRecipientEmail(String recipientEmail) {
        this.recipientEmail = recipientEmail;
    }
    
    /**
     * Gets the email subject.
     * 
     * @return the subject line
     */
    public String getSubject() {
        return subject;
    }
    
    /**
     * Sets the email subject.
     * 
     * @param subject the subject to set
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }
    
    /**
     * Gets the outcome (WON or NOT_WON).
     * 
     * @return the outcome
     */
    public String getOutcome() {
        return outcome;
    }
    
    /**
     * Sets the outcome.
     * 
     * @param outcome the outcome to set
     */
    public void setOutcome(String outcome) {
        this.outcome = outcome;
    }
    
    /**
     * Gets the sent timestamp.
     * 
     * @return when the email was sent
     */
    public Timestamp getSentAt() {
        return sentAt;
    }
    
    /**
     * Sets the sent timestamp.
     * 
     * @param sentAt the timestamp to set
     */
    public void setSentAt(Timestamp sentAt) {
        this.sentAt = sentAt;
    }
    
    /**
     * Gets the email status.
     * 
     * @return the status (SENT or FAILED)
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the email status.
     * 
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the tender reference number for display.
     * 
     * @return the tender reference
     */
    public String getTenderReference() {
        return tenderReference;
    }
    
    /**
     * Sets the tender reference number.
     * 
     * @param tenderReference the reference to set
     */
    public void setTenderReference(String tenderReference) {
        this.tenderReference = tenderReference;
    }
    
    /**
     * Checks if the email was successfully sent.
     * 
     * @return true if status is SENT
     */
    public boolean isSent() {
        return STATUS_SENT.equals(this.status);
    }
    
    /**
     * Returns a string representation of the EmailLog object.
     * 
     * @return string containing log details
     */
    @Override
    public String toString() {
        return "EmailLog{" +
                "logId=" + logId +
                ", tenderId=" + tenderId +
                ", recipientEmail='" + recipientEmail + '\'' +
                ", outcome='" + outcome + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}