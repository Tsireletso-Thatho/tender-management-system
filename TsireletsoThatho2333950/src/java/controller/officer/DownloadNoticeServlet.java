package controller.officer;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Tender;
import service.FileService;
import service.TenderService;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Download Notice Servlet - serves tender notice PDF files.
 * Supports both inline viewing (default) and force download.
 * 
 * Required by Module 2: File download via dedicated Servlet.
 * 
 * @author Tsireletso Thatho
 * @version 1.1
 */
public class DownloadNoticeServlet extends HttpServlet {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = Logger.getLogger(DownloadNoticeServlet.class.getName());
    
    private TenderService tenderService;
    private FileService fileService;
    
    /**
     * Initializes the servlet.
     */
    @Override
    public void init() throws ServletException {
        tenderService = new TenderService();
        fileService = new FileService(getServletContext());
        LOGGER.log(Level.INFO, "DownloadNoticeServlet initialized");
    }
    
    /**
     * Handles GET requests - streams the tender notice file.
     * Supports both inline viewing (default) and force download.
     * 
     * URL parameters:
     *   - id: tender ID (required)
     *   - download: if "true", forces download instead of inline viewing
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
        String downloadParam = request.getParameter("download");
        
        // Check if force download is requested
        boolean forceDownload = "true".equalsIgnoreCase(downloadParam);
        
        if (tenderIdParam == null || tenderIdParam.isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Tender ID is required");
            return;
        }
        
        try {
            int tenderId = Integer.parseInt(tenderIdParam);
            Tender tender = tenderService.getTenderById(tenderId);
            
            if (tender == null || tender.getNoticeDocumentPath() == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Tender notice not found");
                return;
            }
            
            Path filePath = fileService.getFilePath(tender.getNoticeDocumentPath());
            
            if (filePath == null || !Files.exists(filePath)) {
                LOGGER.log(Level.WARNING, "File not found: {0}", tender.getNoticeDocumentPath());
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
                return;
            }
            
            // Set response headers
            String mimeType = fileService.getMimeType(tender.getNoticeDocumentPath());
            response.setContentType(mimeType);
            response.setContentLengthLong(Files.size(filePath));
            
            // Determine Content-Disposition based on download parameter
            String filename = tender.getReferenceNumber().replace("/", "-") + "_notice.pdf";
            
            if (forceDownload) {
                // Force download - saves to computer's Downloads folder
                response.setHeader("Content-Disposition", 
                    "attachment; filename=\"" + filename + "\"");
                LOGGER.log(Level.INFO, "Serving file as download: {0}", filename);
            } else {
                // Inline viewing - opens in browser tab
                response.setHeader("Content-Disposition", 
                    "inline; filename=\"" + filename + "\"");
                LOGGER.log(Level.INFO, "Serving file inline: {0}", filename);
            }
            
            // Stream the file
            try (OutputStream out = response.getOutputStream()) {
                Files.copy(filePath, out);
                out.flush();
            }
            
            LOGGER.log(Level.FINE, "File served: {0} for tender {1}", 
                       new Object[]{tender.getNoticeDocumentPath(), tender.getReferenceNumber()});
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid tender ID");
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error serving file: {0}", e.getMessage());
            if (!response.isCommitted()) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error serving file");
            }
        }
    }
}