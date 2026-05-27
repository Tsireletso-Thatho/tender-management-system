package util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import model.User;

/**
 * Utility class for session validation and role checking. Provides reusable
 * methods for verifying user authentication and authorization.
 *
 * Required by Module 1: All protected pages must verify session validity and
 * role. The session check on protected pages must be in a reusable method or
 * utility class.
 *
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class SessionValidator {

    // Session key prefix for per-email failed attempts tracking
    private static final String SESSION_FAILED_ATTEMPTS_PREFIX = "failedAttempts_";

    /**
     * Private constructor to prevent instantiation. This is a utility class
     * with only static methods.
     */
    private SessionValidator() {
    }

    /**
     * Checks if a user is currently logged in.
     *
     * @param request the HttpServletRequest
     * @return true if a user is logged in and session is valid
     */
    public static boolean isLoggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }

        User user = (User) session.getAttribute(Constants.SESSION_USER);
        return user != null && user.getUserId() > 0;
    }

    /**
     * Gets the currently logged-in user from the session.
     *
     * @param request the HttpServletRequest
     * @return the User object, or null if not logged in
     */
    public static User getLoggedInUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        return (User) session.getAttribute(Constants.SESSION_USER);
    }

    /**
     * Gets the ID of the currently logged-in user.
     *
     * @param request the HttpServletRequest
     * @return the user ID, or -1 if not logged in
     */
    public static int getLoggedInUserId(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null ? user.getUserId() : -1;
    }

    /**
     * Gets the role of the currently logged-in user.
     *
     * @param request the HttpServletRequest
     * @return the role string, or null if not logged in
     */
    public static String getLoggedInUserRole(HttpServletRequest request) {
        User user = getLoggedInUser(request);
        return user != null ? user.getRole() : null;
    }

    /**
     * Checks if the logged-in user has a specific role.
     *
     * @param request the HttpServletRequest
     * @param requiredRole the role to check for
     * @return true if the user is logged in and has the required role
     */
    public static boolean hasRole(HttpServletRequest request, String requiredRole) {
        User user = getLoggedInUser(request);
        return user != null && requiredRole != null && requiredRole.equals(user.getRole());
    }

    /**
     * Checks if the logged-in user is a Procurement Officer.
     *
     * @param request the HttpServletRequest
     * @return true if the user is a Procurement Officer
     */
    public static boolean isProcurementOfficer(HttpServletRequest request) {
        return hasRole(request, Constants.ROLE_PROCUREMENT_OFFICER);
    }

    /**
     * Checks if the logged-in user is an Evaluation Committee Member.
     *
     * @param request the HttpServletRequest
     * @return true if the user is an Evaluation Committee Member
     */
    public static boolean isEvaluationCommittee(HttpServletRequest request) {
        return hasRole(request, Constants.ROLE_EVALUATION_COMMITTEE);
    }

    /**
     * Checks if the logged-in user is a Supplier.
     *
     * @param request the HttpServletRequest
     * @return true if the user is a Supplier
     */
    public static boolean isSupplier(HttpServletRequest request) {
        return hasRole(request, Constants.ROLE_SUPPLIER);
    }

    /**
     * Validates that a user is logged in. If not, throws an exception. Use this
     * in protected servlets to enforce authentication.
     *
     * @param request the HttpServletRequest
     * @throws SecurityException if user is not logged in
     */
    public static void requireLogin(HttpServletRequest request) throws SecurityException {
        if (!isLoggedIn(request)) {
            throw new SecurityException("Authentication required");
        }
    }

    /**
     * Validates that a user has a specific role. If not, throws an exception.
     *
     * @param request the HttpServletRequest
     * @param requiredRole the required role
     * @throws SecurityException if user doesn't have the required role
     */
    public static void requireRole(HttpServletRequest request, String requiredRole)
            throws SecurityException {
        requireLogin(request);
        if (!hasRole(request, requiredRole)) {
            throw new SecurityException("Insufficient permissions: " + requiredRole + " required");
        }
    }

    /**
     * Gets the dashboard redirect URL based on user role.
     *
     * @param role the user's role
     * @return the dashboard URL path
     */
    public static String getDashboardForRole(String role) {
        if (Constants.ROLE_PROCUREMENT_OFFICER.equals(role)) {
            return "/officer/dashboard";
        } else if (Constants.ROLE_EVALUATION_COMMITTEE.equals(role)) {
            return "/evaluator/dashboard";
        } else if (Constants.ROLE_SUPPLIER.equals(role)) {
            return "/supplier/dashboard";
        }
        return "/login";
    }

    /**
     * Invalidates the current session (logout).
     *
     * @param request the HttpServletRequest
     */
    public static void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Sets the logged-in user in the session.
     *
     * @param request the HttpServletRequest
     * @param user the authenticated User object
     */
    public static void setLoggedInUser(HttpServletRequest request, User user) {
        HttpSession session = request.getSession(true);
        session.setAttribute(Constants.SESSION_USER, user);
        session.setAttribute(Constants.SESSION_USER_ID, user.getUserId());
        session.setAttribute(Constants.SESSION_USER_EMAIL, user.getEmail());
        session.setAttribute(Constants.SESSION_USER_ROLE, user.getRole());
    }

    /**
     * Gets the failed login attempts for a specific email address.
     *
     * @param request the HttpServletRequest
     * @param email the email address
     * @return the number of failed attempts
     */
    public static int getFailedLoginAttemptsForEmail(HttpServletRequest request, String email) {
        if (email == null || email.trim().isEmpty()) {
            return 0;
        }
        HttpSession session = request.getSession(true);
        String key = SESSION_FAILED_ATTEMPTS_PREFIX + email.trim().toLowerCase();
        Integer attempts = (Integer) session.getAttribute(key);
        return attempts != null ? attempts : 0;
    }

    /**
     * Increments the failed login attempts for a specific email address.
     *
     * @param request the HttpServletRequest
     * @param email the email address
     * @return the new attempt count
     */
    public static int incrementFailedLoginAttemptsForEmail(HttpServletRequest request, String email) {
        if (email == null || email.trim().isEmpty()) {
            return 0;
        }
        HttpSession session = request.getSession(true);
        String key = SESSION_FAILED_ATTEMPTS_PREFIX + email.trim().toLowerCase();
        int attempts = getFailedLoginAttemptsForEmail(request, email) + 1;
        session.setAttribute(key, attempts);
        return attempts;
    }

    /**
     * Resets the failed login attempts for a specific email address.
     *
     * @param request the HttpServletRequest
     * @param email the email address
     */
    public static void resetFailedLoginAttemptsForEmail(HttpServletRequest request, String email) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }
        HttpSession session = request.getSession(false);
        if (session != null) {
            String key = SESSION_FAILED_ATTEMPTS_PREFIX + email.trim().toLowerCase();
            session.removeAttribute(key);
        }
    }

    /**
     * Checks if an account is locked for a specific email address. Lock occurs
     * after MAX_LOGIN_ATTEMPTS (3) failed attempts.
     *
     * @param request the HttpServletRequest
     * @param email the email address
     * @return true if failed attempts >= 3
     */
    public static boolean isAccountLockedForEmail(HttpServletRequest request, String email) {
        return getFailedLoginAttemptsForEmail(request, email) >= Constants.MAX_LOGIN_ATTEMPTS;
    }

    // =====================================================
    // LEGACY METHODS 
    // =====================================================
    /**
     * Gets the failed login attempts count from session (legacy - global).
     *
     * @deprecated Use getFailedLoginAttemptsForEmail() for per-email tracking
     * @param request the HttpServletRequest
     * @return the number of failed attempts
     */
    @Deprecated
    public static int getFailedLoginAttempts(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        Integer attempts = (Integer) session.getAttribute(Constants.SESSION_FAILED_ATTEMPTS);
        return attempts != null ? attempts : 0;
    }

    /**
     * Increments the failed login attempts counter in session (legacy -
     * global).
     *
     * @deprecated Use incrementFailedLoginAttemptsForEmail() for per-email
     * tracking
     * @param request the HttpServletRequest
     * @return the new attempt count
     */
    @Deprecated
    public static int incrementFailedLoginAttempts(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        int attempts = getFailedLoginAttempts(request) + 1;
        session.setAttribute(Constants.SESSION_FAILED_ATTEMPTS, attempts);
        return attempts;
    }

    /**
     * Resets the failed login attempts counter (legacy - global).
     *
     * @deprecated Use resetFailedLoginAttemptsForEmail() for per-email tracking
     * @param request the HttpServletRequest
     */
    @Deprecated
    public static void resetFailedLoginAttempts(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Constants.SESSION_FAILED_ATTEMPTS);
        }
    }

    /**
     * Checks if the account is locked due to too many failed attempts (legacy -
     * global).
     *
     * @deprecated Use isAccountLockedForEmail() for per-email tracking
     * @param request the HttpServletRequest
     * @return true if failed attempts >= 3
     */
    @Deprecated
    public static boolean isAccountLocked(HttpServletRequest request) {
        return getFailedLoginAttempts(request) >= Constants.MAX_LOGIN_ATTEMPTS;
    }

    /**
     * Logs out the current user by removing their session attributes. Does NOT
     * invalidate the entire session.
     *
     * @param request the HttpServletRequest
     */
    public static void logoutUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(Constants.SESSION_USER);
            session.removeAttribute(Constants.SESSION_USER_ID);
            session.removeAttribute(Constants.SESSION_USER_EMAIL);
            session.removeAttribute(Constants.SESSION_USER_ROLE);
            session.removeAttribute("redirectAfterLogin");
        }
    }
}
