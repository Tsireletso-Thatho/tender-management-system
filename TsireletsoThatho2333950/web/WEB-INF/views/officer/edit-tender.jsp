<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Edit Tender" />
</jsp:include>

<!-- ==================================================== -->
<!-- EDIT TENDER - PROCOUREGOV TENDER MANAGEMENT           -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- Only DRAFT tenders can be edited                      -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Edit Tender: ${tender.referenceNumber}</h1>
        <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tender
        </a>
    </div>
    <p class="gov-text-muted">Edit tender details. Only tenders in DRAFT status can be edited.</p>
</div>

<!-- Warning if not in DRAFT status -->
<c:if test="${tender.status != 'DRAFT'}">
    <div class="gov-alert gov-alert-warning">
        <span class="gov-alert-icon">⚠</span>
        <span class="gov-alert-message">This tender is not in DRAFT status and cannot be edited.</span>
    </div>
</c:if>

<!-- Edit Tender Form -->
<div class="gov-card">
    <form action="${pageContext.request.contextPath}/officer/tender/edit" method="POST" enctype="multipart/form-data" class="gov-form">
        <input type="hidden" name="tenderId" value="${tender.tenderId}">

        <!-- Reference Number (Read-only) -->
        <div class="gov-form-row">
            <div class="gov-form-group">
                <label class="gov-form-label">Reference Number</label>
                <input type="text" class="gov-form-input" value="${tender.referenceNumber}" disabled>
                <p class="gov-form-hint">Reference number cannot be changed</p>
            </div>

            <div class="gov-form-group">
                <label for="category" class="gov-form-label">
                    Category <span class="gov-required">*</span>
                </label>
                <select id="category" name="category" class="gov-form-select" required ${tender.status != 'DRAFT' ? 'disabled' : ''}>
                    <option value="CONSTRUCTION" ${tender.category == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                    <option value="ROADS" ${tender.category == 'ROADS' ? 'selected' : ''}>Roads</option>
                    <option value="ELECTRICAL" ${tender.category == 'ELECTRICAL' ? 'selected' : ''}>Electrical</option>
                    <option value="PLUMBING" ${tender.category == 'PLUMBING' ? 'selected' : ''}>Plumbing</option>
                    <option value="GENERAL_SERVICES" ${tender.category == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
                </select>
            </div>
        </div>

        <!-- Title Field -->
        <div class="gov-form-group">
            <label for="title" class="gov-form-label">
                Tender Title <span class="gov-required">*</span>
            </label>
            <input type="text" id="title" name="title" 
                   class="gov-form-input" 
                   value="${tender.title}" 
                   required ${tender.status != 'DRAFT' ? 'readonly' : ''}>
        </div>

        <!-- Description Field -->
        <div class="gov-form-group">
            <label for="description" class="gov-form-label">
                Description <span class="gov-required">*</span>
            </label>
            <textarea id="description" name="description" 
                      class="gov-form-textarea" 
                      rows="5" 
                      required ${tender.status != 'DRAFT' ? 'readonly' : ''}>${tender.description}</textarea>
        </div>

        <!-- Value and Deadline Row -->
        <div class="gov-form-row">
            <div class="gov-form-group">
                <label for="estimatedValue" class="gov-form-label">
                    Estimated Value (Maloti) <span class="gov-required">*</span>
                </label>
                <input type="number" id="estimatedValue" name="estimatedValue" 
                       class="gov-form-input" 
                       value="${tender.estimatedValue}" 
                       step="0.01" min="0.01" 
                       required ${tender.status != 'DRAFT' ? 'readonly' : ''}>
            </div>

            <div class="gov-form-group">
                <label for="submissionDeadline" class="gov-form-label">
                    Submission Deadline <span class="gov-required">*</span>
                </label>
                <input type="datetime-local" id="submissionDeadline" name="submissionDeadline" 
                       class="gov-form-input" 
                       value="<fmt:formatDate value='${tender.submissionDeadline}' pattern='yyyy-MM-dd\'T\'HH:mm' />" 
                       required ${tender.status != 'DRAFT' ? 'readonly' : ''}>
                <p class="gov-form-hint">Select date and time from the picker</p>
            </div>
        </div>

        <!-- Current Document Display -->
        <div class="gov-form-group">
            <label class="gov-form-label">Current Tender Notice</label>
            <c:if test="${not empty tender.noticeDocumentPath}">
                <div class="gov-flex gov-items-center gov-gap-md">
                    <span>📄 Document uploaded</span>
                    <a href="${pageContext.request.contextPath}/tender/download?id=${tender.tenderId}" 
                       class="gov-btn gov-btn-outline gov-btn-sm" target="_blank">
                        View Current Document
                    </a>
                </div>
            </c:if>
            <c:if test="${empty tender.noticeDocumentPath}">
                <p class="gov-text-muted">No document uploaded.</p>
            </c:if>
        </div>

        <!-- New Document Upload (Optional) -->
        <div class="gov-form-group">
            <label for="noticeDocument" class="gov-form-label">
                Upload New Notice Document (Optional)
            </label>
            <div class="gov-file-upload" onclick="document.getElementById('noticeDocument').click()">
                <input type="file" id="noticeDocument" name="noticeDocument" accept=".pdf" style="display: none;" ${tender.status != 'DRAFT' ? 'disabled' : ''}>
                <div class="gov-file-upload-icon">📄</div>
                <div class="gov-file-upload-text">
                    <strong>Click to upload new document</strong><br>
                    PDF only, maximum 5MB<br>
                    Leave empty to keep current document
                </div>
                <div id="file-name" class="gov-text-muted gov-mt-sm"></div>
            </div>
        </div>

        <!-- Save Button (only if DRAFT) -->
        <c:if test="${tender.status == 'DRAFT'}">
            <div class="gov-form-actions">
                <button type="submit" class="gov-btn gov-btn-primary">
                    <span>💾</span> Save Changes
                </button>
                <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
                    Cancel
                </a>
            </div>
        </c:if>
    </form>
</div>

<!-- Publish Section (only if DRAFT) -->
<c:if test="${tender.status == 'DRAFT'}">
    <div class="gov-card gov-mt-lg">
        <h3>Publish Tender</h3>
        <p>Once published, the tender will be visible to all suppliers and can no longer be edited.</p>
        <form action="${pageContext.request.contextPath}/officer/tender/publish" method="POST" onsubmit="return confirmPublish('Are you sure you want to publish this tender? It will become visible to all suppliers and cannot be edited.')">
            <input type="hidden" name="tenderId" value="${tender.tenderId}">
            <button type="submit" class="gov-btn gov-btn-gold">
                <span>📢</span> Publish Tender
            </button>
            <script>
                // File upload display and validation
                document.getElementById('noticeDocument').addEventListener('change', function (e) {
                    var file = e.target.files[0];
                    if (file) {
                        var maxSize = 5 * 1024 * 1024; // 5MB in bytes

                        if (file.size > maxSize) {
                            alert('File is too large! Maximum size is 5MB.\n\nYour file: ' + (file.size / (1024 * 1024)).toFixed(2) + 'MB\nMaximum allowed: 5MB\n\nPlease select a smaller file.');
                            this.value = '';
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
</c:if>

<script>
    document.getElementById('noticeDocument').addEventListener('change', function (e) {
        var fileName = e.target.files[0] ? e.target.files[0].name : 'No file selected';
        document.getElementById('file-name').textContent = fileName;
    });
</script>

<c:if test="${false}">
    DEVELOPER NOTES:
    - Only DRAFT tenders can be edited
    - Fields become readonly if tender is not DRAFT
    - Publish button triggers status transition DRAFT → OPEN
    - Form submits to EditTenderServlet.doPost()
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />