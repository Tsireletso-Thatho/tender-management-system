package service;

import dao.implementations.AwardDAOImpl;
import dao.implementations.BidDAOImpl;
import dao.implementations.EmailLogDAOImpl;
import dao.implementations.SupplierDAOImpl;
import dao.implementations.TenderDAOImpl;
import model.Award;
import model.EmailLog;
import model.Supplier;
import model.Tender;
import util.Constants;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Service class for email notification business logic. Handles sending award
 * notifications to suppliers.
 *
 * Required by Module 6: Supplier Email Notification. Uses JavaMail API for
 * email sending.
 *
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailService.class.getName());

    private final EmailLogDAOImpl emailLogDAO;
    private final TenderDAOImpl tenderDAO;
    private final BidDAOImpl bidDAO;
    private final SupplierDAOImpl supplierDAO;
    private final AwardDAOImpl awardDAO;

    // Email configuration
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SMTP_USERNAME = "";
    private static final String SMTP_PASSWORD = "";
    private static final String FROM_EMAIL = "";
    private static final String FROM_NAME = "ProcureGov - Ministry of Public Works";

    /**
     * Constructor initializes DAO instances.
     */
    public EmailService() {
        this.emailLogDAO = new EmailLogDAOImpl();
        this.tenderDAO = new TenderDAOImpl();
        this.bidDAO = new BidDAOImpl();
        this.supplierDAO = new SupplierDAOImpl();
        this.awardDAO = new AwardDAOImpl();
    }

    /**
     * Sends award notification emails to all suppliers who bid on a tender.
     * Called when a tender is awarded.
     *
     * @param tenderId the awarded tender ID
     * @param contextPath the application context path for generating links
     * @return the number of emails sent successfully
     */
    public int sendAwardNotifications(int tenderId, String contextPath) {
        Tender tender = tenderDAO.findById(tenderId);
        if (tender == null) {
            LOGGER.log(Level.WARNING, "Cannot send notifications - tender not found: ID {0}", tenderId);
            return 0;
        }

        Award award = awardDAO.findByTenderId(tenderId);
        if (award == null) {
            LOGGER.log(Level.WARNING, "Cannot send notifications - award not found for tender: {0}",
                    tender.getReferenceNumber());
            return 0;
        }

        // Get all suppliers who bid on this tender
        List<Integer> biddingSupplierIds = bidDAO.getBiddingSupplierIds(tenderId);
        int winningSupplierId = awardDAO.getWinningSupplierId(tenderId);

        int sentCount = 0;

        for (int supplierId : biddingSupplierIds) {
            Supplier supplier = supplierDAO.findById(supplierId);
            if (supplier == null || supplier.getEmail() == null) {
                continue;
            }

            boolean isWinner = (supplierId == winningSupplierId);
            String outcome = isWinner ? Constants.OUTCOME_WON : Constants.OUTCOME_NOT_WON;

            String subject = buildEmailSubject(tender, isWinner);
            String body = buildEmailBody(tender, award, supplier, isWinner, contextPath);

            boolean sent = sendEmail(supplier.getEmail(), subject, body);

            // Log the email
            EmailLog log = new EmailLog();
            log.setTenderId(tenderId);
            log.setRecipientEmail(supplier.getEmail());
            log.setSubject(subject);
            log.setOutcome(outcome);
            log.setStatus(sent ? Constants.EMAIL_STATUS_SENT : Constants.EMAIL_STATUS_FAILED);
            emailLogDAO.create(log);

            if (sent) {
                sentCount++;
            }
        }

        LOGGER.log(Level.INFO, "Sent {0} award notifications for tender: {1}",
                new Object[]{sentCount, tender.getReferenceNumber()});

        return sentCount;
    }

    /**
     * Builds the email subject line.
     *
     * @param tender the tender
     * @param isWinner whether the recipient won
     * @return the email subject
     */
    private String buildEmailSubject(Tender tender, boolean isWinner) {
        if (isWinner) {
            return "CONGRATULATIONS! Your bid for " + tender.getReferenceNumber() + " has been awarded";
        } else {
            return "Tender Result: " + tender.getReferenceNumber() + " - " + tender.getTitle();
        }
    }

    /**
     * Builds the email body content.
     *
     * @param tender the tender
     * @param award the award details
     * @param supplier the recipient supplier
     * @param isWinner whether the recipient won
     * @param contextPath the application context path
     * @return the email body HTML
     */
    private String buildEmailBody(Tender tender, Award award, Supplier supplier,
            boolean isWinner, String contextPath) {
        StringBuilder body = new StringBuilder();

        body.append("<html><body style='font-family: Arial, sans-serif;'>");
        body.append("<div style='max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd;'>");

        // Header
        body.append("<div style='background-color: #1a2a4a; color: white; padding: 15px; text-align: center;'>");
        body.append("<h2>Ministry of Public Works</h2>");
        body.append("<h3>ProcureGov Tender Management System</h3>");
        body.append("</div>");

        // Content
        body.append("<div style='padding: 20px;'>");
        body.append("<p>Dear ").append(supplier.getCompanyName()).append(",</p>");

        body.append("<p><strong>Tender Reference:</strong> ").append(tender.getReferenceNumber()).append("<br>");
        body.append("<strong>Tender Title:</strong> ").append(tender.getTitle()).append("</p>");

        if (isWinner) {
            body.append("<div style='background-color: #d4edda; border: 1px solid #c3e6cb; padding: 15px; margin: 20px 0;'>");
            body.append("<h3 style='color: #155724; margin: 0;'>🎉 CONGRATULATIONS!</h3>");
            body.append("<p style='color: #155724;'>Your bid has been selected as the winning bid for this tender.</p>");
            body.append("<p><strong>Awarded Value:</strong> M ").append(String.format("%,.2f", award.getAwardedValue())).append("</p>");
            body.append("<p><strong>Award Date:</strong> ").append(award.getAwardedAt()).append("</p>");
            body.append("</div>");

            body.append("<p>The Ministry of Public Works will contact you shortly to finalize the contract details.</p>");
        } else {
            body.append("<div style='background-color: #f8d7da; border: 1px solid #f5c6cb; padding: 15px; margin: 20px 0;'>");
            body.append("<p style='color: #721c24;'>We regret to inform you that your bid was not selected for this tender.</p>");
            body.append("<p style='color: #721c24;'>The contract has been awarded to another supplier.</p>");
            body.append("</div>");

            body.append("<p>We appreciate your participation and encourage you to bid on future tender opportunities.</p>");
        }

        // Link to award notice
        String awardNoticeUrl = contextPath + "/supplier/award?tenderId=" + tender.getTenderId();
        body.append("<p><a href='").append(awardNoticeUrl).append("' style='background-color: #c9a84c; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;'>");
        body.append("View Full Award Notice</a></p>");

        body.append("<p>Thank you for using the ProcureGov Tender Management System.</p>");
        body.append("<p>Ministry of Public Works<br>Kingdom of Lesotho</p>");

        body.append("</div>");

        // Footer
        body.append("<div style='background-color: #f5f5f5; padding: 10px; text-align: center; font-size: 12px; color: #666;'>");
        body.append("This is an automated message from the ProcureGov system. Please do not reply to this email.");
        body.append("</div>");

        body.append("</div>");
        body.append("</body></html>");

        return body.toString();
    }

    /**
     * Sends an email using JavaMail API.
     *
     * @param toEmail the recipient email address
     * @param subject the email subject
     * @param body the email body (HTML)
     * @return true if email was sent successfully
     */
    private boolean sendEmail(String toEmail, String subject, String body) {
        try {
            // Configure mail session
            Properties props = new Properties();
            props.put("mail.smtp.host", SMTP_HOST);
            props.put("mail.smtp.port", SMTP_PORT);
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            props.put("mail.smtp.ssl.trust", SMTP_HOST);

            Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
                @Override
                protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                    return new jakarta.mail.PasswordAuthentication(SMTP_USERNAME, SMTP_PASSWORD);
                }
            });

            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM_EMAIL, FROM_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");

            // Send message
            Transport.send(message);

            LOGGER.log(Level.INFO, "Email sent successfully to: {0}", toEmail);
            return true;

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to {0}: {1}",
                    new Object[]{toEmail, e.getMessage()});
            return false;
        }
    }

    /**
     * Gets email logs for a specific tender.
     *
     * @param tenderId the tender ID
     * @return List of EmailLog objects
     */
    public List<EmailLog> getEmailLogsByTenderId(int tenderId) {
        return emailLogDAO.findDetailedByTenderId(tenderId);
    }

    /**
     * Checks if an email has already been sent to a recipient for a tender.
     *
     * @param tenderId the tender ID
     * @param recipientEmail the recipient's email
     * @return true if an email log exists
     */
    public boolean hasEmailBeenSent(int tenderId, String recipientEmail) {
        return emailLogDAO.hasEmailBeenSent(tenderId, recipientEmail);
    }
}
