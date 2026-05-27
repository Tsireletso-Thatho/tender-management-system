<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="View Tender" />
</jsp:include>

<!-- ==================================================== -->
<!-- VIEW TENDER - PROCOUREGOV TENDER MANAGEMENT           -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Tender Details</h1>
        <div class="gov-flex gov-gap-md">
            <a href="${pageContext.request.contextPath}/officer/tender/list" class="gov-btn gov-btn-secondary">
                <span>←</span> Back to List
            </a>
            <c:if test="${tender.status == 'DRAFT'}">
                <a href="${pageContext.request.contextPath}/officer/tender/edit?id=${tender.tenderId}" class="gov-btn gov-btn-primary">
                    <span>✏️</span> Edit Tender
                </a>
            </c:if>
        </div>
    </div>
</div>

<!-- Tender Information Grid -->
<div class="gov-grid gov-grid-cols-2 gov-gap-lg">

    <!-- Left Column: Tender Details -->
    <div class="gov-card">
        <h3>Tender Information</h3>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Reference Number:</span>
            <span class="gov-error-detail-value gov-font-mono">${tender.referenceNumber}</span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Status:</span>
            <span class="gov-error-detail-value">
                <span class="gov-badge gov-badge-${fn:toLowerCase(tender.status)}">${tender.status}</span>
            </span>
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

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Submission Deadline:</span>
            <span class="gov-error-detail-value">
                <fmt:formatDate value="${tender.submissionDeadline}" pattern="dd MMM yyyy, HH:mm" />
                <c:if test="${tender.status == 'OPEN'}">
                    <jsp:useBean id="now" class="java.util.Date" />
                    <c:if test="${now.after(tender.submissionDeadline)}">
                        <span class="gov-badge gov-badge-closed gov-ml-sm">Passed</span>
                    </c:if>
                </c:if>
            </span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Created By:</span>
            <span class="gov-error-detail-value">${tender.createdByName}</span>
        </div>

        <div class="gov-error-detail-item">
            <span class="gov-error-detail-label">Created At:</span>
            <span class="gov-error-detail-value">
                <fmt:formatDate value="${tender.createdAt}" pattern="dd MMM yyyy, HH:mm" />
            </span>
        </div>

        <c:if test="${not empty tender.publishedAt}">
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Published At:</span>
                <span class="gov-error-detail-value">
                    <fmt:formatDate value="${tender.publishedAt}" pattern="dd MMM yyyy, HH:mm" />
                </span>
            </div>
        </c:if>

        <c:if test="${not empty tender.closedAt}">
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Closed At:</span>
                <span class="gov-error-detail-value">
                    <fmt:formatDate value="${tender.closedAt}" pattern="dd MMM yyyy, HH:mm" />
                </span>
            </div>
        </c:if>

        <c:if test="${not empty tender.evaluatedAt}">
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Evaluated At:</span>
                <span class="gov-error-detail-value">
                    <fmt:formatDate value="${tender.evaluatedAt}" pattern="dd MMM yyyy, HH:mm" />
                </span>
            </div>
        </c:if>

        <c:if test="${not empty tender.awardedAt}">
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Awarded At:</span>
                <span class="gov-error-detail-value">
                    <fmt:formatDate value="${tender.awardedAt}" pattern="dd MMM yyyy, HH:mm" />
                </span>
            </div>
        </c:if>
    </div>

    <!-- Right Column: Description and Document -->
    <div class="gov-card">
        <h3>Description</h3>
        <p style="white-space: pre-wrap;">${tender.description}</p>

        <h3 class="gov-mt-lg">Tender Notice Document</h3>
        <c:choose>
            <c:when test="${not empty tender.noticeDocumentPath}">
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
            </c:when>
            <c:otherwise>
                <p class="gov-text-muted">No document uploaded.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Status-Specific Actions -->
<div class="gov-card gov-mt-lg">
    <h3>Actions</h3>
    <div class="gov-flex gov-gap-md gov-flex-wrap">
        <c:choose>
            <c:when test="${tender.status == 'DRAFT'}">
                <a href="${pageContext.request.contextPath}/officer/tender/edit?id=${tender.tenderId}" class="gov-btn gov-btn-primary">
                    <span>✏️</span> Edit Tender
                </a>
                <form action="${pageContext.request.contextPath}/officer/tender/publish" method="POST" style="display: inline;" onsubmit="return confirmPublish()">
                    <input type="hidden" name="tenderId" value="${tender.tenderId}">
                    <button type="submit" class="gov-btn gov-btn-gold">
                        <span>📢</span> Publish Tender
                    </button>
                </form>
            </c:when>

            <c:when test="${tender.status == 'CLOSED'}">
                <c:if test="${tender.bidCount > 0}">
                    <form action="${pageContext.request.contextPath}/officer/tender/start-evaluation" method="POST">
                        <input type="hidden" name="tenderId" value="${tender.tenderId}">
                        <button type="submit" class="gov-btn gov-btn-primary">
                            <span>⭐</span> Start Evaluation
                        </button>
                    </form>
                </c:if>
                <c:if test="${tender.bidCount == 0}">
                    <div class="gov-alert gov-alert-warning">
                        <span>⚠</span> No bids were submitted for this tender.
                    </div>
                </c:if>
            </c:when>

            <c:when test="${tender.status == 'UNDER_EVALUATION'}">
                <a href="${pageContext.request.contextPath}/officer/evaluate?tenderId=${tender.tenderId}" class="gov-btn gov-btn-primary">
                    <span>⭐</span> Continue Evaluation
                </a>
            </c:when>

            <c:when test="${tender.status == 'EVALUATED'}">
                <a href="${pageContext.request.contextPath}/officer/tender/award?id=${tender.tenderId}" class="gov-btn gov-btn-gold">
                    <span>🏆</span> Award Tender
                </a>
                <a href="${pageContext.request.contextPath}/officer/evaluate?tenderId=${tender.tenderId}" class="gov-btn gov-btn-secondary">
                    <span>📊</span> View Evaluation Results
                </a>
            </c:when>

            <c:when test="${tender.status == 'AWARDED'}">
                <a href="${pageContext.request.contextPath}/officer/tender/award-notice?id=${tender.tenderId}" class="gov-btn gov-btn-primary">
                    <span>📜</span> View Award Notice
                </a>
            </c:when>
        </c:choose>

        <a href="${pageContext.request.contextPath}/officer/tender/list" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to List
        </a>
    </div>
</div>

<!-- Bids Section (visible after tender is closed) -->
<c:if test="${tender.status == 'CLOSED' || tender.status == 'UNDER_EVALUATION' || tender.status == 'EVALUATED' || tender.status == 'AWARDED'}">
    <div class="gov-card gov-mt-lg">
        <h3>Submitted Bids (${bidCount})</h3>
        <c:choose>
            <c:when test="${not empty bids}">
                <div class="gov-table-wrapper">
                    <table class="gov-table">
                        <thead>
                            <tr>
                                <th>#</th>
                                <th>Supplier</th>
                                <th>Bid Amount (M)</th>
                                <th>Timeline (Days)</th>
                                <th>Submitted</th>
                                <th>Status</th>
                                <th>Document</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${bids}" var="bid" varStatus="loop">
                            <tr>
                                <td>${loop.index + 1}</td>
                                <td>${bid.supplierName}</td>
                                <td><fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" /></td>
                            <td>${bid.proposedTimelineDays}</td>
                            <td><fmt:formatDate value="${bid.submittedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                            <td>
                            <c:choose>
                                <c:when test="${bid.evaluated}">
                                    <span class="gov-badge gov-badge-evaluated">EVALUATED</span>
                                </c:when>
                                <c:when test="${bid.status == 'WON'}">
                                    <span class="gov-badge gov-badge-won">WON</span>
                                </c:when>
                                <c:when test="${bid.status == 'NOT_WON'}">
                                    <span class="gov-badge gov-badge-lost">NOT WON</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="gov-badge gov-badge-submitted">PENDING</span>
                                </c:otherwise>
                            </c:choose>
                            </td>
                            <td>
                            <c:if test="${not empty bid.supportingDocumentPath}">
                                <a href="${pageContext.request.contextPath}/supplier/bid/download?bidId=${bid.bidId}" 
                                   class="gov-table-action-btn" title="Download Document">📎</a>
                            </c:if>
                            </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p class="gov-text-muted">No bids have been submitted for this tender.</p>
            </c:otherwise>
        </c:choose>
    </div>
</c:if>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Status-specific actions change based on tender.status
    - Bids only visible after tender is CLOSED
    - Evaluation can only start if bids exist (bidCount > 0)
    - Award button appears when status is EVALUATED
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />