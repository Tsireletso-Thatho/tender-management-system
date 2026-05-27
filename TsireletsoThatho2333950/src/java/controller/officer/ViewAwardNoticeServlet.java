package controller.officer;

import dao.implementations.AwardDAOImpl;
import dao.implementations.TenderDAOImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Award;
import model.Tender;
import util.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Officer View Award Notice Servlet - displays the award notice for an awarded tender.
 * Shows tender details, winning supplier, awarded value, and justification.
 * 
 * Required by Module 2: Award Notice page generation.
 * 
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class ViewAwardNoticeServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(ViewAwardNoticeServlet.class.getName());
    
    private TenderDAOImpl tenderDAO;
    private AwardDAOImpl awardDAO;
    
    @Override
    public void init() throws ServletException {
        tenderDAO = new TenderDAOImpl();
        awardDAO = new AwardDAOImpl();
        LOGGER.log(Level.INFO, "Officer ViewAwardNoticeServlet initialized");
    }
    
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
            
            // Check if tender is awarded
            if (!Constants.TENDER_STATUS_AWARDED.equals(tender.getStatus())) {
                request.getSession().setAttribute("errorMessage", "This tender has not been awarded yet");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            // Get award details
            Award award = awardDAO.findDetailedByTenderId(tenderId);
            
            if (award == null) {
                request.getSession().setAttribute("errorMessage", "Award information not found");
                response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_VIEW_TENDER + "?id=" + tenderId);
                return;
            }
            
            request.setAttribute("tender", tender);
            request.setAttribute("award", award);
            
            request.getRequestDispatcher(Constants.PAGE_OFFICER_AWARD_NOTICE).forward(request, response);
            
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + Constants.URL_OFFICER_LIST_TENDERS);
        }
    }
}