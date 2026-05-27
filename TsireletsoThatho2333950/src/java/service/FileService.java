package service;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import util.Constants;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for file upload and download operations.
 * Handles storing tender notices and bid supporting documents.
 * 
 * CRITICAL: Upload paths are dynamically resolved relative to the project folder.
 * NO hard-coded absolute paths. NO web.xml configuration needed.
 * 
 * Required by Module 2 and Module 3: File upload using Part API.
 * Files stored outside the WAR in configurable directory.
 * 
 * @author Tsireletso Thatho
 * @version 2.0
 */
public class FileService {
    
    private static final Logger LOGGER = Logger.getLogger(FileService.class.getName());
    
    private final String uploadBasePath;
    private final String tendersPath;
    private final String bidsPath;
    
    // Allowed file extensions
    private static final String[] ALLOWED_TENDER_EXTENSIONS = {".pdf"};
    private static final String[] ALLOWED_BID_EXTENSIONS = {".pdf", ".docx", ".doc"};
    
    /**
     * Constructor that dynamically resolves upload paths relative to the project folder.
     * NO web.xml parameters needed - paths are automatically determined.
     * 
     * @param servletContext the ServletContext for getting real paths
     */
    public FileService(ServletContext servletContext) {
        // Get the project root dynamically
        String projectRoot = resolveProjectRoot(servletContext);
        
        // Build upload paths relative to project root
        this.uploadBasePath = projectRoot + File.separator + "uploads";
        this.tendersPath = uploadBasePath + File.separator + "tenders";
        this.bidsPath = uploadBasePath + File.separator + "bids";
        
        LOGGER.log(Level.INFO, "FileService initialized with dynamic paths:");
        LOGGER.log(Level.INFO, "  Project Root: {0}", projectRoot);
        LOGGER.log(Level.INFO, "  Upload Base: {0}", uploadBasePath);
        LOGGER.log(Level.INFO, "  Tenders Path: {0}", tendersPath);
        LOGGER.log(Level.INFO, "  Bids Path: {0}", bidsPath);
        
        // Create directories if they don't exist
        createDirectories();
    }
    
    /**
     * Resolves the project root directory dynamically.
     * Works in both development (NetBeans) and production (deployed WAR).
     * 
     * @param servletContext the ServletContext
     * @return absolute path to the project root
     */
    private String resolveProjectRoot(ServletContext servletContext) {
        // Get the real path of the web application
        String webappPath = servletContext.getRealPath("/");
        
        if (webappPath == null) {
            // Fallback: use system property or current directory
            LOGGER.warning("Could not resolve webapp path, using current directory");
            return System.getProperty("user.dir");
        }
        
        // In NetBeans: webappPath is like: C:\TsireletsoThatho2333950\build\web\
        // We need to go up 2 levels to get to project root: C:\TsireletsoThatho2333950\
        File webappDir = new File(webappPath);
        File buildDir = webappDir.getParentFile();      // build
        File projectDir = buildDir.getParentFile();      // project root
        
        if (projectDir != null && projectDir.exists()) {
            return projectDir.getAbsolutePath();
        }
        
        // Fallback: if structure is different, use webapp parent
        LOGGER.warning("Could not resolve project root, using webapp parent");
        return webappDir.getParent();
    }
    
    /**
     * Constructor with explicit paths (for testing).
     * 
     * @param uploadBasePath the base upload directory
     */
    public FileService(String uploadBasePath) {
        this.uploadBasePath = uploadBasePath;
        this.tendersPath = uploadBasePath + File.separator + "tenders";
        this.bidsPath = uploadBasePath + File.separator + "bids";
        
        LOGGER.log(Level.INFO, "FileService initialized with explicit path: {0}", uploadBasePath);
        createDirectories();
    }
    
