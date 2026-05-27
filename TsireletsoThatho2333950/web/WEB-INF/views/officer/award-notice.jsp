<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Award Notice" />
</jsp:include>

<!-- ==================================================== -->
<!-- AWARD NOTICE - PROCOUREGOV TENDER MANAGEMENT          -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Award Notice</h1>
        <div class="gov-flex gov-gap-md">
            <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
                <span>←</span> Back to Tender
            </a>
            <button onclick="window.print()" class="gov-btn gov-btn-outline">
                <span>🖨️</span> Print
            </button>
        </div>
    </div>
</div>

<!-- Award Notice Card -->
<div class="gov-card">
    <div class="gov-award-header">
        <div class="gov-award-title">
            <span class="gov-badge gov-badge-awarded gov-mb-md">AWARDED</span>
            <h2>Contract Award Notice</h2>
            <p class="gov-text-muted">Ministry of Public Works - Kingdom of Lesotho</p>
        </div>
    </div>

    <div class="gov-award-body">

        <!-- Tender Information Section -->
        <div class="gov-award-section">
            <h3>Tender Information</h3>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Reference Number:</span>
                <span class="gov-error-detail-value gov-font-mono">${tender.referenceNumber}</span>
            </div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Title:</span>
                <span class="gov-error-detail-value"><strong>${tender.title}</strong></span>
            </div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Category:</span>
                <span class="gov-error-detail-value">${tender.categoryDisplayName}</span>
            </div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Estimated Value:</span>
                <span class="gov-error-detail-value">M <fmt:formatNumber value="${tender.estimatedValue}" pattern="#,##0.00" /></span>
            </div>
        </div>

        <div class="gov-divider"></div>

        <!-- Award Details Section -->
        <div class="gov-award-section">
            <h3>Award Details</h3>
            <div class="gov-award-winner">
                <div class="gov-award-winner-icon">🏆</div>
                <div class="gov-award-winner-info">
                    <div class="gov-award-winner-label">Awarded To</div>
                    <div class="gov-award-winner-name">${award.winningSupplierName}</div>
                </div>
            </div>

            <div class="gov-grid gov-grid-cols-2 gov-gap-md gov-mt-lg">
                <div class="gov-error-detail-item">
                    <span class="gov-error-detail-label">Awarded Value:</span>
                    <span class="gov-error-detail-value gov-text-large">
                        <strong>M <fmt:formatNumber value="${award.awardedValue}" pattern="#,##0.00" /></strong>
                    </span>
                </div>
                <div class="gov-error-detail-item">
                    <span class="gov-error-detail-label">Award Date:</span>
                    <span class="gov-error-detail-value">
                        <fmt:formatDate value="${award.awardedAt}" pattern="dd MMM yyyy" />
                    </span>
                </div>
            </div>

            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Awarded By:</span>
                <span class="gov-error-detail-value">${award.awardedByName}</span>
            </div>
        </div>

        <div class="gov-divider"></div>

        <!-- Justification Section -->
        <div class="gov-award-section">
            <h3>Justification</h3>
            <div class="gov-award-justification">
                <p style="white-space: pre-wrap;">${award.justification}</p>
            </div>
        </div>

    </div>

    <!-- Footer -->
    <div class="gov-award-footer">
        <p class="gov-text-muted">
            This is an official award notice from the Ministry of Public Works, Kingdom of Lesotho.
        </p>
        <p class="gov-text-muted">
            Suppliers who submitted bids for this tender have been notified via email.
        </p>
    </div>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Award notice is generated after tender is awarded
    - Visible to procurement officers and winning supplier
    - Print functionality available via window.print()
    - Email notifications sent to all bidding suppliers upon award
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />