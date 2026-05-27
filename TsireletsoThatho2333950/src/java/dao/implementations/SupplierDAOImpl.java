package dao.implementations;

import dao.interfaces.SupplierDAO;
import model.Supplier;
import model.User;
import util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Implementation of the SupplierDAO interface.
 * Provides database operations for Supplier entities using JDBC.
 * 
 * CRITICAL: No circular dependencies - uses lazy-loading getter for UserDAO.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class SupplierDAOImpl implements SupplierDAO {
    
    private static final Logger LOGGER = Logger.getLogger(SupplierDAOImpl.class.getName());
    
    /**
     * Default constructor.
     */
    public SupplierDAOImpl() {
    }
    
    /**
     * Lazy-loading getter for UserDAO to avoid circular dependencies.
     * 
     * @return UserDAOImpl instance
     */
    private UserDAOImpl getUserDAO() {
        return new UserDAOImpl();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int create(Supplier supplier) {
        String sql = "INSERT INTO suppliers (user_id, registration_number, company_name, physical_address, contact_number) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setInt(1, supplier.getUserId());
            pstmt.setString(2, supplier.getRegistrationNumber());
            pstmt.setString(3, supplier.getCompanyName());
            pstmt.setString(4, supplier.getPhysicalAddress());
            pstmt.setString(5, supplier.getContactNumber());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error creating supplier: {0}", e.getMessage());
        }
        
        return -1;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier findById(int supplierId) {
        String sql = "SELECT s.*, u.email, u.status, u.created_at as user_created_at " +
                     "FROM suppliers s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding supplier by ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier findByUserId(int userId) {
        String sql = "SELECT s.*, u.email, u.status, u.created_at as user_created_at " +
                     "FROM suppliers s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.user_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, userId);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding supplier by user ID: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier findByRegistrationNumber(String registrationNumber) {
        String sql = "SELECT s.*, u.email, u.status, u.created_at as user_created_at " +
                     "FROM suppliers s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.registration_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, registrationNumber);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding supplier by registration number: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier findByEmail(String email) {
        String sql = "SELECT s.*, u.email, u.status, u.created_at as user_created_at " +
                     "FROM suppliers s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE u.email = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSupplier(rs);
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding supplier by email: {0}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean update(Supplier supplier) {
        String sql = "UPDATE suppliers SET company_name = ?, physical_address = ?, contact_number = ? " +
                     "WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, supplier.getCompanyName());
            pstmt.setString(2, supplier.getPhysicalAddress());
            pstmt.setString(3, supplier.getContactNumber());
            pstmt.setInt(4, supplier.getSupplierId());
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error updating supplier: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean delete(int supplierId) {
        String sql = "DELETE FROM suppliers WHERE supplier_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, supplierId);
            
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error deleting supplier: {0}", e.getMessage());
        }
        
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Supplier> findAll() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT s.*, u.email, u.status, u.created_at as user_created_at " +
                     "FROM suppliers s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "ORDER BY s.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding all suppliers: {0}", e.getMessage());
        }
        
        return suppliers;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Supplier> findAllActive() {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT s.*, u.email, u.status, u.created_at as user_created_at " +
                     "FROM suppliers s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE u.status = 'ACTIVE' " +
                     "ORDER BY s.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                suppliers.add(mapResultSetToSupplier(rs));
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error finding active suppliers: {0}", e.getMessage());
        }
        
        return suppliers;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Supplier> searchByCompanyName(String searchTerm) {
        List<Supplier> suppliers = new ArrayList<>();
        String sql = "SELECT s.*, u.email, u.status, u.created_at as user_created_at " +
                     "FROM suppliers s " +
                     "JOIN users u ON s.user_id = u.user_id " +
                     "WHERE s.company_name LIKE ? " +
                     "ORDER BY s.created_at DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "%" + searchTerm + "%");
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    suppliers.add(mapResultSetToSupplier(rs));
                }
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error searching suppliers: {0}", e.getMessage());
        }
        
        return suppliers;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String generateRegistrationNumber() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int sequence = getNextSequenceNumber();
        return String.format("SUP-%d-%03d", year, sequence);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int countAll() {
        String sql = "SELECT COUNT(*) FROM suppliers";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error counting suppliers: {0}", e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public int getNextSequenceNumber() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        String sql = "SELECT COUNT(*) + 1 FROM suppliers WHERE registration_number LIKE ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, "SUP-" + year + "-%");
            
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
     * Maps a ResultSet row to a Supplier object with associated User.
     * 
     * @param rs the ResultSet positioned at the current row
     * @return the mapped Supplier object
     * @throws SQLException if a database error occurs
     */
    private Supplier mapResultSetToSupplier(ResultSet rs) throws SQLException {
        Supplier supplier = new Supplier();
        supplier.setSupplierId(rs.getInt("supplier_id"));
        supplier.setUserId(rs.getInt("user_id"));
        supplier.setRegistrationNumber(rs.getString("registration_number"));
        supplier.setCompanyName(rs.getString("company_name"));
        supplier.setPhysicalAddress(rs.getString("physical_address"));
        supplier.setContactNumber(rs.getString("contact_number"));
        supplier.setCreatedAt(rs.getTimestamp("created_at"));
        
        // Create associated User object
        User user = new User();
        user.setUserId(rs.getInt("user_id"));
        user.setEmail(rs.getString("email"));
        user.setStatus(rs.getString("status"));
        user.setCreatedAt(rs.getTimestamp("user_created_at"));
        supplier.setUser(user);
        
        return supplier;
    }
}