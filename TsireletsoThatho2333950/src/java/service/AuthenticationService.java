package service;

import dao.implementations.SupplierDAOImpl;
import dao.implementations.UserDAOImpl;
import model.Supplier;
import model.User;
import util.Constants;
import util.PasswordHasher;
import util.SessionValidator;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for authentication-related business logic. Handles user login,
 * registration, and session management.
 *
 * Required by Module 1: User Registration & Authentication.
 *
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class AuthenticationService {

    private static final Logger LOGGER = Logger.getLogger(AuthenticationService.class.getName());

    private final UserDAOImpl userDAO;
    private final SupplierDAOImpl supplierDAO;

    /**
     * Constructor initializes DAO instances.
     */
    public AuthenticationService() {
        this.userDAO = new UserDAOImpl();
        this.supplierDAO = new SupplierDAOImpl();
    }

    /**
     * Authenticates a user with email and password. Implements failed login
     * attempt tracking PER EMAIL and session-based account lockout. Lock is
     * SESSION-BASED only - does NOT persist to database.
     *
     * @param email the user's email address
     * @param plainPassword the plain text password
     * @param request the HttpServletRequest for session tracking
     * @return the authenticated User object, or null if authentication fails
     */
    public User authenticate(String email, String plainPassword, HttpServletRequest request) {
        // Trim email
        String trimmedEmail = email != null ? email.trim() : "";

        // Check if THIS SPECIFIC EMAIL is locked in session
        if (SessionValidator.isAccountLockedForEmail(request, trimmedEmail)) {
            LOGGER.log(Level.WARNING, "Login attempt blocked - account locked in session: {0}", trimmedEmail);
            return null;
        }

        // Find user by email
        User user = userDAO.findByEmail(trimmedEmail);

        if (user == null) {
            LOGGER.log(Level.INFO, "Login failed - user not found: {0}", trimmedEmail);
            // Track failed attempt for this email
            SessionValidator.incrementFailedLoginAttemptsForEmail(request, trimmedEmail);
            return null;
        }

        // NOTE: Database status field is NOT used for lockout (session-based only)
        // We still check it in case admin manually locked account
        if (user.isLocked()) {
            LOGGER.log(Level.WARNING, "Login attempt blocked - account manually locked in database: {0}", trimmedEmail);
            return null;
        }

        // Hash the provided password and compare
        String hashedPassword = PasswordHasher.hashPassword(plainPassword);

        if (hashedPassword != null && hashedPassword.equals(user.getPasswordHash())) {
            // Successful login - reset failed attempts for this email
            SessionValidator.resetFailedLoginAttemptsForEmail(request, trimmedEmail);
            LOGGER.log(Level.INFO, "Login successful: {0} ({1})", new Object[]{trimmedEmail, user.getRole()});
            return user;
        } else {
            // Failed login - increment counter for this email
            int attempts = SessionValidator.incrementFailedLoginAttemptsForEmail(request, trimmedEmail);
            LOGGER.log(Level.WARNING, "Login failed - invalid password for: {0} (Attempt {1}/3)",
                    new Object[]{trimmedEmail, attempts});

            // DO NOT lock account in database - lock is session-based only!
            // The lock is enforced by SessionValidator.isAccountLockedForEmail()
            return null;
        }
    }

    /**
     * Registers a new supplier. Creates both User and Supplier records in a
     * transaction.
     *
     * @param supplier the Supplier object with registration details
     * @param email the supplier's email address
     * @param plainPassword the plain text password
     * @return the generated supplier ID, or -1 if registration fails
     */
    public int registerSupplier(Supplier supplier, String email, String plainPassword) {
        try {
            // Check if email already exists
            if (userDAO.emailExists(email)) {
                LOGGER.log(Level.WARNING, "Registration failed - email already exists: {0}", email);
                return -1;
            }

            // Generate registration number
            String registrationNumber = supplierDAO.generateRegistrationNumber();
            supplier.setRegistrationNumber(registrationNumber);

            // Create User record
            User user = new User();
            user.setEmail(email);
            user.setPasswordHash(PasswordHasher.hashPassword(plainPassword));
            user.setRole(Constants.ROLE_SUPPLIER);
            user.setStatus(Constants.STATUS_ACTIVE);

            int userId = userDAO.create(user);

            if (userId == -1) {
                LOGGER.log(Level.SEVERE, "Failed to create user for supplier: {0}", email);
                return -1;
            }

            // Create Supplier record
            supplier.setUserId(userId);
            int supplierId = supplierDAO.create(supplier);

            if (supplierId != -1) {
                LOGGER.log(Level.INFO, "Supplier registered successfully: {0} (Reg#: {1})",
                        new Object[]{email, registrationNumber});
            }

            return supplierId;

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error registering supplier: {0}", e.getMessage());
            return -1;
        }
    }

    /**
     * Logs out a user by invalidating the session. Preserves failed login
     * attempts by saving and restoring them.
     *
     * @param request the HttpServletRequest
     */
    public void logout(HttpServletRequest request) {
        HttpSession oldSession = request.getSession(false);
        String email = null;

        // Save all failed attempt attributes before invalidating
        Map<String, Integer> savedFailedAttempts = new HashMap<>();

        if (oldSession != null) {
            User user = (User) oldSession.getAttribute(Constants.SESSION_USER);
            if (user != null) {
                email = user.getEmail();
            }

            // Save all failedAttempts_* attributes
            Enumeration<String> attributeNames = oldSession.getAttributeNames();
            while (attributeNames.hasMoreElements()) {
                String name = attributeNames.nextElement();
                if (name.startsWith("failedAttempts_")) {
                    Integer value = (Integer) oldSession.getAttribute(name);
                    if (value != null) {
                        savedFailedAttempts.put(name, value);
                    }
                }
            }

            // Now invalidate the old session
            oldSession.invalidate();
        }

        // Create a new session and restore failed attempts
        HttpSession newSession = request.getSession(true);
        for (Map.Entry<String, Integer> entry : savedFailedAttempts.entrySet()) {
            newSession.setAttribute(entry.getKey(), entry.getValue());
        }

        // Set success message
        newSession.setAttribute("successMessage", "You have been successfully logged out.");

        LOGGER.log(Level.INFO, "User logged out (session invalidated, failed attempts preserved): {0}",
                email != null ? email : "unknown");
    }

    /**
     * Gets the appropriate dashboard URL for a user based on their role.
     *
     * @param user the authenticated User
     * @return the dashboard URL path
     */
    public String getDashboardUrl(User user) {
        return SessionValidator.getDashboardForRole(user.getRole());
    }

    /**
     * Changes a user's password.
     *
     * @param userId the user ID
     * @param currentPassword the current plain text password
     * @param newPassword the new plain text password
     * @return true if password was changed successfully
     */
    public boolean changePassword(int userId, String currentPassword, String newPassword) {
        User user = userDAO.findById(userId);

        if (user == null) {
            return false;
        }

        // Verify current password
        if (!PasswordHasher.verifyPassword(currentPassword, user.getPasswordHash())) {
            LOGGER.log(Level.WARNING, "Password change failed - incorrect current password for user ID: {0}", userId);
            return false;
        }

        // Update with new password
        String newHash = PasswordHasher.hashPassword(newPassword);
        boolean success = userDAO.updatePassword(userId, newHash);

        if (success) {
            LOGGER.log(Level.INFO, "Password changed successfully for user ID: {0}", userId);
        }

        return success;
    }

    /**
     * Unlocks a user account (manual admin action).
     *
     * @param userId the user ID
     * @return true if account was unlocked
     */
    public boolean unlockAccount(int userId) {
        boolean success = userDAO.updateStatus(userId, Constants.STATUS_ACTIVE);
        if (success) {
            LOGGER.log(Level.INFO, "Account unlocked for user ID: {0}", userId);
        }
        return success;
    }

    /**
     * Gets a supplier by their user ID.
     *
     * @param userId the user ID
     * @return the Supplier object, or null if not found
     */
    public Supplier getSupplierByUserId(int userId) {
        return supplierDAO.findByUserId(userId);
    }
}
