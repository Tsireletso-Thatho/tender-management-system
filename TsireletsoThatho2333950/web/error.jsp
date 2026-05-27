<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page isErrorPage="true" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <c:choose>
        <c:when test="${pageContext.errorData.statusCode == 404}">
            <title>Page Not Found - Ministry of Public Works | ProcureGov</title>
        </c:when>
        <c:when test="${pageContext.errorData.statusCode == 403}">
            <title>Access Denied - Ministry of Public Works | ProcureGov</title>
        </c:when>
        <c:when test="${pageContext.errorData.statusCode == 400}">
            <title>Bad Request - Ministry of Public Works | ProcureGov</title>
        </c:when>
        <c:when test="${pageContext.errorData.statusCode == 500}">
            <title>Server Error - Ministry of Public Works | ProcureGov</title>
        </c:when>
        <c:otherwise>
            <title>Error - Ministry of Public Works | ProcureGov</title>
        </c:otherwise>
    </c:choose>

    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body class="gov-error-page">

    <div class="gov-error-container">

        <header class="gov-error-header-bar">
            <div class="gov-container">
                <div class="gov-error-brand">
                    <div class="gov-navbar-logo-icon">MPW</div>
                    <div class="gov-error-brand-text">
                        <span class="gov-error-ministry">Ministry of Public Works</span>
                        <span class="gov-error-system">ProcureGov Tender Management System</span>
                    </div>
                </div>
            </div>
        </header>

        <main class="gov-error-main">
            <div class="gov-container gov-container-narrow">
                <div class="gov-error-card">

                    <div class="gov-error-header">
                        <c:choose>
                            <c:when test="${pageContext.errorData.statusCode == 404}">
                                <div class="gov-error-code">404</div>
                                <h1 class="gov-error-title">Page Not Found</h1>
                                <p class="gov-error-description">
                                    The page you are looking for might have been removed, 
                                    had its name changed, or is temporarily unavailable.
                                </p>
                            </c:when>
                            <c:when test="${pageContext.errorData.statusCode == 403}">
                                <div class="gov-error-code">403</div>
                                <h1 class="gov-error-title">Access Denied</h1>
                                <p class="gov-error-description">
                                    You do not have permission to access this resource.
                                </p>
                            </c:when>
                            <c:when test="${pageContext.errorData.statusCode == 400}">
                                <div class="gov-error-code">400</div>
                                <h1 class="gov-error-title">Bad Request</h1>
                                <p class="gov-error-description">
                                    The request could not be understood by the server.
                                </p>
                            </c:when>
                            <c:when test="${pageContext.errorData.statusCode == 500}">
                                <div class="gov-error-code">500</div>
                                <h1 class="gov-error-title">Internal Server Error</h1>
                                <p class="gov-error-description">
                                    The server encountered an unexpected condition.
                                </p>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${not empty pageContext.errorData.statusCode}">
                                    <div class="gov-error-code">${pageContext.errorData.statusCode}</div>
                                </c:if>
                                <c:if test="${empty pageContext.errorData.statusCode}">
                                    <div class="gov-error-code gov-error-code-small">Error</div>
                                </c:if>
                                <h1 class="gov-error-title">Unexpected Error</h1>
                                <p class="gov-error-description">
                                    An unexpected error occurred while processing your request.
                                </p>
                            </c:otherwise>
                        </c:choose>
                    </div>

                    <div class="gov-error-body">

                        <c:if test="${not empty sessionScope.errorMessage}">
                            <div class="gov-alert gov-alert-error">
                                <span class="gov-alert-icon">⚠</span>
                                <span class="gov-alert-message">${sessionScope.errorMessage}</span>
                            </div>
                            <c:remove var="errorMessage" scope="session" />
                        </c:if>

                        <c:if test="${not empty pageContext.exception}">
                            <div class="gov-error-details">
                                <div class="gov-error-details-title">
                                    <span class="gov-error-details-icon">📋</span>
                                    Technical Details
                                </div>
                                <div class="gov-error-detail-item">
                                    <span class="gov-error-detail-label">Exception:</span>
                                    <span class="gov-error-detail-value">${pageContext.exception.getClass().getName()}</span>
                                </div>
                                <c:if test="${not empty pageContext.exception.message}">
                                    <div class="gov-error-detail-item">
                                        <span class="gov-error-detail-label">Message:</span>
                                        <span class="gov-error-detail-value">${pageContext.exception.message}</span>
                                    </div>
                                </c:if>
                                <div class="gov-error-detail-item">
                                    <span class="gov-error-detail-label">Request URI:</span>
                                    <span class="gov-error-detail-value">${pageContext.errorData.requestURI}</span>
                                </div>
                                <div class="gov-error-detail-item">
                                    <span class="gov-error-detail-label">Servlet:</span>
                                    <span class="gov-error-detail-value">${pageContext.errorData.servletName}</span>
                                </div>
                            </div>
                        </c:if>

                        <div class="gov-error-actions">
                            <a href="${pageContext.request.contextPath}/" class="gov-btn gov-btn-primary">
                                <span class="gov-btn-icon-left">🏠</span>
                                Return to Home
                            </a>

                            <c:choose>
                                <c:when test="${not empty sessionScope.loggedInUser}">
                                    <c:choose>
                                        <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                                            <a href="${pageContext.request.contextPath}/officer/dashboard" class="gov-btn gov-btn-secondary">
                                                <span class="gov-btn-icon-left">📊</span>
                                                Officer Dashboard
                                            </a>
                                        </c:when>
                                        <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                                            <a href="${pageContext.request.contextPath}/supplier/dashboard" class="gov-btn gov-btn-secondary">
                                                <span class="gov-btn-icon-left">📊</span>
                                                Supplier Dashboard
                                            </a>
                                        </c:when>
                                        <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                                            <a href="${pageContext.request.contextPath}/evaluator/dashboard" class="gov-btn gov-btn-secondary">
                                                <span class="gov-btn-icon-left">📊</span>
                                                Evaluator Dashboard
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/login" class="gov-btn gov-btn-secondary">
                                                <span class="gov-btn-icon-left">🔐</span>
                                                Login
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </c:when>
                                <c:otherwise>
                                    <a href="${pageContext.request.contextPath}/login" class="gov-btn gov-btn-secondary">
                                        <span class="gov-btn-icon-left">🔐</span>
                                        Login
                                    </a>
                                </c:otherwise>
                            </c:choose>

                            <button type="button" class="gov-btn gov-btn-outline" onclick="history.back()">
                                <span class="gov-btn-icon-left">←</span>
                                Go Back
                            </button>
                        </div>

                        <div class="gov-error-help">
                            <p class="gov-error-help-text">
                                If the problem persists, please contact the ICT Helpdesk at 
                                <strong>helpdesk@mpw.gov.ls</strong> or call <strong>+266 2222 0000</strong>
                            </p>
                        </div>

                    </div>

                    <jsp:useBean id="now" class="java.util.Date" />
                    <div class="gov-error-footer">
                        <div class="gov-error-footer-links">
                            <a href="${pageContext.request.contextPath}/" class="gov-error-footer-link">Home</a>
                            <span class="gov-error-footer-separator">|</span>
                            <a href="#" class="gov-error-footer-link">Help</a>
                            <span class="gov-error-footer-separator">|</span>
                            <a href="#" class="gov-error-footer-link">Contact Support</a>
                        </div>
                        <div class="gov-error-copyright">
                            &copy; <fmt:formatDate value="${now}" pattern="yyyy" /> Ministry of Public Works, Kingdom of Lesotho. All rights reserved.
                        </div>
                    </div>

                </div>
            </div>
        </main>

    </div>

</body>
</html>