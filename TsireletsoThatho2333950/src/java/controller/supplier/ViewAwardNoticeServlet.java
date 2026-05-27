package controller.supplier;

import dao.implementations.AwardDAOImpl;
import dao.implementations.SupplierDAOImpl;
import dao.implementations.TenderDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Award;
import model.Supplier;
import model.Tender;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Supplier View Award Notice Servlet - displays award notice for suppliers.
 * Shows tender details, winning supplier, awarded value, and justification.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ViewAwardNoticeServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewAwardNoticeServlet.class.getName());
    
    private TenderDAOImpl tenderDAO;
    private AwardDAOImpl awardDAO;
    private SupplierDAOImpl supplierDAO;
    
    @Override
    public void init() throws ServletException {
        tenderDAO = new TenderDAOImpl();
        awardDAO = new AwardDAOImpl();
        supplierDAO = new SupplierDAOImpl();
        LOGGER.log(Level.INFO, "Supplier ViewAwardNoticeServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String tenderIdParam = request.getParameter("tenderId");
        
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_MY_BIDS);
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);
            
            if (tender == null) {
                request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_FOUND);
                response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_MY_BIDS);
                return;
            }
            
            // Check if tender is awarded
            if (!Constants.TENDER_STATUS_AWARDED.equals(tender.getStatus())) {
                request.getSession().setAttribute("errorMessage", "This tender has not been awarded yet");
                response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_MY_BIDS);
                return;
            }
            
            // Get award details
            Award award = awardDAO.findDetailedByTenderId(tenderId);
            
            if (award == null) {
                request.getSession().setAttribute("errorMessage", "Award information not found");
                response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_MY_BIDS);
                return;
            }
            
            // Get supplier info
            int userId = SessionValidator.getLoggedInUserId(request);
            Supplier supplier = supplierDAO.findByUserId(userId);
            
            if (supplier == null) {
                response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
                return;
            }
            
            request.setAttribute("tender", tender);
            request.setAttribute("award", award);
            request.setAttribute("supplier", supplier);
            
            request.getRequestDispatcher(Constants.PAGE_SUPPLIER_VIEW_AWARD).forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_SUPPLIER_MY_BIDS);
        }
    }
}