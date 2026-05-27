package dao.interfaces;

import model.EvaluationScore;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Data Access Object interface for EvaluationScore entity operations. Provides
 * methods for submitting, retrieving, and calculating evaluation scores.
 *
 * @author Tsireletso Thatho
 * @version 1.0
 */
public interface EvaluationDAO {

    /**
     * Creates a new evaluation score in the database.
     *
     * @param score the EvaluationScore object to create
     * @return the generated score ID, or -1 if creation failed
     */
    int create(EvaluationScore score);

    /**
     * Finds an evaluation score by its unique ID.
     *
     * @param scoreId the score ID to search for
     * @return the EvaluationScore object, or null if not found
     */
    EvaluationScore findById(int scoreId);

    /**
     * Finds an evaluator's score for a specific bid.
     *
     * @param bidId the bid ID
     * @param evaluatorId the evaluator ID
     * @return the EvaluationScore object, or null if not found
     */
    EvaluationScore findByBidAndEvaluator(int bidId, int evaluatorId);

    /**
     * Finds all evaluation scores for a specific tender.
     *
     * @param tenderId the tender ID
     * @return List of EvaluationScore objects for the tender
     */
    List<EvaluationScore> findByTenderId(int tenderId);

    /**
     * Finds all evaluation scores for a specific bid.
     *
     * @param bidId the bid ID
     * @return List of EvaluationScore objects for the bid
     */
    List<EvaluationScore> findByBidId(int bidId);

    /**
     * Finds all scores submitted by a specific evaluator.
     *
     * @param evaluatorId the evaluator ID
     * @return List of EvaluationScore objects submitted by the evaluator
     */
    List<EvaluationScore> findByEvaluatorId(int evaluatorId);

    /**
     * Finds all scores submitted by an evaluator for a specific tender.
     *
     * @param tenderId the tender ID
     * @param evaluatorId the evaluator ID
     * @return List of EvaluationScore objects
     */
    List<EvaluationScore> findByTenderAndEvaluator(int tenderId, int evaluatorId);

    /**
     * Updates an existing evaluation score.
     *
     * @param score the EvaluationScore object with updated information
     * @return true if update was successful, false otherwise
     */
    boolean update(EvaluationScore score);

    /**
     * Deletes an evaluation score from the database.
     *
     * @param scoreId the score ID to delete
     * @return true if deletion was successful, false otherwise
     */
    boolean delete(int scoreId);

    /**
     * Checks if an evaluator has already scored a specific bid.
     *
     * @param bidId the bid ID
     * @param evaluatorId the evaluator ID
     * @return true if a score exists, false otherwise
     */
    boolean hasEvaluatorScored(int bidId, int evaluatorId);

    /**
     * Checks if an evaluator has scored all bids for a tender.
     *
     * @param tenderId the tender ID
     * @param evaluatorId the evaluator ID
     * @return true if all bids are scored, false otherwise
     */
    boolean hasEvaluatorCompletedTender(int tenderId, int evaluatorId);

    /**
     * Checks if all evaluators have scored all bids for a tender.
     *
     * @param tenderId the tender ID
     * @return true if evaluation is complete, false otherwise
     */
    boolean isTenderEvaluationComplete(int tenderId);

    /**
     * Calculates the average weighted total for a bid across all evaluators.
     *
     * @param bidId the bid ID
     * @return the average weighted total score
     */
    BigDecimal calculateAverageWeightedTotal(int bidId);

    /**
     * Gets the final scores for all bids in a tender. Returns a map of bid ID
     * to average weighted total.
     *
     * @param tenderId the tender ID
     * @return Map of bid ID to final score
     */
    Map<Integer, BigDecimal> getFinalScores(int tenderId);

    /**
     * Gets ranked bids for a tender ordered by final score descending.
     *
     * @param tenderId the tender ID
     * @return List of bid IDs ordered by rank (highest score first)
     */
    List<Integer> getRankedBids(int tenderId);

    /**
     * Retrieves detailed evaluation results for a tender. Includes bid details,
     * individual evaluator scores, and final averages.
     *
     * @param tenderId the tender ID
     * @return List of EvaluationScore objects with complete details
     */
    List<EvaluationScore> getDetailedResults(int tenderId);

    /**
     * Gets the number of evaluators who have submitted scores for a tender.
     *
     * @param tenderId the tender ID
     * @return the count of evaluators who have submitted at least one score
     */
    int getEvaluatorCountForTender(int tenderId);

    /**
     * Gets the total number of evaluators assigned to a tender.
     *
     * @param tenderId the tender ID
     * @return the total evaluator count
     */
    int getTotalEvaluatorCount();

    /**
     * Counts the total number of evaluation scores.
     *
     * @return the total score count
     */
    int countAll();

    /**
     * Counts scores for a specific tender.
     *
     * @param tenderId the tender ID
     * @return the number of scores for the tender
     */
    int countByTenderId(int tenderId);

    /**
     * Counts the number of tenders that an evaluator has fully completed.
     *
     * @param evaluatorId the evaluator's user_id
     * @return count of fully evaluated tenders
     */
    int countCompletedTendersByEvaluator(int evaluatorId);
}
