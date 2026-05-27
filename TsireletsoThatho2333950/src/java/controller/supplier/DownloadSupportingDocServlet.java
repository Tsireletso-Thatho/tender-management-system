package controller.supplier;

import dao.implementations.BidDAOImpl;
import dao.implementations.SupplierDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bid;
import model.Supplier;
import model.User;
import service.FileService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Download Supporting Document Servlet - serves bid supporting documents.
 * Suppliers can only download their own documents.
 * Officers and Evaluators can download any bid document.
 * 
 * Required by Module 3: Supporting document download.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class DownloadSupportingDocServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(DownloadSupportingDocServlet.class.getName());
    
    private BidDAOImpl bidDAO;
    private SupplierDAOImpl supplierDAO;
    private FileService fileService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        bidDAO = new BidDAOImpl();
        supplierDAO = new SupplierDAOImpl();
        fileService = new FileService(getServletContext());
        LOGGER.log(Level.INFO, "DownloadSupportingDocServlet initialized");
    }
    
    /**
     * Handles GET requests - streams the supporting document for download.
     * Verifies that the requesting user has permission to view the document.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String bidIdParam = request.getParameter("bidId");
        
        if (bidIdParam == null || bidIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Bid ID is required");
            return;
        }
        
        try {
            int bidId = Integer.parseInt(bidIdParam);
            Bid bid = bidDAO.findById(bidId);
            
            if (bid == null || bid.getSupportingDocumentPath() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Document not found");
                return;
            }
            
            // Get logged-in user information
            User loggedInUser = SessionValidator.getLoggedInUser(request);
            if (loggedInUser == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Please login");
                return;
            }
            
            String userRole = loggedInUser.getRole();
            int userId = loggedInUser.getUserId();
            
            // Permission check based on role
            boolean hasPermission = false;
            
            if (Constants.ROLE_SUPPLIER.equals(userRole)) {
                // Suppliers can ONLY view their own bid documents
                Supplier supplier = supplierDAO.findByUserId(userId);
                if (supplier != null && bid.getSupplierId() == supplier.getSupplierId()) {
                    hasPermission = true;
                }
            } else if (Constants.ROLE_PROCUREMENT_OFFICER.equals(userRole) || 
                       Constants.ROLE_EVALUATION_COMMITTEE.equals(userRole)) {
                // Officers and Evaluators can view ALL bid documents
                hasPermission = true;
            }
            
            if (!hasPermission) {
                LOGGER.log(Level.WARNING, "Unauthorized document access attempt: Bid ID {0}, User ID {1}, Role {2}", 
                           new Object[]{bidId, userId, userRole});
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access denied");
                return;
            }
            
            Path filePath = fileService.getFilePath(bid.getSupportingDocumentPath());
            
            if (filePath == null || !Files.exists(filePath)) {
                LOGGER.log(Level.WARNING, "File not found: {0}", bid.getSupportingDocumentPath());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }
            
            // Set response headers
            String mimeType = fileService.getMimeType(bid.getSupportingDocumentPath());
            response.setContentType(mimeType);
            response.setHeader("Content-Disposition", 
                "attachment; filename=\"bid_" + bidId + "_document" + getFileExtension(bid.getSupportingDocumentPath()) + "\"");
            response.setContentLengthLong(Files.size(filePath));
            
            // Stream the file
            try (OutputStream out = response.getOutputStream()) {
                Files.copy(filePath, out);
                out.flush();
            }
            
            LOGGER.log(Level.INFO, "Document served: Bid ID {0} to User ID {1} ({2})", 
                       new Object[]{bidId, userId, userRole});
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid bid ID");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error serving document: {0}", e.getMessage());
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving file");
            }
        }
    }
    
    /**
     * Gets the file extension from a path.
     * 
     * @param path the file path
     * @return the extension including dot
     */
    private String getFileExtension(String path) {
        if (path == null) return "";
        int lastDot = path.lastIndexOf('.');
        return lastDot != -1 ? path.substring(lastDot) : "";
    }
}