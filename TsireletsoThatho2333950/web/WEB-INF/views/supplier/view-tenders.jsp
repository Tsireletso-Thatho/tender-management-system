<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Browse Tenders" />
</jsp:include>

<!-- ==================================================== -->
<!-- BROWSE TENDERS - PROCOUREGOV TENDER MANAGEMENT        -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Browse Open Tenders</h1>
    </div>
    <p class="gov-text-muted">View and bid on available government tenders.</p>
</div>

<!-- Category Filter -->
<div class="gov-card gov-mb-lg">
    <form action="${pageContext.request.contextPath}/supplier/tenders" method="GET" class="gov-flex gov-gap-md gov-items-end">
        <div class="gov-form-group" style="min-width: 250px;">
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
                <span>🔍</span> Apply Filter
            </button>
            <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn gov-btn-secondary">
                Clear
            </a>
        </div>
    </form>
</div>

<!-- Tenders Grid -->
<div class="gov-card">
    <h3>Available Tenders (${totalCount})</h3>

    <c:choose>
        <c:when test="${not empty tenders}">
            <div class="gov-tender-grid">
                <c:forEach items="${tenders}" var="tender">
                    <div class="gov-tender-card">
                        <div class="gov-tender-header">
                            <span class="gov-tender-ref">${tender.referenceNumber}</span>
                            <span class="gov-badge gov-badge-${fn:toLowerCase(tender.status)}">${tender.status}</span>
                        </div>
                        <div class="gov-tender-body">
                            <h4 class="gov-tender-title">${tender.title}</h4>
                            <span class="gov-tender-category">${tender.categoryDisplayName}</span>
                            <p class="gov-tender-description">
                            <c:choose>
                                <c:when test="${fn:length(tender.description) > 150}">
                                    ${fn:substring(tender.description, 0, 150)}...
                                </c:when>
                                <c:otherwise>
                                    ${tender.description}
                                </c:otherwise>
                            </c:choose>
                            </p>
                            <div class="gov-tender-detail">
                                <span>📅</span> Deadline: 
                                <strong><fmt:formatDate value="${tender.submissionDeadline}" pattern="dd MMM yyyy, HH:mm" /></strong>
                            </div>
                            <div class="gov-tender-detail">
                                <span>💰</span> Estimated Value: 
                                <strong>M <fmt:formatNumber value="${tender.estimatedValue}" pattern="#,##0.00" /></strong>
                            </div>
                            <div class="gov-tender-detail">
                                <span>📊</span> Bids Submitted: ${tender.bidCount}
                            </div>
                        </div>
                        <div class="gov-tender-footer">
                            <div class="gov-flex gov-gap-sm gov-items-center">
                                <c:choose>
                                    <c:when test="${tender.status == 'OPEN'}">
                                        <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${tender.tenderId}" 
                                           class="gov-btn gov-btn-primary">View & Bid</a>
                                    </c:when>
                                    <c:when test="${tender.status == 'CLOSED'}">
                                        <span class="gov-badge gov-badge-closed">CLOSED</span>
                                        <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${tender.tenderId}" 
                                           class="gov-btn gov-btn-secondary">View</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${tender.tenderId}" 
                                           class="gov-btn gov-btn-secondary">View Details</a>
                                    </c:otherwise>
                                </c:choose>

                                <c:if test="${not empty tender.noticeDocumentPath}">
                                    <a href="${pageContext.request.contextPath}/tender/download?id=${tender.tenderId}" 
                                       class="gov-btn gov-btn-outline gov-btn-sm" target="_blank" title="View PDF">
                                        <span>📄</span>
                                    </a>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </c:when>
        <c:otherwise>
            <div class="gov-alert gov-alert-info">
                <span class="gov-alert-icon">ℹ️</span>
                <span class="gov-alert-message">No tenders available at this time. Please check back later.</span>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Category filter uses GET parameter 'category'
    - fn:substring used to truncate long descriptions
    - Status determines button text and action
    - PDF button appears when notice document exists
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />