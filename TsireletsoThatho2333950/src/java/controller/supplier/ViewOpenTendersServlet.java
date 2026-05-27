package controller.supplier;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TenderService;
import util.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View Open Tenders Servlet - displays all open tenders available for bidding.
 * Suppliers can browse and view details of open tenders.
 * 
 * Required by Module 3: Browse open tenders.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ViewOpenTendersServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ViewOpenTendersServlet.class.getName());
    
    private TenderService tenderService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        LOGGER.log(Level.INFO, "ViewOpenTendersServlet initialized");
    }
    
    /**
     * Handles GET requests - displays list of open tenders.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String categoryFilter = request.getParameter("category");
        
        // Get open tenders
        var tenders = tenderService.getOpenTenders();
        
        // Filter by category if specified
        if (categoryFilter != null && !categoryFilter.isEmpty()) {
            tenders = tenders.stream()
                    .filter(t -> categoryFilter.equals(t.getCategory()))
                    .toList();
        }
        
        request.setAttribute("tenders", tenders);
        request.setAttribute("categoryFilter", categoryFilter);
        request.setAttribute("totalCount", tenders.size());
        
        // Forward to view tenders JSP
        request.getRequestDispatcher(Constants.PAGE_SUPPLIER_TENDERS).forward(request, response);
    }
}