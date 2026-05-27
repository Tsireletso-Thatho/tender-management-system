<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluation Results" />
</jsp:include>

<!-- ==================================================== -->
<!-- VIEW RESULTS - PROCOUREGOV TENDER MANAGEMENT          -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<c:choose>
    <%-- ==================================================== --%>
    <%-- LIST VIEW: Show all evaluated/awarded tenders         --%>
    <%-- ==================================================== --%>
    <c:when test="${showList}">
        <div class="gov-page-header">
            <div class="gov-page-title">
                <h1>Evaluation Results</h1>
            </div>
            <p class="gov-text-muted">View completed and awarded tenders.</p>
        </div>

        <div class="gov-card">
            <h3>Completed Tenders</h3>

            <c:choose>
                <c:when test="${not empty resultsTenders}">
                    <div class="gov-table-wrapper">
                        <table class="gov-table">
                            <thead>
                                <tr>
                                    <th>Reference</th>
                                    <th>Title</th>
                                    <th>Category</th>
                                    <th>Status</th>
                                    <th>Bids</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                            <c:forEach items="${resultsTenders}" var="tender">
                                <tr>
                                    <td class="gov-font-mono">${tender.referenceNumber}</td>
                                    <td>${tender.title}</td>
                                    <td>${tender.categoryDisplayName}</td>
                                    <td>
                                        <span class="gov-badge gov-badge-${fn:toLowerCase(tender.status)}">
                                            ${tender.status}
                                        </span>
                                    </td>
                                    <td>${tender.bidCount}</td>
                                    <td>
                                        <a href="${pageContext.request.contextPath}/evaluator/results?tenderId=${tender.tenderId}" 
                                           class="gov-btn gov-btn-secondary gov-btn-sm">View Results</a>
                                    </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="gov-alert gov-alert-info">
                        <span class="gov-alert-icon">ℹ️</span>
                        <span class="gov-alert-message">No completed tenders available.</span>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </c:when>

    <%-- ==================================================== --%>
    <%-- DETAIL VIEW: Show specific tender results             --%>
    <%-- ==================================================== --%>
    <c:otherwise>
        <div class="gov-page-header">
            <div class="gov-page-title">
                <h1>Evaluation Results: ${tender.referenceNumber}</h1>
                <a href="${pageContext.request.contextPath}/evaluator/results" class="gov-btn gov-btn-secondary">
                    <span>←</span> Back to Results
                </a>
            </div>
            <p class="gov-text-muted">${tender.title}</p>
        </div>

        <!-- Not Completed Warning -->
        <c:if test="${!hasCompleted && !evaluationComplete}">
            <div class="gov-alert gov-alert-warning gov-mb-lg">
                <span class="gov-alert-icon">🔒</span>
                <span class="gov-alert-message">
                    You must complete your evaluation for all bids before viewing other evaluators' scores.
                </span>
            </div>
        </c:if>

        <!-- Evaluation Progress -->
        <div class="gov-card gov-mb-lg">
            <h3>Evaluation Progress</h3>
            <div class="gov-flex gov-items-center gov-gap-lg">
                <div class="gov-stat-card">
                    <div class="gov-stat-value">${evaluatorsSubmitted}</div>
                    <div class="gov-stat-label">Evaluators Submitted</div>
                </div>
                <div class="gov-stat-card">
                    <div class="gov-stat-value">${totalEvaluators}</div>
                    <div class="gov-stat-label">Total Evaluators</div>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${evaluationComplete}">
                            <span class="gov-badge gov-badge-success">EVALUATION COMPLETE</span>
                        </c:when>
                        <c:otherwise>
                            <span class="gov-badge gov-badge-evaluation">IN PROGRESS</span>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>

        <!-- Ranked Bids -->
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
                                    <th>Your Score</th>
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
                                <td>
                                <c:set var="myScore" value="${myScoresMap[bid.bidId]}" />
                                <c:choose>
                                    <c:when test="${not empty myScore}">
                                        <fmt:formatNumber value="${myScore}" pattern="#0.00" />%
                                    </c:when>
                                    <c:otherwise>
                                        <span class="gov-text-muted">Not scored</span>
                                    </c:otherwise>
                                </c:choose>
                                </td>
                                </tr>
                            </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="gov-text-muted">No bids available.</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Detailed Scores by Evaluator -->
        <c:if test="${hasCompleted || evaluationComplete}">
            <div class="gov-card">
                <h3>Detailed Scores by Evaluator</h3>

                <c:forEach items="${bids}" var="bid">
                    <div class="gov-evaluation-panel">
                        <div class="gov-evaluation-header">
                            <span class="gov-evaluation-title">
                                ${bid.supplierName} - Bid: M <fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" />
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
                                                    <td>
                                                        ${score.evaluatorName}
                                                <c:if test="${score.evaluatorName == sessionScope.userEmail}">
                                                    <span class="gov-badge gov-badge-submitted gov-ml-sm">YOU</span>
                                                </c:if>
                                                </td>
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

        <!-- Evaluation Complete Message -->
        <c:if test="${evaluationComplete}">
            <div class="gov-alert gov-alert-success gov-mt-lg">
                <span class="gov-alert-icon">✅</span>
                <span class="gov-alert-message">
                    Evaluation is complete! The Procurement Officer can now proceed to award the tender.
                </span>
            </div>
        </c:if>
    </c:otherwise>
</c:choose>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - showList: true when no tenderId provided (shows list of EVALUATED/AWARDED tenders)
    - showList: false when tenderId provided (shows detailed results)
    - hasCompleted: true if this evaluator has scored all bids for this tender
    - evaluationComplete: true if ALL evaluators have scored all bids
    - scoresByBid: map of bidId to list of EvaluationScore objects
    - myScoresMap: map of bidId to this evaluator's technical score
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />