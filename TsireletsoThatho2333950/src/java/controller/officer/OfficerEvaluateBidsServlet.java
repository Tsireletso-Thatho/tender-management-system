package controller.officer;

import dao.implementations.BidDAOImpl;
import dao.implementations.EvaluationDAOImpl;
import dao.implementations.TenderDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bid;
import model.EvaluationScore;
import model.Tender;
import service.ScoringService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Officer Evaluate Bids Servlet - handles bid scoring by Procurement Officers.
 * Officers participate in evaluation as part of the committee.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class OfficerEvaluateBidsServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OfficerEvaluateBidsServlet.class.getName());
    
    private TenderDAOImpl tenderDAO;
    private BidDAOImpl bidDAO;
    private EvaluationDAOImpl evaluationDAO;
    private ScoringService scoringService;
    
    @Override
    public void init() throws ServletException {
        tenderDAO = new TenderDAOImpl();
        bidDAO = new BidDAOImpl();
        evaluationDAO = new EvaluationDAOImpl();
        scoringService = new ScoringService();
        LOGGER.log(Level.INFO, "OfficerEvaluateBidsServlet initialized");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String tenderIdParam = request.getParameter("tenderId");
        
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
            
            // Verify tender is in evaluation status
            if (!tender.isReadyForEvaluation()) {
                request.getSession().setAttribute("errorMessage", "This tender is not ready for evaluation");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            int userId = SessionValidator.getLoggedInUserId(request);
            
            // Get all bids for this tender
            List<Bid> bids = bidDAO.findByTenderId(tenderId);
            
            // Check which bids this officer has already scored
            for (Bid bid : bids) {
                boolean hasScored = evaluationDAO.hasEvaluatorScored(bid.getBidId(), userId);
                bid.setEvaluated(hasScored);
            }
            
            // Check if all bids are already scored by this officer
            boolean allScored = true;
            for (Bid bid : bids) {
                if (!bid.isEvaluated()) {
                    allScored = false;
                    break;
                }
            }
            
            // Get this officer's existing scores
            List<EvaluationScore> myScores = evaluationDAO.findByTenderAndEvaluator(tenderId, userId);
            Map<Integer, BigDecimal> myScoresMap = new HashMap<>();
            for (EvaluationScore score : myScores) {
                myScoresMap.put(score.getBidId(), score.getTechnicalScore());
            }
            
            request.setAttribute("tender", tender);
            request.setAttribute("bids", bids);
            request.setAttribute("allScored", allScored);
            request.setAttribute("myScoresMap", myScoresMap);
            request.setAttribute("lowestBid", bidDAO.getLowestBidAmount(tenderId));
            request.setAttribute("shortestTimeline", bidDAO.getShortestTimeline(tenderId));
            
            request.getRequestDispatcher(Constants.PAGE_OFFICER_EVALUATE_BIDS).forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String tenderIdParam = request.getParameter("tenderId");
        
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
            return;
        }
        
        int tenderId = Integer.parseInt(tenderIdParam);
        int userId = SessionValidator.getLoggedInUserId(request);
        
        // Get all bids for this tender
        List<Bid> bids = bidDAO.findByTenderId(tenderId);
        int scoresSubmitted = 0;
        
        // Process each bid's technical score
        for (Bid bid : bids) {
            String technicalScoreParam = request.getParameter("technicalScore_" + bid.getBidId());
            
            if (technicalScoreParam == null || technicalScoreParam.trim().isEmpty()) {
                continue;
            }
            
            // Check if already scored
            if (evaluationDAO.hasEvaluatorScored(bid.getBidId(), userId)) {
                continue;
            }
            
            try {
                BigDecimal technicalScore = new BigDecimal(technicalScoreParam);
                
                // Validate score range
                if (!scoringService.isValidTechnicalScore(technicalScore)) {
                    continue;
                }
                
                // Submit evaluation
                int scoreId = scoringService.submitEvaluation(
                    tenderId, 
                    bid.getBidId(), 
                    userId, 
                    technicalScore
                );
                
                if (scoreId != -1) {
                    scoresSubmitted++;
                }
                
            } catch (NumberFormatException e) {
                // Skip invalid scores
            }
        }
        
        if (scoresSubmitted > 0) {
            LOGGER.log(Level.INFO, "Officer {0} submitted {1} scores for tender ID {2}", 
                       new Object[]{userId, scoresSubmitted, tenderId});
            request.getSession().setAttribute("successMessage", 
                "Successfully submitted " + scoresSubmitted + " evaluation score(s).");
        }
        
        // Check if officer has completed all scores
        boolean completed = evaluationDAO.hasEvaluatorCompletedTender(tenderId, userId);
        
        if (completed) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
        } else {
            response.sendRedirect(request.getContextPath() + "/officer/evaluate?tenderId=" + tenderId);
        }
    }
}