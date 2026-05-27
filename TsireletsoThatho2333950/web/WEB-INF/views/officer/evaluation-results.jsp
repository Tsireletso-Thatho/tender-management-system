<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluation Results" />
</jsp:include>

<!-- ==================================================== -->
<!-- EVALUATION RESULTS - PROCOUREGOV TENDER MANAGEMENT    -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Evaluation Results: ${tender.referenceNumber}</h1>
        <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tender
        </a>
    </div>
    <p class="gov-text-muted">Final evaluation scores and bid rankings.</p>
</div>

<!-- Ranked Bids Table -->
<div class="gov-card gov-mb-lg">
    <h3>Ranked Bids</h3>

    <c:choose>
        <c:when test="${not empty bids}">
            <div class="gov-table-wrapper">
                <table class="gov-table">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Supplier</th>
                            <th>Bid Amount (M)</th>
                            <th>Final Score</th>
                            <th>Evaluators</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${bids}" var="bid">
                        <tr>
                            <td>
                        <c:choose>
                            <c:when test="${bid.rank == 1}">
                                <span class="gov-rank-badge gov-rank-1">🥇 1st</span>
                            </c:when>
                            <c:when test="${bid.rank == 2}">
                                <span class="gov-rank-badge gov-rank-2">🥈 2nd</span>
                            </c:when>
                            <c:when test="${bid.rank == 3}">
                                <span class="gov-rank-badge gov-rank-3">🥉 3rd</span>
                            </c:when>
                            <c:otherwise>
                                <span class="gov-rank-badge gov-rank-other">${bid.rank}th</span>
                            </c:otherwise>
                        </c:choose>
                        </td>
                        <td><strong>${bid.supplierName}</strong></td>
                        <td><fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" /></td>
                        <td>
                            <strong class="gov-score-display">
                                <fmt:formatNumber value="${bid.finalScore}" pattern="#0.00" />
                            </strong>
                        </td>
                        <td>${evaluatorsSubmitted} / ${totalEvaluators}</td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>

            <c:if test="${evaluationComplete}">
                <div class="gov-alert gov-alert-success gov-mt-lg">
                    <span class="gov-alert-icon">✅</span>
                    <span class="gov-alert-message">Evaluation is complete! You can now proceed to award the tender.</span>
                </div>

                <div class="gov-form-actions gov-mt-lg">
                    <a href="${pageContext.request.contextPath}/officer/tender/award?id=${tender.tenderId}" class="gov-btn gov-btn-gold">
                        <span>🏆</span> Proceed to Award
                    </a>
                </div>
            </c:if>

            <c:if test="${!evaluationComplete}">
                <div class="gov-alert gov-alert-info gov-mt-lg">
                    <span class="gov-alert-icon">ℹ️</span>
                    <span class="gov-alert-message">
                        Waiting for all evaluators to submit their scores. 
                        ${evaluatorsSubmitted} of ${totalEvaluators} evaluators have submitted.
                    </span>
                </div>
            </c:if>
        </c:when>
        <c:otherwise>
            <p class="gov-text-muted">No bids available.</p>
        </c:otherwise>
    </c:choose>
</div>

<!-- Detailed Scores by Evaluator -->
<c:if test="${not empty scoresByBid}">
    <div class="gov-card">
        <h3>Detailed Scores by Evaluator</h3>

        <c:forEach items="${bids}" var="bid">
            <div class="gov-evaluation-panel">
                <div class="gov-evaluation-header">
                    <span class="gov-evaluation-title">
                        ${bid.supplierName} - Bid Amount: M <fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" />
                    </span>
                </div>
                <div class="gov-evaluation-body">
                    <c:set var="bidScores" value="${scoresByBid[bid.bidId]}" />
                    <c:choose>
                        <c:when test="${not empty bidScores}">
                            <div class="gov-table-wrapper">
                                <table class="gov-table">
                                    <thead>
                                        <tr>
                                            <th>Evaluator</th>
                                            <th>Price (40%)</th>
                                            <th>Technical (35%)</th>
                                            <th>Timeline (25%)</th>
                                            <th>Weighted Total</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                    <c:forEach items="${bidScores}" var="score">
                                        <tr>
                                            <td>${score.evaluatorName}</td>
                                            <td><fmt:formatNumber value="${score.priceScore}" pattern="#0.00" />%</td>
                                        <td><fmt:formatNumber value="${score.technicalScore}" pattern="#0.00" />%</td>
                                        <td><fmt:formatNumber value="${score.timelineScore}" pattern="#0.00" />%</td>
                                        <td><strong><fmt:formatNumber value="${score.weightedTotal}" pattern="#0.00" /></strong></td>
                                        </tr>
                                    </c:forEach>
                                    </tbody>
                                    <tfoot>
                                        <tr style="background-color: var(--gov-blue-pale);">
                                            <td><strong>AVERAGE</strong></td>
                                            <td colspan="3"></td>
                                            <td><strong><fmt:formatNumber value="${bid.finalScore}" pattern="#0.00" /></strong></td>
                                        </tr>
                                    </tfoot>
                                </table>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <p class="gov-text-muted">No scores submitted yet.</p>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </c:forEach>
    </div>
</c:if>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Bids are ranked by finalScore (descending)
    - finalScore is the average of all evaluators' weightedTotal
    - evaluationComplete becomes true when all evaluators have scored all bids
    - Scores are retrieved from EvaluationDAO
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />