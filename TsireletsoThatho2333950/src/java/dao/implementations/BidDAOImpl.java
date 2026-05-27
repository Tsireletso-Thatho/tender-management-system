package dao.implementations;

import dao.interfaces.BidDAO;
import model.Bid;
import util.DatabaseConnection;

import java.math.BigDecimal;
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
 * Implementation of the BidDAO interface.
 * Provides database operations for Bid entities using JDBC.
 * 
 * CRITICAL: No circular dependencies - this class does not initialize other DAOs as fields.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class BidDAOImpl implements BidDAO {
    
    private static final Logger LOGGER = Logger.getLogger(BidDAOImpl.class.getName());
    
    /**
     * Default constructor.
     */
    public BidDAOImpl() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int create(Bid bid) {
        String sql = "INSERT INTO bids (tender_id, supplier_id, bid_amount, " +
                     "technical_compliance_statement, proposed_timeline_days, " +
                     "supporting_document_path, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, bid.getTenderId());
            pstmt.setInt(2, bid.getSupplierId());
            pstmt.setBigDecimal(3, bid.getBidAmount());
            pstmt.setString(4, bid.getTechnicalComplianceStatement());
            pstmt.setInt(5, bid.getProposedTimelineDays());
            pstmt.setString(6, bid.getSupportingDocumentPath());
            pstmt.setString(7, bid.getStatus() != null ? bid.getStatus() : Bid.STATUS_SUBMITTED);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating bid: {0}", e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Bid findById(int bidId) {
        String sql = "SELECT b.*, s.company_name as supplier_name, " +
                     "t.reference_number as tender_reference, t.title as tender_title " +
                     "FROM bids b " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "JOIN tenders t ON b.tender_id = t.tender_id " +
                     "WHERE b.bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bidId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBid(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bid by ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bid> findByTenderId(int tenderId) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, s.company_name as supplier_name " +
                     "FROM bids b " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "WHERE b.tender_id = ? " +
                     "ORDER BY b.submitted_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bids.add(mapResultSetToBid(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bids by tender ID: {0}", e.getMessage());
        }
        
        return bids;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bid> findBySupplierId(int supplierId) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, t.reference_number as tender_reference, t.title as tender_title " +
                     "FROM bids b " +
                     "JOIN tenders t ON b.tender_id = t.tender_id " +
                     "WHERE b.supplier_id = ? " +
                     "ORDER BY b.submitted_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Bid bid = new Bid();
                    bid.setBidId(rs.getInt("bid_id"));
                    bid.setTenderId(rs.getInt("tender_id"));
                    bid.setSupplierId(rs.getInt("supplier_id"));
                    bid.setBidAmount(rs.getBigDecimal("bid_amount"));
                    bid.setTechnicalComplianceStatement(rs.getString("technical_compliance_statement"));
                    bid.setProposedTimelineDays(rs.getInt("proposed_timeline_days"));
                    bid.setSupportingDocumentPath(rs.getString("supporting_document_path"));
                    bid.setStatus(rs.getString("status"));
                    bid.setSubmittedAt(rs.getTimestamp("submitted_at"));
                    bid.setUpdatedAt(rs.getTimestamp("updated_at"));
                    bid.setTenderReference(rs.getString("tender_reference"));
                    bid.setTenderTitle(rs.getString("tender_title"));
                    bids.add(bid);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bids by supplier ID: {0}", e.getMessage());
        }
        
        return bids;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Bid findByTenderAndSupplier(int tenderId, int supplierId) {
        String sql = "SELECT b.*, s.company_name as supplier_name " +
                     "FROM bids b " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "WHERE b.tender_id = ? AND b.supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            pstmt.setInt(2, supplierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToBid(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bid by tender and supplier: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Bid bid) {
        String sql = "UPDATE bids SET bid_amount = ?, technical_compliance_statement = ?, " +
                     "proposed_timeline_days = ?, supporting_document_path = ? " +
                     "WHERE bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setBigDecimal(1, bid.getBidAmount());
            pstmt.setString(2, bid.getTechnicalComplianceStatement());
            pstmt.setInt(3, bid.getProposedTimelineDays());
            pstmt.setString(4, bid.getSupportingDocumentPath());
            pstmt.setInt(5, bid.getBidId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bid: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateStatus(int bidId, String status) {
        String sql = "UPDATE bids SET status = ? WHERE bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, bidId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bid status: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateBidOutcomes(int tenderId, int winningBidId) {
        Connection conn = null;
        PreparedStatement pstmt1 = null;
        PreparedStatement pstmt2 = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Set winning bid to WON
            String sql1 = "UPDATE bids SET status = 'WON' WHERE bid_id = ?";
            pstmt1 = conn.prepareStatement(sql1);
            pstmt1.setInt(1, winningBidId);
            pstmt1.executeUpdate();
            
            // Set all other bids to NOT_WON
            String sql2 = "UPDATE bids SET status = 'NOT_WON' WHERE tender_id = ? AND bid_id != ?";
            pstmt2 = conn.prepareStatement(sql2);
            pstmt2.setInt(1, tenderId);
            pstmt2.setInt(2, winningBidId);
            pstmt2.executeUpdate();
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating bid outcomes: {0}", e.getMessage());
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Error rolling back transaction: {0}", ex.getMessage());
            }
        } finally {
            try {
                if (pstmt1 != null) pstmt1.close();
                if (pstmt2 != null) pstmt2.close();
                if (conn != null) conn.setAutoCommit(true);
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Error closing resources: {0}", e.getMessage());
            }
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int bidId) {
        String sql = "DELETE FROM bids WHERE bid_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bidId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting bid: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bid> findAll() {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, s.company_name as supplier_name, " +
                     "t.reference_number as tender_reference, t.title as tender_title " +
                     "FROM bids b " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "JOIN tenders t ON b.tender_id = t.tender_id " +
                     "ORDER BY b.submitted_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                bids.add(mapResultSetToBid(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all bids: {0}", e.getMessage());
        }
        
        return bids;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bid> findByStatus(String status) {
        List<Bid> bids = new ArrayList<>();
        String sql = "SELECT b.*, s.company_name as supplier_name " +
                     "FROM bids b " +
                     "JOIN suppliers s ON b.supplier_id = s.supplier_id " +
                     "WHERE b.status = ? " +
                     "ORDER BY b.submitted_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    bids.add(mapResultSetToBid(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding bids by status: {0}", e.getMessage());
        }
        
        return bids;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bid> findDetailedBidsByTenderId(int tenderId) {
        return findByTenderId(tenderId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Bid> findDetailedBidsBySupplierId(int supplierId) {
        return findBySupplierId(supplierId);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasSupplierBid(int tenderId, int supplierId) {
        String sql = "SELECT COUNT(*) FROM bids WHERE tender_id = ? AND supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            pstmt.setInt(2, supplierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking supplier bid: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getLowestBidAmount(int tenderId) {
        String sql = "SELECT MIN(bid_amount) FROM bids WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting lowest bid amount: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getShortestTimeline(int tenderId) {
        String sql = "SELECT MIN(proposed_timeline_days) FROM bids WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting shortest timeline: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Integer> getBiddingSupplierIds(int tenderId) {
        List<Integer> supplierIds = new ArrayList<>();
        String sql = "SELECT DISTINCT supplier_id FROM bids WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    supplierIds.add(rs.getInt("supplier_id"));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error getting bidding supplier IDs: {0}", e.getMessage());
        }
        
        return supplierIds;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM bids";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting bids: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countByTenderId(int tenderId) {
        String sql = "SELECT COUNT(*) FROM bids WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting bids by tender ID: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countBySupplierId(int supplierId) {
        String sql = "SELECT COUNT(*) FROM bids WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting bids by supplier ID: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Maps a ResultSet row to a Bid object.
     * 
     * @param rs the ResultSet positioned at the current row
     * @return the mapped Bid object
     * @throws SQLException if a database error occurs
     */
    private Bid mapResultSetToBid(ResultSet rs) throws SQLException {
        Bid bid = new Bid();
        bid.setBidId(rs.getInt("bid_id"));
        bid.setTenderId(rs.getInt("tender_id"));
        bid.setSupplierId(rs.getInt("supplier_id"));
        bid.setBidAmount(rs.getBigDecimal("bid_amount"));
        bid.setTechnicalComplianceStatement(rs.getString("technical_compliance_statement"));
        bid.setProposedTimelineDays(rs.getInt("proposed_timeline_days"));
        bid.setSupportingDocumentPath(rs.getString("supporting_document_path"));
        bid.setStatus(rs.getString("status"));
        bid.setSubmittedAt(rs.getTimestamp("submitted_at"));
        bid.setUpdatedAt(rs.getTimestamp("updated_at"));
        
        try {
            bid.setSupplierName(rs.getString("supplier_name"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }
        
        try {
            bid.setTenderReference(rs.getString("tender_reference"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }
        
        try {
            bid.setTenderTitle(rs.getString("tender_title"));
        } catch (SQLException e) {
            // Column may not be present in all queries
        }
        
        return bid;
    }
}