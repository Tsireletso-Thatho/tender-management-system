<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Officer Dashboard" />
</jsp:include>

<!-- ==================================================== -->
<!-- OFFICER DASHBOARD - PROCOUREGOV TENDER MANAGEMENT      -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Procurement Officer Dashboard</h1>
        <a href="${pageContext.request.contextPath}/officer/tender/create" class="gov-btn gov-btn-primary">
            <span>➕</span> Create New Tender
        </a>
    </div>
    <p class="gov-text-muted">Welcome back, ${sessionScope.userEmail}. Manage tenders and track procurement activities.</p>
</div>

<!-- Statistics Cards Section -->
<div class="gov-dashboard-grid">

    <!-- Total Tenders Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon">
            <span>📋</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${totalTenders}</div>
            <div class="gov-stat-label">Total Tenders</div>
        </div>
    </div>

    <!-- Draft Tenders Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-gold">
            <span>✏️</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${draftTenders}</div>
            <div class="gov-stat-label">Draft Tenders</div>
        </div>
    </div>

    <!-- Open Tenders Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-teal">
            <span>🔓</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${openTenders}</div>
            <div class="gov-stat-label">Open Tenders</div>
        </div>
    </div>

    <!-- Closed Tenders Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon">
            <span>🔒</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${closedTenders}</div>
            <div class="gov-stat-label">Closed Tenders</div>
        </div>
    </div>

    <!-- Under Evaluation Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-gold">
            <span>⭐</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${underEvaluationTenders}</div>
            <div class="gov-stat-label">Under Evaluation</div>
        </div>
    </div>

    <!-- Evaluated Tenders Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-teal">
            <span>📊</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${evaluatedTenders}</div>
            <div class="gov-stat-label">Evaluated</div>
        </div>
    </div>

    <!-- Awarded Tenders Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon">
            <span>🏆</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${awardedTenders}</div>
            <div class="gov-stat-label">Awarded</div>
        </div>
    </div>
</div>

<!-- Quick Actions Section -->
<div class="gov-card gov-mb-lg">
    <h3>Quick Actions</h3>
    <div class="gov-flex gov-gap-md gov-flex-wrap">
        <a href="${pageContext.request.contextPath}/officer/tender/create" class="gov-btn gov-btn-primary">
            <span>➕</span> Create Tender
        </a>
        <a href="${pageContext.request.contextPath}/officer/tender/list" class="gov-btn gov-btn-secondary">
            <span>📋</span> View All Tenders
        </a>
        <a href="${pageContext.request.contextPath}/officer/tender/list?status=OPEN" class="gov-btn gov-btn-secondary">
            <span>🔓</span> Open Tenders
        </a>
        <a href="${pageContext.request.contextPath}/officer/tender/list?status=CLOSED" class="gov-btn gov-btn-secondary">
            <span>🔒</span> Closed Tenders
        </a>
    </div>
</div>

<!-- Recent Tenders Table -->
<div class="gov-card">
    <div class="gov-flex gov-justify-between gov-items-center gov-mb-md">
        <h3 style="margin-bottom: 0;">Recent Tenders</h3>
        <a href="${pageContext.request.contextPath}/officer/tender/list" class="gov-btn-link">View All →</a>
    </div>

    <c:choose>
        <c:when test="${not empty recentTenders}">
            <div class="gov-table-wrapper">
                <table class="gov-table">
                    <thead>
                        <tr>
                            <th>Reference</th>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Status</th>
                            <th>Deadline</th>
                            <th>Bids</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${recentTenders}" var="tender" begin="0" end="4">
                        <tr>
                            <td class="gov-font-mono">${tender.referenceNumber}</td>
                            <td>${tender.title}</td>
                            <td>${tender.categoryDisplayName}</td>
                            <td>
                                <span class="gov-badge gov-badge-${fn:toLowerCase(tender.status)}">
                                    ${tender.status}
                                </span>
                            </td>
                            <td>
                        <fmt:formatDate value="${tender.submissionDeadline}" pattern="dd/MM/yyyy HH:mm" />
                        </td>
                        <td>${tender.bidCount}</td>
                        <td>
                            <div class="gov-table-actions">
                                <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" 
                                   class="gov-table-action-btn" title="View Details">👁</a>
                                <c:if test="${tender.status == 'DRAFT'}">
                                    <a href="${pageContext.request.contextPath}/officer/tender/edit?id=${tender.tenderId}" 
                                       class="gov-table-action-btn" title="Edit">✏️</a>
                                </c:if>
                            </div>
                        </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <p class="gov-text-muted">No tenders created yet.</p>
            <a href="${pageContext.request.contextPath}/officer/tender/create" class="gov-btn gov-btn-primary">
                Create Your First Tender
            </a>
        </c:otherwise>
    </c:choose>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Statistics are set in OfficerDashboardServlet.doGet()
    - recentTenders limited to 5 items using begin="0" end="4"
    - Status badge classes: gov-badge-draft, gov-badge-open, etc.
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />