package controller.supplier;

import dao.implementations.SupplierDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Supplier;
import service.BidService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View My Bids Servlet - displays all bids submitted by the logged-in supplier.
 * Shows bid status and allows viewing award notices for awarded tenders.
 * 
 * Required by Module 3: Track their own bid status, view award notices.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ViewMyBidsServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ViewMyBidsServlet.class.getName());
    
    private BidService bidService;
    private SupplierDAOImpl supplierDAO;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        bidService = new BidService();
        supplierDAO = new SupplierDAOImpl();
        LOGGER.log(Level.INFO, "ViewMyBidsServlet initialized");
    }
    
    /**
     * Handles GET requests - displays the supplier's bids.
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
            response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
            return;
        }
        
        // Get all bids by this supplier
        var bids = bidService.getBidsBySupplierId(supplier.getSupplierId());
        
        request.setAttribute("bids", bids);
        request.setAttribute("bidCount", bids.size());
        request.setAttribute("supplier", supplier);
        
        // Forward to my bids JSP
        request.getRequestDispatcher(Constants.PAGE_SUPPLIER_MY_BIDS).forward(request, response);
    }
}