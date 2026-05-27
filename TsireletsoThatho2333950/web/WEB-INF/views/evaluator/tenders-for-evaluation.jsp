<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Tenders for Evaluation" />
</jsp:include>

<!-- ==================================================== -->
<!-- TENDERS FOR EVALUATION - PROCOUREGOV TENDER MANAGEMENT -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Tenders for Evaluation</h1>
    </div>
    <p class="gov-text-muted">View all tenders that are ready for evaluation.</p>
</div>

<!-- All Tenders in Evaluation Stage -->
<div class="gov-card gov-mb-lg">
    <h3>All Tenders in Evaluation Stage</h3>

    <c:choose>
        <c:when test="${not empty allEvaluationTenders}">
            <div class="gov-table-wrapper">
                <table class="gov-table">
                    <thead>
                        <tr>
                            <th>Reference</th>
                            <th>Title</th>
                            <th>Category</th>
                            <th>Status</th>
                            <th>Bids</th>
                            <th>Your Status</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${allEvaluationTenders}" var="tender">
                        <c:set var="isAvailable" value="false" />
                        <c:forEach items="${availableTenders}" var="avail">
                            <c:if test="${avail.tenderId == tender.tenderId}">
                                <c:set var="isAvailable" value="true" />
                            </c:if>
                        </c:forEach>

                        <tr>
                            <td class="gov-font-mono">${tender.referenceNumber}</td>
                            <td>
                                <a href="${pageContext.request.contextPath}/evaluator/evaluate?tenderId=${tender.tenderId}">
                                    ${tender.title}
                                </a>
                            </td>
                            <td>${tender.categoryDisplayName}</td>
                            <td>
                                <span class="gov-badge gov-badge-${fn:toLowerCase(tender.status)}">
                                    ${tender.status}
                                </span>
                            </td>
                            <td>${tender.bidCount}</td>
                            <td>
                        <c:choose>
                            <c:when test="${tender.bidCount == 0}">
                                <span class="gov-badge gov-badge-closed">NO BIDS</span>
                            </c:when>
                            <c:when test="${tender.evaluated}">
                                <span class="gov-badge gov-badge-evaluated">COMPLETED</span>
                            </c:when>
                            <c:otherwise>
                                <span class="gov-badge gov-badge-evaluation">PENDING</span>
                            </c:otherwise>
                        </c:choose>
                        </td>
                        <td>
                        <c:choose>
                            <c:when test="${tender.bidCount == 0}">
                                <a href="${pageContext.request.contextPath}/evaluator/tender/detail?id=${tender.tenderId}" 
                                   class="gov-btn gov-btn-secondary gov-btn-sm">View</a>
                            </c:when>
                            <c:when test="${tender.evaluated}">
                                <a href="${pageContext.request.contextPath}/evaluator/results?tenderId=${tender.tenderId}" 
                                   class="gov-btn gov-btn-secondary gov-btn-sm">View Results</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/evaluator/evaluate?tenderId=${tender.tenderId}" 
                                   class="gov-btn gov-btn-primary gov-btn-sm">Evaluate</a>
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
            <div class="gov-alert gov-alert-info">
                <span class="gov-alert-icon">ℹ️</span>
                <span class="gov-alert-message">No tenders are currently in the evaluation stage.</span>
            </div>
        </c:otherwise>
    </c:choose>
</div>

<!-- Tenders Awaiting Your Evaluation (Card View) -->
<div class="gov-card">
    <h3>Tenders Awaiting Your Evaluation</h3>

    <c:choose>
        <c:when test="${not empty availableTenders}">
            <div class="gov-tender-grid">
                <c:forEach items="${availableTenders}" var="tender">
                    <c:if test="${tender.bidCount > 0}">
                        <div class="gov-tender-card">
                            <div class="gov-tender-header">
                                <span class="gov-tender-ref">${tender.referenceNumber}</span>
                                <span class="gov-badge gov-badge-evaluation">PENDING</span>
                            </div>
                            <div class="gov-tender-body">
                                <h4 class="gov-tender-title">${tender.title}</h4>
                                <span class="gov-tender-category">${tender.categoryDisplayName}</span>
                                <div class="gov-tender-detail">
                                    <span>📊</span> Bids: ${tender.bidCount}
                                </div>
                                <div class="gov-tender-detail">
                                    <span>📅</span> Closed: 
                                    <fmt:formatDate value="${tender.closedAt}" pattern="dd MMM yyyy" />
                                </div>
                            </div>
                            <div class="gov-tender-footer">
                                <a href="${pageContext.request.contextPath}/evaluator/evaluate?tenderId=${tender.tenderId}" 
                                   class="gov-btn gov-btn-primary">Start Evaluation</a>
                            </div>
                        </div>
                    </c:if>
                </c:forEach>
            </div>
            <c:if test="${empty availableTendersWithBids}">
                <p class="gov-text-muted">You have no pending evaluations with bids at this time.</p>
            </c:if>
        </c:when>
        <c:otherwise>
            <p class="gov-text-muted">You have no pending evaluations at this time.</p>
        </c:otherwise>
    </c:choose>
</div>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />