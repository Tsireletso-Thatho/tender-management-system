package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Evaluation Committee Member model class representing Ministry officials
 * appointed to score bids. They cannot create tenders or view bids
 * before the tender is formally closed.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class EvaluationCommittee implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int evaluatorId;
    private int userId;
    private String fullName;
    private String department;
    private String employeeNumber;
    private Timestamp createdAt;
    
    private User user;
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public EvaluationCommittee() {
    }
    
    /**
     * Constructs a new Evaluation Committee Member.
     * 
     * @param userId the associated user ID
     * @param fullName the evaluator's full name
     * @param department the department (default: Evaluation)
     * @param employeeNumber the employee number (format: MPW-EVAL-NNN)
     */
    public EvaluationCommittee(int userId, String fullName, String department,
                               String employeeNumber) {
        this.userId = userId;
        this.fullName = fullName;
        this.department = department;
        this.employeeNumber = employeeNumber;
    }
    
    /**
     * Gets the evaluator ID.
     * 
     * @return the unique identifier for this evaluator
     */
    public int getEvaluatorId() {
        return evaluatorId;
    }
    
    /**
     * Sets the evaluator ID.
     * 
     * @param evaluatorId the unique identifier to set
     */
    public void setEvaluatorId(int evaluatorId) {
        this.evaluatorId = evaluatorId;
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
     * Gets the evaluator's full name.
     * 
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Sets the evaluator's full name.
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
     * @return the employee number in format MPW-EVAL-NNN
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
     * Returns a string representation of the EvaluationCommittee object.
     * 
     * @return string containing evaluator details
     */
    @Override
    public String toString() {
        return "EvaluationCommittee{" +
                "evaluatorId=" + evaluatorId +
                ", fullName='" + fullName + '\'' +
                ", employeeNumber='" + employeeNumber + '\'' +
                ", department='" + department + '\'' +
                '}';
    }
}