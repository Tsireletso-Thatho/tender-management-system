package util;

/**
 * Application-wide constants for the ProcureGov Tender Management System.
 * Centralizes all constant values used across the application.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class Constants {
    
    /**
     * Private constructor to prevent instantiation.
     * This is a utility class with only static constants.
     */
    private Constants() {
    }
    
    // =====================================================
    // SESSION ATTRIBUTES
    // =====================================================
    public static final String SESSION_USER = "loggedInUser";
    public static final String SESSION_USER_ID = "userId";
    public static final String SESSION_USER_EMAIL = "userEmail";
    public static final String SESSION_USER_ROLE = "userRole";
    public static final String SESSION_FAILED_ATTEMPTS = "failedLoginAttempts";
    public static final String SESSION_ACCOUNT_LOCKED = "accountLocked";
    
    // =====================================================
    // USER ROLES
    // =====================================================
    public static final String ROLE_SUPPLIER = "SUPPLIER";
    public static final String ROLE_PROCUREMENT_OFFICER = "PROCUREMENT_OFFICER";
    public static final String ROLE_EVALUATION_COMMITTEE = "EVALUATION_COMMITTEE";
    
    // =====================================================
    // USER STATUSES
    // =====================================================
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_LOCKED = "LOCKED";
    
    // =====================================================
    // TENDER STATUSES
    // =====================================================
    public static final String TENDER_STATUS_DRAFT = "DRAFT";
    public static final String TENDER_STATUS_OPEN = "OPEN";
    public static final String TENDER_STATUS_CLOSED = "CLOSED";
    public static final String TENDER_STATUS_UNDER_EVALUATION = "UNDER_EVALUATION";
    public static final String TENDER_STATUS_EVALUATED = "EVALUATED";
    public static final String TENDER_STATUS_AWARDED = "AWARDED";
    
    // =====================================================
    // TENDER CATEGORIES
    // =====================================================
    public static final String CATEGORY_CONSTRUCTION = "CONSTRUCTION";
    public static final String CATEGORY_ROADS = "ROADS";
    public static final String CATEGORY_ELECTRICAL = "ELECTRICAL";
    public static final String CATEGORY_PLUMBING = "PLUMBING";
    public static final String CATEGORY_GENERAL_SERVICES = "GENERAL_SERVICES";
    
    // =====================================================
    // BID STATUSES
    // =====================================================
    public static final String BID_STATUS_SUBMITTED = "SUBMITTED";
    public static final String BID_STATUS_EVALUATED = "EVALUATED";
    public static final String BID_STATUS_WON = "WON";
    public static final String BID_STATUS_NOT_WON = "NOT_WON";
    
    // =====================================================
    // EMAIL OUTCOMES
    // =====================================================
    public static final String OUTCOME_WON = "WON";
    public static final String OUTCOME_NOT_WON = "NOT_WON";
    
    // =====================================================
    // EMAIL STATUSES
    // =====================================================
    public static final String EMAIL_STATUS_SENT = "SENT";
    public static final String EMAIL_STATUS_FAILED = "FAILED";
    
    // =====================================================
    // SECURITY SETTINGS
    // =====================================================
    public static final int MAX_LOGIN_ATTEMPTS = 3;
    public static final int SESSION_TIMEOUT_MINUTES = 30;
    
    // =====================================================
    // FILE UPLOAD SETTINGS
    // =====================================================
    public static final long MAX_TENDER_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    public static final long MAX_BID_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    
    // =====================================================
    // SCORING WEIGHTS
    // =====================================================
    public static final double PRICE_WEIGHT = 0.40;
    public static final double TECHNICAL_WEIGHT = 0.35;
    public static final double TIMELINE_WEIGHT = 0.25;
    
    // =====================================================
    // REFERENCE NUMBER FORMATS
    // =====================================================
    public static final String TENDER_REF_PREFIX = "MPW";
    public static final String SUPPLIER_REG_PREFIX = "SUP";
    
    // =====================================================
    // VALIDATION CONSTANTS
    // =====================================================
    public static final int MAX_COMPLIANCE_STATEMENT_LENGTH = 600;
    public static final int MIN_TIMELINE_DAYS = 1;
    public static final int MAX_TIMELINE_DAYS = 365;
    public static final double MIN_SCORE = 0.0;
    public static final double MAX_SCORE = 100.0;
    
    // =====================================================
    // ERROR MESSAGES
    // =====================================================
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid email or password";
    public static final String ERROR_ACCOUNT_LOCKED = "Account is locked. Please try again later";
    public static final String ERROR_ACCESS_DENIED = "Access denied. You do not have permission to view this page";
    public static final String ERROR_SESSION_EXPIRED = "Your session has expired. Please login again";
    public static final String ERROR_TENDER_NOT_FOUND = "Tender not found";
    public static final String ERROR_BID_ALREADY_SUBMITTED = "You have already submitted a bid for this tender";
    public static final String ERROR_DEADLINE_PASSED = "The submission deadline has passed";
    public static final String ERROR_TENDER_NOT_OPEN = "This tender is not open for bidding";
    public static final String ERROR_FILE_TOO_LARGE = "File size exceeds maximum allowed size";
    public static final String ERROR_INVALID_FILE_TYPE = "Invalid file type. Only PDF and DOCX are allowed";
    
    // =====================================================
    // SUCCESS MESSAGES
    // =====================================================
    public static final String SUCCESS_REGISTRATION = "Registration successful! You can now login";
    public static final String SUCCESS_TENDER_CREATED = "Tender created successfully";
    public static final String SUCCESS_TENDER_PUBLISHED = "Tender published successfully";
    public static final String SUCCESS_BID_SUBMITTED = "Bid submitted successfully";
    public static final String SUCCESS_SCORES_SUBMITTED = "Evaluation scores submitted successfully";
    public static final String SUCCESS_TENDER_AWARDED = "Tender awarded successfully";
    
    // =====================================================
    // PAGE PATHS
    // =====================================================
    public static final String PAGE_LOGIN = "/WEB-INF/views/auth/login.jsp";
    public static final String PAGE_REGISTER = "/WEB-INF/views/auth/register-supplier.jsp";
    public static final String PAGE_ERROR = "/error.jsp";
    
    public static final String PAGE_OFFICER_DASHBOARD = "/WEB-INF/views/officer/dashboard.jsp";
    public static final String PAGE_OFFICER_CREATE_TENDER = "/WEB-INF/views/officer/create-tender.jsp";
    public static final String PAGE_OFFICER_EDIT_TENDER = "/WEB-INF/views/officer/edit-tender.jsp";
    public static final String PAGE_OFFICER_VIEW_TENDER = "/WEB-INF/views/officer/view-tender.jsp";
    public static final String PAGE_OFFICER_LIST_TENDERS = "/WEB-INF/views/officer/list-tenders.jsp";
    public static final String PAGE_OFFICER_AWARD_TENDER = "/WEB-INF/views/officer/award-tender.jsp";
    public static final String PAGE_OFFICER_AWARD_NOTICE = "/WEB-INF/views/officer/award-notice.jsp";
    public static final String PAGE_OFFICER_EVALUATE_BIDS = "/WEB-INF/views/officer/evaluate-bids.jsp";
    public static final String PAGE_OFFICER_EVALUATION_RESULTS = "/WEB-INF/views/officer/evaluation-results.jsp";
    
    public static final String PAGE_SUPPLIER_DASHBOARD = "/WEB-INF/views/supplier/dashboard.jsp";
    public static final String PAGE_SUPPLIER_TENDERS = "/WEB-INF/views/supplier/view-tenders.jsp";
    public static final String PAGE_SUPPLIER_TENDER_DETAIL = "/WEB-INF/views/supplier/tender-detail.jsp";
    public static final String PAGE_SUPPLIER_SUBMIT_BID = "/WEB-INF/views/supplier/submit-bid.jsp";
    public static final String PAGE_SUPPLIER_MY_BIDS = "/WEB-INF/views/supplier/my-bids.jsp";
    public static final String PAGE_SUPPLIER_VIEW_AWARD = "/WEB-INF/views/supplier/view-award.jsp";
    
    public static final String PAGE_EVALUATOR_DASHBOARD = "/WEB-INF/views/evaluator/dashboard.jsp";
    public static final String PAGE_EVALUATOR_TENDERS = "/WEB-INF/views/evaluator/tenders-for-evaluation.jsp";
    public static final String PAGE_EVALUATOR_EVALUATE = "/WEB-INF/views/evaluator/evaluate-bids.jsp";
    public static final String PAGE_EVALUATOR_RESULTS = "/WEB-INF/views/evaluator/view-results.jsp";
    public static final String PAGE_EVALUATOR_MY_EVALUATIONS = "/WEB-INF/views/evaluator/my-evaluations.jsp";
    
    // =====================================================
    // COMMON INCLUDES
    // =====================================================
    public static final String INCLUDE_HEADER = "/WEB-INF/views/common/header.jsp";
    public static final String INCLUDE_FOOTER = "/WEB-INF/views/common/footer.jsp";
    public static final String INCLUDE_NAVBAR = "/WEB-INF/views/common/navbar.jsp";
    
    // =====================================================
    // SERVLET URL PATTERNS
    // =====================================================
    public static final String URL_LOGIN = "/login";
    public static final String URL_LOGOUT = "/logout";
    public static final String URL_REGISTER = "/register";
    
    public static final String URL_OFFICER_DASHBOARD = "/officer/dashboard";
    public static final String URL_OFFICER_CREATE_TENDER = "/officer/tender/create";
    public static final String URL_OFFICER_EDIT_TENDER = "/officer/tender/edit";
    public static final String URL_OFFICER_VIEW_TENDER = "/officer/tender/view";
    public static final String URL_OFFICER_LIST_TENDERS = "/officer/tender/list";
    public static final String URL_OFFICER_PUBLISH = "/officer/tender/publish";
    public static final String URL_OFFICER_START_EVALUATION = "/officer/tender/start-evaluation";
    public static final String URL_OFFICER_AWARD = "/officer/tender/award";
    public static final String URL_OFFICER_DOWNLOAD = "/officer/tender/download";
    public static final String URL_OFFICER_AWARD_NOTICE = "/officer/tender/award-notice";
    public static final String URL_OFFICER_EVALUATE = "/officer/evaluate";
    
    public static final String URL_SUPPLIER_DASHBOARD = "/supplier/dashboard";
    public static final String URL_SUPPLIER_TENDERS = "/supplier/tenders";
    public static final String URL_SUPPLIER_TENDER_DETAIL = "/supplier/tender/detail";
    public static final String URL_SUPPLIER_SUBMIT_BID = "/supplier/bid/submit";
    public static final String URL_SUPPLIER_MY_BIDS = "/supplier/bids";
    public static final String URL_SUPPLIER_DOWNLOAD = "/supplier/bid/download";
    public static final String URL_SUPPLIER_VIEW_AWARD = "/supplier/award";
    
    public static final String URL_EVALUATOR_DASHBOARD = "/evaluator/dashboard";
    public static final String URL_EVALUATOR_TENDERS = "/evaluator/tenders";
    public static final String URL_EVALUATOR_EVALUATE = "/evaluator/evaluate";
    public static final String URL_EVALUATOR_RESULTS = "/evaluator/results";
}