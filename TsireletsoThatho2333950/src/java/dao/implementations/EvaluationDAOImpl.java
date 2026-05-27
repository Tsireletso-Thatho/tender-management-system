package dao.implementations;

import dao.interfaces.EvaluationDAO;
import model.EvaluationScore;
import model.Bid;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the EvaluationDAO interface. Provides database operations
 * for EvaluationScore entities using JDBC.
 *
 * CRITICAL: No circular dependencies - uses lazy-loading getters for other
 * DAOs.
 *
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class EvaluationDAOImpl implements EvaluationDAO {

    private static final Logger LOGGER = Logger.getLogger(EvaluationDAOImpl.class.getName());

    /**
     * Default constructor.
     */
    public EvaluationDAOImpl() {
    }

    /**
     * Lazy-loading getter for BidDAO to avoid circular dependencies.
     *
     * @return BidDAOImpl instance
     */
    private BidDAOImpl getBidDAO() {
        return new BidDAOImpl();
    }

    /**
     * Lazy-loading getter for TenderDAO to avoid circular dependencies.
     *
     * @return TenderDAOImpl instance
     */
    private TenderDAOImpl getTenderDAO() {
        return new TenderDAOImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(EvaluationScore score) {
        String sql = "INSERT INTO evaluation_scores (tender_id, bid_id, evaluator_id, "
                + "technical_score, price_score, timeline_score, weighted_total) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, score.getTenderId());
            pstmt.setInt(2, score.getBidId());
            pstmt.setInt(3, score.getEvaluatorId());
            pstmt.setBigDecimal(4, score.getTechnicalScore());
            pstmt.setBigDecimal(5, score.getPriceScore());
            pstmt.setBigDecimal(6, score.getTimelineScore());
            pstmt.setBigDecimal(7, score.getWeightedTotal());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int scoreId = rs.getInt(1);

                        // Check if evaluation is complete and auto-transition tender status
                        int tenderId = score.getTenderId();
                        if (isTenderEvaluationComplete(tenderId)) {
                            getTenderDAO().markAsEvaluated(tenderId);
                            LOGGER.log(Level.INFO, "Tender {0} automatically marked as EVALUATED", tenderId);
                        }

                        return scoreId;
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating evaluation score: {0}", e.getMessage());
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EvaluationScore findById(int scoreId) {
        String sql = "SELECT es.*, u.email as evaluator_name, "
                + "s.company_name as supplier_name, b.bid_amount "
                + "FROM evaluation_scores es "
                + "LEFT JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN suppliers s ON b.supplier_id = s.supplier_id "
                + "WHERE es.score_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, scoreId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvaluationScore(rs);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding evaluation score by ID: {0}", e.getMessage());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EvaluationScore findByBidAndEvaluator(int bidId, int evaluatorId) {
        String sql = "SELECT * FROM evaluation_scores WHERE bid_id = ? AND evaluator_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bidId);
            pstmt.setInt(2, evaluatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    EvaluationScore score = new EvaluationScore();
                    score.setScoreId(rs.getInt("score_id"));
                    score.setTenderId(rs.getInt("tender_id"));
                    score.setBidId(rs.getInt("bid_id"));
                    score.setEvaluatorId(rs.getInt("evaluator_id"));
                    score.setTechnicalScore(rs.getBigDecimal("technical_score"));
                    score.setPriceScore(rs.getBigDecimal("price_score"));
                    score.setTimelineScore(rs.getBigDecimal("timeline_score"));
                    score.setWeightedTotal(rs.getBigDecimal("weighted_total"));
                    score.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    return score;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding evaluation score: {0}", e.getMessage());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EvaluationScore> findByTenderId(int tenderId) {
        List<EvaluationScore> scores = new ArrayList<>();
        String sql = "SELECT es.*, u.email as evaluator_name, "
                + "s.company_name as supplier_name, b.bid_amount "
                + "FROM evaluation_scores es "
                + "LEFT JOIN users u ON es.evaluator_id = u.user_id "
                + "JOIN bids b ON es.bid_id = b.bid_id "
                + "JOIN suppliers s ON b.supplier_id = s.supplier_id "
                + "WHERE es.tender_id = ? "
                + "ORDER BY es.submitted_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(mapResultSetToEvaluationScore(rs));
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding scores by tender ID: {0}", e.getMessage());
        }

        return scores;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EvaluationScore> findByBidId(int bidId) {
        List<EvaluationScore> scores = new ArrayList<>();
        String sql = "SELECT es.*, u.email as evaluator_name "
                + "FROM evaluation_scores es "
                + "LEFT JOIN users u ON es.evaluator_id = u.user_id "
                + "WHERE es.bid_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bidId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EvaluationScore score = new EvaluationScore();
                    score.setScoreId(rs.getInt("score_id"));
                    score.setTenderId(rs.getInt("tender_id"));
                    score.setBidId(rs.getInt("bid_id"));
                    score.setEvaluatorId(rs.getInt("evaluator_id"));
                    score.setTechnicalScore(rs.getBigDecimal("technical_score"));
                    score.setPriceScore(rs.getBigDecimal("price_score"));
                    score.setTimelineScore(rs.getBigDecimal("timeline_score"));
                    score.setWeightedTotal(rs.getBigDecimal("weighted_total"));
                    score.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    score.setEvaluatorName(rs.getString("evaluator_name"));
                    scores.add(score);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding scores by bid ID: {0}", e.getMessage());
        }

        return scores;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EvaluationScore> findByEvaluatorId(int evaluatorId) {
        List<EvaluationScore> scores = new ArrayList<>();
        String sql = "SELECT es.*, t.reference_number as tender_reference "
                + "FROM evaluation_scores es "
                + "JOIN tenders t ON es.tender_id = t.tender_id "
                + "WHERE es.evaluator_id = ? "
                + "ORDER BY es.submitted_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, evaluatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EvaluationScore score = new EvaluationScore();
                    score.setScoreId(rs.getInt("score_id"));
                    score.setTenderId(rs.getInt("tender_id"));
                    score.setBidId(rs.getInt("bid_id"));
                    score.setEvaluatorId(rs.getInt("evaluator_id"));
                    score.setTechnicalScore(rs.getBigDecimal("technical_score"));
                    score.setPriceScore(rs.getBigDecimal("price_score"));
                    score.setTimelineScore(rs.getBigDecimal("timeline_score"));
                    score.setWeightedTotal(rs.getBigDecimal("weighted_total"));
                    score.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    scores.add(score);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding scores by evaluator ID: {0}", e.getMessage());
        }

        return scores;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EvaluationScore> findByTenderAndEvaluator(int tenderId, int evaluatorId) {
        List<EvaluationScore> scores = new ArrayList<>();
        String sql = "SELECT * FROM evaluation_scores WHERE tender_id = ? AND evaluator_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);
            pstmt.setInt(2, evaluatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EvaluationScore score = new EvaluationScore();
                    score.setScoreId(rs.getInt("score_id"));
                    score.setTenderId(rs.getInt("tender_id"));
                    score.setBidId(rs.getInt("bid_id"));
                    score.setEvaluatorId(rs.getInt("evaluator_id"));
                    score.setTechnicalScore(rs.getBigDecimal("technical_score"));
                    score.setPriceScore(rs.getBigDecimal("price_score"));
                    score.setTimelineScore(rs.getBigDecimal("timeline_score"));
                    score.setWeightedTotal(rs.getBigDecimal("weighted_total"));
                    score.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    scores.add(score);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding scores by tender and evaluator: {0}", e.getMessage());
        }

        return scores;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(EvaluationScore score) {
        String sql = "UPDATE evaluation_scores SET technical_score = ?, price_score = ?, "
                + "timeline_score = ?, weighted_total = ? WHERE score_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setBigDecimal(1, score.getTechnicalScore());
            pstmt.setBigDecimal(2, score.getPriceScore());
            pstmt.setBigDecimal(3, score.getTimelineScore());
            pstmt.setBigDecimal(4, score.getWeightedTotal());
            pstmt.setInt(5, score.getScoreId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating evaluation score: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int scoreId) {
        String sql = "DELETE FROM evaluation_scores WHERE score_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, scoreId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting evaluation score: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEvaluatorScored(int bidId, int evaluatorId) {
        String sql = "SELECT COUNT(*) FROM evaluation_scores WHERE bid_id = ? AND evaluator_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bidId);
            pstmt.setInt(2, evaluatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if evaluator scored: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc} FIXED: If totalBids == 0, return false (cannot complete
     * evaluation with no bids)
     */
    @Override
    public boolean hasEvaluatorCompletedTender(int tenderId, int evaluatorId) {
        int totalBids = getBidDAO().countByTenderId(tenderId);

        // If no bids, evaluation cannot be meaningfully "completed"
        if (totalBids == 0) {
            LOGGER.log(Level.INFO, "Tender {0} has no bids - evaluator {1} cannot complete",
                    new Object[]{tenderId, evaluatorId});
            return false;
        }

        String sql = "SELECT COUNT(*) FROM evaluation_scores WHERE tender_id = ? AND evaluator_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);
            pstmt.setInt(2, evaluatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int scoresSubmitted = rs.getInt(1);
                    boolean completed = scoresSubmitted >= totalBids;
                    LOGGER.log(Level.INFO, "Evaluator {0} for tender {1}: {2}/{3} bids scored, completed={4}",
                            new Object[]{evaluatorId, tenderId, scoresSubmitted, totalBids, completed});
                    return completed;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking evaluator completion: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * Counts the number of tenders that an evaluator has fully completed. A
     * tender is "completed" when the evaluator has scored ALL bids for that
     * tender.
     *
     * @param evaluatorId the evaluator's user_id
     * @return count of fully evaluated tenders
     */
    public int countCompletedTendersByEvaluator(int evaluatorId) {
        String sql = "SELECT COUNT(DISTINCT t.tender_id) "
                + "FROM tenders t "
                + "JOIN bids b ON t.tender_id = b.tender_id "
                + "WHERE t.status IN ('UNDER_EVALUATION', 'EVALUATED', 'AWARDED') "
                + "AND t.tender_id NOT IN ("
                + "    SELECT DISTINCT b2.tender_id "
                + "    FROM bids b2 "
                + "    WHERE NOT EXISTS ("
                + "        SELECT 1 FROM evaluation_scores es "
                + "        WHERE es.bid_id = b2.bid_id AND es.evaluator_id = ?"
                + "    )"
                + ")";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, evaluatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    LOGGER.log(Level.INFO, "Evaluator {0} has completed {1} tenders",
                            new Object[]{evaluatorId, count});
                    return count;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting completed tenders for evaluator {0}: {1}",
                    new Object[]{evaluatorId, e.getMessage()});
        }

        return 0;
    }

    /**
     * {@inheritDoc} FIXED: Counts BOTH Evaluation Committee AND Procurement
     * Officers
     */
    @Override
    public boolean isTenderEvaluationComplete(int tenderId) {
        int totalBids = getBidDAO().countByTenderId(tenderId);
        if (totalBids == 0) {
            LOGGER.log(Level.INFO, "Tender {0} has no bids - evaluation cannot be complete", tenderId);
            return false;
        }

        int totalEvaluators = getTotalEvaluatorCount();
        if (totalEvaluators == 0) {
            LOGGER.log(Level.WARNING, "No evaluators found in the system");
            return false;
        }

        int expectedScores = totalBids * totalEvaluators;

        String sql = "SELECT COUNT(*) FROM evaluation_scores WHERE tender_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int actualScores = rs.getInt(1);
                    boolean complete = actualScores >= expectedScores;
                    LOGGER.log(Level.INFO, "Tender {0}: {1}/{2} scores submitted (bids={3}, evaluators={4}), complete={5}",
                            new Object[]{tenderId, actualScores, expectedScores, totalBids, totalEvaluators, complete});
                    return complete;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking tender evaluation completion: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal calculateAverageWeightedTotal(int bidId) {
        String sql = "SELECT AVG(weighted_total) FROM evaluation_scores WHERE bid_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, bidId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BigDecimal avg = rs.getBigDecimal(1);
                    return avg != null ? avg.setScale(2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error calculating average weighted total: {0}", e.getMessage());
        }

        return BigDecimal.ZERO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Integer, BigDecimal> getFinalScores(int tenderId) {
        Map<Integer, BigDecimal> finalScores = new HashMap<>();
        List<Bid> bids = getBidDAO().findByTenderId(tenderId);

        for (Bid bid : bids) {
            BigDecimal avgScore = calculateAverageWeightedTotal(bid.getBidId());
            finalScores.put(bid.getBidId(), avgScore);
        }

        return finalScores;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getRankedBids(int tenderId) {
        Map<Integer, BigDecimal> finalScores = getFinalScores(tenderId);

        List<Map.Entry<Integer, BigDecimal>> entries = new ArrayList<>(finalScores.entrySet());
        entries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        List<Integer> rankedBidIds = new ArrayList<>();
        for (Map.Entry<Integer, BigDecimal> entry : entries) {
            rankedBidIds.add(entry.getKey());
        }

        return rankedBidIds;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<EvaluationScore> getDetailedResults(int tenderId) {
        return findByTenderId(tenderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getEvaluatorCountForTender(int tenderId) {
        String sql = "SELECT COUNT(DISTINCT evaluator_id) FROM evaluation_scores WHERE tender_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting evaluator count for tender: {0}", e.getMessage());
        }

        return 0;
    }

    /**
     * {@inheritDoc} FIXED: Counts BOTH Evaluation Committee members AND
     * Procurement Officers
     */
    @Override
    public int getTotalEvaluatorCount() {
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM evaluation_committee ec JOIN users u ON ec.user_id = u.user_id WHERE u.status = 'ACTIVE') + "
                + "(SELECT COUNT(*) FROM procurement_officers po JOIN users u ON po.user_id = u.user_id WHERE u.status = 'ACTIVE') "
                + "AS total_evaluators";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                int total = rs.getInt(1);
                LOGGER.log(Level.INFO, "Total evaluators (Officers + Committee): {0}", total);
                return total;
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting total evaluator count: {0}", e.getMessage());
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM evaluation_scores";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting evaluation scores: {0}", e.getMessage());
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByTenderId(int tenderId) {
        String sql = "SELECT COUNT(*) FROM evaluation_scores WHERE tender_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting scores by tender ID: {0}", e.getMessage());
        }

        return 0;
    }

    /**
     * Maps a ResultSet row to an EvaluationScore object.
     *
     * @param rs the ResultSet positioned at the current row
     * @return the mapped EvaluationScore object
     * @throws SQLException if a database error occurs
     */
    private EvaluationScore mapResultSetToEvaluationScore(ResultSet rs) throws SQLException {
        EvaluationScore score = new EvaluationScore();
        score.setScoreId(rs.getInt("score_id"));
        score.setTenderId(rs.getInt("tender_id"));
        score.setBidId(rs.getInt("bid_id"));
        score.setEvaluatorId(rs.getInt("evaluator_id"));
        score.setTechnicalScore(rs.getBigDecimal("technical_score"));
        score.setPriceScore(rs.getBigDecimal("price_score"));
        score.setTimelineScore(rs.getBigDecimal("timeline_score"));
        score.setWeightedTotal(rs.getBigDecimal("weighted_total"));
        score.setSubmittedAt(rs.getTimestamp("submitted_at"));

        try {
            score.setEvaluatorName(rs.getString("evaluator_name"));
        } catch (SQLException e) {
            // Column may not be present
        }

        try {
            score.setSupplierName(rs.getString("supplier_name"));
        } catch (SQLException e) {
            // Column may not be present
        }

        try {
            score.setBidAmount(rs.getBigDecimal("bid_amount"));
        } catch (SQLException e) {
            // Column may not be present
        }

        return score;
    }
}
