package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CORS (Cross-Origin Resource Sharing) Filter.
 * Adds CORS headers to allow cross-origin requests if needed.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class CorsFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(CorsFilter.class.getName());
    
    /**
     * Default constructor.
     */
    public CorsFilter() {
    }
    
    /**
     * Initializes the filter.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "CorsFilter initialized");
    }
    
    /**
     * Filters requests and adds CORS headers.
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
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        // Add CORS headers
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        
        chain.doFilter(request, response);
    }
    
    /**
     * Cleans up filter resources.
     */
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "CorsFilter destroyed");
    }
}