package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User model class representing the base user entity for all system roles.
 * This class stores authentication and role information shared across
 * Suppliers, Procurement Officers, and Evaluation Committee Members.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class User implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int userId;
    private String email;
    private String passwordHash;
    private String role;
    private String status;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public User() {
    }
    
    /**
     * Constructs a new User with email, password hash, and role.
     * Status defaults to ACTIVE.
     * 
     * @param email the user's email address
     * @param passwordHash the SHA-256 hashed password
     * @param role the user's role (SUPPLIER, PROCUREMENT_OFFICER, EVALUATION_COMMITTEE)
     */
    public User(String email, String passwordHash, String role) {
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.status = "ACTIVE";
    }
    
    /**
     * Gets the user ID.
     * 
     * @return the unique identifier for this user
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Sets the user ID.
     * 
     * @param userId the unique identifier to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the user's email address.
     * 
     * @return the email address
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the user's email address.
     * 
     * @param email the email address to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the SHA-256 hashed password.
     * 
     * @return the password hash
     */
    public String getPasswordHash() {
        return passwordHash;
    }
    
    /**
     * Sets the SHA-256 hashed password.
     * 
     * @param passwordHash the password hash to set
     */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }
    
    /**
     * Gets the user's role.
     * 
     * @return the role (SUPPLIER, PROCUREMENT_OFFICER, or EVALUATION_COMMITTEE)
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Sets the user's role.
     * 
     * @param role the role to set
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Gets the account status.
     * 
     * @return the status (ACTIVE or LOCKED)
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the account status.
     * 
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the account creation timestamp.
     * 
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the account creation timestamp.
     * 
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last update timestamp.
     * 
     * @return the update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the last update timestamp.
     * 
     * @param updatedAt the timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Checks if the account is locked.
     * 
     * @return true if status is LOCKED, false otherwise
     */
    public boolean isLocked() {
        return "LOCKED".equals(this.status);
    }
    
    /**
     * Checks if the user is a Procurement Officer.
     * 
     * @return true if role is PROCUREMENT_OFFICER
     */
    public boolean isProcurementOfficer() {
        return "PROCUREMENT_OFFICER".equals(this.role);
    }
    
    /**
     * Checks if the user is an Evaluation Committee Member.
     * 
     * @return true if role is EVALUATION_COMMITTEE
     */
    public boolean isEvaluationCommittee() {
        return "EVALUATION_COMMITTEE".equals(this.role);
    }
    
    /**
     * Checks if the user is a Supplier.
     * 
     * @return true if role is SUPPLIER
     */
    public boolean isSupplier() {
        return "SUPPLIER".equals(this.role);
    }
    
    /**
     * Returns a string representation of the User object.
     * 
     * @return string containing user details (password hash excluded)
     */
    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}