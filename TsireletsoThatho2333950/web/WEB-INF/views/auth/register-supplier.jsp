<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Supplier Registration - Ministry of Public Works | ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css?v=2">
    </head>
    <body class="gov-register-page">

        <div class="gov-register-container">
            <div class="gov-register-card">
                <div class="gov-register-header">
                    <div class="gov-register-logo">
                        <div class="gov-navbar-logo-icon">MPW</div>
                    </div>
                    <h1 class="gov-register-title">Supplier Registration</h1>
                    <p class="gov-register-subtitle">Ministry of Public Works - Kingdom of Lesotho</p>
                    <p class="gov-register-description">Register your company to participate in government tenders</p>
                </div>

                <div class="gov-register-body">
                    <c:if test="${not empty requestScope.errorMessage}">
                        <div class="gov-alert gov-alert-error">
                            <span class="gov-alert-icon">⚠</span>
                            <span class="gov-alert-message">${requestScope.errorMessage}</span>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/register" method="POST" class="gov-form">
                        <div class="gov-form-group">
                            <label for="companyName" class="gov-form-label">
                                Company / Individual Name <span class="gov-required">*</span>
                            </label>
                            <input type="text" id="companyName" name="companyName" 
                                   class="gov-form-input" 
                                   value="${requestScope.companyName}" 
                                   placeholder="Enter your company or individual name" 
                                   required>
                        </div>

                        <div class="gov-form-row">
                            <div class="gov-form-group">
                                <label for="email" class="gov-form-label">
                                    Email Address <span class="gov-required">*</span>
                                </label>
                                <input type="email" id="email" name="email" 
                                       class="gov-form-input" 
                                       value="${requestScope.email}" 
                                       placeholder="Enter your email address" 
                                       required>
                                <p class="gov-form-hint">This will be used for login and notifications</p>
                            </div>

                            <div class="gov-form-group">
                                <label for="contactNumber" class="gov-form-label">
                                    Contact Number <span class="gov-required">*</span>
                                </label>
                                <input type="tel" id="contactNumber" name="contactNumber" 
                                       class="gov-form-input" 
                                       value="${requestScope.contactNumber}" 
                                       placeholder="+266 0000 0000" 
                                       required>
                            </div>
                        </div>

                        <div class="gov-form-group">
                            <label for="physicalAddress" class="gov-form-label">
                                Physical Address <span class="gov-required">*</span>
                            </label>
                            <textarea id="physicalAddress" name="physicalAddress" 
                                      class="gov-form-textarea" 
                                      rows="3" 
                                      placeholder="Enter your physical business address" 
                                      required>${requestScope.physicalAddress}</textarea>
                        </div>

                        <div class="gov-form-row">
                            <div class="gov-form-group">
                                <label for="password" class="gov-form-label">
                                    Password <span class="gov-required">*</span>
                                </label>
                                <input type="password" id="password" name="password" 
                                       class="gov-form-input" 
                                       placeholder="Minimum 6 characters" 
                                       required>
                            </div>

                            <div class="gov-form-group">
                                <label for="confirmPassword" class="gov-form-label">
                                    Confirm Password <span class="gov-required">*</span>
                                </label>
                                <input type="password" id="confirmPassword" name="confirmPassword" 
                                       class="gov-form-input" 
                                       placeholder="Re-enter your password" 
                                       required>
                            </div>
                        </div>

                        <div class="gov-form-actions">
                            <button type="submit" class="gov-btn gov-btn-primary">
                                Register as Supplier
                            </button>
                            <a href="${pageContext.request.contextPath}/login" class="gov-btn gov-btn-secondary">
                                Back to Login
                            </a>
                        </div>
                    </form>

                    <div class="gov-register-footer">
                        <p class="gov-register-login">
                            Already registered? 
                            <a href="${pageContext.request.contextPath}/login" class="gov-register-link">
                                Sign in here
                            </a>
                        </p>
                    </div>
                </div>

                <jsp:useBean id="now" class="java.util.Date" />
                <div class="gov-register-copyright">
                    &copy; <fmt:formatDate value="${now}" pattern="yyyy" /> Ministry of Public Works, Kingdom of Lesotho
                </div>
            </div>
        </div>

    </body>
</html>