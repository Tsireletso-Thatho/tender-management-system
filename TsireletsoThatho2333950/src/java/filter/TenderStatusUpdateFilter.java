package filter;

import dao.implementations.TenderDAOImpl;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import service.TenderService;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Tender Status Update Filter - automatically updates tender statuses.
 * Checks for tenders that need automatic status changes:
 * - OPEN tenders past deadline → CLOSED
 * 
 * CRITICAL: Do NOT initialize DAOs in init() method.
 * Create DAO instances locally in doFilter() method.
 * 
 * Required by Module 2: Automatic tender status updates.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class TenderStatusUpdateFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(TenderStatusUpdateFilter.class.getName());
    
    /**
     * Default constructor.
     */
    public TenderStatusUpdateFilter() {
    }
    
    /**
     * Initializes the filter.
     * NOTE: No DAOs initialized here to avoid circular dependencies.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "TenderStatusUpdateFilter initialized");
        // DO NOT initialize DAOs here!
    }
    
    /**
     * Filters requests and updates tender statuses automatically.
     * Closes expired tenders before processing the request.
     * 
     * @param request the ServletRequest
     * @param response the ServletResponse
     * @param chain the FilterChain
     * @throws IOException if an I/O error occurs
     * @throws ServletException if a servlet error occurs
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        try {
            // Create DAO and Service instances LOCALLY (not as fields)
            TenderDAOImpl tenderDAO = new TenderDAOImpl();
            TenderService tenderService = new TenderService();
            
            // Auto-close expired tenders
            int closed = tenderDAO.closeExpiredTenders();
            if (closed > 0) {
                LOGGER.log(Level.INFO, "Auto-closed {0} expired tender(s)", closed);
            }
            
        } catch (Exception e) {
            // Log but don't block the request
            LOGGER.log(Level.WARNING, "Error in TenderStatusUpdateFilter: {0}", e.getMessage());
        }
        
        // Continue with the request
        chain.doFilter(request, response);
    }
    
    /**
     * Cleans up filter resources.
     */
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "TenderStatusUpdateFilter destroyed");
    }
}