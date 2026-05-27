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
import util.SessionValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.logging.Level;
import java.util.logging.Logger;

@MultipartConfig(
        maxFileSize = 5 * 1024 * 1024,
        maxRequestSize = 10 * 1024 * 1024,
        fileSizeThreshold = 1024 * 1024
)
public class CreateTenderServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(CreateTenderServlet.class.getName());

    private TenderService tenderService;
    private FileService fileService;

    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        fileService = new FileService(getServletContext());
        LOGGER.log(Level.INFO, "CreateTenderServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if this is a redirect from a file size error
        String error = request.getParameter("error");
        if ("file_too_large".equals(error)) {
            request.setAttribute("errorMessage", "File size exceeds maximum allowed size (5MB). Please choose a smaller file.");
        }
        request.getRequestDispatcher(Constants.PAGE_OFFICER_CREATE_TENDER).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String title = request.getParameter("title");
        String category = request.getParameter("category");
        String description = request.getParameter("description");
        String estimatedValueStr = request.getParameter("estimatedValue");
        String deadlineStr = request.getParameter("submissionDeadline");
        LOGGER.log(Level.WARNING, "DEBUG: Raw deadline string received: [" + deadlineStr + "]");
        Part filePart = request.getPart("noticeDocument");

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
        if (filePart == null || filePart.getSize() == 0) {
            errorMessage.append("Tender notice document is required. ");
        }

        if (errorMessage.length() > 0) {
            request.setAttribute("errorMessage", errorMessage.toString());
            setFormAttributes(request, title, category, description, estimatedValueStr, deadlineStr);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_CREATE_TENDER).forward(request, response);
            return;
        }

        // Parse deadline using robust DateUtils
        Timestamp deadline = DateUtils.parseDateTime(deadlineStr);
        if (deadline == null) {
            request.setAttribute("errorMessage", "Invalid deadline format. Please use: DD/MM/YYYY HH:MM (e.g., 16/04/2026 17:00)");
            setFormAttributes(request, title, category, description, estimatedValueStr, deadlineStr);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_CREATE_TENDER).forward(request, response);
            return;
        }

        // Validate deadline is in the future
        if (DateUtils.isPast(deadline)) {
            request.setAttribute("errorMessage", "Submission deadline must be in the future");
            setFormAttributes(request, title, category, description, estimatedValueStr, deadlineStr);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_CREATE_TENDER).forward(request, response);
            return;
        }

        // Parse estimated value
        BigDecimal estimatedValue;
        try {
            estimatedValue = new BigDecimal(estimatedValueStr);
            if (estimatedValue.compareTo(BigDecimal.ZERO) <= 0) {
                throw new NumberFormatException("Value must be positive");
            }
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid estimated value. Must be a positive number.");
            setFormAttributes(request, title, category, description, estimatedValueStr, deadlineStr);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_CREATE_TENDER).forward(request, response);
            return;
        }

        // Save uploaded file
        String filePath = null;
        try {
            filePath = fileService.saveTenderNotice(filePart);
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            setFormAttributes(request, title, category, description, estimatedValueStr, deadlineStr);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_CREATE_TENDER).forward(request, response);
            return;
        }

        // Create tender
        int userId = SessionValidator.getLoggedInUserId(request);

        Tender tender = new Tender();
        tender.setTitle(title.trim());
        tender.setCategory(category);
        tender.setDescription(description.trim());
        tender.setEstimatedValue(estimatedValue);
        tender.setSubmissionDeadline(deadline);
        tender.setNoticeDocumentPath(filePath);
        tender.setCreatedBy(userId);

        int tenderId = tenderService.createTender(tender);

        if (tenderId != -1) {
            LOGGER.log(Level.INFO, "Tender created: {0}", tender.getReferenceNumber());
            request.getSession().setAttribute("successMessage", Constants.SUCCESS_TENDER_CREATED);
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
        } else {
            if (filePath != null) {
                fileService.deleteFile(filePath);
            }
            request.setAttribute("errorMessage", "Failed to create tender. Please try again.");
            setFormAttributes(request, title, category, description, estimatedValueStr, deadlineStr);
            request.getRequestDispatcher(Constants.PAGE_OFFICER_CREATE_TENDER).forward(request, response);
        }
    }

    private void setFormAttributes(HttpServletRequest request,
            String title, String category, String description,
            String estimatedValue, String deadline) {
        request.setAttribute("title", title);
        request.setAttribute("category", category);
        request.setAttribute("description", description);
        request.setAttribute("estimatedValue", estimatedValue);
        request.setAttribute("submissionDeadline", deadline);
    }
}