    /**
     * Creates the upload directories if they don't exist.
     */
    private void createDirectories() {
        try {
            if (tendersPath != null) {
                Files.createDirectories(Paths.get(tendersPath));
                LOGGER.log(Level.INFO, "Tenders directory created/verified: {0}", tendersPath);
            }
            if (bidsPath != null) {
                Files.createDirectories(Paths.get(bidsPath));
                LOGGER.log(Level.INFO, "Bids directory created/verified: {0}", bidsPath);
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to create upload directories: {0}", e.getMessage());
            throw new RuntimeException("Could not create upload directories", e);
        }
    }
    
    /**
     * Saves an uploaded tender notice file.
     * 
     * @param filePart the Part containing the uploaded file
     * @return the relative file path, or null if save fails
     * @throws IOException if file operations fail
     * @throws IllegalArgumentException if file validation fails
     */
    public String saveTenderNotice(Part filePart) throws IOException, IllegalArgumentException {
        return saveFile(filePart, tendersPath, ALLOWED_TENDER_EXTENSIONS, 
                        Constants.MAX_TENDER_FILE_SIZE, "tender");
    }
    
    /**
     * Saves an uploaded bid supporting document.
     * 
     * @param filePart the Part containing the uploaded file
     * @return the relative file path, or null if save fails
     * @throws IOException if file operations fail
     * @throws IllegalArgumentException if file validation fails
     */
    public String saveBidDocument(Part filePart) throws IOException, IllegalArgumentException {
        return saveFile(filePart, bidsPath, ALLOWED_BID_EXTENSIONS, 
                        Constants.MAX_BID_FILE_SIZE, "bid");
    }
    
    /**
     * Saves an uploaded file to the specified directory.
     * 
     * @param filePart the Part containing the uploaded file
     * @param targetDir the target directory (dynamically resolved)
     * @param allowedExtensions array of allowed file extensions
     * @param maxSize maximum file size in bytes
     * @param fileType type of file for logging
     * @return the relative file path (not absolute path)
     * @throws IOException if file operations fail
     * @throws IllegalArgumentException if file validation fails
     */
    private String saveFile(Part filePart, String targetDir, String[] allowedExtensions,
                            long maxSize, String fileType) throws IOException, IllegalArgumentException {
        
        if (filePart == null || filePart.getSize() == 0) {
            throw new IllegalArgumentException("No file uploaded");
        }
        
        // Validate file size
        if (filePart.getSize() > maxSize) {
            throw new IllegalArgumentException("File size exceeds maximum allowed size (" + 
                (maxSize / (1024 * 1024)) + "MB)");
        }
        
        // Get original filename
        String submittedFileName = getSubmittedFileName(filePart);
        if (submittedFileName == null || submittedFileName.isEmpty()) {
            throw new IllegalArgumentException("Invalid file name");
        }
        
        // Validate file extension
        String fileExtension = getFileExtension(submittedFileName).toLowerCase();
        boolean validExtension = false;
        for (String ext : allowedExtensions) {
            if (ext.equalsIgnoreCase(fileExtension)) {
                validExtension = true;
                break;
            }
        }
        
        if (!validExtension) {
            throw new IllegalArgumentException("Invalid file type. Allowed: " + 
                                               String.join(", ", allowedExtensions));
        }
        
        // Generate unique filename
        String uniqueFileName = generateUniqueFileName(fileExtension);
        Path filePath = Paths.get(targetDir, uniqueFileName);
        
        // Save the file
        try (InputStream input = filePart.getInputStream()) {
            Files.copy(input, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
        
        // Return RELATIVE path only (filename with type prefix) for database storage
        String relativePath = fileType + "s/" + uniqueFileName;
        LOGGER.log(Level.INFO, "{0} file saved: {1} -> {2}", 
                   new Object[]{fileType, relativePath, filePath});
        
        return relativePath;
    }
    
    /**
     * Gets the submitted filename from a Part.
     * 
     * @param part the Part
     * @return the filename
     */
    private String getSubmittedFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }
        
        for (String token : contentDisposition.split(";")) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
    
    /**
     * Gets the file extension from a filename.
     * 
     * @param filename the filename
     * @return the extension including dot, or empty string if none
     */
    private String getFileExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot == -1) {
            return "";
        }
        return filename.substring(lastDot);
    }
    
    /**
     * Generates a unique filename using UUID.
     * 
     * @param extension the file extension
     * @return unique filename
     */
    private String generateUniqueFileName(String extension) {
        return UUID.randomUUID().toString() + extension;
    }
    
    /**
     * Gets the full file path for a stored file.
     * Constructs the absolute path from the dynamically resolved base directory.
     * 
     * @param relativePath the relative path stored in database
     * @return the full Path object
     */
    public Path getFilePath(String relativePath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        
        // Handle paths that may or may not include the subdirectory
        if (relativePath.startsWith("tenders/")) {
            return Paths.get(tendersPath, relativePath.substring(8));
        } else if (relativePath.startsWith("bids/")) {
            return Paths.get(bidsPath, relativePath.substring(5));
        }
        
        // Fallback - use base path
        return Paths.get(uploadBasePath, relativePath);
    }
    
    /**
     * Checks if a file exists.
     * 
     * @param relativePath the relative path stored in database
     * @return true if file exists
     */
    public boolean fileExists(String relativePath) {
        Path path = getFilePath(relativePath);
        return path != null && Files.exists(path);
    }
    
    /**
     * Deletes a file.
     * 
     * @param relativePath the relative path stored in database
     * @return true if deletion was successful
     */
    public boolean deleteFile(String relativePath) {
        Path path = getFilePath(relativePath);
        if (path == null || !Files.exists(path)) {
            return false;
        }
        
        try {
            Files.delete(path);
            LOGGER.log(Level.INFO, "File deleted: {0}", relativePath);
            return true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "Failed to delete file: {0} - {1}", 
                       new Object[]{relativePath, e.getMessage()});
            return false;
        }
    }
    
    /**
     * Gets the MIME type for a file.
     * 
     * @param relativePath the relative path
     * @return the MIME type string
     */
    public String getMimeType(String relativePath) {
        if (relativePath == null) {
            return "application/octet-stream";
        }
        
        String ext = getFileExtension(relativePath).toLowerCase();
        
        switch (ext) {
            case ".pdf":
                return "application/pdf";
            case ".docx":
                return "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            case ".doc":
                return "application/msword";
            default:
                return "application/octet-stream";
        }
    }
    
    /**
     * Gets the upload base path (dynamically resolved).
     * 
     * @return the base upload directory path
     */
    public String getUploadBasePath() {
        return uploadBasePath;
    }
    
    /**
     * Gets the tenders upload path (dynamically resolved).
     * 
     * @return the tenders directory path
     */
    public String getTendersPath() {
        return tendersPath;
    }
    
    /**
     * Gets the bids upload path (dynamically resolved).
     * 
     * @return the bids directory path
     */
    public String getBidsPath() {
        return bidsPath;
    }
}