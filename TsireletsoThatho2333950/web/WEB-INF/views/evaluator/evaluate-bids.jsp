<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluate Bids" />
</jsp:include>

<!-- ==================================================== -->
<!-- EVALUATE BIDS - PROCOUREGOV TENDER MANAGEMENT         -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Evaluate Bids: ${tender.referenceNumber}</h1>
        <a href="${pageContext.request.contextPath}/evaluator/tenders" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tenders
        </a>
    </div>
    <p class="gov-text-muted">Evaluate each bid by providing a technical compliance score (0-100).</p>
</div>

<!-- Already Completed Message -->
<c:if test="${allScored}">
    <div class="gov-alert gov-alert-success gov-mb-lg">
        <span class="gov-alert-icon">✅</span>
        <span class="gov-alert-message">
            You have already completed evaluation for this tender. 
            <a href="${pageContext.request.contextPath}/evaluator/results?tenderId=${tender.tenderId}">View Results →</a>
        </span>
    </div>
</c:if>

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
    <p class="gov-text-muted">Enter your technical compliance score for each bid. Price and timeline scores are calculated automatically.</p>

    <c:choose>
        <c:when test="${not empty bids}">
            <form action="${pageContext.request.contextPath}/evaluator/evaluate" method="POST">
                <input type="hidden" name="tenderId" value="${tender.tenderId}">

                <c:forEach items="${bids}" var="bid">
                    <div class="gov-evaluation-panel">
                        <div class="gov-evaluation-header">
                            <span class="gov-evaluation-title">Bid #${bid.bidId}: ${bid.supplierName}</span>
                            <c:if test="${bid.evaluated}">
                                <span class="gov-badge gov-badge-evaluated gov-ml-sm">ALREADY SCORED</span>
                            </c:if>
                        </div>
                        <div class="gov-evaluation-body">
                            <div class="gov-grid gov-grid-cols-2 gov-gap-lg">
                                <!-- Left Column: Bid Details -->
                                <div>
                                    <div class="gov-error-detail-item">
                                        <span class="gov-error-detail-label">Bid Amount:</span>
                                        <span class="gov-error-detail-value">
                                            <strong>M <fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" /></strong>
                                        </span>
                                    </div>
                                    <div class="gov-error-detail-item">
                                        <span class="gov-error-detail-label">Proposed Timeline:</span>
                                        <span class="gov-error-detail-value">${bid.proposedTimelineDays} days</span>
                                    </div>
                                    <div class="gov-error-detail-item">
                                        <span class="gov-error-detail-label">Technical Compliance Statement:</span>
                                        <span class="gov-error-detail-value">${bid.technicalComplianceStatement}</span>
                                    </div>
                                    <c:if test="${not empty bid.supportingDocumentPath}">
                                        <div class="gov-mt-sm">
                                            <a href="${pageContext.request.contextPath}/supplier/bid/download?bidId=${bid.bidId}" 
                                               class="gov-btn gov-btn-outline gov-btn-sm" target="_blank">
                                                <span>📎</span> View Supporting Document
                                            </a>
                                        </div>
                                    </c:if>
                                </div>

                                <!-- Right Column: Scoring -->
                                <div>
                                    <div class="gov-form-group">
                                        <label for="technicalScore_${bid.bidId}" class="gov-form-label">
                                            Technical Compliance Score (0-100)
                                        </label>
                                        <c:choose>
                                            <c:when test="${bid.evaluated}">
                                                <c:set var="existingScore" value="" />
                                                <c:forEach items="${myScoresMap}" var="entry">
                                                    <c:if test="${entry.key == bid.bidId}">
                                                        <c:set var="existingScore" value="${entry.value}" />
                                                    </c:if>
                                                </c:forEach>
                                                <input type="text" class="gov-form-input" value="${existingScore}" readonly disabled>
                                                <p class="gov-form-hint">Already scored - cannot modify</p>
                                            </c:when>
                                            <c:otherwise>
                                                <input type="number" id="technicalScore_${bid.bidId}" 
                                                       name="technicalScore_${bid.bidId}" 
                                                       class="gov-form-input gov-score-input" 
                                                       min="0" max="100" step="0.01" 
                                                       placeholder="0-100" 
                                                       required>
                                            </c:otherwise>
                                        </c:choose>
                                    </div>

                                    <!-- Score Preview -->
                                    <div class="gov-score-breakdown gov-mt-lg">
                                        <div class="gov-score-item">
                                            <div class="gov-score-label">Price Score (40%)</div>
                                            <c:set var="priceScore" value="${(lowestBid / bid.bidAmount) * 100}" />
                                            <div class="gov-score-value">
                                                <fmt:formatNumber value="${priceScore}" pattern="#0.00" />%
                                            </div>
                                        </div>
                                        <div class="gov-score-item">
                                            <div class="gov-score-label">Timeline Score (25%)</div>
                                            <c:set var="timelineScore" value="${(shortestTimeline / bid.proposedTimelineDays) * 100}" />
                                            <div class="gov-score-value">
                                                <fmt:formatNumber value="${timelineScore}" pattern="#0.00" />%
                                            </div>
                                        </div>
                                        <div class="gov-score-item">
                                            <div class="gov-score-label">Technical Weight</div>
                                            <div class="gov-score-value">35%</div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </c:forEach>

                <c:if test="${!allScored}">
                    <!-- Scoring Summary -->
                    <div class="gov-evaluation-summary gov-mt-lg">
                        <h4>Scoring Summary</h4>
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

                    <!-- Confidentiality Notice -->
                    <div class="gov-alert gov-alert-warning gov-mt-lg">
                        <span class="gov-alert-icon">🔒</span>
                        <span class="gov-alert-message">
                            <strong>Confidential:</strong> Your scores are confidential. You will not be able to see other evaluators' scores until you submit your own scores for all bids.
                        </span>
                    </div>

                    <!-- Form Actions -->
                    <div class="gov-form-actions">
                        <button type="submit" class="gov-btn gov-btn-primary">
                            <span>💾</span> Submit Evaluation Scores
                        </button>
                        <a href="${pageContext.request.contextPath}/evaluator/tenders" class="gov-btn gov-btn-secondary">
                            Cancel
                        </a>
                    </div>
                </c:if>
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
    - myScoresMap contains previously submitted scores (key: bidId, value: technicalScore)
    - allScored flag indicates if evaluator has scored all bids
    - Price score = (lowestBid / bidAmount) × 100
    - Timeline score = (shortestTimeline / timelineDays) × 100
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />