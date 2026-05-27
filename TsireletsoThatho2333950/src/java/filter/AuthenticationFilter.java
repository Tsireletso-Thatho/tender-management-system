package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Authentication Filter - checks if a user is logged in before accessing protected pages.
 * Redirects to login page with error message if session is invalid.
 * 
 * Required by Module 1: All protected pages must verify session validity.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class AuthenticationFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(AuthenticationFilter.class.getName());
    
    /**
     * Default constructor.
     */
    public AuthenticationFilter() {
    }
    
    /**
     * Initializes the filter.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "AuthenticationFilter initialized");
    }
    
    /**
     * Filters requests to protected resources.
     * Checks if user is logged in. If not, redirects to login page.
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
        
        // Check if user is logged in
        if (!SessionValidator.isLoggedIn(httpRequest)) {
            LOGGER.log(Level.WARNING, "Unauthorized access attempt to: {0}", requestURI);
            
            // Store the requested URL for redirect after login
            HttpSession session = httpRequest.getSession(true);
            session.setAttribute("redirectAfterLogin", requestURI);
            session.setAttribute("errorMessage", Constants.ERROR_SESSION_EXPIRED);
            
            // Redirect to login page
            httpResponse.sendRedirect(contextPath + Constants.URL_LOGIN);
            return;
        }
        
        // User is authenticated, continue
        chain.doFilter(request, response);
    }
    
    /**
     * Cleans up filter resources.
     */
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "AuthenticationFilter destroyed");
    }
}