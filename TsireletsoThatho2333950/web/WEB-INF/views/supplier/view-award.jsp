<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Award Notice" />
</jsp:include>

<!-- ==================================================== -->
<!-- VIEW AWARD - PROCOUREGOV TENDER MANAGEMENT            -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Award Notice</h1>
        <div class="gov-flex gov-gap-md">
            <a href="${pageContext.request.contextPath}/supplier/bids" class="gov-btn gov-btn-secondary">
                <span>←</span> Back to My Bids
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

        <!-- Tender Information -->
        <div class="gov-award-section">
            <h3>Tender Information</h3>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Reference Number:</span>
                <span class="gov-error-detail-value gov-font-mono">${award.tenderReference}</span>
            </div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Title:</span>
                <span class="gov-error-detail-value"><strong>${award.tenderTitle}</strong></span>
            </div>
        </div>

        <div class="gov-divider"></div>

        <!-- Award Details -->
        <div class="gov-award-section">
            <h3>Award Details</h3>

            <c:choose>
                <c:when test="${award.winningSupplierName == supplier.companyName}">
                    <!-- Winner Message -->
                    <div class="gov-alert gov-alert-success gov-mb-lg">
                        <span class="gov-alert-icon">🎉</span>
                        <span class="gov-alert-message">
                            <strong>Congratulations!</strong> Your bid has been selected as the winning bid for this tender!
                        </span>
                    </div>

                    <div class="gov-award-winner">
                        <div class="gov-award-winner-icon">🏆</div>
                        <div class="gov-award-winner-info">
                            <div class="gov-award-winner-label">Awarded To</div>
                            <div class="gov-award-winner-name">${award.winningSupplierName}</div>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <!-- Not Winner Message -->
                    <div class="gov-alert gov-alert-info gov-mb-lg">
                        <span class="gov-alert-icon">ℹ️</span>
                        <span class="gov-alert-message">
                            This tender has been awarded to another supplier. Thank you for your participation.
                        </span>
                    </div>

                    <div class="gov-error-detail-item">
                        <span class="gov-error-detail-label">Awarded To:</span>
                        <span class="gov-error-detail-value"><strong>${award.winningSupplierName}</strong></span>
                    </div>
                </c:otherwise>
            </c:choose>

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
        </div>

        <div class="gov-divider"></div>

        <!-- Justification -->
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
    </div>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Award object contains tender and winner details
    - supplier object used to check if current user is winner
    - Different message displayed for winner vs non-winner
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />