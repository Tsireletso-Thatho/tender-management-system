package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Award model class representing a contract awarded to a winning supplier.
 * Created when a Procurement Officer selects the winning bid.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class Award implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int awardId;
    private int tenderId;
    private int winningBidId;
    private BigDecimal awardedValue;
    private String justification;
    private int awardedBy;
    private Timestamp awardedAt;
    
    // Additional fields for display
    private String tenderReference;
    private String tenderTitle;
    private String winningSupplierName;
    private String awardedByName;
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public Award() {
    }
    
    /**
     * Constructs a new Award.
     * 
     * @param tenderId the tender being awarded
     * @param winningBidId the winning bid ID
     * @param awardedValue the awarded contract value
     * @param justification the award justification note
     * @param awardedBy the officer making the award
     */
    public Award(int tenderId, int winningBidId, BigDecimal awardedValue,
                 String justification, int awardedBy) {
        this.tenderId = tenderId;
        this.winningBidId = winningBidId;
        this.awardedValue = awardedValue;
        this.justification = justification;
        this.awardedBy = awardedBy;
    }
    
    /**
     * Gets the award ID.
     * 
     * @return the unique identifier for this award
     */
    public int getAwardId() {
        return awardId;
    }
    
    /**
     * Sets the award ID.
     * 
     * @param awardId the unique identifier to set
     */
    public void setAwardId(int awardId) {
        this.awardId = awardId;
    }
    
    /**
     * Gets the tender ID.
     * 
     * @return the tender ID
     */
    public int getTenderId() {
        return tenderId;
    }
    
    /**
     * Sets the tender ID.
     * 
     * @param tenderId the tender ID to set
     */
    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }
    
    /**
     * Gets the winning bid ID.
     * 
     * @return the winning bid ID
     */
    public int getWinningBidId() {
        return winningBidId;
    }
    
    /**
     * Sets the winning bid ID.
     * 
     * @param winningBidId the bid ID to set
     */
    public void setWinningBidId(int winningBidId) {
        this.winningBidId = winningBidId;
    }
    
    /**
     * Gets the awarded contract value.
     * 
     * @return the awarded value in Maloti
     */
    public BigDecimal getAwardedValue() {
        return awardedValue;
    }
    
    /**
     * Sets the awarded contract value.
     * 
     * @param awardedValue the value to set
     */
    public void setAwardedValue(BigDecimal awardedValue) {
        this.awardedValue = awardedValue;
    }
    
    /**
     * Gets the award justification.
     * 
     * @return the justification note
     */
    public String getJustification() {
        return justification;
    }
    
    /**
     * Sets the award justification.
     * 
     * @param justification the justification to set
     */
    public void setJustification(String justification) {
        this.justification = justification;
    }
    
    /**
     * Gets the awarding officer's user ID.
     * 
     * @return the officer's user ID
     */
    public int getAwardedBy() {
        return awardedBy;
    }
    
    /**
     * Sets the awarding officer's user ID.
     * 
     * @param awardedBy the user ID to set
     */
    public void setAwardedBy(int awardedBy) {
        this.awardedBy = awardedBy;
    }
    
    /**
     * Gets the award timestamp.
     * 
     * @return when the award was made
     */
    public Timestamp getAwardedAt() {
        return awardedAt;
    }
    
    /**
     * Sets the award timestamp.
     * 
     * @param awardedAt the timestamp to set
     */
    public void setAwardedAt(Timestamp awardedAt) {
        this.awardedAt = awardedAt;
    }
    
    /**
     * Gets the tender reference number.
     * 
     * @return the tender reference
     */
    public String getTenderReference() {
        return tenderReference;
    }
    
    /**
     * Sets the tender reference number.
     * 
     * @param tenderReference the reference to set
     */
    public void setTenderReference(String tenderReference) {
        this.tenderReference = tenderReference;
    }
    
    /**
     * Gets the tender title.
     * 
     * @return the tender title
     */
    public String getTenderTitle() {
        return tenderTitle;
    }
    
    /**
     * Sets the tender title.
     * 
     * @param tenderTitle the title to set
     */
    public void setTenderTitle(String tenderTitle) {
        this.tenderTitle = tenderTitle;
    }
    
    /**
     * Gets the winning supplier's name.
     * 
     * @return the supplier name
     */
    public String getWinningSupplierName() {
        return winningSupplierName;
    }
    
    /**
     * Sets the winning supplier's name.
     * 
     * @param winningSupplierName the name to set
     */
    public void setWinningSupplierName(String winningSupplierName) {
        this.winningSupplierName = winningSupplierName;
    }
    
    /**
     * Gets the awarding officer's name.
     * 
     * @return the officer's name
     */
    public String getAwardedByName() {
        return awardedByName;
    }
    
    /**
     * Sets the awarding officer's name.
     * 
     * @param awardedByName the name to set
     */
    public void setAwardedByName(String awardedByName) {
        this.awardedByName = awardedByName;
    }
    
    /**
     * Returns a string representation of the Award object.
     * 
     * @return string containing award details
     */
    @Override
    public String toString() {
        return "Award{" +
                "awardId=" + awardId +
                ", tenderId=" + tenderId +
                ", winningBidId=" + winningBidId +
                ", awardedValue=" + awardedValue +
                ", awardedAt=" + awardedAt +
                '}';
    }
}