package service;

import dao.implementations.BidDAOImpl;
import dao.implementations.TenderDAOImpl;
import model.Tender;
import util.Constants;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for tender-related business logic.
 * Handles tender creation, publication, lifecycle management, and queries.
 * 
 * Required by Module 2: Tender Management.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class TenderService {
    
    private static final Logger LOGGER = Logger.getLogger(TenderService.class.getName());
    
    private final TenderDAOImpl tenderDAO;
    private final BidDAOImpl bidDAO;
    private final ReferenceNumberGenerator referenceNumberGenerator;
    
    /**
     * Constructor initializes DAO instances.
     */
    public TenderService() {
        this.tenderDAO = new TenderDAOImpl();
        this.bidDAO = new BidDAOImpl();
        this.referenceNumberGenerator = new ReferenceNumberGenerator();
    }
    
    /**
     * Creates a new tender in DRAFT status.
     * 
     * @param tender the Tender object to create
     * @return the generated tender ID, or -1 if creation fails
     */
    public int createTender(Tender tender) {
        String referenceNumber = referenceNumberGenerator.generateTenderReference();
        tender.setReferenceNumber(referenceNumber);
        tender.setStatus(Constants.TENDER_STATUS_DRAFT);
        
        int tenderId = tenderDAO.create(tender);
        
        if (tenderId != -1) {
            LOGGER.log(Level.INFO, "Tender created: {0} by user ID: {1}", 
                       new Object[]{referenceNumber, tender.getCreatedBy()});
        }
        
        return tenderId;
    }
    
    /**
     * Updates an existing tender.
     * Only allowed when tender is in DRAFT status.
     * 
     * @param tender the Tender object with updated information
     * @return true if update was successful
     */
    public boolean updateTender(Tender tender) {
        Tender existing = tenderDAO.findById(tender.getTenderId());
        
        if (existing == null) {
            LOGGER.log(Level.WARNING, "Cannot update - tender not found: ID {0}", tender.getTenderId());
            return false;
        }
        
        if (!existing.isEditable()) {
            LOGGER.log(Level.WARNING, "Cannot update - tender is not in DRAFT status: {0}", 
                       existing.getReferenceNumber());
            return false;
        }
        
        boolean success = tenderDAO.update(tender);
        
        if (success) {
            LOGGER.log(Level.INFO, "Tender updated: {0}", existing.getReferenceNumber());
        }
        
        return success;
    }
    
    /**
     * Publishes a tender, making it visible to suppliers.
     * Status changes from DRAFT to OPEN.
     * 
     * @param tenderId the tender ID to publish
     * @return true if publication was successful
     */
    public boolean publishTender(int tenderId) {
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null) {
            LOGGER.log(Level.WARNING, "Cannot publish - tender not found: ID {0}", tenderId);
            return false;
        }
        
        if (!Constants.TENDER_STATUS_DRAFT.equals(tender.getStatus())) {
            LOGGER.log(Level.WARNING, "Cannot publish - tender is not in DRAFT status: {0}", 
                       tender.getReferenceNumber());
            return false;
        }
        
        boolean success = tenderDAO.publish(tenderId);
        
        if (success) {
            LOGGER.log(Level.INFO, "Tender published: {0}", tender.getReferenceNumber());
        }
        
        return success;
    }
    
    /**
     * Closes expired tenders automatically.
     * Changes status from OPEN to CLOSED for tenders past their deadline.
     * This method is called by the TenderStatusUpdateFilter.
     * 
     * @return the number of tenders closed
     */
    public int closeExpiredTenders() {
        int closed = tenderDAO.closeExpiredTenders();
        if (closed > 0) {
            LOGGER.log(Level.INFO, "Auto-closed {0} expired tenders", closed);
        }
        return closed;
    }
    
    /**
     * Starts the evaluation process for a tender.
     * Status changes from CLOSED to UNDER_EVALUATION.
     * 
     * @param tenderId the tender ID
     * @return true if successful
     */
    public boolean startEvaluation(int tenderId) {
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null) {
            LOGGER.log(Level.WARNING, "Cannot start evaluation - tender not found: ID {0}", tenderId);
            return false;
        }
        
        if (!Constants.TENDER_STATUS_CLOSED.equals(tender.getStatus())) {
            LOGGER.log(Level.WARNING, "Cannot start evaluation - tender is not CLOSED: {0}", 
                       tender.getReferenceNumber());
            return false;
        }
        
        if (!tenderDAO.hasBids(tenderId)) {
            LOGGER.log(Level.WARNING, "Cannot start evaluation - no bids submitted: {0}", 
                       tender.getReferenceNumber());
            return false;
        }
        
        boolean success = tenderDAO.startEvaluation(tenderId);
        
        if (success) {
            LOGGER.log(Level.INFO, "Evaluation started for tender: {0}", tender.getReferenceNumber());
        }
        
        return success;
    }
    
    /**
     * Gets a tender by its ID with bid count.
     * 
     * @param tenderId the tender ID
     * @return the Tender object, or null if not found
     */
    public Tender getTenderById(int tenderId) {
        return tenderDAO.findById(tenderId);
    }
    
    /**
     * Gets a tender by its reference number.
     * 
     * @param referenceNumber the reference number
     * @return the Tender object, or null if not found
     */
    public Tender getTenderByReference(String referenceNumber) {
        return tenderDAO.findByReferenceNumber(referenceNumber);
    }
    
    /**
     * Gets all tenders.
     * 
     * @return List of all Tender objects
     */
    public List<Tender> getAllTenders() {
        return tenderDAO.findAll();
    }
    
    /**
     * Gets tenders filtered by status and/or category.
     * 
     * @param status the status filter (can be null)
     * @param category the category filter (can be null)
     * @return List of matching Tender objects
     */
    public List<Tender> getTendersByFilters(String status, String category) {
        return tenderDAO.findByFilters(status, category);
    }
    
    /**
     * Gets all open tenders that are accepting bids.
     * 
     * @return List of OPEN Tender objects with future deadlines
     */
    public List<Tender> getOpenTenders() {
        return tenderDAO.findOpenTenders();
    }
    
    /**
     * Gets tenders created by a specific officer.
     * 
     * @param userId the officer's user ID
     * @return List of Tender objects
     */
    public List<Tender> getTendersByOfficer(int userId) {
        return tenderDAO.findByCreator(userId);
    }
    
    /**
     * Gets tenders that are ready for evaluation.
     * Includes CLOSED, UNDER_EVALUATION, and EVALUATED tenders.
     * AWARDED tenders appear in Results, not here.
     * 
     * @return List of Tender objects in evaluation-related statuses
     */
    public List<Tender> getTendersForEvaluation() {
        return tenderDAO.findTendersForEvaluation();
    }
    
    /**
     * Gets tenders available for a specific evaluator.
     * 
     * @param evaluatorId the evaluator's ID
     * @return List of Tender objects
     */
    public List<Tender> getTendersForEvaluator(int evaluatorId) {
        return tenderDAO.findTendersForEvaluator(evaluatorId);
    }
    
    /**
     * Gets the lowest bid amount for a tender.
     * 
     * @param tenderId the tender ID
     * @return the lowest bid amount
     */
    public BigDecimal getLowestBidAmount(int tenderId) {
        return tenderDAO.getLowestBidAmount(tenderId);
    }
    
    /**
     * Gets the shortest proposed timeline for a tender.
     * 
     * @param tenderId the tender ID
     * @return the shortest timeline in days
     */
    public int getShortestTimeline(int tenderId) {
        return tenderDAO.getShortestTimeline(tenderId);
    }
    
    /**
     * Checks if a tender is open for bidding.
     * 
     * @param tenderId the tender ID
     * @return true if the tender is OPEN and deadline has not passed
     */
    public boolean isTenderOpenForBidding(int tenderId) {
        Tender tender = tenderDAO.findById(tenderId);
        return tender != null && tender.isOpenForBidding();
    }
    
    /**
     * Checks if the submission deadline has passed.
     * 
     * @param tenderId the tender ID
     * @return true if current time is after the deadline
     */
    public boolean isDeadlinePassed(int tenderId) {
        Tender tender = tenderDAO.findById(tenderId);
        return tender != null && tender.isDeadlinePassed();
    }
    
    /**
     * Deletes a tender.
     * Only allowed when tender is in DRAFT status.
     * 
     * @param tenderId the tender ID
     * @return true if deletion was successful
     */
    public boolean deleteTender(int tenderId) {
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null) {
            return false;
        }
        
        if (!tender.isEditable()) {
            LOGGER.log(Level.WARNING, "Cannot delete - tender is not in DRAFT status: {0}", 
                       tender.getReferenceNumber());
            return false;
        }
        
        return tenderDAO.delete(tenderId);
    }
    
    /**
     * Gets the total number of tenders.
     * 
     * @return total tender count
     */
    public int getTotalTenderCount() {
        return tenderDAO.countAll();
    }
    
    /**
     * Gets the number of tenders by status.
     * 
     * @param status the status to count
     * @return count of tenders with the specified status
     */
    public int getTenderCountByStatus(String status) {
        return tenderDAO.countByStatus(status);
    }
}