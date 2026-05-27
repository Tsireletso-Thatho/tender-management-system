package dao.implementations;

import dao.interfaces.EmailLogDAO;
import model.EmailLog;
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
 * Implementation of the EmailLogDAO interface.
 * Provides database operations for EmailLog entities using JDBC.
 * Required for Module 6: Supplier Email Notification.
 * 
 * CRITICAL: No circular dependencies - this class does not initialize other DAOs as fields.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class EmailLogDAOImpl implements EmailLogDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EmailLogDAOImpl.class.getName());
    
    /**
     * Default constructor.
     */
    public EmailLogDAOImpl() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int create(EmailLog emailLog) {
        String sql = "INSERT INTO email_logs (tender_id, recipient_email, subject, outcome, status) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, emailLog.getTenderId());
            pstmt.setString(2, emailLog.getRecipientEmail());
            pstmt.setString(3, emailLog.getSubject());
            pstmt.setString(4, emailLog.getOutcome());
            pstmt.setString(5, emailLog.getStatus() != null ? emailLog.getStatus() : EmailLog.STATUS_SENT);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating email log: {0}", e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EmailLog findById(int logId) {
        String sql = "SELECT * FROM email_logs WHERE log_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, logId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEmailLog(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding email log by ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailLog> findByTenderId(int tenderId) {
        List<EmailLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM email_logs WHERE tender_id = ? ORDER BY sent_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEmailLog(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding email logs by tender ID: {0}", e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailLog> findByRecipientEmail(String recipientEmail) {
        List<EmailLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM email_logs WHERE recipient_email = ? ORDER BY sent_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, recipientEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEmailLog(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding email logs by recipient: {0}", e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailLog> findByOutcome(String outcome) {
        List<EmailLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM email_logs WHERE outcome = ? ORDER BY sent_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, outcome);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEmailLog(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding email logs by outcome: {0}", e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailLog> findByStatus(String status) {
        List<EmailLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM email_logs WHERE status = ? ORDER BY sent_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    logs.add(mapResultSetToEmailLog(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding email logs by status: {0}", e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean updateStatus(int logId, String status) {
        String sql = "UPDATE email_logs SET status = ? WHERE log_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, status);
            pstmt.setInt(2, logId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating email log status: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int logId) {
        String sql = "DELETE FROM email_logs WHERE log_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, logId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting email log: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int deleteByTenderId(int tenderId) {
        String sql = "DELETE FROM email_logs WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            return pstmt.executeUpdate();
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting email logs by tender ID: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailLog> findAll() {
        List<EmailLog> logs = new ArrayList<>();
        String sql = "SELECT * FROM email_logs ORDER BY sent_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                logs.add(mapResultSetToEmailLog(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all email logs: {0}", e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EmailLog> findDetailedByTenderId(int tenderId) {
        List<EmailLog> logs = new ArrayList<>();
        String sql = "SELECT el.*, t.reference_number as tender_reference " +
                     "FROM email_logs el " +
                     "JOIN tenders t ON el.tender_id = t.tender_id " +
                     "WHERE el.tender_id = ? " +
                     "ORDER BY el.sent_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    EmailLog log = mapResultSetToEmailLog(rs);
                    log.setTenderReference(rs.getString("tender_reference"));
                    logs.add(log);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding detailed email logs: {0}", e.getMessage());
        }
        
        return logs;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean hasEmailBeenSent(int tenderId, String recipientEmail) {
        String sql = "SELECT COUNT(*) FROM email_logs WHERE tender_id = ? AND recipient_email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            pstmt.setString(2, recipientEmail);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error checking if email sent: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM email_logs";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting email logs: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countByTenderId(int tenderId) {
        String sql = "SELECT COUNT(*) FROM email_logs WHERE tender_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, tenderId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting email logs by tender ID: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countByOutcome(String outcome) {
        String sql = "SELECT COUNT(*) FROM email_logs WHERE outcome = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, outcome);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting email logs by outcome: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Maps a ResultSet row to an EmailLog object.
     * 
     * @param rs the ResultSet positioned at the current row
     * @return the mapped EmailLog object
     * @throws SQLException if a database error occurs
     */
    private EmailLog mapResultSetToEmailLog(ResultSet rs) throws SQLException {
        EmailLog log = new EmailLog();
        log.setLogId(rs.getInt("log_id"));
        log.setTenderId(rs.getInt("tender_id"));
        log.setRecipientEmail(rs.getString("recipient_email"));
        log.setSubject(rs.getString("subject"));
        log.setOutcome(rs.getString("outcome"));
        log.setSentAt(rs.getTimestamp("sent_at"));
        log.setStatus(rs.getString("status"));
        return log;
    }
}