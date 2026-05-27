package dao.interfaces;

import model.User;
import java.util.List;

/**
 * Data Access Object interface for User entity operations.
 * Provides methods for user authentication, registration, and management.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface UserDAO {
    
    /**
     * Creates a new user in the database.
     * 
     * @param user the User object to create
     * @return the generated user ID, or -1 if creation failed
     */
    int create(User user);
    
    /**
     * Finds a user by their unique ID.
     * 
     * @param userId the user ID to search for
     * @return the User object, or null if not found
     */
    User findById(int userId);
    
    /**
     * Finds a user by their email address.
     * 
     * @param email the email address to search for
     * @return the User object, or null if not found
     */
    User findByEmail(String email);
    
    /**
     * Authenticates a user with email and password hash.
     * 
     * @param email the user's email address
     * @param passwordHash the SHA-256 hashed password
     * @return the authenticated User object, or null if authentication fails
     */
    User authenticate(String email, String passwordHash);
    
    /**
     * Updates an existing user's information.
     * 
     * @param user the User object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(User user);
    
    /**
     * Updates a user's account status (ACTIVE or LOCKED).
     * 
     * @param userId the user ID to update
     * @param status the new status
     * @return true if update was successful, false otherwise
     */
    boolean updateStatus(int userId, String status);
    
    /**
     * Updates a user's password.
     * 
     * @param userId the user ID to update
     * @param newPasswordHash the new SHA-256 hashed password
     * @return true if update was successful, false otherwise
     */
    boolean updatePassword(int userId, String newPasswordHash);
    
    /**
     * Deletes a user from the database.
     * 
     * @param userId the user ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(int userId);
    
    /**
     * Retrieves all users from the database.
     * 
     * @return List of all User objects
     */
    List<User> findAll();
    
    /**
     * Retrieves all users with a specific role.
     * 
     * @param role the role to filter by (SUPPLIER, PROCUREMENT_OFFICER, EVALUATION_COMMITTEE)
     * @return List of User objects with the specified role
     */
    List<User> findByRole(String role);
    
    /**
     * Checks if an email address is already registered.
     * 
     * @param email the email address to check
     * @return true if email exists, false otherwise
     */
    boolean emailExists(String email);
    
    /**
     * Counts the total number of users in the system.
     * 
     * @return the total user count
     */
    int countAll();
    
    /**
     * Counts users by role.
     * 
     * @param role the role to count
     * @return the number of users with the specified role
     */
    int countByRole(String role);
}