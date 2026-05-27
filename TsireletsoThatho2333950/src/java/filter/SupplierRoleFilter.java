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
 * Supplier Role Filter - restricts access to Supplier pages only.
 * Ensures only users with SUPPLIER role can access /supplier/* URLs.
 * EXCEPTION: /supplier/bid/download is accessible by Officers and Evaluators too.
 * 
 * Required by Module 1: Role-based access control.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class SupplierRoleFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(SupplierRoleFilter.class.getName());
    
    // URLs that should be accessible by multiple roles (Officers, Evaluators, Suppliers)
    private static final String[] SHARED_URLS = {
        "/supplier/bid/download"
    };
    
    /**
     * Default constructor.
     */
    public SupplierRoleFilter() {
    }
    
    /**
     * Initializes the filter.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "SupplierRoleFilter initialized");
    }
    
    /**
     * Filters requests to supplier pages.
     * Checks if user has SUPPLIER role. If not, redirects with access denied message.
     * Exception: Shared URLs are accessible by Officers and Evaluators as well.
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
        
        // Check if this is a shared URL accessible by multiple roles
        if (isSharedUrl(requestURI)) {
            // Verify user is at least logged in (AuthenticationFilter already did this)
            if (SessionValidator.isLoggedIn(httpRequest)) {
                String userRole = SessionValidator.getLoggedInUserRole(httpRequest);
                // Allow Suppliers, Officers, and Evaluators
                if (Constants.ROLE_SUPPLIER.equals(userRole) || 
                    Constants.ROLE_PROCUREMENT_OFFICER.equals(userRole) ||
                    Constants.ROLE_EVALUATION_COMMITTEE.equals(userRole)) {
                    LOGGER.log(Level.FINE, "Allowing access to shared resource: {0} for role: {1}", 
                               new Object[]{requestURI, userRole});
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        
        // Check if user has required SUPPLIER role
        if (!SessionValidator.isSupplier(httpRequest)) {
            String userRole = SessionValidator.getLoggedInUserRole(httpRequest);
            LOGGER.log(Level.WARNING, "Access denied to supplier page: {0} (User role: {1})", 
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
     * Checks if the URL is a shared resource accessible by multiple roles.
     * 
     * @param requestURI the request URI
     * @return true if the URL is shared
     */
    private boolean isSharedUrl(String requestURI) {
        if (requestURI == null) {
            return false;
        }
        for (String sharedUrl : SHARED_URLS) {
            if (requestURI.contains(sharedUrl)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Cleans up filter resources.
     */
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "SupplierRoleFilter destroyed");
    }
}