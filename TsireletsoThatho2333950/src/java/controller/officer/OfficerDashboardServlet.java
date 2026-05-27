package controller.officer;

import dao.implementations.EvaluationDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Tender;
import model.User;
import service.TenderService;
import util.Constants;
import util.SessionValidator;

import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Officer Dashboard Servlet - displays the procurement officer's dashboard.
 * Shows overview statistics and quick actions for tender management.
 * Also shows personal evaluation progress for the logged-in officer.
 * 
 * Required by Module 2: Tender Management.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class OfficerDashboardServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(OfficerDashboardServlet.class.getName());
    
    private TenderService tenderService;
    private EvaluationDAOImpl evaluationDAO;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        evaluationDAO = new EvaluationDAOImpl();
        LOGGER.log(Level.INFO, "OfficerDashboardServlet initialized");
    }
    
    /**
     * Handles GET requests - displays the officer dashboard.
     * 
     * @param request the HttpServletRequest
     * @param response the HttpServletResponse
     * @throws ServletException if a servlet error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        User user = SessionValidator.getLoggedInUser(request);
        int userId = user.getUserId();
        
        // Get system-wide statistics for dashboard cards
        int totalTenders = tenderService.getTotalTenderCount();
        int draftTenders = tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_DRAFT);
        int openTenders = tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_OPEN);
        int closedTenders = tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_CLOSED);
        int underEvaluationTenders = tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_UNDER_EVALUATION);
        int evaluatedTenders = tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_EVALUATED);
        int awardedTenders = tenderService.getTenderCountByStatus(Constants.TENDER_STATUS_AWARDED);
        
        // Get THIS officer's personal evaluation progress
        List<Tender> allEvaluationTenders = tenderService.getTendersForEvaluation();
        
        int personalPendingCount = 0;
        int personalCompletedCount = 0;
        
        for (Tender tender : allEvaluationTenders) {
            if (tender.getBidCount() > 0) {
                boolean hasCompleted = evaluationDAO.hasEvaluatorCompletedTender(tender.getTenderId(), userId);
                if (hasCompleted) {
                    personalCompletedCount++;
                } else {
                    personalPendingCount++;
                }
            }
        }
        
        // Set attributes for JSP
        request.setAttribute("user", user);
        request.setAttribute("totalTenders", totalTenders);
        request.setAttribute("draftTenders", draftTenders);
        request.setAttribute("openTenders", openTenders);
        request.setAttribute("closedTenders", closedTenders);
        request.setAttribute("underEvaluationTenders", underEvaluationTenders);
        request.setAttribute("evaluatedTenders", evaluatedTenders);
        request.setAttribute("awardedTenders", awardedTenders);
        
        // Personal progress
        request.setAttribute("personalPending", personalPendingCount);
        request.setAttribute("personalCompleted", personalCompletedCount);
        
        // Get recent tenders created by this officer
        request.setAttribute("recentTenders", tenderService.getTendersByOfficer(userId));
        
        // Forward to dashboard JSP
        request.getRequestDispatcher(Constants.PAGE_OFFICER_DASHBOARD).forward(request, response);
    }
}