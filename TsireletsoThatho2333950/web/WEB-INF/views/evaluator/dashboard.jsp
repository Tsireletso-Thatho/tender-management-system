<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Evaluator Dashboard" />
</jsp:include>

<!-- ==================================================== -->
<!-- EVALUATOR DASHBOARD - PROCOUREGOV TENDER MANAGEMENT   -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Evaluation Committee Dashboard</h1>
    </div>
    <p class="gov-text-muted">Welcome, ${evaluator.fullName}. Department: ${evaluator.department}</p>
</div>

<!-- Statistics Cards -->
<div class="gov-dashboard-grid">

    <!-- Pending Evaluations Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-teal">
            <span>📋</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${pendingEvaluations}</div>
            <div class="gov-stat-label">Pending Evaluations</div>
        </div>
    </div>

    <!-- Completed Evaluations Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon">
            <span>✅</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${completedEvaluations}</div>
            <div class="gov-stat-label">Completed Evaluations</div>
        </div>
    </div>

    <!-- Tenders in Evaluation Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-gold">
            <span>📊</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${fn:length(tendersForEvaluation)}</div>
            <div class="gov-stat-label">Tenders in Evaluation</div>
        </div>
    </div>
</div>

<!-- Two Column Layout -->
<div class="gov-grid gov-grid-cols-2 gov-gap-lg">

    <!-- Tenders Awaiting Evaluation -->
    <div class="gov-card">
        <div class="gov-flex gov-justify-between gov-items-center gov-mb-md">
            <h3 style="margin-bottom: 0;">Tenders Awaiting Your Evaluation</h3>
            <a href="${pageContext.request.contextPath}/evaluator/tenders" class="gov-btn-link">View All →</a>
        </div>

        <c:choose>
            <c:when test="${not empty availableTenders}">
                <div class="gov-table-wrapper">
                    <table class="gov-table">
                        <thead>
                            <tr>
                                <th>Reference</th>
                                <th>Title</th>
                                <th>Category</th>
                                <th>Action</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${availableTenders}" var="tender" begin="0" end="4">
                            <tr>
                                <td class="gov-font-mono">${tender.referenceNumber}</td>
                                <td>${tender.title}</td>
                                <td>${tender.categoryDisplayName}</td>
                                <td>
                                    <a href="${pageContext.request.contextPath}/evaluator/evaluate?tenderId=${tender.tenderId}" 
                                       class="gov-btn gov-btn-primary gov-btn-sm">Evaluate</a>
                                </td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p class="gov-text-muted">No tenders awaiting your evaluation.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- Recently Completed Evaluations -->
    <div class="gov-card">
        <div class="gov-flex gov-justify-between gov-items-center gov-mb-md">
            <h3 style="margin-bottom: 0;">Recently Completed Evaluations</h3>
        </div>

        <c:choose>
            <c:when test="${completedEvaluations > 0}">
                <p class="gov-text-muted">You have completed ${completedEvaluations} evaluation(s).</p>
                <a href="${pageContext.request.contextPath}/evaluator/tenders" class="gov-btn gov-btn-secondary">
                    View All Tenders
                </a>
            </c:when>
            <c:otherwise>
                <p class="gov-text-muted">You haven't completed any evaluations yet.</p>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Quick Actions -->
<div class="gov-card gov-mt-lg">
    <h3>Quick Actions</h3>
    <div class="gov-flex gov-gap-md gov-flex-wrap">
        <a href="${pageContext.request.contextPath}/evaluator/tenders" class="gov-btn gov-btn-primary">
            <span>📋</span> View All Tenders for Evaluation
        </a>
    </div>
</div>

<!-- Evaluation Guidelines -->
<div class="gov-card gov-mt-lg">
    <h3>Evaluation Guidelines</h3>
    <div class="gov-evaluation-summary">
        <h4>Scoring Criteria</h4>
        <div class="gov-score-breakdown">
            <div class="gov-score-item">
                <div class="gov-score-label">Price Score</div>
                <div class="gov-score-value">40%</div>
                <p class="gov-text-muted gov-mt-sm">Automatically calculated: (Lowest Bid / This Bid) × 100</p>
            </div>
            <div class="gov-score-item">
                <div class="gov-score-label">Technical Compliance</div>
                <div class="gov-score-value">35%</div>
                <p class="gov-text-muted gov-mt-sm">Manual score (0-100) based on technical merit</p>
            </div>
            <div class="gov-score-item">
                <div class="gov-score-label">Delivery Timeline</div>
                <div class="gov-score-value">25%</div>
                <p class="gov-text-muted gov-mt-sm">Automatically calculated: (Shortest Timeline / This Timeline) × 100</p>
            </div>
        </div>
        <div class="gov-alert gov-alert-info gov-mt-lg">
            <span class="gov-alert-icon">ℹ️</span>
            <span class="gov-alert-message">
                <strong>Note:</strong> You cannot see other evaluators' scores until you submit your own scores for all bids in a tender.
            </span>
        </div>
    </div>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - evaluator object contains evaluator details (fullName, department)
    - pendingEvaluations: count of tenders not yet fully scored by this evaluator
    - completedEvaluations: count of tenders fully scored by this evaluator
    - availableTenders limited to 5 items
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />