package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.AuthenticationService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Login Servlet - handles user authentication. Processes login form submission,
 * authenticates users, and redirects to role-specific dashboards. Implements
 * failed login attempt tracking and account lockout PER EMAIL.
 *
 * Required by Module 1: User Registration & Authentication.
 *
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class LoginServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(LoginServlet.class.getName());

    private AuthenticationService authService;

    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
        LOGGER.log(Level.INFO, "LoginServlet initialized");
    }

    /**
     * Handles GET requests - displays the login page.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if already logged in
        if (SessionValidator.isLoggedIn(request)) {
            User user = SessionValidator.getLoggedInUser(request);
            String dashboardUrl = authService.getDashboardUrl(user);
            response.sendRedirect(request.getContextPath() + dashboardUrl);
            return;
        }

        // Display login page
        request.getRequestDispatcher(Constants.PAGE_LOGIN).forward(request, response);
    }

    /**
     * Handles POST requests - processes login form submission. Authenticates
     * user and redirects to appropriate dashboard.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validate input
        if (email == null || email.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Email and password are required");
            request.setAttribute("email", email);
            request.getRequestDispatcher(Constants.PAGE_LOGIN).forward(request, response);
            return;
        }

        String trimmedEmail = email.trim();

        // Check if THIS SPECIFIC EMAIL is locked in session
        if (SessionValidator.isAccountLockedForEmail(request, trimmedEmail)) {
            request.setAttribute("errorMessage", Constants.ERROR_ACCOUNT_LOCKED);
            request.setAttribute("email", trimmedEmail);
            request.getRequestDispatcher(Constants.PAGE_LOGIN).forward(request, response);
            return;
        }

        // Authenticate user
        User user = authService.authenticate(trimmedEmail, password, request);

        if (user != null) {
            // Check if account is manually locked in database
            if (user.isLocked()) {
                request.setAttribute("errorMessage", Constants.ERROR_ACCOUNT_LOCKED);
                request.setAttribute("email", trimmedEmail);
                request.getRequestDispatcher(Constants.PAGE_LOGIN).forward(request, response);
                return;
            }

            // Successful login
            SessionValidator.setLoggedInUser(request, user);
            SessionValidator.resetFailedLoginAttemptsForEmail(request, trimmedEmail);

            LOGGER.log(Level.INFO, "User logged in: {0} ({1})",
                    new Object[]{user.getEmail(), user.getRole()});

            // Get redirect URL (either requested page or dashboard)
            HttpSession session = request.getSession();
            String redirectUrl = (String) session.getAttribute("redirectAfterLogin");

            if (redirectUrl != null && !redirectUrl.isEmpty()) {
                session.removeAttribute("redirectAfterLogin");
                // Check if redirectUrl already contains context path
                if (redirectUrl.startsWith(request.getContextPath())) {
                    response.sendRedirect(redirectUrl);
                } else {
                    response.sendRedirect(request.getContextPath() + redirectUrl);
                }
            } else {
                String dashboardUrl = authService.getDashboardUrl(user);
                response.sendRedirect(request.getContextPath() + dashboardUrl);
            }

        } else {
            // Failed login
            int attempts = SessionValidator.getFailedLoginAttemptsForEmail(request, trimmedEmail);
            int remainingAttempts = Constants.MAX_LOGIN_ATTEMPTS - attempts;

            if (remainingAttempts <= 0) {
                request.setAttribute("errorMessage", Constants.ERROR_ACCOUNT_LOCKED);
            } else {
                request.setAttribute("errorMessage",
                        Constants.ERROR_INVALID_CREDENTIALS + ". " + remainingAttempts + " attempt(s) remaining.");
            }

            request.setAttribute("email", trimmedEmail);
            request.getRequestDispatcher(Constants.PAGE_LOGIN).forward(request, response);
        }
    }
}
