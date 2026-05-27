package dao.implementations;

import dao.interfaces.EvaluationCommitteeDAO;
import model.EvaluationCommittee;
import model.User;
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
 * Implementation of the EvaluationCommitteeDAO interface.
 * Provides database operations for EvaluationCommittee entities using JDBC.
 * 
 * CRITICAL: No circular dependencies - this class does not initialize other DAOs as fields.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class EvaluationCommitteeDAOImpl implements EvaluationCommitteeDAO {
    
    private static final Logger LOGGER = Logger.getLogger(EvaluationCommitteeDAOImpl.class.getName());
    
    /**
     * Default constructor.
     */
    public EvaluationCommitteeDAOImpl() {
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EvaluationCommittee findById(int evaluatorId) {
        String sql = "SELECT ec.*, u.email, u.status, u.role, u.created_at as user_created_at " +
                     "FROM evaluation_committee ec " +
                     "JOIN users u ON ec.user_id = u.user_id " +
                     "WHERE ec.evaluator_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, evaluatorId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvaluator(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding evaluator by ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EvaluationCommittee findByUserId(int userId) {
        String sql = "SELECT ec.*, u.email, u.status, u.role, u.created_at as user_created_at " +
                     "FROM evaluation_committee ec " +
                     "JOIN users u ON ec.user_id = u.user_id " +
                     "WHERE ec.user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvaluator(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding evaluator by user ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public EvaluationCommittee findByEmployeeNumber(String employeeNumber) {
        String sql = "SELECT ec.*, u.email, u.status, u.role, u.created_at as user_created_at " +
                     "FROM evaluation_committee ec " +
                     "JOIN users u ON ec.user_id = u.user_id " +
                     "WHERE ec.employee_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, employeeNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToEvaluator(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding evaluator by employee number: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EvaluationCommittee> findAll() {
        List<EvaluationCommittee> evaluators = new ArrayList<>();
        String sql = "SELECT ec.*, u.email, u.status, u.role, u.created_at as user_created_at " +
                     "FROM evaluation_committee ec " +
                     "JOIN users u ON ec.user_id = u.user_id " +
                     "ORDER BY ec.full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                evaluators.add(mapResultSetToEvaluator(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all evaluators: {0}", e.getMessage());
        }
        
        return evaluators;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<EvaluationCommittee> findAllActive() {
        List<EvaluationCommittee> evaluators = new ArrayList<>();
        String sql = "SELECT ec.*, u.email, u.status, u.role, u.created_at as user_created_at " +
                     "FROM evaluation_committee ec " +
                     "JOIN users u ON ec.user_id = u.user_id " +
                     "WHERE u.status = 'ACTIVE' " +
                     "ORDER BY ec.full_name";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                evaluators.add(mapResultSetToEvaluator(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding active evaluators: {0}", e.getMessage());
        }
        
        return evaluators;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM evaluation_committee";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting evaluators: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countActive() {
        String sql = "SELECT COUNT(*) FROM evaluation_committee ec " +
                     "JOIN users u ON ec.user_id = u.user_id " +
                     "WHERE u.status = 'ACTIVE'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting active evaluators: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Maps a ResultSet row to an EvaluationCommittee object.
     * 
     * @param rs the ResultSet positioned at the current row
     * @return the mapped EvaluationCommittee object
     * @throws SQLException if a database error occurs
     */
    private EvaluationCommittee mapResultSetToEvaluator(ResultSet rs) throws SQLException {
        EvaluationCommittee evaluator = new EvaluationCommittee();
        evaluator.setEvaluatorId(rs.getInt("evaluator_id"));
        evaluator.setUserId(rs.getInt("user_id"));
        evaluator.setFullName(rs.getString("full_name"));
        evaluator.setDepartment(rs.getString("department"));
        evaluator.setEmployeeNumber(rs.getString("employee_number"));
        evaluator.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Create associated User object
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getString("status"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getTimestamp("user_created_at"));
        evaluator.setUser(user);
        
        return evaluator;
    }
}