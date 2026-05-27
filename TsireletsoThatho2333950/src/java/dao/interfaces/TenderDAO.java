package dao.interfaces;

import model.Tender;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

/**
 * Data Access Object interface for Tender entity operations.
 * Provides methods for tender creation, retrieval, status management, and lifecycle operations.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface TenderDAO {
    
    /**
     * Creates a new tender in the database.
     * 
     * @param tender the Tender object to create
     * @return the generated tender ID, or -1 if creation failed
     */
    int create(Tender tender);
    
    /**
     * Finds a tender by its unique ID.
     * 
     * @param tenderId the tender ID to search for
     * @return the Tender object, or null if not found
     */
    Tender findById(int tenderId);
    
    /**
     * Finds a tender by its reference number.
     * 
     * @param referenceNumber the reference number (MPW-YYYY-NNNN)
     * @return the Tender object, or null if not found
     */
    Tender findByReferenceNumber(String referenceNumber);
    
    /**
     * Updates an existing tender's information.
     * Only allowed when tender is in DRAFT status.
     * 
     * @param tender the Tender object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(Tender tender);
    
    /**
     * Updates a tender's status.
     * Enforces valid status transitions in the lifecycle.
     * 
     * @param tenderId the tender ID to update
     * @param newStatus the new status
     * @return true if update was successful, false otherwise
     */
    boolean updateStatus(int tenderId, String newStatus);
    
    /**
     * Publishes a tender, changing status from DRAFT to OPEN.
     * Sets the published_at timestamp.
     * 
     * @param tenderId the tender ID to publish
     * @return true if publication was successful, false otherwise
     */
    boolean publish(int tenderId);
    
    /**
     * Automatically closes tenders whose submission deadline has passed.
     * Changes status from OPEN to CLOSED and sets closed_at timestamp.
     * 
     * @return the number of tenders that were closed
     */
    int closeExpiredTenders();
    
    /**
     * Starts the evaluation process for a tender.
     * Changes status from CLOSED to UNDER_EVALUATION.
     * 
     * @param tenderId the tender ID to start evaluation for
     * @return true if successful, false otherwise
     */
    boolean startEvaluation(int tenderId);
    
    /**
     * Marks a tender as evaluated when all evaluators have submitted scores.
     * Changes status from UNDER_EVALUATION to EVALUATED.
     * 
     * @param tenderId the tender ID to mark as evaluated
     * @return true if successful, false otherwise
     */
    boolean markAsEvaluated(int tenderId);
    
    /**
     * Awards a tender to the winning supplier.
     * Changes status from EVALUATED to AWARDED.
     * 
     * @param tenderId the tender ID to award
     * @return true if successful, false otherwise
     */
    boolean award(int tenderId);
    
    /**
     * Deletes a tender from the database.
     * Only allowed when tender is in DRAFT status.
     * 
     * @param tenderId the tender ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(int tenderId);
    
    /**
     * Retrieves all tenders from the database.
     * 
     * @return List of all Tender objects
     */
    List<Tender> findAll();
    
    /**
     * Retrieves tenders filtered by status.
     * 
     * @param status the status to filter by
     * @return List of Tender objects with the specified status
     */
    List<Tender> findByStatus(String status);
    
    /**
     * Retrieves tenders filtered by category.
     * 
     * @param category the category to filter by
     * @return List of Tender objects in the specified category
     */
    List<Tender> findByCategory(String category);
    
    /**
     * Retrieves tenders filtered by both status and category.
     * 
     * @param status the status to filter by (can be null)
     * @param category the category to filter by (can be null)
     * @return List of matching Tender objects
     */
    List<Tender> findByFilters(String status, String category);
    
    /**
     * Retrieves all open tenders that are accepting bids.
     * 
     * @return List of OPEN Tender objects with future deadlines
     */
    List<Tender> findOpenTenders();
    
    /**
     * Retrieves tenders created by a specific officer.
     * 
     * @param userId the officer's user ID
     * @return List of Tender objects created by the officer
     */
    List<Tender> findByCreator(int userId);
    
    /**
     * Retrieves tenders that are ready for evaluation.
     * Status must be CLOSED or UNDER_EVALUATION.
     * 
     * @return List of Tender objects ready for evaluation
     */
    List<Tender> findTendersForEvaluation();
    
    /**
     * Retrieves tenders that a specific evaluator can evaluate.
     * 
     * @param evaluatorId the evaluator's ID
     * @return List of Tender objects available for the evaluator
     */
    List<Tender> findTendersForEvaluator(int evaluatorId);
    
    /**
     * Generates the next sequential reference number.
     * Format: MPW-YYYY-NNNN where YYYY is current year and NNNN is sequential.
     * 
     * @return the generated reference number
     */
    String generateReferenceNumber();
    
    /**
     * Checks if a tender has any bids submitted.
     * 
     * @param tenderId the tender ID to check
     * @return true if bids exist, false otherwise
     */
    boolean hasBids(int tenderId);
    
    /**
     * Gets the number of bids submitted for a tender.
     * 
     * @param tenderId the tender ID
     * @return the bid count
     */
    int getBidCount(int tenderId);
    
    /**
     * Gets the lowest bid amount for a tender.
     * 
     * @param tenderId the tender ID
     * @return the lowest bid amount, or null if no bids
     */
    BigDecimal getLowestBidAmount(int tenderId);
    
    /**
     * Gets the shortest proposed timeline for a tender.
     * 
     * @param tenderId the tender ID
     * @return the shortest timeline in days, or 0 if no bids
     */
    int getShortestTimeline(int tenderId);
    
    /**
     * Counts the total number of tenders.
     * 
     * @return the total tender count
     */
    int countAll();
    
    /**
     * Counts tenders by status.
     * 
     * @param status the status to count
     * @return the number of tenders with the specified status
     */
    int countByStatus(String status);
    
    /**
     * Gets the next sequential number for the current year.
     * 
     * @return the next sequential number
     */
    int getNextSequenceNumber();
}