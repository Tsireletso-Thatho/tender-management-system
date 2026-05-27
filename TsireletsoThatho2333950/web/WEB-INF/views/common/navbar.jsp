<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<nav class="gov-navbar">
    <div class="gov-navbar-container">
        <div class="gov-navbar-brand">
            <a href="${pageContext.request.contextPath}/" class="gov-navbar-logo">
                <div class="gov-navbar-logo-icon">MPW</div>
                <div class="gov-navbar-title">
                    ProcureGov
                    <small>Ministry of Public Works</small>
                </div>
            </a>
        </div>

        <ul class="gov-navbar-nav">
            <c:choose>
                <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/officer/dashboard" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/officer/dashboard') ? 'gov-active' : ''}">
                            <span>📊</span> Dashboard
                        </a>
                    </li>
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/officer/tender/list" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/officer/tender') ? 'gov-active' : ''}">
                            <span>📋</span> Tenders
                        </a>
                    </li>
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/officer/tender/create" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/create') ? 'gov-active' : ''}">
                            <span>➕</span> New Tender
                        </a>
                    </li>
                </c:when>

                <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/supplier/dashboard" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/supplier/dashboard') ? 'gov-active' : ''}">
                            <span>📊</span> Dashboard
                        </a>
                    </li>
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/supplier/tenders" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/supplier/tenders') ? 'gov-active' : ''}">
                            <span>🔍</span> Browse Tenders
                        </a>
                    </li>
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/supplier/bids" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/supplier/bids') ? 'gov-active' : ''}">
                            <span>📝</span> My Bids
                        </a>
                    </li>
                </c:when>

                <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/evaluator/dashboard" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/evaluator/dashboard') ? 'gov-active' : ''}">
                            <span>📊</span> Dashboard
                        </a>
                    </li>
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/evaluator/tenders" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/evaluator/tenders') ? 'gov-active' : ''}">
                            <span>📋</span> Tenders for Evaluation
                        </a>
                    </li>
                    <li class="gov-navbar-item">
                        <a href="${pageContext.request.contextPath}/evaluator/results" 
                           class="gov-navbar-link ${fn:contains(pageContext.request.servletPath, '/results') ? 'gov-active' : ''}">
                            <span>🏆</span> Results
                        </a>
                    </li>
                </c:when>
            </c:choose>
        </ul>

        <div class="gov-navbar-user">
            <div class="gov-user-info">
                <div class="gov-user-name">${sessionScope.userEmail}</div>
                <div class="gov-user-role">
                    <c:choose>
                        <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">Procurement Officer</c:when>
                        <c:when test="${sessionScope.userRole == 'SUPPLIER'}">Supplier</c:when>
                        <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">Evaluation Committee</c:when>
                    </c:choose>
                </div>
            </div>
            <a href="${pageContext.request.contextPath}/logout" class="gov-navbar-logout" title="Logout">
                <span>🚪</span>
            </a>
        </div>
    </div>
</nav>