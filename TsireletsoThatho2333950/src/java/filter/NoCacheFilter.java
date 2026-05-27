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
 * No Cache Filter - prevents browser caching of secure pages.
 * Sets HTTP headers to disable caching for authenticated pages.
 * 
 * Important for security: Prevents sensitive data from being cached.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class NoCacheFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(NoCacheFilter.class.getName());
    
    /**
     * Default constructor.
     */
    public NoCacheFilter() {
    }
    
    /**
     * Initializes the filter.
     * 
     * @param filterConfig the filter configuration
     * @throws ServletException if initialization fails
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "NoCacheFilter initialized");
    }
    
    /**
     * Filters requests and sets no-cache headers.
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
        
        // Set no-cache headers
        httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        httpResponse.setHeader("Pragma", "no-cache");
        httpResponse.setDateHeader("Expires", 0);
        
        chain.doFilter(request, response);
    }
    
    /**
     * Cleans up filter resources.
     */
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "NoCacheFilter destroyed");
    }
}