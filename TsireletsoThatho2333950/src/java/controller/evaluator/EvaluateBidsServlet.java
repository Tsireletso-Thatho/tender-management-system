package controller.evaluator;

import dao.implementations.BidDAOImpl;
import dao.implementations.EvaluationCommitteeDAOImpl;
import dao.implementations.EvaluationDAOImpl;
import dao.implementations.TenderDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Bid;
import model.EvaluationCommittee;
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

public class EvaluateBidsServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EvaluateBidsServlet.class.getName());
    
    private TenderDAOImpl tenderDAO;
    private BidDAOImpl bidDAO;
    private EvaluationDAOImpl evaluationDAO;
    private EvaluationCommitteeDAOImpl evaluatorDAO;
    private ScoringService scoringService;
    
    @Override
    public void init() throws ServletException {
        tenderDAO = new TenderDAOImpl();
        bidDAO = new BidDAOImpl();
        evaluationDAO = new EvaluationDAOImpl();
        evaluatorDAO = new EvaluationCommitteeDAOImpl();
        scoringService = new ScoringService();
        LOGGER.log(Level.INFO, "EvaluateBidsServlet initialized - FIXED VERSION");
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String tenderIdParam = request.getParameter("tenderId");
        
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_TENDERS);
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);
            
            if (tender == null) {
                request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_FOUND);
                response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_TENDERS);
                return;
            }
            
            if (!tender.isReadyForEvaluation()) {
                request.getSession().setAttribute("errorMessage", "This tender is not ready for evaluation");
                response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_TENDERS);
                return;
            }
            
            // Use userId to find logged user
            int userId = SessionValidator.getLoggedInUserId(request);
            EvaluationCommittee evaluator = evaluatorDAO.findByUserId(userId);
            
            if (evaluator == null) {
                response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
                return;
            }
            
            List<Bid> bids = bidDAO.findByTenderId(tenderId);
            
            // Check scores using userId
            for (Bid bid : bids) {
                boolean hasScored = evaluationDAO.hasEvaluatorScored(bid.getBidId(), userId);
                bid.setEvaluated(hasScored);
            }
            
            boolean allScored = true;
            for (Bid bid : bids) {
                if (!bid.isEvaluated()) {
                    allScored = false;
                    break;
                }
            }
            
            // Get scores using userId
            List<EvaluationScore> myScores = evaluationDAO.findByTenderAndEvaluator(tenderId, userId);
            Map<Integer, BigDecimal> myScoresMap = new HashMap<>();
            for (EvaluationScore score : myScores) {
                myScoresMap.put(score.getBidId(), score.getTechnicalScore());
            }
            
            request.setAttribute("tender", tender);
            request.setAttribute("bids", bids);
            request.setAttribute("evaluator", evaluator);
            request.setAttribute("allScored", allScored);
            request.setAttribute("myScoresMap", myScoresMap);
            request.setAttribute("lowestBid", bidDAO.getLowestBidAmount(tenderId));
            request.setAttribute("shortestTimeline", bidDAO.getShortestTimeline(tenderId));
            
            request.getRequestDispatcher(Constants.PAGE_EVALUATOR_EVALUATE).forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_TENDERS);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String tenderIdParam = request.getParameter("tenderId");
        
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_TENDERS);
            return;
        }
        
        int tenderId = Integer.parseInt(tenderIdParam);
        
        // Use userId for evaluation
        int userId = SessionValidator.getLoggedInUserId(request);
        EvaluationCommittee evaluator = evaluatorDAO.findByUserId(userId);
        
        if (evaluator == null) {
            response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
            return;
        }
        
        List<Bid> bids = bidDAO.findByTenderId(tenderId);
        int scoresSubmitted = 0;
        
        for (Bid bid : bids) {
            String technicalScoreParam = request.getParameter("technicalScore_" + bid.getBidId());
            
            if (technicalScoreParam == null || technicalScoreParam.trim().isEmpty()) {
                continue;
            }
            
            // Check using userId
            if (evaluationDAO.hasEvaluatorScored(bid.getBidId(), userId)) {
                continue;
            }
            
            try {
                BigDecimal technicalScore = new BigDecimal(technicalScoreParam);
                
                if (!scoringService.isValidTechnicalScore(technicalScore)) {
                    continue;
                }
                
                // Submit with userId
                int scoreId = scoringService.submitEvaluation(
                    tenderId, 
                    bid.getBidId(), 
                    userId,
                    technicalScore
                );
                
                if (scoreId != -1) {
                    scoresSubmitted++;
                    LOGGER.log(Level.INFO, "Score saved: userId={0}, bidId={1}, score={2}", 
                               new Object[]{userId, bid.getBidId(), technicalScore});
                }
                
            } catch (NumberFormatException e) {
                LOGGER.log(Level.WARNING, "Invalid score format for bid {0}", bid.getBidId());
            }
        }
        
        if (scoresSubmitted > 0) {
            LOGGER.log(Level.INFO, "Evaluator (userId={0}) submitted {1} scores for tender ID {2}", 
                       new Object[]{userId, scoresSubmitted, tenderId});
            request.getSession().setAttribute("successMessage", 
                "Successfully submitted " + scoresSubmitted + " evaluation score(s).");
        } else {
            request.getSession().setAttribute("errorMessage", "No scores were submitted. Please enter valid scores.");
        }
        
        boolean completed = evaluationDAO.hasEvaluatorCompletedTender(tenderId, userId);
        
        if (completed) {
            response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_RESULTS + "?tenderId=" + tenderId);
        } else {
            response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_EVALUATE + "?tenderId=" + tenderId);
        }
    }
}