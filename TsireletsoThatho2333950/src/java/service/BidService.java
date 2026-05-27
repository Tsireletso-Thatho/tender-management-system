package service;

import dao.implementations.BidDAOImpl;
import dao.implementations.TenderDAOImpl;
import model.Bid;
import model.Tender;
import util.Constants;
import util.DateUtils;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for bid-related business logic.
 * Handles bid submission, validation, and queries.
 * 
 * Required by Module 3: Supplier Bid Submission.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class BidService {
    
    private static final Logger LOGGER = Logger.getLogger(BidService.class.getName());
    
    private final BidDAOImpl bidDAO;
    private final TenderDAOImpl tenderDAO;
    
    /**
     * Constructor initializes DAO instances.
     */
    public BidService() {
        this.bidDAO = new BidDAOImpl();
        this.tenderDAO = new TenderDAOImpl();
    }
    
    /**
     * Submits a new bid for a tender.
     * Validates that the tender is open, deadline hasn't passed,
     * and supplier hasn't already submitted a bid.
     * 
     * @param bid the Bid object to submit
     * @return the generated bid ID, or -1 if submission fails
     * @throws IllegalStateException if validation fails
     */
    public int submitBid(Bid bid) throws IllegalStateException {
        // Get the tender
        Tender tender = tenderDAO.findById(bid.getTenderId());
        
        if (tender == null) {
            throw new IllegalStateException("Tender not found");
        }
        
        // Validate tender is open
        if (!Constants.TENDER_STATUS_OPEN.equals(tender.getStatus())) {
            throw new IllegalStateException("This tender is not open for bidding");
        }
        
        // Validate deadline hasn't passed (server-side enforcement)
        if (DateUtils.isDeadlinePassed(tender.getSubmissionDeadline())) {
            throw new IllegalStateException("The submission deadline has passed");
        }
        
        // Validate supplier hasn't already bid
        if (bidDAO.hasSupplierBid(bid.getTenderId(), bid.getSupplierId())) {
            throw new IllegalStateException("You have already submitted a bid for this tender");
        }
        
        // Set initial status
        bid.setStatus(Constants.BID_STATUS_SUBMITTED);
        
        int bidId = bidDAO.create(bid);
        
        if (bidId != -1) {
            LOGGER.log(Level.INFO, "Bid submitted: Tender ID {0}, Supplier ID {1}, Amount {2}", 
                       new Object[]{bid.getTenderId(), bid.getSupplierId(), bid.getBidAmount()});
        }
        
        return bidId;
    }
    
    /**
     * Gets a bid by its ID.
     * 
     * @param bidId the bid ID
     * @return the Bid object, or null if not found
     */
    public Bid getBidById(int bidId) {
        return bidDAO.findById(bidId);
    }
    
    /**
     * Gets all bids for a specific tender.
     * 
     * @param tenderId the tender ID
     * @return List of Bid objects
     */
    public List<Bid> getBidsByTenderId(int tenderId) {
        return bidDAO.findDetailedBidsByTenderId(tenderId);
    }
    
    /**
     * Gets all bids submitted by a specific supplier.
     * 
     * @param supplierId the supplier ID
     * @return List of Bid objects with tender details
     */
    public List<Bid> getBidsBySupplierId(int supplierId) {
        return bidDAO.findDetailedBidsBySupplierId(supplierId);
    }
    
    /**
     * Gets a supplier's bid for a specific tender.
     * 
     * @param tenderId the tender ID
     * @param supplierId the supplier ID
     * @return the Bid object, or null if not found
     */
    public Bid getSupplierBidForTender(int tenderId, int supplierId) {
        return bidDAO.findByTenderAndSupplier(tenderId, supplierId);
    }
    
    /**
     * Checks if a supplier has already submitted a bid for a tender.
     * 
     * @param tenderId the tender ID
     * @param supplierId the supplier ID
     * @return true if a bid exists
     */
    public boolean hasSupplierBid(int tenderId, int supplierId) {
        return bidDAO.hasSupplierBid(tenderId, supplierId);
    }
    
    /**
     * Gets the number of bids for a tender.
     * 
     * @param tenderId the tender ID
     * @return the bid count
     */
    public int getBidCountForTender(int tenderId) {
        return bidDAO.countByTenderId(tenderId);
    }
    
    /**
     * Gets the lowest bid amount for a tender.
     * 
     * @param tenderId the tender ID
     * @return the lowest bid amount
     */
    public java.math.BigDecimal getLowestBidAmount(int tenderId) {
        return bidDAO.getLowestBidAmount(tenderId);
    }
    
    /**
     * Gets the shortest proposed timeline for a tender.
     * 
     * @param tenderId the tender ID
     * @return the shortest timeline in days
     */
    public int getShortestTimeline(int tenderId) {
        return bidDAO.getShortestTimeline(tenderId);
    }
    
    /**
     * Gets all supplier IDs who bid on a tender.
     * 
     * @param tenderId the tender ID
     * @return List of supplier IDs
     */
    public List<Integer> getBiddingSupplierIds(int tenderId) {
        return bidDAO.getBiddingSupplierIds(tenderId);
    }
    
    /**
     * Updates bid outcomes after a tender is awarded.
     * Sets winning bid to WON and all others to NOT_WON.
     * 
     * @param tenderId the tender ID
     * @param winningBidId the winning bid ID
     * @return true if update was successful
     */
    public boolean updateBidOutcomes(int tenderId, int winningBidId) {
        boolean success = bidDAO.updateBidOutcomes(tenderId, winningBidId);
        
        if (success) {
            LOGGER.log(Level.INFO, "Bid outcomes updated for tender ID {0}, winning bid ID {1}", 
                       new Object[]{tenderId, winningBidId});
        }
        
        return success;
    }
    
    /**
     * Validates if a bid can be submitted for a tender.
     * Returns a validation message or null if valid.
     * 
     * @param tenderId the tender ID
     * @param supplierId the supplier ID
     * @return error message if invalid, null if valid
     */
    public String validateBidSubmission(int tenderId, int supplierId) {
        Tender tender = tenderDAO.findById(tenderId);
        
        if (tender == null) {
            return "Tender not found";
        }
        
        if (!Constants.TENDER_STATUS_OPEN.equals(tender.getStatus())) {
            return "This tender is not open for bidding";
        }
        
        if (DateUtils.isDeadlinePassed(tender.getSubmissionDeadline())) {
            return "The submission deadline has passed";
        }
        
        if (bidDAO.hasSupplierBid(tenderId, supplierId)) {
            return "You have already submitted a bid for this tender";
        }
        
        return null;
    }
}