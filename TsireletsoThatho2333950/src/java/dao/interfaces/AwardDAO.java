package dao.interfaces;

import model.Award;
import java.util.List;

/**
 * Data Access Object interface for Award entity operations.
 * Provides methods for creating and retrieving contract awards.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface AwardDAO {
    
    /**
     * Creates a new award in the database.
     * 
     * @param award the Award object to create
     * @return the generated award ID, or -1 if creation failed
     */
    int create(Award award);
    
    /**
     * Finds an award by its unique ID.
     * 
     * @param awardId the award ID to search for
     * @return the Award object, or null if not found
     */
    Award findById(int awardId);
    
    /**
     * Finds the award for a specific tender.
     * 
     * @param tenderId the tender ID
     * @return the Award object, or null if not awarded
     */
    Award findByTenderId(int tenderId);
    
    /**
     * Finds awards made by a specific officer.
     * 
     * @param userId the officer's user ID
     * @return List of Award objects made by the officer
     */
    List<Award> findByAwardedBy(int userId);
    
    /**
     * Updates an existing award.
     * 
     * @param award the Award object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(Award award);
    
    /**
     * Deletes an award from the database.
     * 
     * @param awardId the award ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(int awardId);
    
    /**
     * Retrieves all awards from the database.
     * 
     * @return List of all Award objects
     */
    List<Award> findAll();
    
    /**
     * Retrieves awards with full details including tender and supplier information.
     * 
     * @return List of Award objects with complete details
     */
    List<Award> findAllDetailed();
    
    /**
     * Retrieves an award with full details.
     * 
     * @param awardId the award ID
     * @return the Award object with complete details, or null if not found
     */
    Award findDetailedById(int awardId);
    
    /**
     * Retrieves an award for a tender with full details.
     * 
     * @param tenderId the tender ID
     * @return the Award object with complete details, or null if not awarded
     */
    Award findDetailedByTenderId(int tenderId);
    
    /**
     * Checks if a tender has been awarded.
     * 
     * @param tenderId the tender ID
     * @return true if an award exists, false otherwise
     */
    boolean isTenderAwarded(int tenderId);
    
    /**
     * Gets the winning supplier ID for a tender.
     * 
     * @param tenderId the tender ID
     * @return the winning supplier ID, or -1 if not awarded
     */
    int getWinningSupplierId(int tenderId);
    
    /**
     * Counts the total number of awards.
     * 
     * @return the total award count
     */
    int countAll();
}