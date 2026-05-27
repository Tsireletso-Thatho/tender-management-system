package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Bid model class representing a supplier's submission for a specific tender.
 * Each supplier can submit only one bid per tender.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class Bid implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int bidId;
    private int tenderId;
    private int supplierId;
    private BigDecimal bidAmount;
    private String technicalComplianceStatement;
    private int proposedTimelineDays;
    private String supportingDocumentPath;
    private String status;
    private Timestamp submittedAt;
    private Timestamp updatedAt;
    
    // Additional fields for display
    private String supplierName;
    private String tenderReference;
    private String tenderTitle;
    private BigDecimal finalScore;
    private int rank;
    
    // Transient field for UI state (not persisted to database)
    private transient boolean evaluated;
    
    // Constants for bid statuses
    public static final String STATUS_SUBMITTED = "SUBMITTED";
    public static final String STATUS_EVALUATED = "EVALUATED";
    public static final String STATUS_WON = "WON";
    public static final String STATUS_NOT_WON = "NOT_WON";
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public Bid() {
        this.status = STATUS_SUBMITTED;
        this.evaluated = false;
    }
    
    /**
     * Constructs a new Bid with required fields.
     * 
     * @param tenderId the tender being bid on
     * @param supplierId the supplier submitting the bid
     * @param bidAmount the bid amount in Maloti
     * @param technicalComplianceStatement compliance statement (max 600 chars)
     * @param proposedTimelineDays proposed delivery timeline in days
     */
    public Bid(int tenderId, int supplierId, BigDecimal bidAmount,
               String technicalComplianceStatement, int proposedTimelineDays) {
        this.tenderId = tenderId;
        this.supplierId = supplierId;
        this.bidAmount = bidAmount;
        this.technicalComplianceStatement = technicalComplianceStatement;
        this.proposedTimelineDays = proposedTimelineDays;
        this.status = STATUS_SUBMITTED;
        this.evaluated = false;
    }
    
    /**
     * Gets the bid ID.
     * 
     * @return the unique identifier for this bid
     */
    public int getBidId() {
        return bidId;
    }
    
    /**
     * Sets the bid ID.
     * 
     * @param bidId the unique identifier to set
     */
    public void setBidId(int bidId) {
        this.bidId = bidId;
    }
    
    /**
     * Gets the associated tender ID.
     * 
     * @return the tender ID
     */
    public int getTenderId() {
        return tenderId;
    }
    
    /**
     * Sets the associated tender ID.
     * 
     * @param tenderId the tender ID to set
     */
    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }
    
    /**
     * Gets the supplier ID.
     * 
     * @return the supplier ID
     */
    public int getSupplierId() {
        return supplierId;
    }
    
    /**
     * Sets the supplier ID.
     * 
     * @param supplierId the supplier ID to set
     */
    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }
    
    /**
     * Gets the bid amount in Maloti.
     * 
     * @return the bid amount
     */
    public BigDecimal getBidAmount() {
        return bidAmount;
    }
    
    /**
     * Sets the bid amount.
     * 
     * @param bidAmount the amount to set
     */
    public void setBidAmount(BigDecimal bidAmount) {
        this.bidAmount = bidAmount;
    }
    
    /**
     * Gets the technical compliance statement.
     * 
     * @return the compliance statement
     */
    public String getTechnicalComplianceStatement() {
        return technicalComplianceStatement;
    }
    
    /**
     * Sets the technical compliance statement.
     * 
     * @param technicalComplianceStatement the statement to set
     */
    public void setTechnicalComplianceStatement(String technicalComplianceStatement) {
        this.technicalComplianceStatement = technicalComplianceStatement;
    }
    
    /**
     * Gets the proposed timeline in days.
     * 
     * @return the timeline in days
     */
    public int getProposedTimelineDays() {
        return proposedTimelineDays;
    }
    
    /**
     * Sets the proposed timeline in days.
     * 
     * @param proposedTimelineDays the timeline to set
     */
    public void setProposedTimelineDays(int proposedTimelineDays) {
        this.proposedTimelineDays = proposedTimelineDays;
    }
    
    /**
     * Gets the supporting document file path.
     * 
     * @return the file path on server
     */
    public String getSupportingDocumentPath() {
        return supportingDocumentPath;
    }
    
    /**
     * Sets the supporting document file path.
     * 
     * @param supportingDocumentPath the file path to set
     */
    public void setSupportingDocumentPath(String supportingDocumentPath) {
        this.supportingDocumentPath = supportingDocumentPath;
    }
    
    /**
     * Gets the bid status.
     * 
     * @return the status (SUBMITTED, EVALUATED, WON, NOT_WON)
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the bid status.
     * 
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the submission timestamp.
     * 
     * @return when the bid was submitted
     */
    public Timestamp getSubmittedAt() {
        return submittedAt;
    }
    
    /**
     * Sets the submission timestamp.
     * 
     * @param submittedAt the timestamp to set
     */
    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
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
     * Gets the supplier name for display.
     * 
     * @return the supplier's company name
     */
    public String getSupplierName() {
        return supplierName;
    }
    
    /**
     * Sets the supplier name.
     * 
     * @param supplierName the name to set
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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
     * Gets the final evaluation score.
     * 
     * @return the final score (averaged across evaluators)
     */
    public BigDecimal getFinalScore() {
        return finalScore;
    }
    
    /**
     * Sets the final evaluation score.
     * 
     * @param finalScore the score to set
     */
    public void setFinalScore(BigDecimal finalScore) {
        this.finalScore = finalScore;
    }
    
    /**
     * Gets the bid rank among all bids for the tender.
     * 
     * @return the rank (1 = highest score)
     */
    public int getRank() {
        return rank;
    }
    
    /**
     * Sets the bid rank.
     * 
     * @param rank the rank to set
     */
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    /**
     * Checks if this bid has been evaluated by the current evaluator.
     * This is a transient field used for display purposes only.
     * 
     * @return true if evaluated by current evaluator
     */
    public boolean isEvaluated() {
        return evaluated;
    }
    
    /**
     * Sets whether this bid has been evaluated by the current evaluator.
     * This is a transient field used for display purposes only.
     * 
     * @param evaluated true if evaluated
     */
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }
    
    /**
     * Checks if this bid is the winning bid.
     * 
     * @return true if status is WON
     */
    public boolean isWinningBid() {
        return STATUS_WON.equals(this.status);
    }
    
    /**
     * Checks if this bid has been fully evaluated (database status).
     * 
     * @return true if status is EVALUATED, WON, or NOT_WON
     */
    public boolean isFullyEvaluated() {
        return STATUS_EVALUATED.equals(this.status) ||
               STATUS_WON.equals(this.status) ||
               STATUS_NOT_WON.equals(this.status);
    }
    
    /**
     * Returns a formatted string of the bid amount.
     * 
     * @return formatted bid amount with 2 decimal places
     */
    public String getFormattedBidAmount() {
        if (bidAmount == null) {
            return "0.00";
        }
        return String.format("%,.2f", bidAmount);
    }
    
    /**
     * Returns a formatted string of the final score.
     * 
     * @return formatted final score with 2 decimal places
     */
    public String getFormattedFinalScore() {
        if (finalScore == null) {
            return "0.00";
        }
        return String.format("%.2f", finalScore);
    }
    
    /**
     * Returns a string representation of the Bid object.
     * 
     * @return string containing bid details
     */
    @Override
    public String toString() {
        return "Bid{" +
                "bidId=" + bidId +
                ", tenderId=" + tenderId +
                ", supplierId=" + supplierId +
                ", bidAmount=" + bidAmount +
                ", status='" + status + '\'' +
                ", evaluated=" + evaluated +
                '}';
    }
}