package dao.interfaces;

import model.EmailLog;
import java.util.List;

/**
 * Data Access Object interface for EmailLog entity operations.
 * Provides methods for tracking email notifications sent to suppliers.
 * Required for Module 6: Supplier Email Notification.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface EmailLogDAO {
    
    /**
     * Creates a new email log entry in the database.
     * 
     * @param emailLog the EmailLog object to create
     * @return the generated log ID, or -1 if creation failed
     */
    int create(EmailLog emailLog);
    
    /**
     * Finds an email log by its unique ID.
     * 
     * @param logId the log ID to search for
     * @return the EmailLog object, or null if not found
     */
    EmailLog findById(int logId);
    
    /**
     * Finds all email logs for a specific tender.
     * 
     * @param tenderId the tender ID
     * @return List of EmailLog objects for the tender
     */
    List<EmailLog> findByTenderId(int tenderId);
    
    /**
     * Finds all email logs sent to a specific recipient.
     * 
     * @param recipientEmail the recipient's email address
     * @return List of EmailLog objects sent to the recipient
     */
    List<EmailLog> findByRecipientEmail(String recipientEmail);
    
    /**
     * Finds email logs filtered by outcome.
     * 
     * @param outcome the outcome (WON or NOT_WON)
     * @return List of EmailLog objects with the specified outcome
     */
    List<EmailLog> findByOutcome(String outcome);
    
    /**
     * Finds email logs filtered by status.
     * 
     * @param status the status (SENT or FAILED)
     * @return List of EmailLog objects with the specified status
     */
    List<EmailLog> findByStatus(String status);
    
    /**
     * Updates an email log's status.
     * 
     * @param logId the log ID to update
     * @param status the new status
     * @return true if update was successful, false otherwise
     */
    boolean updateStatus(int logId, String status);
    
    /**
     * Deletes an email log from the database.
     * 
     * @param logId the log ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(int logId);
    
    /**
     * Deletes all email logs for a specific tender.
     * 
     * @param tenderId the tender ID
     * @return the number of logs deleted
     */
    int deleteByTenderId(int tenderId);
    
    /**
     * Retrieves all email logs from the database.
     * 
     * @return List of all EmailLog objects
     */
    List<EmailLog> findAll();
    
    /**
     * Retrieves email logs for a tender with tender reference populated.
     * 
     * @param tenderId the tender ID
     * @return List of EmailLog objects with tender references
     */
    List<EmailLog> findDetailedByTenderId(int tenderId);
    
    /**
     * Checks if an email has already been sent to a recipient for a tender.
     * 
     * @param tenderId the tender ID
     * @param recipientEmail the recipient's email
     * @return true if an email log exists, false otherwise
     */
    boolean hasEmailBeenSent(int tenderId, String recipientEmail);
    
    /**
     * Counts the total number of email logs.
     * 
     * @return the total log count
     */
    int countAll();
    
    /**
     * Counts email logs for a specific tender.
     * 
     * @param tenderId the tender ID
     * @return the number of logs for the tender
     */
    int countByTenderId(int tenderId);
    
    /**
     * Counts email logs by outcome.
     * 
     * @param outcome the outcome to count
     * @return the number of logs with the specified outcome
     */
    int countByOutcome(String outcome);
}