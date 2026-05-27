package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import service.AuthenticationService;
import util.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Logout Servlet - handles user logout.
 * Removes user attributes from session without invalidating the entire session.
 * This preserves failed login attempt tracking for other users.
 * 
 * Required by Module 1: A logout function must explicitly log out the user.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class LogoutServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(LogoutServlet.class.getName());
    
    private AuthenticationService authService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
        LOGGER.log(Level.INFO, "LogoutServlet initialized");
    }
    
    /**
     * Handles GET requests - performs logout.
     * Removes user attributes and redirects to login page.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }
    
    /**
     * Handles POST requests - performs logout.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        performLogout(request, response);
    }
    
    /**
     * Performs the logout operation.
     * Removes user attributes from session WITHOUT invalidating the entire session.
     * This preserves failed login attempt tracking for other users.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws IOException if an I/O error occurs
     */
    private void performLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        
        String email = null;
        HttpSession session = request.getSession(false);
        
        if (session != null) {
            Object userEmail = session.getAttribute(Constants.SESSION_USER_EMAIL);
            if (userEmail != null) {
                email = userEmail.toString();
            }
        }
        
        // Perform logout - removes user attributes only (does NOT invalidate session)
        authService.logout(request);
        
        LOGGER.log(Level.INFO, "User logged out: {0}", email != null ? email : "unknown");
        
        // Set success message in the EXISTING session (not a new one)
        HttpSession currentSession = request.getSession();
        currentSession.setAttribute("successMessage", "You have been successfully logged out.");
        
        // Redirect to login page using dynamic context path
        response.sendRedirect(request.getContextPath() + Constants.URL_LOGIN);
    }
}