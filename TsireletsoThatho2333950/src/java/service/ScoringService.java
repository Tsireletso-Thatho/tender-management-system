package service;

import dao.implementations.BidDAOImpl;
import dao.implementations.EvaluationDAOImpl;
import dao.implementations.TenderDAOImpl;
import model.Bid;
import model.EvaluationScore;
import model.Tender;
import util.Constants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for bid scoring and evaluation business logic.
 * Handles automatic score calculations, weighted totals, and evaluation completion.
 * 
 * Required by Module 4: Bid Evaluation.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ScoringService {
    
    private static final Logger LOGGER = Logger.getLogger(ScoringService.class.getName());
    
    private final EvaluationDAOImpl evaluationDAO;
    private final BidDAOImpl bidDAO;
    private final TenderDAOImpl tenderDAO;
    
    /**
     * Constructor initializes DAO instances.
     */
    public ScoringService() {
        this.evaluationDAO = new EvaluationDAOImpl();
        this.bidDAO = new BidDAOImpl();
        this.tenderDAO = new TenderDAOImpl();
    }
    
    /**
     * Calculates the price score for a bid.
     * Formula: (Lowest Bid Amount / This Bid Amount) × 100
     * 
     * @param tenderId the tender ID
     * @param bidAmount the bid amount to score
     * @return the calculated price score (0-100)
     */
    public BigDecimal calculatePriceScore(int tenderId, BigDecimal bidAmount) {
        BigDecimal lowestBid = bidDAO.getLowestBidAmount(tenderId);
        
        if (lowestBid == null || lowestBid.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        if (bidAmount == null || bidAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return lowestBid.divide(bidAmount, 4, RoundingMode.HALF_UP)
                       .multiply(new BigDecimal("100"))
                       .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculates the timeline score for a bid.
     * Formula: (Shortest Proposed Timeline / This Bid's Timeline) × 100
     * 
     * @param tenderId the tender ID
     * @param proposedTimelineDays the timeline to score
     * @return the calculated timeline score (0-100)
     */
    public BigDecimal calculateTimelineScore(int tenderId, int proposedTimelineDays) {
        int shortestTimeline = bidDAO.getShortestTimeline(tenderId);
        
        if (shortestTimeline == 0 || proposedTimelineDays == 0) {
            return BigDecimal.ZERO;
        }
        
        double score = ((double) shortestTimeline / proposedTimelineDays) * 100;
        return BigDecimal.valueOf(score).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Calculates the weighted total score for a bid evaluation.
     * Formula: (Price × 0.40) + (Technical × 0.35) + (Timeline × 0.25)
     * 
     * @param priceScore the price score (0-100)
     * @param technicalScore the technical score (0-100)
     * @param timelineScore the timeline score (0-100)
     * @return the weighted total score
     */
    public BigDecimal calculateWeightedTotal(BigDecimal priceScore, 
                                              BigDecimal technicalScore, 
                                              BigDecimal timelineScore) {
        if (priceScore == null || technicalScore == null || timelineScore == null) {
            return BigDecimal.ZERO;
        }
        
        double priceComponent = priceScore.doubleValue() * Constants.PRICE_WEIGHT;
        double technicalComponent = technicalScore.doubleValue() * Constants.TECHNICAL_WEIGHT;
        double timelineComponent = timelineScore.doubleValue() * Constants.TIMELINE_WEIGHT;
        
        return BigDecimal.valueOf(priceComponent + technicalComponent + timelineComponent)
                         .setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Submits an evaluation score for a bid.
     * Automatically calculates price and timeline scores, then weighted total.
     * 
     * @param tenderId the tender ID
     * @param bidId the bid ID
     * @param evaluatorId the evaluator ID
     * @param technicalScore the manual technical score (0-100)
     * @return the generated score ID, or -1 if submission fails
     */
    public int submitEvaluation(int tenderId, int bidId, int evaluatorId, BigDecimal technicalScore) {
        // Check if evaluator has already scored this bid
        if (evaluationDAO.hasEvaluatorScored(bidId, evaluatorId)) {
            LOGGER.log(Level.WARNING, "Evaluator {0} already scored bid {1}", 
                       new Object[]{evaluatorId, bidId});
            return -1;
        }
        
        // Get bid details
        Bid bid = bidDAO.findById(bidId);
        if (bid == null) {
            LOGGER.log(Level.WARNING, "Bid not found: ID {0}", bidId);
            return -1;
        }
        
        // Calculate automatic scores
        BigDecimal priceScore = calculatePriceScore(tenderId, bid.getBidAmount());
        BigDecimal timelineScore = calculateTimelineScore(tenderId, bid.getProposedTimelineDays());
        
        // Calculate weighted total
        BigDecimal weightedTotal = calculateWeightedTotal(priceScore, technicalScore, timelineScore);
        
        // Create evaluation score
        EvaluationScore score = new EvaluationScore();
        score.setTenderId(tenderId);
        score.setBidId(bidId);
        score.setEvaluatorId(evaluatorId);
        score.setTechnicalScore(technicalScore);
        score.setPriceScore(priceScore);
        score.setTimelineScore(timelineScore);
        score.setWeightedTotal(weightedTotal);
        
        int scoreId = evaluationDAO.create(score);
        
        if (scoreId != -1) {
            LOGGER.log(Level.INFO, "Evaluation submitted: Evaluator {0}, Bid {1}, Weighted Total {2}", 
                       new Object[]{evaluatorId, bidId, weightedTotal});
            
            // Update bid status to EVALUATED
            bidDAO.updateStatus(bidId, Constants.BID_STATUS_EVALUATED);
        }
        
        return scoreId;
    }
    
    /**
     * Gets all evaluation scores for a tender.
     * 
     * @param tenderId the tender ID
     * @return List of EvaluationScore objects
     */
    public List<EvaluationScore> getScoresByTenderId(int tenderId) {
        return evaluationDAO.findByTenderId(tenderId);
    }
    
    /**
     * Gets all scores submitted by a specific evaluator.
     * 
     * @param evaluatorId the evaluator ID
     * @return List of EvaluationScore objects
     */
    public List<EvaluationScore> getScoresByEvaluatorId(int evaluatorId) {
        return evaluationDAO.findByEvaluatorId(evaluatorId);
    }
    
    /**
     * Gets the final scores for all bids in a tender.
     * Averages weighted totals across all evaluators.
     * 
     * @param tenderId the tender ID
     * @return Map of bid ID to final score
     */
    public Map<Integer, BigDecimal> getFinalScores(int tenderId) {
        return evaluationDAO.getFinalScores(tenderId);
    }
    
    /**
     * Gets ranked bid IDs for a tender ordered by final score descending.
     * 
     * @param tenderId the tender ID
     * @return List of bid IDs ordered by rank (highest score first)
     */
    public List<Integer> getRankedBids(int tenderId) {
        return evaluationDAO.getRankedBids(tenderId);
    }
    
    /**
     * Checks if a tender evaluation is complete.
     * All evaluators must have scored all bids.
     * 
     * @param tenderId the tender ID
     * @return true if evaluation is complete
     */
    public boolean isEvaluationComplete(int tenderId) {
        return evaluationDAO.isTenderEvaluationComplete(tenderId);
    }
    
    /**
     * Checks if an evaluator has completed scoring a tender.
     * 
     * @param tenderId the tender ID
     * @param evaluatorId the evaluator ID
     * @return true if evaluator has scored all bids
     */
    public boolean hasEvaluatorCompletedTender(int tenderId, int evaluatorId) {
        return evaluationDAO.hasEvaluatorCompletedTender(tenderId, evaluatorId);
    }
    
    /**
     * Gets the average weighted total for a specific bid.
     * 
     * @param bidId the bid ID
     * @return the average weighted total score
     */
    public BigDecimal getAverageWeightedTotal(int bidId) {
        return evaluationDAO.calculateAverageWeightedTotal(bidId);
    }
    
    /**
     * Gets detailed evaluation results for a tender.
     * 
     * @param tenderId the tender ID
     * @return List of EvaluationScore objects with complete details
     */
    public List<EvaluationScore> getDetailedResults(int tenderId) {
        return evaluationDAO.getDetailedResults(tenderId);
    }
    
    /**
     * Gets the number of evaluators who have submitted scores for a tender.
     * 
     * @param tenderId the tender ID
     * @return count of evaluators who have submitted at least one score
     */
    public int getEvaluatorCountForTender(int tenderId) {
        return evaluationDAO.getEvaluatorCountForTender(tenderId);
    }
    
    /**
     * Validates a technical score value.
     * 
     * @param score the score to validate
     * @return true if score is between 0 and 100
     */
    public boolean isValidTechnicalScore(BigDecimal score) {
        if (score == null) {
            return false;
        }
        double value = score.doubleValue();
        return value >= Constants.MIN_SCORE && value <= Constants.MAX_SCORE;
    }
}