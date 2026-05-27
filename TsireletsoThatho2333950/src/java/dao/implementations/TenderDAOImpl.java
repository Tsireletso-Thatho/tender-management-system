package dao.implementations;

import dao.interfaces.TenderDAO;
import model.Tender;
import util.DatabaseConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the TenderDAO interface. Provides database operations for
 * Tender entities using JDBC.
 *
 * CRITICAL: No circular dependencies - uses lazy-loading getters for other
 * DAOs.
 *
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class TenderDAOImpl implements TenderDAO {

    private static final Logger LOGGER = Logger.getLogger(TenderDAOImpl.class.getName());

    /**
     * Default constructor.
     */
    public TenderDAOImpl() {
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
     * Lazy-loading getter for EvaluationDAO to avoid circular dependencies.
     *
     * @return EvaluationDAOImpl instance
     */
    private EvaluationDAOImpl getEvaluationDAO() {
        return new EvaluationDAOImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int create(Tender tender) {
        String sql = "INSERT INTO tenders (reference_number, title, category, description, "
                + "estimated_value, submission_deadline, status, notice_document_path, created_by) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, tender.getReferenceNumber());
            pstmt.setString(2, tender.getTitle());
            pstmt.setString(3, tender.getCategory());
            pstmt.setString(4, tender.getDescription());
            pstmt.setBigDecimal(5, tender.getEstimatedValue());
            pstmt.setTimestamp(6, tender.getSubmissionDeadline());
            pstmt.setString(7, tender.getStatus() != null ? tender.getStatus() : Tender.STATUS_DRAFT);
            pstmt.setString(8, tender.getNoticeDocumentPath());
            pstmt.setInt(9, tender.getCreatedBy());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating tender: {0}", e.getMessage());
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tender findById(int tenderId) {
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.tender_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Tender tender = mapResultSetToTender(rs);
                    tender.setBidCount(getBidDAO().countByTenderId(tenderId));
                    return tender;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tender by ID: {0}", e.getMessage());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Tender findByReferenceNumber(String referenceNumber) {
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.reference_number = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, referenceNumber);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Tender tender = mapResultSetToTender(rs);
                    tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                    return tender;
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tender by reference number: {0}", e.getMessage());
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Tender tender) {
        String sql = "UPDATE tenders SET title = ?, category = ?, description = ?, "
                + "estimated_value = ?, submission_deadline = ?, notice_document_path = ? "
                + "WHERE tender_id = ? AND status = 'DRAFT'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, tender.getTitle());
            pstmt.setString(2, tender.getCategory());
            pstmt.setString(3, tender.getDescription());
            pstmt.setBigDecimal(4, tender.getEstimatedValue());
            pstmt.setTimestamp(5, tender.getSubmissionDeadline());
            pstmt.setString(6, tender.getNoticeDocumentPath());
            pstmt.setInt(7, tender.getTenderId());

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating tender: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateStatus(int tenderId, String newStatus) {
        String sql = "UPDATE tenders SET status = ? WHERE tender_id = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, newStatus);
            pstmt.setInt(2, tenderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating tender status: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean publish(int tenderId) {
        String sql = "UPDATE tenders SET status = 'OPEN', published_at = NOW() "
                + "WHERE tender_id = ? AND status = 'DRAFT'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error publishing tender: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int closeExpiredTenders() {
        String sql = "UPDATE tenders SET status = 'CLOSED', closed_at = NOW() "
                + "WHERE status = 'OPEN' AND submission_deadline < NOW()";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement()) {

            int affectedRows = stmt.executeUpdate(sql);
            LOGGER.log(Level.INFO, "Closed {0} expired tenders", affectedRows);
            return affectedRows;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error closing expired tenders: {0}", e.getMessage());
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean startEvaluation(int tenderId) {
        String sql = "UPDATE tenders SET status = 'UNDER_EVALUATION', evaluation_started_at = NOW() "
                + "WHERE tender_id = ? AND status = 'CLOSED'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error starting evaluation: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean markAsEvaluated(int tenderId) {
        String sql = "UPDATE tenders SET status = 'EVALUATED', evaluated_at = NOW() "
                + "WHERE tender_id = ? AND status = 'UNDER_EVALUATION'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error marking tender as evaluated: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean award(int tenderId) {
        String sql = "UPDATE tenders SET status = 'AWARDED', awarded_at = NOW() "
                + "WHERE tender_id = ? AND status = 'EVALUATED'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error awarding tender: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int tenderId) {
        String sql = "DELETE FROM tenders WHERE tender_id = ? AND status = 'DRAFT'";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, tenderId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting tender: {0}", e.getMessage());
        }

        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findAll() {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "ORDER BY t.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tender tender = mapResultSetToTender(rs);
                tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                tenders.add(tender);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all tenders: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findByStatus(String status) {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.status = ? "
                + "ORDER BY t.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tender tender = mapResultSetToTender(rs);
                    tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                    tenders.add(tender);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tenders by status: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findByCategory(String category) {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.category = ? "
                + "ORDER BY t.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tender tender = mapResultSetToTender(rs);
                    tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                    tenders.add(tender);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tenders by category: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findByFilters(String status, String category) {
        List<Tender> tenders = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id WHERE 1=1 "
        );

        if (status != null && !status.isEmpty()) {
            sqlBuilder.append("AND t.status = ? ");
        }
        if (category != null && !category.isEmpty()) {
            sqlBuilder.append("AND t.category = ? ");
        }
        sqlBuilder.append("ORDER BY t.created_at DESC");

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sqlBuilder.toString())) {

            int paramIndex = 1;
            if (status != null && !status.isEmpty()) {
                pstmt.setString(paramIndex++, status);
            }
            if (category != null && !category.isEmpty()) {
                pstmt.setString(paramIndex++, category);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tender tender = mapResultSetToTender(rs);
                    tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                    tenders.add(tender);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tenders by filters: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findOpenTenders() {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.status = 'OPEN' AND t.submission_deadline > NOW() "
                + "ORDER BY t.submission_deadline ASC";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tender tender = mapResultSetToTender(rs);
                tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                tenders.add(tender);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding open tenders: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findByCreator(int userId) {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.created_by = ? "
                + "ORDER BY t.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, userId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tender tender = mapResultSetToTender(rs);
                    tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                    tenders.add(tender);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tenders by creator: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findTendersForEvaluation() {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "WHERE t.status IN ('CLOSED', 'UNDER_EVALUATION', 'EVALUATED') "
                + "ORDER BY t.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Tender tender = mapResultSetToTender(rs);
                tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                tenders.add(tender);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tenders for evaluation: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Tender> findTendersForEvaluator(int evaluatorId) {
        List<Tender> tenders = new ArrayList<>();
        String sql = "SELECT DISTINCT t.*, u.email as created_by_email "
                + "FROM tenders t "
                + "LEFT JOIN users u ON t.created_by = u.user_id "
                + "JOIN bids b ON t.tender_id = b.tender_id "
                + "WHERE t.status IN ('CLOSED', 'UNDER_EVALUATION') "
                + "AND NOT EXISTS ("
                + "    SELECT 1 FROM evaluation_scores es "
                + "    WHERE es.bid_id = b.bid_id AND es.evaluator_id = ?"
                + ") "
                + "ORDER BY t.created_at DESC";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, evaluatorId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Tender tender = mapResultSetToTender(rs);
                    tender.setBidCount(getBidDAO().countByTenderId(tender.getTenderId()));
                    tenders.add(tender);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding tenders for evaluator: {0}", e.getMessage());
        }

        return tenders;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String generateReferenceNumber() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int sequence = getNextSequenceNumber();
        return String.format("MPW-%d-%04d", year, sequence);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasBids(int tenderId) {
        return getBidDAO().countByTenderId(tenderId) > 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getBidCount(int tenderId) {
        return getBidDAO().countByTenderId(tenderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getLowestBidAmount(int tenderId) {
        return getBidDAO().getLowestBidAmount(tenderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getShortestTimeline(int tenderId) {
        return getBidDAO().getShortestTimeline(tenderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM tenders";

        try (Connection conn = DatabaseConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting tenders: {0}", e.getMessage());
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM tenders WHERE status = ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, status);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting tenders by status: {0}", e.getMessage());
        }

        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNextSequenceNumber() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String sql = "SELECT COUNT(*) + 1 FROM tenders WHERE reference_number LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, "MPW-" + year + "-%");

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting next sequence number: {0}", e.getMessage());
        }

        return 1;
    }

    /**
     * Maps a ResultSet row to a Tender object.
     *
     * @param rs the ResultSet positioned at the current row
     * @return the mapped Tender object
     * @throws SQLException if a database error occurs
     */
    private Tender mapResultSetToTender(ResultSet rs) throws SQLException {
        Tender tender = new Tender();
        tender.setTenderId(rs.getInt("tender_id"));
        tender.setReferenceNumber(rs.getString("reference_number"));
        tender.setTitle(rs.getString("title"));
        tender.setCategory(rs.getString("category"));
        tender.setDescription(rs.getString("description"));
        tender.setEstimatedValue(rs.getBigDecimal("estimated_value"));
        tender.setSubmissionDeadline(rs.getTimestamp("submission_deadline"));
        tender.setStatus(rs.getString("status"));
        tender.setNoticeDocumentPath(rs.getString("notice_document_path"));
        tender.setCreatedBy(rs.getInt("created_by"));
        tender.setCreatedAt(rs.getTimestamp("created_at"));
        tender.setUpdatedAt(rs.getTimestamp("updated_at"));
        tender.setPublishedAt(rs.getTimestamp("published_at"));
        tender.setClosedAt(rs.getTimestamp("closed_at"));
        tender.setEvaluationStartedAt(rs.getTimestamp("evaluation_started_at"));
        tender.setEvaluatedAt(rs.getTimestamp("evaluated_at"));
        tender.setAwardedAt(rs.getTimestamp("awarded_at"));

        try {
            tender.setCreatedByName(rs.getString("created_by_email"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }

        return tender;
    }
}
