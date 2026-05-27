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
 * View Tenders for Evaluation Servlet - displays tenders available for the evaluator.
 * Shows tenders in CLOSED and UNDER_EVALUATION status that the evaluator can score.
 * 
 * Required by Module 4: View tenders in Closed/Under Evaluation status.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class ViewTendersForEvaluationServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewTendersForEvaluationServlet.class.getName());
    
    private TenderService tenderService;
    private EvaluationCommitteeDAOImpl evaluatorDAO;
    private EvaluationDAOImpl evaluationDAO;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        evaluatorDAO = new EvaluationCommitteeDAOImpl();
        evaluationDAO = new EvaluationDAOImpl();
        LOGGER.log(Level.INFO, "ViewTendersForEvaluationServlet initialized");
    }
    
    /**
     * Handles GET requests - displays list of tenders for evaluation.
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
            response.sendRedirect(request.getContextPath() + Constants.URL_LOGOUT);
            return;
        }
        
        // Get all tenders in evaluation status (CLOSED, UNDER_EVALUATION, EVALUATED)
        List<Tender> allEvaluationTenders = tenderService.getTendersForEvaluation();
        
        // Create a list with personalized status for THIS evaluator
        List<Tender> tendersWithStatus = new ArrayList<>();
        List<Tender> availableTenders = new ArrayList<>();
        
        for (Tender tender : allEvaluationTenders) {
            // Check if THIS SPECIFIC evaluator has completed this tender
            boolean hasCompleted = evaluationDAO.hasEvaluatorCompletedTender(tender.getTenderId(), userId);
            
            // Store the completion status for the JSP
            tender.setEvaluated(hasCompleted);
            tendersWithStatus.add(tender);
            
            // Only add to availableTenders if NOT completed AND has bids
            if (!hasCompleted && tender.getBidCount() > 0) {
                availableTenders.add(tender);
            }
        }
        
        request.setAttribute("allEvaluationTenders", tendersWithStatus);
        request.setAttribute("availableTenders", availableTenders);
        request.setAttribute("evaluator", evaluator);
        
        request.getRequestDispatcher(Constants.PAGE_EVALUATOR_TENDERS).forward(request, response);
    }
}