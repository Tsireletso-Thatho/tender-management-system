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
 * Start Evaluation Servlet - starts the evaluation process for a tender.
 * Changes status from CLOSED to UNDER_EVALUATION.
 * 
 * Required by Module 2: Manual status transition (CLOSED → UNDER_EVALUATION).
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class StartEvaluationServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(StartEvaluationServlet.class.getName());
    
    private TenderService tenderService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        LOGGER.log(Level.INFO, "StartEvaluationServlet initialized");
    }
    
    /**
     * Handles POST requests - starts evaluation for a tender.
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
            
            // Check if tender can be moved to evaluation
            if (!Constants.TENDER_STATUS_CLOSED.equals(tender.getStatus())) {
                request.getSession().setAttribute("errorMessage", "Only closed tenders can be moved to evaluation");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            // Check if there are bids to evaluate - FIXED: use getBidCount() instead of hasBids()
            if (tender.getBidCount() == 0) {
                request.getSession().setAttribute("errorMessage", "Cannot start evaluation - no bids have been submitted");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            boolean success = tenderService.startEvaluation(tenderId);
            
            if (success) {
                LOGGER.log(Level.INFO, "Evaluation started for tender: {0}", tender.getReferenceNumber());
                request.getSession().setAttribute("successMessage", "Evaluation process started successfully");
                response.sendRedirect(request.getContextPath() + "/officer/evaluate?tenderId=" + tenderId);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to start evaluation");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }
}