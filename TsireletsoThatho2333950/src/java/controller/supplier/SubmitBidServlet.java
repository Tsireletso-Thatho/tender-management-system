package controller.supplier;

import dao.implementations.SupplierDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Bid;
import model.Supplier;
import model.Tender;
import service.BidService;
import service.FileService;
import service.TenderService;
import util.Constants;
import util.DateUtils;
import util.SessionValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Submit Bid Servlet - handles bid submission for a tender. Enforces closing
 * date server-side and one-bid-per-tender rule. Uses Part API for file upload.
 *
 * Required by Module 3: Bid submission with server-side validation.
 *
 * @author Tsireletso Thatho
 * @version 1.0
 */
@MultipartConfig(
        maxFileSize = 10 * 1024 * 1024, // 10MB
        maxRequestSize = 20 * 1024 * 1024, // 20MB
        fileSizeThreshold = 1024 * 1024 // 1MB
)
public class SubmitBidServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(SubmitBidServlet.class.getName());

    private TenderService tenderService;
    private BidService bidService;
    private FileService fileService;
    private SupplierDAOImpl supplierDAO;

    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        bidService = new BidService();
        fileService = new FileService(getServletContext());
        supplierDAO = new SupplierDAOImpl();
        LOGGER.log(Level.INFO, "SubmitBidServlet initialized");
    }

    /**
     * Handles GET requests - displays the bid submission form.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Check if this is a redirect from a file size error
        String error = request.getParameter("error");
        String tenderIdParam = request.getParameter("tenderId");

        // If no tenderId provided, redirect to tenders page
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDERS);
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderService.getTenderById(tenderId);

            if (tender == null) {
                request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_FOUND);
                response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDERS);
                return;
            }

            // Get supplier info
            int userId = SessionValidator.getLoggedInUserId(request);
            Supplier supplier = supplierDAO.findByUserId(userId);

            // If this is a file size error redirect, show error but still display the form
            if ("file_too_large".equals(error)) {
                request.setAttribute("errorMessage", "File size exceeds maximum allowed size (10MB). Please choose a smaller file.");
                request.setAttribute("tender", tender);
                request.getRequestDispatcher(Constants.PAGE_SUPPLIER_SUBMIT_BID).forward(request, response);
                return;
            }

            // Validate bid submission
            String validationError = bidService.validateBidSubmission(tenderId, supplier.getSupplierId());
            if (validationError != null) {
                request.getSession().setAttribute("errorMessage", validationError);
                response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDER_DETAIL + "?id=" + tenderId);
                return;
            }

            request.setAttribute("tender", tender);
            request.getRequestDispatcher(Constants.PAGE_SUPPLIER_SUBMIT_BID).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDERS);
        }
    }

    /**
     * Handles POST requests - processes bid submission. Enforces server-side
     * deadline check and one-bid-per-tender.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tenderIdParam = request.getParameter("tenderId");
        String bidAmountStr = request.getParameter("bidAmount");
        String complianceStatement = request.getParameter("complianceStatement");
        String timelineDaysStr = request.getParameter("timelineDays");
        Part filePart = request.getPart("supportingDocument");

        // Validate tender ID
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDERS);
            return;
        }

        int tenderId = Integer.parseInt(tenderIdParam);
        Tender tender = tenderService.getTenderById(tenderId);

        if (tender == null) {
            request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_FOUND);
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDERS);
            return;
        }

        // Get supplier info
        int userId = SessionValidator.getLoggedInUserId(request);
        Supplier supplier = supplierDAO.findByUserId(userId);

        // SERVER-SIDE: Validate tender is still open
        if (!Constants.TENDER_STATUS_OPEN.equals(tender.getStatus())) {
            request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_OPEN);
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDER_DETAIL + "?id=" + tenderId);
            return;
        }

        // SERVER-SIDE: Enforce closing date
        if (DateUtils.isDeadlinePassed(tender.getSubmissionDeadline())) {
            request.getSession().setAttribute("errorMessage", Constants.ERROR_DEADLINE_PASSED);
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDER_DETAIL + "?id=" + tenderId);
            return;
        }

        // SERVER-SIDE: Enforce one bid per tender
        if (bidService.hasSupplierBid(tenderId, supplier.getSupplierId())) {
            request.getSession().setAttribute("errorMessage", Constants.ERROR_BID_ALREADY_SUBMITTED);
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDER_DETAIL + "?id=" + tenderId);
            return;
        }

        // Validate required fields
        StringBuilder errorMessage = new StringBuilder();

        if (bidAmountStr == null || bidAmountStr.trim().isEmpty()) {
            errorMessage.append("Bid amount is required. ");
        }

        if (complianceStatement == null || complianceStatement.trim().isEmpty()) {
            errorMessage.append("Technical compliance statement is required. ");
        } else if (complianceStatement.length() > Constants.MAX_COMPLIANCE_STATEMENT_LENGTH) {
            errorMessage.append("Compliance statement must not exceed ")
                    .append(Constants.MAX_COMPLIANCE_STATEMENT_LENGTH)
                    .append(" characters. ");
        }

        if (timelineDaysStr == null || timelineDaysStr.trim().isEmpty()) {
            errorMessage.append("Proposed timeline is required. ");
        }

        if (filePart == null || filePart.getSize() == 0) {
            errorMessage.append("Supporting document is required. ");
        }

        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());
            request.setAttribute("tender", tender);
            setFormAttributes(request, bidAmountStr, complianceStatement, timelineDaysStr);
            request.getRequestDispatcher(Constants.PAGE_SUPPLIER_SUBMIT_BID).forward(request, response);
            return;
        }

        // Parse bid amount
        BigDecimal bidAmount;
        try {
            bidAmount = new BigDecimal(bidAmountStr);
            if (bidAmount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Bid amount must be positive");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid bid amount. Must be a positive number.");
            request.setAttribute("tender", tender);
            setFormAttributes(request, bidAmountStr, complianceStatement, timelineDaysStr);
            request.getRequestDispatcher(Constants.PAGE_SUPPLIER_SUBMIT_BID).forward(request, response);
            return;
        }

        // Parse timeline days
        int timelineDays;
        try {
            timelineDays = Integer.parseInt(timelineDaysStr);
            if (timelineDays < Constants.MIN_TIMELINE_DAYS || timelineDays > Constants.MAX_TIMELINE_DAYS) {
                throw new NumberFormatException("Timeline must be between "
                        + Constants.MIN_TIMELINE_DAYS + " and " + Constants.MAX_TIMELINE_DAYS + " days");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid timeline. Must be a number between "
                    + Constants.MIN_TIMELINE_DAYS + " and " + Constants.MAX_TIMELINE_DAYS + ".");
            request.setAttribute("tender", tender);
            setFormAttributes(request, bidAmountStr, complianceStatement, timelineDaysStr);
            request.getRequestDispatcher(Constants.PAGE_SUPPLIER_SUBMIT_BID).forward(request, response);
            return;
        }

        // Save uploaded file
        String filePath = null;
        try {
            filePath = fileService.saveBidDocument(filePart);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            request.setAttribute("tender", tender);
            setFormAttributes(request, bidAmountStr, complianceStatement, timelineDaysStr);
            request.getRequestDispatcher(Constants.PAGE_SUPPLIER_SUBMIT_BID).forward(request, response);
            return;
        }

        // Create bid
        Bid bid = new Bid();
        bid.setTenderId(tenderId);
        bid.setSupplierId(supplier.getSupplierId());
        bid.setBidAmount(bidAmount);
        bid.setTechnicalComplianceStatement(complianceStatement.trim());
        bid.setProposedTimelineDays(timelineDays);
        bid.setSupportingDocumentPath(filePath);

        try {
            int bidId = bidService.submitBid(bid);

            if (bidId != -1) {
                LOGGER.log(Level.INFO, "Bid submitted: Tender ID {0}, Supplier ID {1}, Amount {2}",
                        new Object[]{tenderId, supplier.getSupplierId(), bidAmount});

                request.getSession().setAttribute("successMessage", Constants.SUCCESS_BID_SUBMITTED);
                response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_MY_BIDS);
            } else {
                // Delete uploaded file if bid creation fails
                if (filePath != null) {
                    fileService.deleteFile(filePath);
                }

                request.setAttribute("errorMessage", "Failed to submit bid. Please try again.");
                request.setAttribute("tender", tender);
                setFormAttributes(request, bidAmountStr, complianceStatement, timelineDaysStr);
                request.getRequestDispatcher(Constants.PAGE_SUPPLIER_SUBMIT_BID).forward(request, response);
            }

        } catch (IllegalStateException e) {
            // Delete uploaded file
            if (filePath != null) {
                fileService.deleteFile(filePath);
            }

            request.getSession().setAttribute("errorMessage", e.getMessage());
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDER_DETAIL + "?id=" + tenderId);
        }
    }

    /**
     * Sets form attributes to repopulate the form after validation errors.
     *
     * @param request the HttpServletRequest
     * @param bidAmount the bid amount string
     * @param complianceStatement the compliance statement
     * @param timelineDays the timeline days string
     */
    private void setFormAttributes(HttpServletRequest request,
            String bidAmount, String complianceStatement, String timelineDays) {
        request.setAttribute("bidAmount", bidAmount);
        request.setAttribute("complianceStatement", complianceStatement);
        request.setAttribute("timelineDays", timelineDays);
    }
}
