package controller.officer;

import dao.implementations.AwardDAOImpl;
import dao.implementations.BidDAOImpl;
import dao.implementations.TenderDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Award;
import model.Bid;
import model.Tender;
import service.EmailService;
import service.ScoringService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Award Tender Servlet - awards a tender to the winning supplier.
 * Displays ranked bids and processes award selection.
 * Triggers email notifications to all bidding suppliers.
 * 
 * Required by Module 2: Award contract, select winning supplier.
 * Required by Module 6: Email notification on award.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class AwardTenderServlet extends HttpServlet {
    
    private static final Logger LOGGER = Logger.getLogger(AwardTenderServlet.class.getName());
    
    private TenderDAOImpl tenderDAO;
    private BidDAOImpl bidDAO;
    private AwardDAOImpl awardDAO;
    private ScoringService scoringService;
    private EmailService emailService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderDAO = new TenderDAOImpl();
        bidDAO = new BidDAOImpl();
        awardDAO = new AwardDAOImpl();
        scoringService = new ScoringService();
        emailService = new EmailService();
        LOGGER.log(Level.INFO, "AwardTenderServlet initialized");
    }
    
    /**
     * Handles GET requests - displays the award tender page with ranked bids.
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
            Tender tender = tenderDAO.findById(tenderId);
            
            if (tender == null) {
                request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_FOUND);
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
                return;
            }
            
            // Check if tender is in EVALUATED status
            if (!Constants.TENDER_STATUS_EVALUATED.equals(tender.getStatus())) {
                request.getSession().setAttribute("errorMessage", "Tender must be in EVALUATED status to award");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            // Check if already awarded
            if (awardDAO.isTenderAwarded(tenderId)) {
                Award award = awardDAO.findByTenderId(tenderId);
                request.getSession().setAttribute("errorMessage", "This tender has already been awarded");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            // Get ranked bids
            List<Integer> rankedBidIds = scoringService.getRankedBids(tenderId);
            Map<Integer, BigDecimal> finalScores = scoringService.getFinalScores(tenderId);
            List<Bid> bids = bidDAO.findDetailedBidsByTenderId(tenderId);
            
            // Sort bids by rank and attach scores
            for (Bid bid : bids) {
                bid.setFinalScore(finalScores.get(bid.getBidId()));
                bid.setRank(rankedBidIds.indexOf(bid.getBidId()) + 1);
            }
            
            // Sort bids by rank
            bids.sort((b1, b2) -> Integer.compare(b1.getRank(), b2.getRank()));
            
            request.setAttribute("tender", tender);
            request.setAttribute("bids", bids);
            request.setAttribute("lowestBid", bidDAO.getLowestBidAmount(tenderId));
            
            request.getRequestDispatcher(Constants.PAGE_OFFICER_AWARD_TENDER).forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }
    
    /**
     * Handles POST requests - processes the award selection.
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
        String winningBidIdParam = request.getParameter("winningBidId");
        String awardedValueStr = request.getParameter("awardedValue");
        String justification = request.getParameter("justification");
        
        if (tenderIdParam == null || winningBidIdParam == null) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            int winningBidId = Integer.parseInt(winningBidIdParam);
            BigDecimal awardedValue = new BigDecimal(awardedValueStr);
            int awardedBy = SessionValidator.getLoggedInUserId(request);
            
            // Validate justification
            if (justification == null || justification.trim().isEmpty()) {
                request.getSession().setAttribute("errorMessage", "Award justification is required");
                response.sendRedirect(request.getContextPath() + "/officer/tender/award?id=" + tenderId);
                return;
            }
            
            // Create award
            Award award = new Award();
            award.setTenderId(tenderId);
            award.setWinningBidId(winningBidId);
            award.setAwardedValue(awardedValue);
            award.setJustification(justification.trim());
            award.setAwardedBy(awardedBy);
            
            int awardId = awardDAO.create(award);
            
            if (awardId != -1) {
                // Update tender status to AWARDED
                tenderDAO.award(tenderId);
                
                // Update bid outcomes
                bidDAO.updateBidOutcomes(tenderId, winningBidId);
                
                Tender tender = tenderDAO.findById(tenderId);
                LOGGER.log(Level.INFO, "Tender awarded: {0} to bid ID {1} by officer ID {2}", 
                           new Object[]{tender.getReferenceNumber(), winningBidId, awardedBy});
                
                // Send email notifications (Module 6)
                String contextPath = request.getContextPath();
                int emailsSent = emailService.sendAwardNotifications(tenderId, contextPath);
                LOGGER.log(Level.INFO, "Sent {0} award notification emails for tender {1}", 
                           new Object[]{emailsSent, tender.getReferenceNumber()});
                
                request.getSession().setAttribute("successMessage", Constants.SUCCESS_TENDER_AWARDED);
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
            } else {
                request.getSession().setAttribute("errorMessage", "Failed to award tender");
                response.sendRedirect(request.getContextPath() + "/officer/tender/award?id=" + tenderId);
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }
}