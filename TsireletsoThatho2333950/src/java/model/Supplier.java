package model;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * Supplier model class representing registered companies or individuals
 * who can submit bids for published tenders.
 * This class extends the base User information with supplier-specific details.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class Supplier implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int supplierId;
    private int userId;
    private String registrationNumber;
    private String companyName;
    private String physicalAddress;
    private String contactNumber;
    private Timestamp createdAt;
    
    private User user;
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public Supplier() {
    }
    
    /**
     * Constructs a new Supplier with required registration details.
     * 
     * @param userId the associated user ID
     * @param registrationNumber the system-generated registration number (SUP-YYYY-NNNN)
     * @param companyName the company or individual name
     * @param physicalAddress the physical business address
     * @param contactNumber the contact phone number
     */
    public Supplier(int userId, String registrationNumber, String companyName,
                    String physicalAddress, String contactNumber) {
        this.userId = userId;
        this.registrationNumber = registrationNumber;
        this.companyName = companyName;
        this.physicalAddress = physicalAddress;
        this.contactNumber = contactNumber;
    }
    
    /**
     * Gets the supplier ID.
     * 
     * @return the unique identifier for this supplier
     */
    public int getSupplierId() {
        return supplierId;
    }
    
    /**
     * Sets the supplier ID.
     * 
     * @param supplierId the unique identifier to set
     */
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
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
     * Gets the system-generated registration number.
     * 
     * @return the registration number in format SUP-YYYY-NNNN
     */
    public String getRegistrationNumber() {
        return registrationNumber;
    }
    
    /**
     * Sets the registration number.
     * 
     * @param registrationNumber the registration number to set
     */
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }
    
    /**
     * Gets the company or individual name.
     * 
     * @return the company name
     */
    public String getCompanyName() {
        return companyName;
    }
    
    /**
     * Sets the company or individual name.
     * 
     * @param companyName the company name to set
     */
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    /**
     * Gets the physical business address.
     * 
     * @return the physical address
     */
    public String getPhysicalAddress() {
        return physicalAddress;
    }
    
    /**
     * Sets the physical business address.
     * 
     * @param physicalAddress the address to set
     */
    public void setPhysicalAddress(String physicalAddress) {
        this.physicalAddress = physicalAddress;
    }
    
    /**
     * Gets the contact phone number.
     * 
     * @return the contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }
    
    /**
     * Sets the contact phone number.
     * 
     * @param contactNumber the contact number to set
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    /**
     * Gets the registration timestamp.
     * 
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the registration timestamp.
     * 
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the associated User object containing authentication details.
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
     * Returns a string representation of the Supplier object.
     * 
     * @return string containing supplier details
     */
    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", registrationNumber='" + registrationNumber + '\'' +
                ", companyName='" + companyName + '\'' +
                ", contactNumber='" + contactNumber + '\'' +
                '}';
    }
}