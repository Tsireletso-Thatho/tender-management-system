<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Supplier Dashboard" />
</jsp:include>

<!-- ==================================================== -->
<!-- SUPPLIER DASHBOARD - PROCOUREGOV TENDER MANAGEMENT    -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Supplier Dashboard</h1>
        <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn gov-btn-primary">
            <span>🔍</span> Browse Open Tenders
        </a>
    </div>
    <p class="gov-text-muted">Welcome back, ${supplier.companyName}. Registration #: ${supplier.registrationNumber}</p>
</div>

<!-- Statistics Cards -->
<div class="gov-dashboard-grid">

    <!-- Open Tenders Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-teal">
            <span>🔓</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${openTenderCount}</div>
            <div class="gov-stat-label">Open Tenders</div>
        </div>
    </div>

    <!-- Bids Submitted Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon">
            <span>📝</span>
        </div>
        <div class="gov-stat-content">
            <div class="gov-stat-value">${bidCount}</div>
            <div class="gov-stat-label">Bids Submitted</div>
        </div>
    </div>

    <!-- Contracts Won Card -->
    <div class="gov-card gov-stat-card">
        <div class="gov-stat-icon gov-stat-gold">
            <span>🏆</span>
        </div>
        <div class="gov-stat-content">
            <c:set var="wonCount" value="0" />
            <c:forEach items="${myBids}" var="bid">
                <c:if test="${bid.status == 'WON'}">
                    <c:set var="wonCount" value="${wonCount + 1}" />
                </c:if>
            </c:forEach>
            <div class="gov-stat-value">${wonCount}</div>
            <div class="gov-stat-label">Contracts Won</div>
        </div>
    </div>
</div>

<!-- Two Column Layout -->
<div class="gov-grid gov-grid-cols-2 gov-gap-lg">

    <!-- Recent Open Tenders Column -->
    <div class="gov-card">
        <div class="gov-flex gov-justify-between gov-items-center gov-mb-md">
            <h3 style="margin-bottom: 0;">Recent Open Tenders</h3>
            <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn-link">View All →</a>
        </div>

        <c:choose>
            <c:when test="${not empty openTenders}">
                <div class="gov-tender-grid" style="grid-template-columns: 1fr;">
                    <c:forEach items="${openTenders}" var="tender" begin="0" end="2">
                        <div class="gov-tender-card">
                            <div class="gov-tender-header">
                                <span class="gov-tender-ref">${tender.referenceNumber}</span>
                                <span class="gov-badge gov-badge-open">OPEN</span>
                            </div>
                            <div class="gov-tender-body">
                                <h4 class="gov-tender-title">${tender.title}</h4>
                                <span class="gov-tender-category">${tender.categoryDisplayName}</span>
                                <div class="gov-tender-detail">
                                    <span>📅</span> Deadline: 
                                    <fmt:formatDate value="${tender.submissionDeadline}" pattern="dd MMM yyyy, HH:mm" />
                                </div>
                                <div class="gov-tender-detail">
                                    <span>💰</span> Estimated Value: M <fmt:formatNumber value="${tender.estimatedValue}" pattern="#,##0.00" />
                                </div>
                            </div>
                            <div class="gov-tender-footer">
                                <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${tender.tenderId}" 
                                   class="gov-btn gov-btn-primary gov-btn-sm">View Details</a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:when>
            <c:otherwise>
                <p class="gov-text-muted">No open tenders available at this time.</p>
            </c:otherwise>
        </c:choose>
    </div>

    <!-- My Recent Bids Column -->
    <div class="gov-card">
        <div class="gov-flex gov-justify-between gov-items-center gov-mb-md">
            <h3 style="margin-bottom: 0;">My Recent Bids</h3>
            <a href="${pageContext.request.contextPath}/supplier/bids" class="gov-btn-link">View All →</a>
        </div>

        <c:choose>
            <c:when test="${not empty myBids}">
                <div class="gov-table-wrapper">
                    <table class="gov-table">
                        <thead>
                            <tr>
                                <th>Tender</th>
                                <th>Bid Amount</th>
                                <th>Status</th>
                                <th>Submitted</th>
                            </tr>
                        </thead>
                        <tbody>
                        <c:forEach items="${myBids}" var="bid" begin="0" end="4">
                            <tr>
                                <td>
                                    <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${bid.tenderId}">
                                        ${bid.tenderTitle}
                                    </a>
                                </td>
                                <td>M <fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" /></td>
                            <td>
                            <c:choose>
                                <c:when test="${bid.status == 'WON'}">
                                    <span class="gov-badge gov-badge-won">WON</span>
                                </c:when>
                                <c:when test="${bid.status == 'NOT_WON'}">
                                    <span class="gov-badge gov-badge-lost">NOT WON</span>
                                </c:when>
                                <c:when test="${bid.status == 'EVALUATED'}">
                                    <span class="gov-badge gov-badge-evaluated">EVALUATED</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="gov-badge gov-badge-submitted">SUBMITTED</span>
                                </c:otherwise>
                            </c:choose>
                            </td>
                            <td><fmt:formatDate value="${bid.submittedAt}" pattern="dd/MM/yyyy" /></td>
                            </tr>
                        </c:forEach>
                        </tbody>
                    </table>
                </div>
            </c:when>
            <c:otherwise>
                <p class="gov-text-muted">You haven't submitted any bids yet.</p>
                <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn gov-btn-primary gov-btn-sm">
                    Browse Open Tenders
                </a>
            </c:otherwise>
        </c:choose>
    </div>
</div>

<!-- Quick Actions -->
<div class="gov-card gov-mt-lg">
    <h3>Quick Actions</h3>
    <div class="gov-flex gov-gap-md gov-flex-wrap">
        <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn gov-btn-primary">
            <span>🔍</span> Browse Tenders
        </a>
        <a href="${pageContext.request.contextPath}/supplier/bids" class="gov-btn gov-btn-secondary">
            <span>📝</span> View My Bids
        </a>
    </div>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Supplier object available as ${supplier} from SupplierDashboardServlet
    - openTenders limited to 3 items using begin="0" end="2"
    - myBids limited to 5 items using begin="0" end="4"
    - wonCount calculated using JSTL c:forEach and c:if
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />