package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Officer Role Filter - restricts access to Procurement Officer pages only.
 * Ensures only users with PROCUREMENT_OFFICER role can access /officer/* URLs.
 * 
 * Required by Module 1: Role-based access control.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class OfficerRoleFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(OfficerRoleFilter.class.getName());
    
    /**
     * Default constructor.
     */
    public OfficerRoleFilter() {
    }
    
    /**
     * Initializes the filter.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "OfficerRoleFilter initialized");
    }
    
    /**
     * Filters requests to officer pages.
     * Checks if user has PROCUREMENT_OFFICER role. If not, redirects with access denied message.
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
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        String requestURI = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        
        // Check if user has required role
        if (!SessionValidator.isProcurementOfficer(httpRequest)) {
            String userRole = SessionValidator.getLoggedInUserRole(httpRequest);
            LOGGER.log(Level.WARNING, "Access denied to officer page: {0} (User role: {1})", 
                       new Object[]{requestURI, userRole});
            
            // Set error message and redirect to appropriate dashboard or login
            if (SessionValidator.isLoggedIn(httpRequest)) {
                String dashboardUrl = SessionValidator.getDashboardForRole(userRole);
                httpRequest.getSession().setAttribute("errorMessage", Constants.ERROR_ACCESS_DENIED);
                httpResponse.sendRedirect(contextPath + dashboardUrl);
            } else {
                httpResponse.sendRedirect(contextPath + Constants.URL_LOGIN);
            }
            return;
        }
        
        // User has correct role, continue
        chain.doFilter(request, response);
    }
    
    /**
     * Cleans up filter resources.
     */
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "OfficerRoleFilter destroyed");
    }
}