package filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * XSS (Cross-Site Scripting) Protection Filter.
 * Sanitizes request parameters to prevent XSS attacks.
 * Skips date/time fields that contain legitimate special characters.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class XSSFilter implements Filter {
    
    private static final Logger LOGGER = Logger.getLogger(XSSFilter.class.getName());
    
    // Parameter names that should NOT be sanitized (contain legitimate special chars)
    private static final String[] SKIP_PARAMETERS = {
        "submissionDeadline",  // Contains / and :
        "deadline",            // Contains / and :
        "date",                // Contains /
        "datetime"             // Contains / and :
    };
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        LOGGER.log(Level.INFO, "XSSFilter initialized");
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        XSSRequestWrapper wrappedRequest = new XSSRequestWrapper(httpRequest);
        chain.doFilter(wrappedRequest, response);
    }
    
    @Override
    public void destroy() {
        LOGGER.log(Level.INFO, "XSSFilter destroyed");
    }
    
    /**
     * Request wrapper that sanitizes parameter values.
     */
    private class XSSRequestWrapper extends HttpServletRequestWrapper {
        
        public XSSRequestWrapper(HttpServletRequest request) {
            super(request);
        }
        
        @Override
        public String getParameter(String name) {
            String value = super.getParameter(name);
            
            // Skip sanitization for date/time fields
            if (shouldSkipSanitization(name)) {
                return value;
            }
            
            return sanitize(value);
        }
        
        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (values == null) {
                return null;
            }
            
            // Skip sanitization for date/time fields
            if (shouldSkipSanitization(name)) {
                return values;
            }
            
            String[] sanitizedValues = new String[values.length];
            for (int i = 0; i < values.length; i++) {
                sanitizedValues[i] = sanitize(values[i]);
            }
            return sanitizedValues;
        }
        
        /**
         * Checks if a parameter should skip XSS sanitization.
         */
        private boolean shouldSkipSanitization(String name) {
            if (name == null) return false;
            
            String lowerName = name.toLowerCase();
            for (String skip : SKIP_PARAMETERS) {
                if (lowerName.contains(skip)) {
                    return true;
                }
            }
            return false;
        }
        
        /**
         * Sanitizes a string by escaping HTML special characters.
         */
        private String sanitize(String value) {
            if (value == null) {
                return null;
            }
            
            return value
                    .replace("&", "&amp;")
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
        }
    }
}