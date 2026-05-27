<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Create Tender" />
</jsp:include>

<!-- ==================================================== -->
<!-- CREATE TENDER - PROCOUREGOV TENDER MANAGEMENT         -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Create New Tender</h1>
        <a href="${pageContext.request.contextPath}/officer/tender/list" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tenders
        </a>
    </div>
    <p class="gov-text-muted">Create a new tender. It will be saved as DRAFT and can be edited before publishing.</p>
</div>

<!-- ERROR MESSAGE DISPLAY -->
<c:if test="${not empty errorMessage}">
    <div class="gov-alert gov-alert-error">
        <span class="gov-alert-icon">⚠️</span>
        <span class="gov-alert-message">${errorMessage}</span>
    </div>
</c:if>

<!-- Tender Creation Form -->
<div class="gov-card">
    <form action="${pageContext.request.contextPath}/officer/tender/create" method="POST" enctype="multipart/form-data" class="gov-form">

        <!-- Title and Category Row -->
        <div class="gov-form-row">
            <div class="gov-form-group">
                <label for="title" class="gov-form-label">
                    Tender Title <span class="gov-required">*</span>
                </label>
                <input type="text" id="title" name="title" 
                       class="gov-form-input ${not empty requestScope.errorMessage && empty requestScope.title ? 'gov-error' : ''}" 
                       value="${requestScope.title}" 
                       placeholder="e.g., Construction of Maseru District Hospital Access Road" 
                       required>
            </div>

            <div class="gov-form-group">
                <label for="category" class="gov-form-label">
                    Category <span class="gov-required">*</span>
                </label>
                <select id="category" name="category" class="gov-form-select" required>
                    <option value="">-- Select Category --</option>
                    <option value="CONSTRUCTION" ${requestScope.category == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                    <option value="ROADS" ${requestScope.category == 'ROADS' ? 'selected' : ''}>Roads</option>
                    <option value="ELECTRICAL" ${requestScope.category == 'ELECTRICAL' ? 'selected' : ''}>Electrical</option>
                    <option value="PLUMBING" ${requestScope.category == 'PLUMBING' ? 'selected' : ''}>Plumbing</option>
                    <option value="GENERAL_SERVICES" ${requestScope.category == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
                </select>
            </div>
        </div>

        <!-- Description Field -->
        <div class="gov-form-group">
            <label for="description" class="gov-form-label">
                Description <span class="gov-required">*</span>
            </label>
            <textarea id="description" name="description" 
                      class="gov-form-textarea" 
                      rows="5" 
                      placeholder="Provide detailed description of the tender requirements, specifications, and scope of work..." 
                      required>${requestScope.description}</textarea>
            <p class="gov-form-hint">Include all relevant details that suppliers need to prepare their bids.</p>
        </div>

        <!-- Value and Deadline Row -->
        <div class="gov-form-row">
            <div class="gov-form-group">
                <label for="estimatedValue" class="gov-form-label">
                    Estimated Value (Maloti) <span class="gov-required">*</span>
                </label>
                <input type="number" id="estimatedValue" name="estimatedValue" 
                       class="gov-form-input" 
                       value="${requestScope.estimatedValue}" 
                       placeholder="e.g., 3500000" 
                       step="0.01" min="0.01" 
                       required>
            </div>

            <div class="gov-form-group">
                <label for="submissionDeadline" class="gov-form-label">
                    Submission Deadline <span class="gov-required">*</span>
                </label>
                <input type="datetime-local" id="submissionDeadline" name="submissionDeadline" 
                       class="gov-form-input" 
                       value="${requestScope.submissionDeadline}" 
                       required>
                <p class="gov-form-hint">Select date and time from the picker</p>
            </div>
        </div>

        <!-- File Upload Field -->
        <div class="gov-form-group">
            <label for="noticeDocument" class="gov-form-label">
                Tender Notice Document (PDF) <span class="gov-required">*</span>
            </label>
            <div class="gov-file-upload" onclick="document.getElementById('noticeDocument').click()">
                <input type="file" id="noticeDocument" name="noticeDocument" accept=".pdf" style="display: none;" required>
                <div class="gov-file-upload-icon">📄</div>
                <div class="gov-file-upload-text">
                    <strong>Click to upload</strong> or drag and drop<br>
                    PDF only, maximum 5MB
                </div>
                <div id="file-name" class="gov-text-muted gov-mt-sm"></div>
            </div>
        </div>

        <!-- Information Note -->
        <div class="gov-form-note">
            <p><strong>Note:</strong> The tender reference number (MPW-YYYY-NNNN) will be automatically generated when you create the tender.</p>
        </div>

        <!-- Form Actions -->
        <div class="gov-form-actions">
            <button type="submit" class="gov-btn gov-btn-primary">
                <span>💾</span> Create Tender (Draft)
            </button>
            <a href="${pageContext.request.contextPath}/officer/tender/list" class="gov-btn gov-btn-secondary">
                Cancel
            </a>
        </div>
        <script>
            // File upload display and validation
            document.getElementById('noticeDocument').addEventListener('change', function (e) {
                var file = e.target.files[0];
                if (file) {
                    var maxSize = 5 * 1024 * 1024; // 5MB in bytes

                    if (file.size > maxSize) {
                        alert('File is too large! Maximum size is 5MB.\n\nYour file: ' + (file.size / (1024 * 1024)).toFixed(2) + 'MB\nMaximum allowed: 5MB\n\nPlease select a smaller file.');
                        this.value = ''; // Clear the selection
                        document.getElementById('file-name').textContent = '';
                        return;
                    }

                    // Check file type
                    var fileName = file.name;
                    var fileExt = fileName.substring(fileName.lastIndexOf('.')).toLowerCase();
                    if (fileExt !== '.pdf') {
                        alert('Invalid file type! Only PDF files are allowed.');
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

<!-- File Upload JavaScript -->
<script>
    document.getElementById('noticeDocument').addEventListener('change', function (e) {
        var fileName = e.target.files[0] ? e.target.files[0].name : 'No file selected';
        document.getElementById('file-name').textContent = fileName;
    });
</script>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />