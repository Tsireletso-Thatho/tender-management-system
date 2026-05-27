<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <title>${param.title} - Ministry of Public Works | ProcureGov</title>
        <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    </head>
    <body>

    <c:if test="${not empty sessionScope.loggedInUser}">
        <jsp:include page="/WEB-INF/views/common/navbar.jsp" />
    </c:if>

    <main class="gov-main-content">
        <div class="gov-container">

            <c:if test="${not empty sessionScope.successMessage}">
                <div class="gov-alert gov-alert-success">
                    <span class="gov-alert-icon">✓</span>
                    <span class="gov-alert-message">${sessionScope.successMessage}</span>
                    <button type="button" class="gov-alert-close" onclick="this.parentElement.remove()">&times;</button>
                </div>
                <c:remove var="successMessage" scope="session" />
            </c:if>

            <c:if test="${not empty sessionScope.errorMessage}">
                <div class="gov-alert gov-alert-error">
                    <span class="gov-alert-icon">⚠</span>
                    <span class="gov-alert-message">${sessionScope.errorMessage}</span>
                    <button type="button" class="gov-alert-close" onclick="this.parentElement.remove()">&times;</button>
                </div>
                <c:remove var="errorMessage" scope="session" />
            </c:if>

            <c:if test="${not empty requestScope.errorMessage}">
                <div class="gov-alert gov-alert-error">
                    <span class="gov-alert-icon">⚠</span>
                    <span class="gov-alert-message">${requestScope.errorMessage}</span>
                    <button type="button" class="gov-alert-close" onclick="this.parentElement.remove()">&times;</button>
                </div>
            </c:if>