package controller.officer;

import dao.implementations.EvaluationDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bid;
import model.Tender;
import service.BidService;
import service.TenderService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View Tender Servlet - displays detailed information about a specific tender.
 * Shows tender details, bids submitted, and available actions based on status.
 * Also shows the logged-in officer's personal evaluation status for each bid.
 * 
 * Required by Module 2: Tender detail view for officers.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class ViewTenderServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewTenderServlet.class.getName());
    
    private TenderService tenderService;
    private BidService bidService;
    private EvaluationDAOImpl evaluationDAO;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        bidService = new BidService();
        evaluationDAO = new EvaluationDAOImpl();
        LOGGER.log(Level.INFO, "ViewTenderServlet initialized");
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
            
            int userId = SessionValidator.getLoggedInUserId(request);
            
            request.setAttribute("tender", tender);
            
            // Get bids for this tender if status is CLOSED or beyond
            if (Constants.TENDER_STATUS_CLOSED.equals(tender.getStatus()) ||
                Constants.TENDER_STATUS_UNDER_EVALUATION.equals(tender.getStatus()) ||
                Constants.TENDER_STATUS_EVALUATED.equals(tender.getStatus()) ||
                Constants.TENDER_STATUS_AWARDED.equals(tender.getStatus())) {
                
                List<Bid> bids = bidService.getBidsByTenderId(tenderId);
                
                // Mark which bids THIS officer has already evaluated
                for (Bid bid : bids) {
                    boolean hasScored = evaluationDAO.hasEvaluatorScored(bid.getBidId(), userId);
                    bid.setEvaluated(hasScored);
                }
                
                request.setAttribute("bids", bids);
                request.setAttribute("bidCount", bids.size());
            }
            
            request.getRequestDispatcher(Constants.PAGE_OFFICER_VIEW_TENDER).forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }
}