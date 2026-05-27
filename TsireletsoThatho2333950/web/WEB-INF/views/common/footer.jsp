<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

</div>
</main>

<footer class="gov-footer">
    <div class="gov-container">
        <div class="gov-footer-container">
            <jsp:useBean id="now" class="java.util.Date" />
            <div class="gov-footer-copyright">
                &copy; <fmt:formatDate value="${now}" pattern="yyyy" /> Ministry of Public Works, Kingdom of Lesotho. All rights reserved.
            </div>
            <ul class="gov-footer-links">
                <li><a href="${pageContext.request.contextPath}/" class="gov-footer-link">Home</a></li>
                <li><a href="#" class="gov-footer-link">Privacy Policy</a></li>
                <li><a href="#" class="gov-footer-link">Terms of Use</a></li>
                <li><a href="#" class="gov-footer-link">Contact ICT Support</a></li>
            </ul>
        </div>
    </div>
</footer>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        var alerts = document.querySelectorAll('.gov-alert');
        for (var i = 0; i < alerts.length; i++) {
            (function (alert) {
                setTimeout(function () {
                    alert.style.transition = 'opacity 0.3s ease';
                    alert.style.opacity = '0';
                    setTimeout(function () {
                        if (alert.parentNode) {
                            alert.remove();
                        }
                    }, 300);
                }, 5000);
            })(alerts[i]);
        }
    });

    function confirmDelete(message) {
        return confirm(message || 'Are you sure you want to delete this item? This action cannot be undone.');
    }

    function confirmPublish(message) {
        return confirm(message || 'Are you sure you want to publish this tender? Once published, it cannot be edited.');
    }

    function confirmAward(message) {
        return confirm(message || 'Are you sure you want to award this tender? This action cannot be undone.');
    }
</script>
</body>
</html>