package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Evaluation Score model class storing individual evaluator scores for a bid.
 * Scores are calculated per evaluator and averaged for the final score.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class EvaluationScore implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private int scoreId;
    private int tenderId;
    private int bidId;
    private int evaluatorId;
    private BigDecimal technicalScore;
    private BigDecimal priceScore;
    private BigDecimal timelineScore;
    private BigDecimal weightedTotal;
    private Timestamp submittedAt;
    
    // Additional fields for display
    private String evaluatorName;
    private String supplierName;
    private BigDecimal bidAmount;
    
    // Scoring weight constants
    public static final double PRICE_WEIGHT = 0.40;
    public static final double TECHNICAL_WEIGHT = 0.35;
    public static final double TIMELINE_WEIGHT = 0.25;
    
    /**
     * Default constructor required for JavaBean specification.
     */
    public EvaluationScore() {
    }
    
    /**
     * Constructs a new Evaluation Score.
     * 
     * @param tenderId the tender being evaluated
     * @param bidId the bid being scored
     * @param evaluatorId the evaluator submitting the score
     * @param technicalScore the manual technical score (0-100)
     * @param priceScore the auto-calculated price score
     * @param timelineScore the auto-calculated timeline score
     */
    public EvaluationScore(int tenderId, int bidId, int evaluatorId,
                           BigDecimal technicalScore, BigDecimal priceScore,
                           BigDecimal timelineScore) {
        this.tenderId = tenderId;
        this.bidId = bidId;
        this.evaluatorId = evaluatorId;
        this.technicalScore = technicalScore;
        this.priceScore = priceScore;
        this.timelineScore = timelineScore;
        calculateWeightedTotal();
    }
    
    /**
     * Calculates the weighted total score using the formula:
     * (Price × 0.40) + (Technical × 0.35) + (Timeline × 0.25)
     */
    public void calculateWeightedTotal() {
        if (priceScore != null && technicalScore != null && timelineScore != null) {
            double priceComponent = priceScore.doubleValue() * PRICE_WEIGHT;
            double technicalComponent = technicalScore.doubleValue() * TECHNICAL_WEIGHT;
            double timelineComponent = timelineScore.doubleValue() * TIMELINE_WEIGHT;
            this.weightedTotal = BigDecimal.valueOf(priceComponent + technicalComponent + timelineComponent);
        }
    }
    
    /**
     * Gets the score ID.
     * 
     * @return the unique identifier for this score
     */
    public int getScoreId() {
        return scoreId;
    }
    
    /**
     * Sets the score ID.
     * 
     * @param scoreId the unique identifier to set
     */
    public void setScoreId(int scoreId) {
        this.scoreId = scoreId;
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
     * Gets the bid ID.
     * 
     * @return the bid ID
     */
    public int getBidId() {
        return bidId;
    }
    
    /**
     * Sets the bid ID.
     * 
     * @param bidId the bid ID to set
     */
    public void setBidId(int bidId) {
        this.bidId = bidId;
    }
    
    /**
     * Gets the evaluator ID.
     * 
     * @return the evaluator ID
     */
    public int getEvaluatorId() {
        return evaluatorId;
    }
    
    /**
     * Sets the evaluator ID.
     * 
     * @param evaluatorId the evaluator ID to set
     */
    public void setEvaluatorId(int evaluatorId) {
        this.evaluatorId = evaluatorId;
    }
    
    /**
     * Gets the technical score (manual entry, 0-100).
     * 
     * @return the technical score
     */
    public BigDecimal getTechnicalScore() {
        return technicalScore;
    }
    
    /**
     * Sets the technical score.
     * 
     * @param technicalScore the score to set
     */
    public void setTechnicalScore(BigDecimal technicalScore) {
        this.technicalScore = technicalScore;
    }
    
    /**
     * Gets the auto-calculated price score.
     * 
     * @return the price score
     */
    public BigDecimal getPriceScore() {
        return priceScore;
    }
    
    /**
     * Sets the price score.
     * 
     * @param priceScore the score to set
     */
    public void setPriceScore(BigDecimal priceScore) {
        this.priceScore = priceScore;
    }
    
    /**
     * Gets the auto-calculated timeline score.
     * 
     * @return the timeline score
     */
    public BigDecimal getTimelineScore() {
        return timelineScore;
    }
    
    /**
     * Sets the timeline score.
     * 
     * @param timelineScore the score to set
     */
    public void setTimelineScore(BigDecimal timelineScore) {
        this.timelineScore = timelineScore;
    }
    
    /**
     * Gets the weighted total score.
     * 
     * @return the weighted total
     */
    public BigDecimal getWeightedTotal() {
        return weightedTotal;
    }
    
    /**
     * Sets the weighted total score.
     * 
     * @param weightedTotal the total to set
     */
    public void setWeightedTotal(BigDecimal weightedTotal) {
        this.weightedTotal = weightedTotal;
    }
    
    /**
     * Gets the submission timestamp.
     * 
     * @return when the score was submitted
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
     * Gets the evaluator name for display.
     * 
     * @return the evaluator's full name
     */
    public String getEvaluatorName() {
        return evaluatorName;
    }
    
    /**
     * Sets the evaluator name.
     * 
     * @param evaluatorName the name to set
     */
    public void setEvaluatorName(String evaluatorName) {
        this.evaluatorName = evaluatorName;
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
     * Gets the bid amount for reference.
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
     * Returns a formatted string of the weighted total with 2 decimal places.
     * 
     * @return formatted weighted total
     */
    public String getFormattedWeightedTotal() {
        if (weightedTotal == null) {
            return "0.00";
        }
        return String.format("%.2f", weightedTotal);
    }
    
    /**
     * Returns a string representation of the EvaluationScore object.
     * 
     * @return string containing score details
     */
    @Override
    public String toString() {
        return "EvaluationScore{" +
                "scoreId=" + scoreId +
                ", bidId=" + bidId +
                ", evaluatorId=" + evaluatorId +
                ", technicalScore=" + technicalScore +
                ", priceScore=" + priceScore +
                ", timelineScore=" + timelineScore +
                ", weightedTotal=" + weightedTotal +
                '}';
    }
}