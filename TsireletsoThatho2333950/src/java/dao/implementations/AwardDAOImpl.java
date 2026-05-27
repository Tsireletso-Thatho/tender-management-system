package dao.implementations;

import dao.interfaces.AwardDAO;
import model.Award;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the AwardDAO interface.
 * Provides database operations for Award entities using JDBC.
 * 
 * CRITICAL: No circular dependencies - this class does not initialize other DAOs as fields.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class AwardDAOImpl implements AwardDAO {
    
    private static final Logger LOGGER = Logger.getLogger(AwardDAOImpl.class.getName());
    
    /**
     * Default constructor.
     */
    public AwardDAOImpl() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int create(Award award) {
        String sql = "INSERT INTO awards (tender_id, winning_bid_id, awarded_value, justification, awarded_by) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, award.getTenderId());
            pstmt.setInt(2, award.getWinningBidId());
            pstmt.setBigDecimal(3, award.getAwardedValue());
            pstmt.setString(4, award.getJustification());
            pstmt.setInt(5, award.getAwardedBy());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating award: {0}", e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Award findById(int awardId) {
        String sql = "SELECT a.*, t.reference_number as tender_reference, t.title as tender_title, " +
                     "s.company_name as winning_supplier_name, " +
                     "po.full_name as awarded_by_name " +
                     "FROM awards a " +
                     "JOIN tenders t ON a.tender_id = t.tender_id " +
                     "JOIN bids b ON a.winning_bid_id = b.bid_id " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "JOIN procurement_officers po ON a.awarded_by = po.user_id " +
                     "WHERE a.award_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, awardId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAward(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding award by ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Award findByTenderId(int tenderId) {
        String sql = "SELECT * FROM awards WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Award award = new Award();
                    award.setAwardId(rs.getInt("award_id"));
                    award.setTenderId(rs.getInt("tender_id"));
                    award.setWinningBidId(rs.getInt("winning_bid_id"));
                    award.setAwardedValue(rs.getBigDecimal("awarded_value"));
                    award.setJustification(rs.getString("justification"));
                    award.setAwardedBy(rs.getInt("awarded_by"));
                    award.setAwardedAt(rs.getTimestamp("awarded_at"));
                    return award;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding award by tender ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Award> findByAwardedBy(int userId) {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT a.*, t.reference_number as tender_reference, t.title as tender_title " +
                     "FROM awards a " +
                     "JOIN tenders t ON a.tender_id = t.tender_id " +
                     "WHERE a.awarded_by = ? " +
                     "ORDER BY a.awarded_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Award award = new Award();
                    award.setAwardId(rs.getInt("award_id"));
                    award.setTenderId(rs.getInt("tender_id"));
                    award.setWinningBidId(rs.getInt("winning_bid_id"));
                    award.setAwardedValue(rs.getBigDecimal("awarded_value"));
                    award.setJustification(rs.getString("justification"));
                    award.setAwardedBy(rs.getInt("awarded_by"));
                    award.setAwardedAt(rs.getTimestamp("awarded_at"));
                    award.setTenderReference(rs.getString("tender_reference"));
                    award.setTenderTitle(rs.getString("tender_title"));
                    awards.add(award);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding awards by officer: {0}", e.getMessage());
        }
        
        return awards;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Award award) {
        String sql = "UPDATE awards SET awarded_value = ?, justification = ? WHERE award_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, award.getAwardedValue());
            pstmt.setString(2, award.getJustification());
            pstmt.setInt(3, award.getAwardId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating award: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int awardId) {
        String sql = "DELETE FROM awards WHERE award_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, awardId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting award: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Award> findAll() {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT a.*, t.reference_number as tender_reference, t.title as tender_title " +
                     "FROM awards a " +
                     "JOIN tenders t ON a.tender_id = t.tender_id " +
                     "ORDER BY a.awarded_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Award award = new Award();
                award.setAwardId(rs.getInt("award_id"));
                award.setTenderId(rs.getInt("tender_id"));
                award.setWinningBidId(rs.getInt("winning_bid_id"));
                award.setAwardedValue(rs.getBigDecimal("awarded_value"));
                award.setJustification(rs.getString("justification"));
                award.setAwardedBy(rs.getInt("awarded_by"));
                award.setAwardedAt(rs.getTimestamp("awarded_at"));
                award.setTenderReference(rs.getString("tender_reference"));
                award.setTenderTitle(rs.getString("tender_title"));
                awards.add(award);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all awards: {0}", e.getMessage());
        }
        
        return awards;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Award> findAllDetailed() {
        List<Award> awards = new ArrayList<>();
        String sql = "SELECT a.*, t.reference_number as tender_reference, t.title as tender_title, " +
                     "s.company_name as winning_supplier_name, " +
                     "po.full_name as awarded_by_name " +
                     "FROM awards a " +
                     "JOIN tenders t ON a.tender_id = t.tender_id " +
                     "JOIN bids b ON a.winning_bid_id = b.bid_id " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "JOIN procurement_officers po ON a.awarded_by = po.user_id " +
                     "ORDER BY a.awarded_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                awards.add(mapResultSetToAward(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all detailed awards: {0}", e.getMessage());
        }
        
        return awards;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Award findDetailedById(int awardId) {
        return findById(awardId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Award findDetailedByTenderId(int tenderId) {
        String sql = "SELECT a.*, t.reference_number as tender_reference, t.title as tender_title, " +
                     "s.company_name as winning_supplier_name, " +
                     "po.full_name as awarded_by_name " +
                     "FROM awards a " +
                     "JOIN tenders t ON a.tender_id = t.tender_id " +
                     "JOIN bids b ON a.winning_bid_id = b.bid_id " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "JOIN procurement_officers po ON a.awarded_by = po.user_id " +
                     "WHERE a.tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAward(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding detailed award by tender ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTenderAwarded(int tenderId) {
        String sql = "SELECT COUNT(*) FROM awards WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if tender awarded: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getWinningSupplierId(int tenderId) {
        String sql = "SELECT s.supplier_id FROM awards a " +
                     "JOIN bids b ON a.winning_bid_id = b.bid_id " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "WHERE a.tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("supplier_id");
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting winning supplier ID: {0}", e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM awards";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting awards: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Maps a ResultSet row to an Award object.
     * 
     * @param rs the ResultSet positioned at the current row
     * @return the mapped Award object
     * @throws SQLException if a database error occurs
     */
    private Award mapResultSetToAward(ResultSet rs) throws SQLException {
        Award award = new Award();
        award.setAwardId(rs.getInt("award_id"));
        award.setTenderId(rs.getInt("tender_id"));
        award.setWinningBidId(rs.getInt("winning_bid_id"));
        award.setAwardedValue(rs.getBigDecimal("awarded_value"));
        award.setJustification(rs.getString("justification"));
        award.setAwardedBy(rs.getInt("awarded_by"));
        award.setAwardedAt(rs.getTimestamp("awarded_at"));
        
        try {
            award.setTenderReference(rs.getString("tender_reference"));
        } catch (SQLException e) {
            // Column may not be present
        }
        
        try {
            award.setTenderTitle(rs.getString("tender_title"));
        } catch (SQLException e) {
            // Column may not be present
        }
        
        try {
            award.setWinningSupplierName(rs.getString("winning_supplier_name"));
        } catch (SQLException e) {
            // Column may not be present
        }
        
        try {
            award.setAwardedByName(rs.getString("awarded_by_name"));
        } catch (SQLException e) {
            // Column may not be present
        }
        
        return award;
    }
}