package dao.interfaces;

import model.Bid;
import java.math.BigDecimal;
import java.util.List;

/**
 * Data Access Object interface for Bid entity operations.
 * Provides methods for bid submission, retrieval, and status management.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface BidDAO {
    
    /**
     * Creates a new bid in the database.
     * 
     * @param bid the Bid object to create
     * @return the generated bid ID, or -1 if creation failed
     */
    int create(Bid bid);
    
    /**
     * Finds a bid by its unique ID.
     * 
     * @param bidId the bid ID to search for
     * @return the Bid object, or null if not found
     */
    Bid findById(int bidId);
    
    /**
     * Finds all bids submitted for a specific tender.
     * 
     * @param tenderId the tender ID
     * @return List of Bid objects for the tender
     */
    List<Bid> findByTenderId(int tenderId);
    
    /**
     * Finds all bids submitted by a specific supplier.
     * 
     * @param supplierId the supplier ID
     * @return List of Bid objects submitted by the supplier
     */
    List<Bid> findBySupplierId(int supplierId);
    
    /**
     * Finds a supplier's bid for a specific tender.
     * 
     * @param tenderId the tender ID
     * @param supplierId the supplier ID
     * @return the Bid object, or null if not found
     */
    Bid findByTenderAndSupplier(int tenderId, int supplierId);
    
    /**
     * Updates an existing bid's information.
     * 
     * @param bid the Bid object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(Bid bid);
    
    /**
     * Updates a bid's status.
     * 
     * @param bidId the bid ID to update
     * @param status the new status
     * @return true if update was successful, false otherwise
     */
    boolean updateStatus(int bidId, String status);
    
    /**
     * Updates the status of all bids for a tender based on winning bid.
     * Sets winning bid to WON and all others to NOT_WON.
     * 
     * @param tenderId the tender ID
     * @param winningBidId the winning bid ID
     * @return true if update was successful, false otherwise
     */
    boolean updateBidOutcomes(int tenderId, int winningBidId);
    
    /**
     * Deletes a bid from the database.
     * 
     * @param bidId the bid ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(int bidId);
    
    /**
     * Retrieves all bids from the database.
     * 
     * @return List of all Bid objects
     */
    List<Bid> findAll();
    
    /**
     * Retrieves bids filtered by status.
     * 
     * @param status the status to filter by
     * @return List of Bid objects with the specified status
     */
    List<Bid> findByStatus(String status);
    
    /**
     * Retrieves bids for a tender with full details including supplier info.
     * 
     * @param tenderId the tender ID
     * @return List of Bid objects with supplier names populated
     */
    List<Bid> findDetailedBidsByTenderId(int tenderId);
    
    /**
     * Retrieves bids submitted by a supplier with tender details.
     * 
     * @param supplierId the supplier ID
     * @return List of Bid objects with tender information populated
     */
    List<Bid> findDetailedBidsBySupplierId(int supplierId);
    
    /**
     * Checks if a supplier has already submitted a bid for a tender.
     * 
     * @param tenderId the tender ID
     * @param supplierId the supplier ID
     * @return true if a bid exists, false otherwise
     */
    boolean hasSupplierBid(int tenderId, int supplierId);
    
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
     * Gets all suppliers who submitted bids for a tender.
     * 
     * @param tenderId the tender ID
     * @return List of supplier IDs who bid on the tender
     */
    List<Integer> getBiddingSupplierIds(int tenderId);
    
    /**
     * Counts the total number of bids.
     * 
     * @return the total bid count
     */
    int countAll();
    
    /**
     * Counts bids for a specific tender.
     * 
     * @param tenderId the tender ID
     * @return the number of bids for the tender
     */
    int countByTenderId(int tenderId);
    
    /**
     * Counts bids submitted by a specific supplier.
     * 
     * @param supplierId the supplier ID
     * @return the number of bids submitted by the supplier
     */
    int countBySupplierId(int supplierId);
}