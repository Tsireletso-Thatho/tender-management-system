package controller.supplier;

import dao.implementations.SupplierDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Supplier;
import model.Tender;
import service.BidService;
import service.TenderService;
import util.Constants;
import util.DateUtils;
import util.SessionValidator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View Tender Detail Servlet - displays detailed information about a specific tender.
 * Shows tender details and Submit Bid button if tender is open and supplier hasn't bid.
 * 
 * Required by Module 3: Tender Detail page with Submit Bid button.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ViewTenderDetailServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(ViewTenderDetailServlet.class.getName());
    
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
        LOGGER.log(Level.INFO, "ViewTenderDetailServlet initialized");
    }
    
    /**
     * Handles GET requests - displays tender details.
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
            
            // Check if supplier has already bid
            boolean hasBid = bidService.hasSupplierBid(tenderId, supplier.getSupplierId());
            
            // Check if tender is open for bidding
            boolean canBid = Constants.TENDER_STATUS_OPEN.equals(tender.getStatus()) 
                          && !DateUtils.isDeadlinePassed(tender.getSubmissionDeadline())
                          && !hasBid;
            
            request.setAttribute("tender", tender);
            request.setAttribute("hasBid", hasBid);
            request.setAttribute("canBid", canBid);
            request.setAttribute("deadlinePassed", DateUtils.isDeadlinePassed(tender.getSubmissionDeadline()));
            
            // If supplier has bid, show their bid details
            if (hasBid) {
                request.setAttribute("myBid", bidService.getSupplierBidForTender(tenderId, supplier.getSupplierId()));
            }
            
            request.getRequestDispatcher(Constants.PAGE_SUPPLIER_TENDER_DETAIL).forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_TENDERS);
        }
    }
}