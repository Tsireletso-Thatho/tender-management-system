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
 * Evaluator Role Filter - restricts access to Evaluation Committee Member pages only.
 * Ensures only users with EVALUATION_COMMITTEE role can access /evaluator/* URLs.
 * 
 * Required by Module 1: Role-based access control.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class EvaluatorRoleFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(EvaluatorRoleFilter.class.getName());
    
    /**
     * Default constructor.
     */
    public EvaluatorRoleFilter() {
    }
    
    /**
     * Initializes the filter.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "EvaluatorRoleFilter initialized");
    }
    
    /**
     * Filters requests to evaluator pages.
     * Checks if user has EVALUATION_COMMITTEE role. If not, redirects with access denied message.
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
        if (!SessionValidator.isEvaluationCommittee(httpRequest)) {
            String userRole = SessionValidator.getLoggedInUserRole(httpRequest);
            LOGGER.log(Level.WARNING, "Access denied to evaluator page: {0} (User role: {1})", 
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
        LOGGER.log(Level.INFO, "EvaluatorRoleFilter destroyed");
    }
}