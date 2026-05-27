package controller.officer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Tender;
import service.TenderService;
import util.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Publish Tender Servlet - publishes a tender, changing status from DRAFT to OPEN.
 * Once published, the tender becomes visible to suppliers.
 * 
 * Required by Module 2: Manual status transition (DRAFT → OPEN).
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class PublishTenderServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(PublishTenderServlet.class.getName());
    
    private TenderService tenderService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        LOGGER.log(Level.INFO, "PublishTenderServlet initialized");
    }
    
    /**
     * Handles POST requests - publishes a tender.
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
        
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderService.getTenderById(tenderId);
            
            if (tender == null) {
                request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_FOUND);
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
                return;
            }
            
            // Check if tender can be published
            if (!Constants.TENDER_STATUS_DRAFT.equals(tender.getStatus())) {
                request.getSession().setAttribute("errorMessage", "Only draft tenders can be published");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            boolean success = tenderService.publishTender(tenderId);
            
            if (success) {
                LOGGER.log(Level.INFO, "Tender published: {0}", tender.getReferenceNumber());
                request.getSession().setAttribute("successMessage", Constants.SUCCESS_TENDER_PUBLISHED);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to publish tender");
            }
            
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }
}