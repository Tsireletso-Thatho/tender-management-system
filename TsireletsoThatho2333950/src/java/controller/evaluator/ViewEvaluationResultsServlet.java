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
import service.TenderService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * View Evaluation Results Servlet - displays evaluation results. Shows list of
 * evaluated/awarded tenders, or detailed results for a specific tender.
 *
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class ViewEvaluationResultsServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewEvaluationResultsServlet.class.getName());

    private TenderDAOImpl tenderDAO;
    private BidDAOImpl bidDAO;
    private EvaluationDAOImpl evaluationDAO;
    private EvaluationCommitteeDAOImpl evaluatorDAO;
    private ScoringService scoringService;
    private TenderService tenderService;

    @Override
    public void init() throws ServletException {
        tenderDAO = new TenderDAOImpl();
        bidDAO = new BidDAOImpl();
        evaluationDAO = new EvaluationDAOImpl();
        evaluatorDAO = new EvaluationCommitteeDAOImpl();
        scoringService = new ScoringService();
        tenderService = new TenderService();
        LOGGER.log(Level.INFO, "ViewEvaluationResultsServlet initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int userId = SessionValidator.getLoggedInUserId(request);
        EvaluationCommittee evaluator = evaluatorDAO.findByUserId(userId);

        if (evaluator == null) {
            response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
            return;
        }

        String tenderIdParam = request.getParameter("tenderId");

        // If no tenderId provided, show list of evaluated/awarded tenders
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            showResultsList(request, response, evaluator);
            return;
        }

        // Show detailed results for a specific tender
        showTenderResults(request, response, tenderIdParam, evaluator);
    }

    /**
     * Shows a list of all evaluated and awarded tenders.
     */
    private void showResultsList(HttpServletRequest request, HttpServletResponse response,
            EvaluationCommittee evaluator)
            throws ServletException, IOException {

        // Get tenders that are EVALUATED or AWARDED
        List<Tender> evaluatedTenders = tenderDAO.findByStatus("EVALUATED");
        List<Tender> awardedTenders = tenderDAO.findByStatus("AWARDED");

        // Combine lists
        List<Tender> resultsTenders = new ArrayList<>();
        if (evaluatedTenders != null) {
            resultsTenders.addAll(evaluatedTenders);
        }
        if (awardedTenders != null) {
            resultsTenders.addAll(awardedTenders);
        }

        // Add bid counts
        for (Tender tender : resultsTenders) {
            tender.setBidCount(bidDAO.countByTenderId(tender.getTenderId()));
        }

        // Sort by created date (newest first)
        resultsTenders.sort((t1, t2) -> t2.getCreatedAt().compareTo(t1.getCreatedAt()));

        request.setAttribute("resultsTenders", resultsTenders);
        request.setAttribute("evaluator", evaluator);

        // Use the existing view-results.jsp to show the list
        request.setAttribute("showList", true);
        request.getRequestDispatcher(Constants.PAGE_EVALUATOR_RESULTS).forward(request, response);
    }

    /**
     * Shows detailed results for a specific tender.
     */
    private void showTenderResults(HttpServletRequest request, HttpServletResponse response,
            String tenderIdParam, EvaluationCommittee evaluator)
            throws ServletException, IOException {

        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderDAO.findById(tenderId);

            if (tender == null) {
                request.getSession().setAttribute("errorMessage", Constants.ERROR_TENDER_NOT_FOUND);
                response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_RESULTS);
                return;
            }

            int userId = SessionValidator.getLoggedInUserId(request);

            // Check if evaluator has completed their evaluation
            boolean hasCompleted = evaluationDAO.hasEvaluatorCompletedTender(tenderId, userId);

            // Get all bids for this tender
            List<Bid> bids = bidDAO.findByTenderId(tenderId);

            // Get final scores
            Map<Integer, BigDecimal> finalScores = scoringService.getFinalScores(tenderId);
            List<Integer> rankedBidIds = scoringService.getRankedBids(tenderId);

            // Attach scores and ranks to bids
            for (Bid bid : bids) {
                bid.setFinalScore(finalScores.get(bid.getBidId()));
                bid.setRank(rankedBidIds.indexOf(bid.getBidId()) + 1);
            }

            // Sort bids by rank
            bids.sort((b1, b2) -> Integer.compare(b1.getRank(), b2.getRank()));

            // Get individual evaluator scores
            Map<Integer, List<EvaluationScore>> scoresByBid = new HashMap<>();
            boolean evaluationComplete = evaluationDAO.isTenderEvaluationComplete(tenderId);

            if (hasCompleted || evaluationComplete) {
                for (Bid bid : bids) {
                    List<EvaluationScore> scores = evaluationDAO.findByBidId(bid.getBidId());
                    scoresByBid.put(bid.getBidId(), scores);
                }
            }

            // Get this evaluator's scores
            List<EvaluationScore> myScores = evaluationDAO.findByTenderAndEvaluator(tenderId, userId);
            Map<Integer, BigDecimal> myScoresMap = new HashMap<>();
            for (EvaluationScore score : myScores) {
                myScoresMap.put(score.getBidId(), score.getTechnicalScore());
            }

            request.setAttribute("tender", tender);
            request.setAttribute("bids", bids);
            request.setAttribute("evaluator", evaluator);
            request.setAttribute("hasCompleted", hasCompleted);
            request.setAttribute("evaluationComplete", evaluationComplete);
            request.setAttribute("scoresByBid", scoresByBid);
            request.setAttribute("myScoresMap", myScoresMap);
            request.setAttribute("totalEvaluators", evaluationDAO.getTotalEvaluatorCount());
            request.setAttribute("evaluatorsSubmitted", evaluationDAO.getEvaluatorCountForTender(tenderId));
            request.setAttribute("showList", false);

            request.getRequestDispatcher(Constants.PAGE_EVALUATOR_RESULTS).forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_EVALUATOR_RESULTS);
        }
    }
}
