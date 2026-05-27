package controller.evaluator;

import dao.implementations.EvaluationCommitteeDAOImpl;
import dao.implementations.EvaluationDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.EvaluationCommittee;
import model.Tender;
import service.TenderService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Evaluator Dashboard Servlet - displays the evaluation committee member's dashboard.
 * Shows tenders available for evaluation and evaluation statistics.
 * 
 * Required by Module 4: Evaluation Committee Member dashboard.
 * 
 * @author Tsireletso Thatho
 * @version 1.3
 */
public class EvaluatorDashboardServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(EvaluatorDashboardServlet.class.getName());
    
    private TenderService tenderService;
    private EvaluationDAOImpl evaluationDAO;
    private EvaluationCommitteeDAOImpl evaluatorDAO;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        evaluationDAO = new EvaluationDAOImpl();
        evaluatorDAO = new EvaluationCommitteeDAOImpl();
        LOGGER.log(Level.INFO, "EvaluatorDashboardServlet initialized");
    }
    
    /**
     * Handles GET requests - displays the evaluator dashboard.
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
        EvaluationCommittee evaluator = evaluatorDAO.findByUserId(userId);
        
        if (evaluator == null) {
            request.getSession().setAttribute("errorMessage", "Evaluator profile not found");
            response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
            return;
        }
        
        // Get all tenders in evaluation stage (CLOSED, UNDER_EVALUATION, EVALUATED)
        List<Tender> allTenders = tenderService.getTendersForEvaluation();
        
        // Count pending and completed tenders for THIS SPECIFIC evaluator
        int pendingCount = 0;
        int completedCount = 0;
        
        for (Tender tender : allTenders) {
            if (tender.getBidCount() > 0) {
                boolean hasCompleted = evaluationDAO.hasEvaluatorCompletedTender(tender.getTenderId(), userId);
                if (hasCompleted) {
                    completedCount++;
                } else {
                    pendingCount++;
                }
            }
        }
        
        // Get tenders available for THIS evaluator (not yet completed and has bids)
        List<Tender> availableTenders = new ArrayList<>();
        for (Tender tender : allTenders) {
            if (tender.getBidCount() > 0) {
                boolean hasCompleted = evaluationDAO.hasEvaluatorCompletedTender(tender.getTenderId(), userId);
                if (!hasCompleted) {
                    availableTenders.add(tender);
                }
            }
        }
        
        request.setAttribute("availableTenders", availableTenders);
        request.setAttribute("tendersForEvaluation", allTenders);
        request.setAttribute("completedEvaluations", completedCount);
        request.setAttribute("pendingEvaluations", pendingCount);
        request.setAttribute("evaluator", evaluator);
        
        // Log statistics for debugging
        LOGGER.log(Level.INFO, "Evaluator {0} dashboard: pending={1}, completed={2}, totalInEvaluation={3}",
                new Object[]{evaluator.getFullName(), pendingCount, completedCount, allTenders.size()});
        
        // Forward to dashboard JSP
        request.getRequestDispatcher(Constants.PAGE_EVALUATOR_DASHBOARD).forward(request, response);
    }
}