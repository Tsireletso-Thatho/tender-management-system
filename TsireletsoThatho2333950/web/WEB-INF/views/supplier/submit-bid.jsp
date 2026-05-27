<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Submit Bid" />
</jsp:include>

<!-- ==================================================== -->
<!-- SUBMIT BID - PROCOUREGOV TENDER MANAGEMENT            -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Submit Bid</h1>
        <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tender
        </a>
    </div>
</div>

<!-- ERROR MESSAGE DISPLAY -->
<c:if test="${not empty errorMessage}">
    <div class="gov-alert gov-alert-error">
        <span class="gov-alert-icon">⚠️</span>
        <span class="gov-alert-message">${errorMessage}</span>
    </div>
</c:if>

<!-- Tender Summary -->
<div class="gov-card gov-mb-lg">
    <h3>Tender Information</h3>
    <div class="gov-error-detail-item">
        <span class="gov-error-detail-label">Reference:</span>
        <span class="gov-error-detail-value">${tender.referenceNumber}</span>
    </div>
    <div class="gov-error-detail-item">
        <span class="gov-error-detail-label">Title:</span>
        <span class="gov-error-detail-value">${tender.title}</span>
    </div>
    <div class="gov-error-detail-item">
        <span class="gov-error-detail-label">Deadline:</span>
        <span class="gov-error-detail-value">
            <strong><fmt:formatDate value="${tender.submissionDeadline}" pattern="dd MMM yyyy, HH:mm" /></strong>
        </span>
    </div>
</div>

<!-- Bid Submission Form -->
<div class="gov-card">
    <h3>Bid Details</h3>
    <p class="gov-text-muted">All fields are required. Your bid will be sealed until the tender closes.</p>

    <form action="${pageContext.request.contextPath}/supplier/bid/submit" method="POST" enctype="multipart/form-data" class="gov-form">
        <input type="hidden" name="tenderId" value="${tender.tenderId}">

        <!-- Bid Amount -->
        <div class="gov-form-group">
            <label for="bidAmount" class="gov-form-label">
                Bid Amount (Maloti) <span class="gov-required">*</span>
            </label>
            <input type="number" id="bidAmount" name="bidAmount" 
                   class="gov-form-input" 
                   value="${requestScope.bidAmount}" 
                   step="0.01" min="0.01" 
                   placeholder="Enter your bid amount" 
                   required>
            <p class="gov-form-hint">Enter the total amount in Maloti for this tender.</p>
        </div>

        <!-- Timeline -->
        <div class="gov-form-group">
            <label for="timelineDays" class="gov-form-label">
                Proposed Delivery Timeline (Days) <span class="gov-required">*</span>
            </label>
            <input type="number" id="timelineDays" name="timelineDays" 
                   class="gov-form-input" 
                   value="${requestScope.timelineDays}" 
                   min="1" max="365" 
                   placeholder="Enter number of days" 
                   required>
            <p class="gov-form-hint">Estimated number of days to complete the project (1-365 days).</p>
        </div>

        <!-- Compliance Statement -->
        <div class="gov-form-group">
            <label for="complianceStatement" class="gov-form-label">
                Technical Compliance Statement <span class="gov-required">*</span>
            </label>
            <textarea id="complianceStatement" name="complianceStatement" 
                      class="gov-form-textarea" 
                      rows="5" 
                      maxlength="600"
                      placeholder="Describe how your bid meets the technical requirements..." 
                      required>${requestScope.complianceStatement}</textarea>
            <p class="gov-form-hint">
                <span id="char-count">0</span>/600 characters. 
                Explain your technical capability and compliance with tender specifications.
            </p>
        </div>

        <!-- Supporting Document -->
        <div class="gov-form-group">
            <label for="supportingDocument" class="gov-form-label">
                Supporting Document (PDF or DOCX) <span class="gov-required">*</span>
            </label>
            <div class="gov-file-upload" onclick="document.getElementById('supportingDocument').click()">
                <input type="file" id="supportingDocument" name="supportingDocument" 
                       accept=".pdf,.docx,.doc" style="display: none;" required>
                <div class="gov-file-upload-icon">📎</div>
                <div class="gov-file-upload-text">
                    <strong>Click to upload</strong> or drag and drop<br>
                    PDF or DOCX only, maximum 10MB
                </div>
                <div id="file-name" class="gov-text-muted gov-mt-sm"></div>
            </div>
            <p class="gov-form-hint">Upload any supporting documentation (company profile, certifications, etc.).</p>
        </div>

        <!-- Warning -->
        <div class="gov-alert gov-alert-warning">
            <span class="gov-alert-icon">⚠</span>
            <span class="gov-alert-message">
                <strong>Important:</strong> You can only submit one bid per tender. Once submitted, your bid cannot be modified.
                All bids are sealed and will only be opened after the submission deadline.
            </span>
        </div>

        <!-- Form Actions -->
        <div class="gov-form-actions">
            <button type="submit" class="gov-btn gov-btn-primary" onclick="return confirm('Are you sure you want to submit this bid? You cannot modify it after submission.')">
                <span>📝</span> Submit Bid
            </button>
            <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
                Cancel
            </a>
        </div>
        <script>
            // File upload display and validation
            document.getElementById('supportingDocument').addEventListener('change', function (e) {
                var file = e.target.files[0];
                if (file) {
                    var maxSize = 10 * 1024 * 1024; // 10MB in bytes

                    if (file.size > maxSize) {
                        alert('File is too large! Maximum size is 10MB.\n\nYour file: ' + (file.size / (1024 * 1024)).toFixed(2) + 'MB\nMaximum allowed: 10MB\n\nPlease select a smaller file.');
                        this.value = '';
                        document.getElementById('file-name').textContent = '';
                        return;
                    }

                    // Check file type
                    var fileName = file.name;
                    var fileExt = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
                    if (fileExt !== '.pdf' && fileExt !== '.docx' && fileExt !== '.doc') {
                        alert('Invalid file type! Only PDF, DOCX, and DOC files are allowed.');
                        this.value = '';
                        document.getElementById('file-name').textContent = '';
                        return;
                    }

                    document.getElementById('file-name').textContent = fileName;
                }
            });
        </script>
    </form>
</div>

<!-- Character Counter Script -->
<script>
    var textarea = document.getElementById('complianceStatement');
    var charCount = document.getElementById('char-count');

    function updateCharCount() {
        charCount.textContent = textarea.value.length;
    }

    textarea.addEventListener('input', updateCharCount);
    updateCharCount();

    document.getElementById('supportingDocument').addEventListener('change', function (e) {
        var fileName = e.target.files[0] ? e.target.files[0].name : 'No file selected';
        document.getElementById('file-name').textContent = fileName;
    });
</script>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Form submits to SubmitBidServlet.doPost()
    - Server-side validation: tender open, deadline not passed, one bid per supplier
    - Compliance statement max 600 characters
    - Supporting document max 10MB, PDF or DOCX only
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />