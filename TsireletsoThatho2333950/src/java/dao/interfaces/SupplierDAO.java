package dao.interfaces;

import model.Supplier;
import java.util.List;

/**
 * Data Access Object interface for Supplier entity operations.
 * Provides methods for supplier registration, retrieval, and management.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface SupplierDAO {
    
    /**
     * Creates a new supplier in the database.
     * 
     * @param supplier the Supplier object to create
     * @return the generated supplier ID, or -1 if creation failed
     */
    int create(Supplier supplier);
    
    /**
     * Finds a supplier by their unique ID.
     * 
     * @param supplierId the supplier ID to search for
     * @return the Supplier object, or null if not found
     */
    Supplier findById(int supplierId);
    
    /**
     * Finds a supplier by their associated user ID.
     * 
     * @param userId the user ID to search for
     * @return the Supplier object, or null if not found
     */
    Supplier findByUserId(int userId);
    
    /**
     * Finds a supplier by their registration number.
     * 
     * @param registrationNumber the registration number (SUP-YYYY-NNNN)
     * @return the Supplier object, or null if not found
     */
    Supplier findByRegistrationNumber(String registrationNumber);
    
    /**
     * Finds a supplier by their email address (through user association).
     * 
     * @param email the email address to search for
     * @return the Supplier object, or null if not found
     */
    Supplier findByEmail(String email);
    
    /**
     * Updates an existing supplier's information.
     * 
     * @param supplier the Supplier object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(Supplier supplier);
    
    /**
     * Deletes a supplier from the database.
     * 
     * @param supplierId the supplier ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(int supplierId);
    
    /**
     * Retrieves all suppliers from the database.
     * 
     * @return List of all Supplier objects
     */
    List<Supplier> findAll();
    
    /**
     * Retrieves all active suppliers.
     * 
     * @return List of active Supplier objects
     */
    List<Supplier> findAllActive();
    
    /**
     * Searches suppliers by company name (partial match).
     * 
     * @param searchTerm the search term to match against company name
     * @return List of matching Supplier objects
     */
    List<Supplier> searchByCompanyName(String searchTerm);
    
    /**
     * Generates the next sequential registration number.
     * Format: SUP-YYYY-NNNN where YYYY is current year and NNNN is sequential.
     * 
     * @return the generated registration number
     */
    String generateRegistrationNumber();
    
    /**
     * Counts the total number of suppliers.
     * 
     * @return the total supplier count
     */
    int countAll();
    
    /**
     * Gets the next sequential number for the current year.
     * 
     * @return the next sequential number
     */
    int getNextSequenceNumber();
}