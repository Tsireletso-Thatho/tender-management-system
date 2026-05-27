package controller.officer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Tender;
import service.TenderService;
import util.Constants;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * List Tenders Servlet - displays a list of all tenders.
 * Supports filtering by status and category using JSTL.
 * 
 * Required by Module 2: Tender List page with filtering.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ListTendersServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ListTendersServlet.class.getName());
    
    private TenderService tenderService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        LOGGER.log(Level.INFO, "ListTendersServlet initialized");
    }
    
    /**
     * Handles GET requests - displays filtered list of tenders.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String statusFilter = request.getParameter("status");
        String categoryFilter = request.getParameter("category");
        
        // Get filtered tenders
        List<Tender> tenders = tenderService.getTendersByFilters(statusFilter, categoryFilter);
        
        // Set attributes for JSP
        request.setAttribute("tenders", tenders);
        request.setAttribute("statusFilter", statusFilter);
        request.setAttribute("categoryFilter", categoryFilter);
        request.setAttribute("totalCount", tenders.size());
        
        // Get statistics
        request.setAttribute("draftCount", tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_DRAFT));
        request.setAttribute("openCount", tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_OPEN));
        request.setAttribute("closedCount", tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_CLOSED));
        request.setAttribute("evaluationCount", tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_UNDER_EVALUATION));
        request.setAttribute("evaluatedCount", tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_EVALUATED));
        request.setAttribute("awardedCount", tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_AWARDED));
        
        // Forward to list JSP
        request.getRequestDispatcher(Constants.PAGE_OFFICER_LIST_TENDERS).forward(request, response);
    }
}