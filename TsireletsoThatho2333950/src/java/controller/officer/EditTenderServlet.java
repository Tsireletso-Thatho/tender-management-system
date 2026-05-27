package controller.officer;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import model.Tender;
import service.FileService;
import service.TenderService;
import util.Constants;
import util.DateUtils;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Edit Tender Servlet - handles editing of existing tenders. Only allowed when
 * tender is in DRAFT status.
 *
 * Required by Module 2: Officers can edit tenders only while in Draft status.
 *
 * @author Tsireletso Thatho
 * @version 1.0
 */
@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024, // 5MB
        maxRequestSize = 10 * 1024 * 1024, // 10MB
        fileSizeThreshold = 1024 * 1024 // 1MB
)
public class EditTenderServlet extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(EditTenderServlet.class.getName());

    private TenderService tenderService;
    private FileService fileService;

    /**
     * Parses deadline string trying multiple common formats.
     *
     * @param deadlineStr the deadline string
     * @return parsed Timestamp or null if all formats fail
     */
    private Timestamp parseDeadline(String deadlineStr) {
        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            return null;
        }

        deadlineStr = deadlineStr.trim();

        // Try primary format: dd/MM/yyyy HH:mm
        Timestamp ts = DateUtils.parseDateTime(deadlineStr);
        if (ts != null) {
            LOGGER.log(Level.FINE, "Parsed deadline with format dd/MM/yyyy HH:mm: {0}", deadlineStr);
            return ts;
        }

        // Try alternative format: yyyy-MM-dd'T'HH:mm (from datetime-local input)
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
            sdf.setLenient(false);
            java.util.Date date = sdf.parse(deadlineStr);
            LOGGER.log(Level.FINE, "Parsed deadline with format yyyy-MM-dd'T'HH:mm: {0}", deadlineStr);
            return new Timestamp(date.getTime());
        } catch (java.text.ParseException e) {
            // Continue to next format
        }

        // Try alternative format: yyyy-MM-dd HH:mm
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm");
            sdf.setLenient(false);
            java.util.Date date = sdf.parse(deadlineStr);
            LOGGER.log(Level.FINE, "Parsed deadline with format yyyy-MM-dd HH:mm: {0}", deadlineStr);
            return new Timestamp(date.getTime());
        } catch (java.text.ParseException e) {
            // All formats failed
        }

        LOGGER.log(Level.WARNING, "Failed to parse deadline with any format: {0}", deadlineStr);
        return null;
    }

    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        fileService = new FileService(getServletContext());
        LOGGER.log(Level.INFO, "EditTenderServlet initialized");
    }

    /**
     * Handles GET requests - displays the edit tender form.
     *
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tenderIdParam = request.getParameter("id");

        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
            return;
        }

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderService.getTenderById(tenderId);

            if (tender == null) {
                request.setAttribute("errorMessage", "Tender not found");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
                return;
            }

            // Check if tender is editable
            if (!tender.isEditable()) {
                request.setAttribute("errorMessage", "Tender can only be edited in DRAFT status");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }

            request.setAttribute("tender", tender);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_EDIT_TENDER).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }

    /**
     * Handles POST requests - processes tender edit form.
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
        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        String estimatedValueStr = request.getParameter("estimatedValue");
        String deadlineStr = request.getParameter("submissionDeadline");
        Part filePart = request.getPart("noticeDocument");

        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
            return;
        }

        int tenderId = Integer.parseInt(tenderIdParam);
        Tender existingTender = tenderService.getTenderById(tenderId);

        if (existingTender == null) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
            return;
        }

        // Check if editable
        if (!existingTender.isEditable()) {
            request.getSession().setAttribute("errorMessage", "Tender can only be edited in DRAFT status");
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
            return;
        }

        // Validate required fields
        StringBuilder errorMessage = new StringBuilder();

        if (title == null || title.trim().isEmpty()) {
            errorMessage.append("Tender title is required. ");
        }

        if (category == null || category.trim().isEmpty()) {
            errorMessage.append("Category is required. ");
        }

        if (description == null || description.trim().isEmpty()) {
            errorMessage.append("Description is required. ");
        }

        if (estimatedValueStr == null || estimatedValueStr.trim().isEmpty()) {
            errorMessage.append("Estimated value is required. ");
        }

        if (deadlineStr == null || deadlineStr.trim().isEmpty()) {
            errorMessage.append("Submission deadline is required. ");
        }

        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());
            request.setAttribute("tender", existingTender);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_EDIT_TENDER).forward(request, response);
            return;
        }

        // Parse deadline using robust DateUtils
        Timestamp deadline = DateUtils.parseDateTime(deadlineStr);
        if (deadline == null) {
            request.setAttribute("errorMessage", "Invalid deadline format. Please use: DD/MM/YYYY HH:MM (e.g., 16/04/2026 17:00)");
            request.setAttribute("tender", existingTender);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_EDIT_TENDER).forward(request, response);
            return;
        }

        // Parse estimated value
        BigDecimal estimatedValue;
        try {
            estimatedValue = new BigDecimal(estimatedValueStr);
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid estimated value");
            request.setAttribute("tender", existingTender);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_EDIT_TENDER).forward(request, response);
            return;
        }

        // Handle file upload if new file provided
        String newFilePath = existingTender.getNoticeDocumentPath();
        if (filePart != null && filePart.getSize() > 0) {
            try {
                // Delete old file
                if (existingTender.getNoticeDocumentPath() != null) {
                    fileService.deleteFile(existingTender.getNoticeDocumentPath());
                }
                // Save new file
                newFilePath = fileService.saveTenderNotice(filePart);
            } catch (IllegalArgumentException e) {
                request.setAttribute("errorMessage", e.getMessage());
                request.setAttribute("tender", existingTender);
                request.getRequestDispatcher(Constants.PAGE_OFFICER_EDIT_TENDER).forward(request, response);
                return;
            }
        }

        // Update tender
        existingTender.setTitle(title.trim());
        existingTender.setCategory(category);
        existingTender.setDescription(description.trim());
        existingTender.setEstimatedValue(estimatedValue);
        existingTender.setSubmissionDeadline(deadline);
        existingTender.setNoticeDocumentPath(newFilePath);

        boolean success = tenderService.updateTender(existingTender);

        if (success) {
            request.getSession().setAttribute("successMessage", "Tender updated successfully");
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
        } else {
            request.setAttribute("errorMessage", "Failed to update tender");
            request.setAttribute("tender", existingTender);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_EDIT_TENDER).forward(request, response);
        }
    }
}
