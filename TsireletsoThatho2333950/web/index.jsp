<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<c:choose>
    <c:when test="${not empty sessionScope.loggedInUser}">
        <c:choose>
            <c:when test="${sessionScope.userRole == 'PROCUREMENT_OFFICER'}">
                <c:redirect url="officer/dashboard"/>
            </c:when>
            <c:when test="${sessionScope.userRole == 'SUPPLIER'}">
                <c:redirect url="supplier/dashboard"/>
            </c:when>
            <c:when test="${sessionScope.userRole == 'EVALUATION_COMMITTEE'}">
                <c:redirect url="evaluator/dashboard"/>
            </c:when>
            <c:otherwise>
                <c:redirect url="login"/>
            </c:otherwise>
        </c:choose>
    </c:when>
    <c:otherwise>
        <c:redirect url="login"/>
    </c:otherwise>
</c:choose>