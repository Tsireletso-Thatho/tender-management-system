<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Tender Details" />
</jsp:include>

<!-- ==================================================== -->
<!-- TENDER DETAIL - PROCOUREGOV TENDER MANAGEMENT         -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Tender Details</h1>
        <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tenders
        </a>
    </div>
</div>

<!-- Two Column Layout -->
<div class="gov-grid gov-grid-cols-2 gov-gap-lg">

    <!-- Left Column: Tender Information -->
    <div class="gov-card">
        <div class="gov-flex gov-justify-between gov-items-start gov-mb-md">
            <h3 style="margin-bottom: 0;">${tender.title}</h3>
            <span class="gov-badge gov-badge-${fn:toLowerCase(tender.status)}">${tender.status}</span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Reference Number:</span>
            <span class="gov-error-detail-value gov-font-mono">${tender.referenceNumber}</span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Category:</span>
            <span class="gov-error-detail-value">${tender.categoryDisplayName}</span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Estimated Value:</span>
            <span class="gov-error-detail-value">M <fmt:formatNumber value="${tender.estimatedValue}" pattern="#,##0.00" /></span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Submission Deadline:</span>
            <span class="gov-error-detail-value">
                <fmt:formatDate value="${tender.submissionDeadline}" pattern="dd MMM yyyy, HH:mm" />
            </span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Published:</span>
            <span class="gov-error-detail-value">
                <fmt:formatDate value="${tender.publishedAt}" pattern="dd MMM yyyy" />
            </span>
        </div>

        <h4 class="gov-mt-lg">Description</h4>
        <p style="white-space: pre-wrap;">${tender.description}</p>

        <h4 class="gov-mt-lg">Tender Notice Document</h4>
        <c:if test="${not empty tender.noticeDocumentPath}">
            <div class="gov-flex gov-gap-md gov-flex-wrap">
                <a href="${pageContext.request.contextPath}/tender/download?id=${tender.tenderId}" 
                   class="gov-btn gov-btn-secondary" target="_blank">
                    <span>👁️</span> View PDF
                </a>
                <a href="${pageContext.request.contextPath}/tender/download?id=${tender.tenderId}&download=true" 
                   class="gov-btn gov-btn-outline">
                    <span>⬇️</span> Download PDF
                </a>
            </div>
        </c:if>
        <c:if test="${empty tender.noticeDocumentPath}">
            <p class="gov-text-muted">No document uploaded.</p>
        </c:if>
    </div>

    <!-- Right Column: Bid Status / Submit Bid -->
    <div>
        <c:choose>
            <c:when test="${hasBid}">
                <!-- Already Submitted Bid -->
                <div class="gov-card">
                    <h3>Your Bid</h3>
                    <div class="gov-alert gov-alert-info">
                        <span class="gov-alert-icon">📝</span>
                        <span class="gov-alert-message">You have already submitted a bid for this tender.</span>
                    </div>

                    <div class="gov-error-detail-item">
                        <span class="gov-error-detail-label">Bid Amount:</span>
                        <span class="gov-error-detail-value">
                            <strong>M <fmt:formatNumber value="${myBid.bidAmount}" pattern="#,##0.00" /></strong>
                        </span>
                    </div>

                    <div class="gov-error-detail-item">
                        <span class="gov-error-detail-label">Timeline:</span>
                        <span class="gov-error-detail-value">${myBid.proposedTimelineDays} days</span>
                    </div>

                    <div class="gov-error-detail-item">
                        <span class="gov-error-detail-label">Submitted:</span>
                        <span class="gov-error-detail-value">
                            <fmt:formatDate value="${myBid.submittedAt}" pattern="dd MMM yyyy, HH:mm" />
                        </span>
                    </div>

                    <div class="gov-error-detail-item">
                        <span class="gov-error-detail-label">Status:</span>
                        <span class="gov-error-detail-value">
                            <c:choose>
                                <c:when test="${myBid.status == 'WON'}">
                                    <span class="gov-badge gov-badge-won">WON 🏆</span>
                                </c:when>
                                <c:when test="${myBid.status == 'NOT_WON'}">
                                    <span class="gov-badge gov-badge-lost">NOT WON</span>
                                </c:when>
                                <c:when test="${myBid.status == 'EVALUATED'}">
                                    <span class="gov-badge gov-badge-evaluated">EVALUATED</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="gov-badge gov-badge-submitted">SUBMITTED</span>
                                </c:otherwise>
                            </c:choose>
                        </span>
                    </div>

                    <c:if test="${myBid.status == 'WON'}">
                        <div class="gov-mt-lg">
                            <a href="${pageContext.request.contextPath}/supplier/award?tenderId=${tender.tenderId}" 
                               class="gov-btn gov-btn-gold">
                                <span>🏆</span> View Award Notice
                            </a>
                        </div>
                    </c:if>
                </div>
            </c:when>

            <c:when test="${tender.status == 'OPEN'}">
                <!-- Can Submit Bid -->
                <div class="gov-card">
                    <h3>Submit Your Bid</h3>
                    <p>This tender is open for bidding. Submit your proposal before the deadline.</p>
                    <a href="${pageContext.request.contextPath}/supplier/bid/submit?tenderId=${tender.tenderId}" 
                       class="gov-btn gov-btn-primary gov-w-full">
                        <span>📝</span> Submit Bid
                    </a>
                </div>
            </c:when>

            <c:otherwise>
                <!-- Bidding Closed -->
                <div class="gov-card">
                    <h3>Bidding Closed</h3>
                    <p class="gov-text-muted">This tender is no longer open for bidding.</p>
                </div>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - hasBid: true if supplier already submitted bid
    - canBid determined by tender.status == 'OPEN'
    - myBid contains the supplier's bid details if hasBid is true
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />