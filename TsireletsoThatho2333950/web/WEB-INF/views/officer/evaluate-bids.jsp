<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluate Bids" />
</jsp:include>

<!-- ==================================================== -->
<!-- EVALUATE BIDS - PROCOUREGOV TENDER MANAGEMENT         -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- Procurement Officer evaluation panel                  -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Evaluate Bids: ${tender.referenceNumber}</h1>
        <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tender
        </a>
    </div>
    <p class="gov-text-muted">As a Procurement Officer, you can evaluate and score bids for this tender.</p>
</div>

<!-- Tender Summary -->
<div class="gov-card gov-mb-lg">
    <h3>Tender Information</h3>
    <div class="gov-grid gov-grid-cols-3 gov-gap-md">
        <div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Title:</span>
                <span class="gov-error-detail-value">${tender.title}</span>
            </div>
        </div>
        <div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Lowest Bid:</span>
                <span class="gov-error-detail-value">M <fmt:formatNumber value="${lowestBid}" pattern="#,##0.00" /></span>
            </div>
        </div>
        <div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Shortest Timeline:</span>
                <span class="gov-error-detail-value">${shortestTimeline} days</span>
            </div>
        </div>
    </div>
</div>

<!-- Evaluation Form -->
<div class="gov-card">
    <h3>Bid Evaluation</h3>
    <p class="gov-text-muted">Enter technical compliance scores (0-100) for each bid. Price and timeline scores are calculated automatically.</p>

    <c:choose>
        <c:when test="${not empty bids}">
            <form action="${pageContext.request.contextPath}/officer/evaluate" method="POST">
                <input type="hidden" name="tenderId" value="${tender.tenderId}">

                <div class="gov-table-wrapper">
                    <table class="gov-table">
                        <thead>
                            <tr>
                                <th>Supplier</th>
                                <th>Bid Amount (M)</th>
                                <th>Price Score (40%)</th>
                                <th>Timeline (Days)</th>
                                <th>Timeline Score (25%)</th>
                                <th>Technical Score (35%)</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${bids}" var="bid">
                            <tr>
                                <td><strong>${bid.supplierName}</strong></td>
                                <td><fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" /></td>
                            <td>
                            <c:set var="priceScore" value="${(lowestBid / bid.bidAmount) * 100}" />
                            <fmt:formatNumber value="${priceScore}" pattern="#0.00" />%
                            </td>
                            <td>${bid.proposedTimelineDays}</td>
                            <td>
                            <c:set var="timelineScore" value="${(shortestTimeline / bid.proposedTimelineDays) * 100}" />
                            <fmt:formatNumber value="${timelineScore}" pattern="#0.00" />%
                            </td>
                            <td>
                                <input type="number" name="technicalScore_${bid.bidId}" 
                                       class="gov-form-input gov-score-input" 
                                       min="0" max="100" step="0.01" 
                                       value="${myScoresMap[bid.bidId]}" 
                                       placeholder="0-100" 
                                       required>
                            </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>

                <!-- Scoring Weights Summary -->
                <div class="gov-evaluation-summary gov-mt-lg">
                    <h4>Scoring Weights</h4>
                    <div class="gov-score-breakdown">
                        <div class="gov-score-item">
                            <div class="gov-score-label">Price</div>
                            <div class="gov-score-value">40%</div>
                        </div>
                        <div class="gov-score-item">
                            <div class="gov-score-label">Technical</div>
                            <div class="gov-score-value">35%</div>
                        </div>
                        <div class="gov-score-item">
                            <div class="gov-score-label">Timeline</div>
                            <div class="gov-score-value">25%</div>
                        </div>
                    </div>
                    <p class="gov-text-muted gov-mt-sm">
                        Weighted Total = (Price × 0.40) + (Technical × 0.35) + (Timeline × 0.25)
                    </p>
                </div>

                <!-- Form Actions -->
                <div class="gov-form-actions">
                    <button type="submit" class="gov-btn gov-btn-primary">
                        <span>💾</span> Submit Evaluation Scores
                    </button>
                    <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
                        Cancel
                    </a>
                </div>
            </form>
        </c:when>
        <c:otherwise>
            <p class="gov-text-muted">No bids available for evaluation.</p>
        </c:otherwise>
    </c:choose>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Price score auto-calculated: (lowestBid / bidAmount) × 100
    - Timeline score auto-calculated: (shortestTimeline / timelineDays) × 100
    - Technical score entered manually by officer (0-100)
    - Form submits to OfficerEvaluateBidsServlet
    - Weighted total calculated server-side in ScoringService
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />