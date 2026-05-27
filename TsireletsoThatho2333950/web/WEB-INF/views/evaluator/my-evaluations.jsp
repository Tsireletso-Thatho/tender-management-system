<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="My Evaluations" />
</jsp:include>

<!-- ==================================================== -->
<!-- MY EVALUATIONS - PROCOUREGOV TENDER MANAGEMENT        -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>My Evaluations</h1>
    </div>
    <p class="gov-text-muted">View your evaluation history and scores submitted.</p>
</div>

<!-- Evaluation History Table -->
<div class="gov-card">
    <h3>Evaluation History</h3>

    <c:choose>
        <c:when test="${not empty myScores}">
            <div class="gov-table-wrapper">
                <table class="gov-table">
                    <thead>
                        <tr>
                            <th>Date</th>
                            <th>Tender</th>
                            <th>Supplier</th>
                            <th>Technical Score</th>
                            <th>Price Score</th>
                            <th>Timeline Score</th>
                            <th>Weighted Total</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${myScores}" var="score">
                        <tr>
                            <td><fmt:formatDate value="${score.submittedAt}" pattern="dd/MM/yyyy HH:mm" /></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/evaluator/results?tenderId=${score.tenderId}">
                                ${score.tenderReference}
                            </a>
                        </td>
                        <td>${score.supplierName}</td>
                        <td><fmt:formatNumber value="${score.technicalScore}" pattern="#0.00" />%</td>
                        <td><fmt:formatNumber value="${score.priceScore}" pattern="#0.00" />%</td>
                        <td><fmt:formatNumber value="${score.timelineScore}" pattern="#0.00" />%</td>
                        <td><strong><fmt:formatNumber value="${score.weightedTotal}" pattern="#0.00" /></strong></td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <p class="gov-text-muted">You haven't submitted any evaluations yet.</p>
            <a href="${pageContext.request.contextPath}/evaluator/tenders" class="gov-btn gov-btn-primary">
                View Available Tenders
            </a>
        </c:otherwise>
    </c:choose>
</div>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - myScores contains all evaluation scores submitted by this evaluator
    - Each score includes tender reference and supplier name for context
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />