package controller.supplier;

import dao.implementations.SupplierDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bid;
import model.Supplier;
import service.BidService;
import service.TenderService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Supplier Dashboard Servlet - displays the supplier's dashboard.
 * Shows open tenders and the supplier's submitted bids with status.
 * 
 * Required by Module 3: Supplier Dashboard showing open tenders and submitted bids.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class SupplierDashboardServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(SupplierDashboardServlet.class.getName());
    
    private TenderService tenderService;
    private BidService bidService;
    private SupplierDAOImpl supplierDAO;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        bidService = new BidService();
        supplierDAO = new SupplierDAOImpl();
        LOGGER.log(Level.INFO, "SupplierDashboardServlet initialized");
    }
    
    /**
     * Handles GET requests - displays the supplier dashboard.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        int userId = SessionValidator.getLoggedInUserId(request);
        Supplier supplier = supplierDAO.findByUserId(userId);
        
        if (supplier == null) {
            request.getSession().setAttribute("errorMessage", "Supplier profile not found");
            response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
            return;
        }
        
        // Get open tenders (limited to 5 most recent)
        request.setAttribute("openTenders", tenderService.getOpenTenders());
        request.setAttribute("openTenderCount", tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_OPEN));
        
        // Get supplier's bids and use size() for count
        List<Bid> myBids = bidService.getBidsBySupplierId(supplier.getSupplierId());
        request.setAttribute("myBids", myBids);
        request.setAttribute("bidCount", myBids.size());
        
        // Get supplier details
        request.setAttribute("supplier", supplier);
        
        // Forward to dashboard JSP
        request.getRequestDispatcher(Constants.PAGE_SUPPLIER_DASHBOARD).forward(request, response);
    }
}