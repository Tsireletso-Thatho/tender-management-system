<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="All Tenders" />
</jsp:include>

<!-- ==================================================== -->
<!-- LIST TENDERS - PROCOUREGOV TENDER MANAGEMENT          -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>All Tenders</h1>
        <a href="${pageContext.request.contextPath}/officer/tender/create" class="gov-btn gov-btn-primary">
            <span>➕</span> Create New Tender
        </a>
    </div>
    <p class="gov-text-muted">View and manage all tenders. Use filters to find specific tenders.</p>
</div>

<!-- Filter Bar -->
<div class="gov-card gov-mb-lg">
    <form action="${pageContext.request.contextPath}/officer/tender/list" method="GET" class="gov-flex gov-gap-md gov-items-end gov-flex-wrap">
        <div class="gov-form-group" style="min-width: 200px;">
            <label for="status" class="gov-form-label">Filter by Status</label>
            <select id="status" name="status" class="gov-form-select">
                <option value="">All Statuses</option>
                <option value="DRAFT" ${statusFilter == 'DRAFT' ? 'selected' : ''}>Draft</option>
                <option value="OPEN" ${statusFilter == 'OPEN' ? 'selected' : ''}>Open</option>
                <option value="CLOSED" ${statusFilter == 'CLOSED' ? 'selected' : ''}>Closed</option>
                <option value="UNDER_EVALUATION" ${statusFilter == 'UNDER_EVALUATION' ? 'selected' : ''}>Under Evaluation</option>
                <option value="EVALUATED" ${statusFilter == 'EVALUATED' ? 'selected' : ''}>Evaluated</option>
                <option value="AWARDED" ${statusFilter == 'AWARDED' ? 'selected' : ''}>Awarded</option>
            </select>
        </div>

        <div class="gov-form-group" style="min-width: 200px;">
            <label for="category" class="gov-form-label">Filter by Category</label>
            <select id="category" name="category" class="gov-form-select">
                <option value="">All Categories</option>
                <option value="CONSTRUCTION" ${categoryFilter == 'CONSTRUCTION' ? 'selected' : ''}>Construction</option>
                <option value="ROADS" ${categoryFilter == 'ROADS' ? 'selected' : ''}>Roads</option>
                <option value="ELECTRICAL" ${categoryFilter == 'ELECTRICAL' ? 'selected' : ''}>Electrical</option>
                <option value="PLUMBING" ${categoryFilter == 'PLUMBING' ? 'selected' : ''}>Plumbing</option>
                <option value="GENERAL_SERVICES" ${categoryFilter == 'GENERAL_SERVICES' ? 'selected' : ''}>General Services</option>
            </select>
        </div>

        <div class="gov-form-group">
            <button type="submit" class="gov-btn gov-btn-primary">
                <span>🔍</span> Apply Filters
            </button>
            <a href="${pageContext.request.contextPath}/officer/tender/list" class="gov-btn gov-btn-secondary">
                Clear
            </a>
        </div>
    </form>
</div>

<!-- Statistics Summary Badges -->
<div class="gov-flex gov-gap-sm gov-mb-md gov-flex-wrap">
    <span class="gov-badge gov-badge-draft">Draft: ${draftCount}</span>
    <span class="gov-badge gov-badge-open">Open: ${openCount}</span>
    <span class="gov-badge gov-badge-closed">Closed: ${closedCount}</span>
    <span class="gov-badge gov-badge-evaluation">Under Evaluation: ${evaluationCount}</span>
    <span class="gov-badge gov-badge-evaluated">Evaluated: ${evaluatedCount}</span>
    <span class="gov-badge gov-badge-awarded">Awarded: ${awardedCount}</span>
</div>

<!-- Tenders Table -->
<div class="gov-card">
    <div class="gov-flex gov-justify-between gov-items-center gov-mb-md">
        <h3 style="margin-bottom: 0;">Tenders (${totalCount})</h3>
    </div>

    <c:choose>
        <c:when test="${not empty tenders}">
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
                    <c:forEach items="${tenders}" var="tender">
                        <tr>
                            <td class="gov-font-mono">${tender.referenceNumber}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}">
                                    ${tender.title}
                                </a>
                            </td>
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
                                <c:if test="${tender.status == 'EVALUATED'}">
                                    <a href="${pageContext.request.contextPath}/officer/tender/award?id=${tender.tenderId}" 
                                       class="gov-table-action-btn" title="Award">🏆</a>
                                </c:if>
                                <c:if test="${tender.status == 'AWARDED'}">
                                    <a href="${pageContext.request.contextPath}/officer/tender/award-notice?id=${tender.tenderId}" 
                                       class="gov-table-action-btn" title="View Award">📜</a>
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
            <div class="gov-alert gov-alert-info">
                <span class="gov-alert-icon">ℹ️</span>
                <span class="gov-alert-message">No tenders found matching your criteria.</span>
            </div>
            <div class="gov-mt-md">
                <a href="${pageContext.request.contextPath}/officer/tender/create" class="gov-btn gov-btn-primary">
                    Create Your First Tender
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Filtering by status and category using GET parameters
    - Statistics counts provided by ListTendersServlet
    - Actions change based on tender status
    - Reference number uses monospace font
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />