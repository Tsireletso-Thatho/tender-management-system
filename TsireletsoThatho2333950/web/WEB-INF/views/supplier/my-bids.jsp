<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="My Bids" />
</jsp:include>

<!-- ==================================================== -->
<!-- MY BIDS - PROCOUREGOV TENDER MANAGEMENT               -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>My Bids</h1>
        <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn gov-btn-primary">
            <span>🔍</span> Browse Tenders
        </a>
    </div>
    <p class="gov-text-muted">Track the status of all your submitted bids.</p>
</div>

<!-- Bids Table -->
<div class="gov-card">
    <h3>All Bids (${bidCount})</h3>

    <c:choose>
        <c:when test="${not empty bids}">
            <div class="gov-table-wrapper">
                <table class="gov-table">
                    <thead>
                        <tr>
                            <th>Tender Reference</th>
                            <th>Tender Title</th>
                            <th>Bid Amount (M)</th>
                            <th>Timeline</th>
                            <th>Submitted</th>
                            <th>Status</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${bids}" var="bid">
                        <tr>
                            <td class="gov-font-mono">${bid.tenderReference}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${bid.tenderId}">
                                    ${bid.tenderTitle}
                                </a>
                            </td>
                            <td><fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" /></td>
                        <td>${bid.proposedTimelineDays} days</td>
                        <td><fmt:formatDate value="${bid.submittedAt}" pattern="dd/MM/yyyy" /></td>
                        <td>
                        <c:choose>
                            <c:when test="${bid.status == 'SUBMITTED'}">
                                <span class="gov-badge gov-badge-submitted">SUBMITTED</span>
                            </c:when>
                            <c:when test="${bid.status == 'EVALUATED'}">
                                <span class="gov-badge gov-badge-evaluated">EVALUATED</span>
                            </c:when>
                            <c:when test="${bid.status == 'WON'}">
                                <span class="gov-badge gov-badge-won">🏆 WON</span>
                            </c:when>
                            <c:when test="${bid.status == 'NOT_WON'}">
                                <span class="gov-badge gov-badge-lost">NOT WON</span>
                            </c:when>
                        </c:choose>
                        </td>
                        <td>
                            <div class="gov-table-actions">
                                <a href="${pageContext.request.contextPath}/supplier/tender/detail?id=${bid.tenderId}" 
                                   class="gov-table-action-btn" title="View Tender">👁</a>
                                <c:if test="${bid.status == 'WON'}">
                                    <a href="${pageContext.request.contextPath}/supplier/award?tenderId=${bid.tenderId}" 
                                       class="gov-table-action-btn" title="View Award">🏆</a>
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
                <span class="gov-alert-message">You haven't submitted any bids yet.</span>
            </div>
            <div class="gov-mt-md">
                <a href="${pageContext.request.contextPath}/supplier/tenders" class="gov-btn gov-btn-primary">
                    Browse Open Tenders
                </a>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - All bids for the logged-in supplier are displayed
    - Status badges show current bid state
    - Award notice link appears when bid.status == 'WON'
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />