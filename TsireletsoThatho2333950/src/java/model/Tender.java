package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Tender model class representing a government procurement opportunity. Tenders
 * follow a strict lifecycle: DRAFT → OPEN → CLOSED → UNDER_EVALUATION →
 * EVALUATED → AWARDED.
 *
 * @author Tsireletso Thatho
 * @version 1.0
 */
public class Tender implements Serializable {

    private static final long serialVersionUID = 1L;

    private int tenderId;
    private String referenceNumber;
    private String title;
    private String category;
    private String description;
    private BigDecimal estimatedValue;
    private Timestamp submissionDeadline;
    private String status;
    private transient boolean evaluated;
    private String noticeDocumentPath;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp publishedAt;
    private Timestamp closedAt;
    private Timestamp evaluationStartedAt;
    private Timestamp evaluatedAt;
    private Timestamp awardedAt;

    // Additional fields for display
    private String createdByName;
    private int bidCount;

    // Constants for tender categories
    public static final String CATEGORY_CONSTRUCTION = "CONSTRUCTION";
    public static final String CATEGORY_ROADS = "ROADS";
    public static final String CATEGORY_ELECTRICAL = "ELECTRICAL";
    public static final String CATEGORY_PLUMBING = "PLUMBING";
    public static final String CATEGORY_GENERAL_SERVICES = "GENERAL_SERVICES";

    // Constants for tender statuses
    public static final String STATUS_DRAFT = "DRAFT";
    public static final String STATUS_OPEN = "OPEN";
    public static final String STATUS_CLOSED = "CLOSED";
    public static final String STATUS_UNDER_EVALUATION = "UNDER_EVALUATION";
    public static final String STATUS_EVALUATED = "EVALUATED";
    public static final String STATUS_AWARDED = "AWARDED";

    /**
     * Default constructor required for JavaBean specification.
     */
    public Tender() {
        this.status = STATUS_DRAFT;
    }

    /**
     * Constructs a new Tender with required fields.
     *
     * @param referenceNumber the system-generated reference number
     * (MPW-YYYY-NNNN)
     * @param title the tender title
     * @param category the tender category
     * @param description detailed description
     * @param estimatedValue estimated value in Maloti
     * @param submissionDeadline deadline for bid submission
     * @param createdBy user ID of the creating officer
     */
    public Tender(String referenceNumber, String title, String category,
            String description, BigDecimal estimatedValue,
            Timestamp submissionDeadline, int createdBy) {
        this.referenceNumber = referenceNumber;
        this.title = title;
        this.category = category;
        this.description = description;
        this.estimatedValue = estimatedValue;
        this.submissionDeadline = submissionDeadline;
        this.createdBy = createdBy;
        this.status = STATUS_DRAFT;
    }

    /**
     * Gets the tender ID.
     *
     * @return the unique identifier for this tender
     */
    public int getTenderId() {
        return tenderId;
    }

    /**
     * Sets the tender ID.
     *
     * @param tenderId the unique identifier to set
     */
    public void setTenderId(int tenderId) {
        this.tenderId = tenderId;
    }

    /**
     * Gets the system-generated reference number.
     *
     * @return the reference number in format MPW-YYYY-NNNN
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }

    /**
     * Sets the reference number.
     *
     * @param referenceNumber the reference number to set
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    /**
     * Gets the tender title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the tender title.
     *
     * @param title the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Gets the tender category.
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Sets the tender category.
     *
     * @param category the category to set
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Gets the category display name.
     *
     * @return formatted category name
     */
    public String getCategoryDisplayName() {
        if (category == null) {
            return "";
        }
        return category.replace("_", " ").toLowerCase();
    }

    /**
     * Gets the tender description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the tender description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Gets the estimated value in Maloti.
     *
     * @return the estimated value
     */
    public BigDecimal getEstimatedValue() {
        return estimatedValue;
    }

    /**
     * Sets the estimated value in Maloti.
     *
     * @param estimatedValue the estimated value to set
     */
    public void setEstimatedValue(BigDecimal estimatedValue) {
        this.estimatedValue = estimatedValue;
    }

    /**
     * Gets the submission deadline.
     *
     * @return the submission deadline timestamp
     */
    public Timestamp getSubmissionDeadline() {
        return submissionDeadline;
    }

    /**
     * Sets the submission deadline.
     *
     * @param submissionDeadline the deadline to set
     */
    public void setSubmissionDeadline(Timestamp submissionDeadline) {
        this.submissionDeadline = submissionDeadline;
    }

    /**
     * Gets the current tender status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets the tender status.
     *
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets the notice document file path.
     *
     * @return the file path on server
     */
    public String getNoticeDocumentPath() {
        return noticeDocumentPath;
    }

    /**
     * Sets the notice document file path.
     *
     * @param noticeDocumentPath the file path to set
     */
    public void setNoticeDocumentPath(String noticeDocumentPath) {
        this.noticeDocumentPath = noticeDocumentPath;
    }

    /**
     * Gets the creator's user ID.
     *
     * @return the creating officer's user ID
     */
    public int getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets the creator's user ID.
     *
     * @param createdBy the user ID to set
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets the creation timestamp.
     *
     * @return the creation timestamp
     */
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    /**
     * Sets the creation timestamp.
     *
     * @param createdAt the timestamp to set
     */
    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Gets the last update timestamp.
     *
     * @return the update timestamp
     */
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Sets the last update timestamp.
     *
     * @param updatedAt the timestamp to set
     */
    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Gets the publication timestamp.
     *
     * @return when the tender was published
     */
    public Timestamp getPublishedAt() {
        return publishedAt;
    }

    /**
     * Sets the publication timestamp.
     *
     * @param publishedAt the timestamp to set
     */
    public void setPublishedAt(Timestamp publishedAt) {
        this.publishedAt = publishedAt;
    }

    /**
     * Gets the closing timestamp.
     *
     * @return when the tender closed
     */
    public Timestamp getClosedAt() {
        return closedAt;
    }

    /**
     * Sets the closing timestamp.
     *
     * @param closedAt the timestamp to set
     */
    public void setClosedAt(Timestamp closedAt) {
        this.closedAt = closedAt;
    }

    /**
     * Gets the evaluation start timestamp.
     *
     * @return when evaluation began
     */
    public Timestamp getEvaluationStartedAt() {
        return evaluationStartedAt;
    }

    /**
     * Sets the evaluation start timestamp.
     *
     * @param evaluationStartedAt the timestamp to set
     */
    public void setEvaluationStartedAt(Timestamp evaluationStartedAt) {
        this.evaluationStartedAt = evaluationStartedAt;
    }

    /**
     * Gets the evaluation completion timestamp.
     *
     * @return when evaluation was completed
     */
    public Timestamp getEvaluatedAt() {
        return evaluatedAt;
    }

    /**
     * Sets the evaluation completion timestamp.
     *
     * @param evaluatedAt the timestamp to set
     */
    public void setEvaluatedAt(Timestamp evaluatedAt) {
        this.evaluatedAt = evaluatedAt;
    }

    /**
     * Gets the award timestamp.
     *
     * @return when the tender was awarded
     */
    public Timestamp getAwardedAt() {
        return awardedAt;
    }

    /**
     * Sets the award timestamp.
     *
     * @param awardedAt the timestamp to set
     */
    public void setAwardedAt(Timestamp awardedAt) {
        this.awardedAt = awardedAt;
    }

    /**
     * Gets the creator's name for display.
     *
     * @return the creating officer's name
     */
    public String getCreatedByName() {
        return createdByName;
    }

    /**
     * Sets the creator's name.
     *
     * @param createdByName the name to set
     */
    public void setCreatedByName(String createdByName) {
        this.createdByName = createdByName;
    }

    /**
     * Gets the number of bids submitted for this tender.
     *
     * @return the bid count
     */
    public int getBidCount() {
        return bidCount;
    }

    /**
     * Sets the number of bids.
     *
     * @param bidCount the count to set
     */
    public void setBidCount(int bidCount) {
        this.bidCount = bidCount;
    }

    /**
     * Checks if this tender has been fully evaluated by the current evaluator.
     * This is a transient field used for display purposes only.
     *
     * @return true if evaluated by current evaluator
     */
    public boolean isEvaluated() {
        return evaluated;
    }

    /**
     * Sets whether this tender has been fully evaluated by the current
     * evaluator. This is a transient field used for display purposes only.
     *
     * @param evaluated true if evaluated
     */
    public void setEvaluated(boolean evaluated) {
        this.evaluated = evaluated;
    }

    /**
     * Checks if the tender is editable.
     *
     * @return true if status is DRAFT
     */
    public boolean isEditable() {
        return STATUS_DRAFT.equals(this.status);
    }

    /**
     * Checks if the tender is open for bid submission.
     *
     * @return true if status is OPEN and deadline has not passed
     */
    public boolean isOpenForBidding() {
        if (!STATUS_OPEN.equals(this.status)) {
            return false;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return submissionDeadline != null && now.before(submissionDeadline);
    }

    /**
     * Checks if the submission deadline has passed.
     *
     * @return true if current time is after the deadline
     */
    public boolean isDeadlinePassed() {
        if (submissionDeadline == null) {
            return false;
        }
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return now.after(submissionDeadline);
    }

    /**
     * Checks if the tender is ready for evaluation.
     *
     * @return true if status is CLOSED or UNDER_EVALUATION
     */
    public boolean isReadyForEvaluation() {
        return STATUS_CLOSED.equals(this.status)
                || STATUS_UNDER_EVALUATION.equals(this.status);
    }

    /**
     * Checks if the tender has been awarded.
     *
     * @return true if status is AWARDED
     */
    public boolean isAwarded() {
        return STATUS_AWARDED.equals(this.status);
    }

    /**
     * Returns a string representation of the Tender object.
     *
     * @return string containing tender details
     */
    @Override
    public String toString() {
        return "Tender{"
                + "tenderId=" + tenderId
                + ", referenceNumber='" + referenceNumber + '\''
                + ", title='" + title + '\''
                + ", category='" + category + '\''
                + ", status='" + status + '\''
                + ", estimatedValue=" + estimatedValue
                + '}';
    }
}
