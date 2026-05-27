<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - Ministry of Public Works | ProcureGov</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
</head>
<body class="gov-login-page">

    <div class="gov-login-container">
        <div class="gov-login-card">
            <div class="gov-login-header">
                <div class="gov-login-logo">
                    <div class="gov-navbar-logo-icon">MPW</div>
                </div>
                <h1 class="gov-login-title">ProcureGov</h1>
                <p class="gov-login-subtitle">Ministry of Public Works - Kingdom of Lesotho</p>
                <p class="gov-login-description">Tender Management System</p>
            </div>
            
            <div class="gov-login-body">
                <c:if test="${not empty sessionScope.successMessage}">
                    <div class="gov-alert gov-alert-success">
                        <span class="gov-alert-icon">✓</span>
                        <span class="gov-alert-message">${sessionScope.successMessage}</span>
                    </div>
                    <c:remove var="successMessage" scope="session" />
                </c:if>
                
                <c:if test="${not empty requestScope.errorMessage}">
                    <div class="gov-alert gov-alert-error">
                        <span class="gov-alert-icon">⚠</span>
                        <span class="gov-alert-message">${requestScope.errorMessage}</span>
                    </div>
                </c:if>
                
                <form action="${pageContext.request.contextPath}/login" method="POST" class="gov-form">
                    <div class="gov-form-group">
                        <label for="email" class="gov-form-label">
                            Email Address <span class="gov-required">*</span>
                        </label>
                        <input type="email" id="email" name="email" 
                               class="gov-form-input" 
                               value="${requestScope.email}" 
                               placeholder="Enter your email address" 
                               required autofocus>
                    </div>
                    
                    <div class="gov-form-group">
                        <label for="password" class="gov-form-label">
                            Password <span class="gov-required">*</span>
                        </label>
                        <input type="password" id="password" name="password" 
                               class="gov-form-input" 
                               placeholder="Enter your password" 
                               required>
                    </div>
                    
                    <div class="gov-form-actions">
                        <button type="submit" class="gov-btn gov-btn-primary gov-w-full">
                            Sign In
                        </button>
                    </div>
                </form>
                
                <div class="gov-login-footer">
                    <p class="gov-login-register">
                        New supplier? 
                        <a href="${pageContext.request.contextPath}/register" class="gov-login-link">
                            Register your company
                        </a>
                    </p>
                </div>
            </div>
            
            <jsp:useBean id="now" class="java.util.Date" />
            <div class="gov-login-copyright">
                &copy; <fmt:formatDate value="${now}" pattern="yyyy" /> Ministry of Public Works, Kingdom of Lesotho
            </div>
        </div>
    </div>

</body>
</html>