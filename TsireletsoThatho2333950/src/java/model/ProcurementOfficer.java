package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Procurement Officer model class representing Ministry officials
 * who manage the tender process from creation to award.
 * These accounts are created via seed script only.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ProcurementOfficer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int officerId;
    private int userId;
    private String fullName;
    private String department;
    private String employeeNumber;
    private Timestamp createdAt;
    
    private User user;
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public ProcurementOfficer() {
    }
    
    /**
     * Constructs a new Procurement Officer.
     * 
     * @param userId the associated user ID
     * @param fullName the officer's full name
     * @param department the department (default: Procurement)
     * @param employeeNumber the employee number (format: MPW-OFF-NNN)
     */
    public ProcurementOfficer(int userId, String fullName, String department, 
                              String employeeNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.department = department;
        this.employeeNumber = employeeNumber;
    }
    
    /**
     * Gets the officer ID.
     * 
     * @return the unique identifier for this officer
     */
    public int getOfficerId() {
        return officerId;
    }
    
    /**
     * Sets the officer ID.
     * 
     * @param officerId the unique identifier to set
     */
    public void setOfficerId(int officerId) {
        this.officerId = officerId;
    }
    
    /**
     * Gets the associated user ID.
     * 
     * @return the user ID linking to the users table
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Sets the associated user ID.
     * 
     * @param userId the user ID to set
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the officer's full name.
     * 
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Sets the officer's full name.
     * 
     * @param fullName the full name to set
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    /**
     * Gets the department.
     * 
     * @return the department name
     */
    public String getDepartment() {
        return department;
    }
    
    /**
     * Sets the department.
     * 
     * @param department the department to set
     */
    public void setDepartment(String department) {
        this.department = department;
    }
    
    /**
     * Gets the employee number.
     * 
     * @return the employee number in format MPW-OFF-NNN
     */
    public String getEmployeeNumber() {
        return employeeNumber;
    }
    
    /**
     * Sets the employee number.
     * 
     * @param employeeNumber the employee number to set
     */
    public void setEmployeeNumber(String employeeNumber) {
        this.employeeNumber = employeeNumber;
    }
    
    /**
     * Gets the creation timestamp.
     * 
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation timestamp.
     * 
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the associated User object.
     * 
     * @return the User object
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Sets the associated User object.
     * 
     * @param user the User object to set
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * Gets the email address from the associated User.
     * 
     * @return the email address, or null if user is not set
     */
    public String getEmail() {
        return user != null ? user.getEmail() : null;
    }
    
    /**
     * Returns a string representation of the ProcurementOfficer object.
     * 
     * @return string containing officer details
     */
    @Override
    public String toString() {
        return "ProcurementOfficer{" +
                "officerId=" + officerId +
                ", fullName='" + fullName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}