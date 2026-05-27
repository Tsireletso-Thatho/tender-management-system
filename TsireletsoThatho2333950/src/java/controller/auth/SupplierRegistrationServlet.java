package controller.auth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Supplier;
import model.User;
import service.AuthenticationService;
import util.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Supplier Registration Servlet - handles new supplier account registration.
 * Processes registration form submission and creates supplier accounts.
 * Ministry staff accounts are created via seed script only.
 * 
 * Required by Module 1: Supplier Registration page.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class SupplierRegistrationServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(SupplierRegistrationServlet.class.getName());
    
    private AuthenticationService authService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        authService = new AuthenticationService();
        LOGGER.log(Level.INFO, "SupplierRegistrationServlet initialized");
    }
    
    /**
     * Handles GET requests - displays the registration form.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Display registration page
        request.getRequestDispatcher(Constants.PAGE_REGISTER).forward(request, response);
    }
    
    /**
     * Handles POST requests - processes registration form submission.
     * Validates input and creates new supplier account.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Get form parameters
        String companyName = request.getParameter("companyName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String physicalAddress = request.getParameter("physicalAddress");
        String contactNumber = request.getParameter("contactNumber");
        
        // Validate required fields
        if (!validateRequiredFields(request, companyName, email, password, 
                                    confirmPassword, physicalAddress, contactNumber)) {
            request.getRequestDispatcher(Constants.PAGE_REGISTER).forward(request, response);
            return;
        }
        
        // Validate password match
        if (!password.equals(confirmPassword)) {
            request.setAttribute("errorMessage", "Passwords do not match");
            setFormAttributes(request, companyName, email, physicalAddress, contactNumber);
            request.getRequestDispatcher(Constants.PAGE_REGISTER).forward(request, response);
            return;
        }
        
        // Validate password strength
        if (password.length() < 6) {
            request.setAttribute("errorMessage", "Password must be at least 6 characters long");
            setFormAttributes(request, companyName, email, physicalAddress, contactNumber);
            request.getRequestDispatcher(Constants.PAGE_REGISTER).forward(request, response);
            return;
        }
        
        // Validate email format
        if (!isValidEmail(email)) {
            request.setAttribute("errorMessage", "Please enter a valid email address");
            setFormAttributes(request, companyName, null, physicalAddress, contactNumber);
            request.getRequestDispatcher(Constants.PAGE_REGISTER).forward(request, response);
            return;
        }
        
        // Create supplier object
        Supplier supplier = new Supplier();
        supplier.setCompanyName(companyName);
        supplier.setPhysicalAddress(physicalAddress);
        supplier.setContactNumber(contactNumber);
        
        // Register supplier with email
        int supplierId = authService.registerSupplier(supplier, email, password);
        
        if (supplierId != -1) {
            LOGGER.log(Level.INFO, "New supplier registered: {0} (ID: {1})", 
                       new Object[]{email, supplierId});
            
            // Set success message and redirect to login
            request.getSession().setAttribute("successMessage", Constants.SUCCESS_REGISTRATION);
            response.sendRedirect(request.getContextPath() + Constants.URL_LOGIN);
        } else {
            request.setAttribute("errorMessage", "Registration failed. Email may already be registered.");
            setFormAttributes(request, companyName, email, physicalAddress, contactNumber);
            request.getRequestDispatcher(Constants.PAGE_REGISTER).forward(request, response);
        }
    }
    
    /**
     * Validates that all required fields are present and not empty.
     * 
     * @param request the HttpServletRequest for setting attributes
     * @param companyName the company name
     * @param email the email address
     * @param password the password
     * @param confirmPassword the password confirmation
     * @param physicalAddress the physical address
     * @param contactNumber the contact number
     * @return true if all fields are valid
     */
    private boolean validateRequiredFields(HttpServletRequest request,
                                           String companyName, String email,
                                           String password, String confirmPassword,
                                           String physicalAddress, String contactNumber) {
        
        boolean isValid = true;
        StringBuilder errorMessage = new StringBuilder();
        
        if (companyName == null || companyName.trim().isEmpty()) {
            errorMessage.append("Company/Individual name is required. ");
            isValid = false;
        }
        
        if (email == null || email.trim().isEmpty()) {
            errorMessage.append("Email address is required. ");
            isValid = false;
        }
        
        if (password == null || password.trim().isEmpty()) {
            errorMessage.append("Password is required. ");
            isValid = false;
        }
        
        if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
            errorMessage.append("Password confirmation is required. ");
            isValid = false;
        }
        
        if (physicalAddress == null || physicalAddress.trim().isEmpty()) {
            errorMessage.append("Physical address is required. ");
            isValid = false;
        }
        
        if (contactNumber == null || contactNumber.trim().isEmpty()) {
            errorMessage.append("Contact number is required. ");
            isValid = false;
        }
        
        if (!isValid) {
            request.setAttribute("errorMessage", errorMessage.toString());
            setFormAttributes(request, companyName, email, physicalAddress, contactNumber);
        }
        
        return isValid;
    }
    
    /**
     * Sets form attributes to repopulate the form after validation errors.
     * 
     * @param request the HttpServletRequest
     * @param companyName the company name
     * @param email the email address
     * @param physicalAddress the physical address
     * @param contactNumber the contact number
     */
    private void setFormAttributes(HttpServletRequest request,
                                   String companyName, String email,
                                   String physicalAddress, String contactNumber) {
        request.setAttribute("companyName", companyName);
        request.setAttribute("email", email);
        request.setAttribute("physicalAddress", physicalAddress);
        request.setAttribute("contactNumber", contactNumber);
    }
    
    /**
     * Validates an email address format.
     * 
     * @param email the email address to validate
     * @return true if email format is valid
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}