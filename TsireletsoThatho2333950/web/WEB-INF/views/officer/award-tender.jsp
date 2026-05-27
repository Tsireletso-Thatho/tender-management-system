<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<jsp:include page="/WEB-INF/views/common/header.jsp">
    <jsp:param name="title" value="Award Tender" />
</jsp:include>

<!-- ==================================================== -->
<!-- AWARD TENDER - PROCOUREGOV TENDER MANAGEMENT          -->
<!-- Author: Tsireletso Thatho (2333950)                   -->
<!-- ==================================================== -->

<div class="gov-page-header">
    <div class="gov-page-title">
        <h1>Award Tender: ${tender.referenceNumber}</h1>
        <a href="${pageContext.request.contextPath}/officer/tender/view?id=${tender.tenderId}" class="gov-btn gov-btn-secondary">
            <span>←</span> Back to Tender
        </a>
    </div>
    <p class="gov-text-muted">Select the winning bid based on evaluation scores.</p>
</div>

<!-- Tender Summary -->
<div class="gov-card gov-mb-lg">
    <h3>Tender Information</h3>
    <div class="gov-grid gov-grid-cols-2 gov-gap-md">
        <div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Title:</span>
                <span class="gov-error-detail-value">${tender.title}</span>
            </div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Estimated Value:</span>
                <span class="gov-error-detail-value">M <fmt:formatNumber value="${tender.estimatedValue}" pattern="#,##0.00" /></span>
            </div>
        </div>
        <div>
            <div class="gov-error-detail-item">
                <span class="gov-error-detail-label">Lowest Bid:</span>
                <span class="gov-error-detail-value">M <fmt:formatNumber value="${lowestBid}" pattern="#,##0.00" /></span>
            </div>
        </div>
    </div>
</div>

<!-- Ranked Bids for Selection -->
<div class="gov-card">
    <h3>Ranked Bids</h3>
    <p class="gov-text-muted">Bids are ranked by final evaluation score (averaged across all evaluators).</p>

    <c:choose>
        <c:when test="${not empty bids}">
            <div class="gov-table-wrapper">
                <table class="gov-table">
                    <thead>
                        <tr>
                            <th>Rank</th>
                            <th>Supplier</th>
                            <th>Bid Amount (M)</th>
                            <th>Timeline (Days)</th>
                            <th>Final Score</th>
                            <th>Action</th>
                        </tr>
                    </thead>
                    <tbody>
                    <c:forEach items="${bids}" var="bid">
                        <tr>
                            <td>
                        <c:choose>
                            <c:when test="${bid.rank == 1}">
                                <span class="gov-rank-badge gov-rank-1">🥇</span>
                            </c:when>
                            <c:when test="${bid.rank == 2}">
                                <span class="gov-rank-badge gov-rank-2">🥈</span>
                            </c:when>
                            <c:when test="${bid.rank == 3}">
                                <span class="gov-rank-badge gov-rank-3">🥉</span>
                            </c:when>
                            <c:otherwise>
                                <span class="gov-rank-badge gov-rank-other">${bid.rank}</span>
                            </c:otherwise>
                        </c:choose>
                        </td>
                        <td><strong>${bid.supplierName}</strong></td>
                        <td><fmt:formatNumber value="${bid.bidAmount}" pattern="#,##0.00" /></td>
                        <td>${bid.proposedTimelineDays}</td>
                        <td>
                            <strong class="gov-score-display">
                                <fmt:formatNumber value="${bid.finalScore}" pattern="#0.00" />
                            </strong>
                        </td>
                        <td>
                            <button type="button" class="gov-btn gov-btn-gold gov-btn-sm" 
                                    onclick="selectWinner('${bid.bidId}', '${bid.supplierName}', '${bid.bidAmount}')">
                                Select as Winner
                            </button>
                        </td>
                        </tr>
                    </c:forEach>
                    </tbody>
                </table>
            </div>
        </c:when>
        <c:otherwise>
            <p class="gov-text-muted">No bids available for award.</p>
        </c:otherwise>
    </c:choose>
</div>

<!-- Award Form Section (Hidden by default, shown when winner selected) -->
<div class="gov-card gov-mt-lg" id="award-form-section" style="display: none;">
    <h3>Award Contract</h3>
    <form action="${pageContext.request.contextPath}/officer/tender/award" method="POST" onsubmit="return confirmAward('Are you sure you want to award this tender? This action cannot be undone.')">
        <input type="hidden" name="tenderId" value="${tender.tenderId}">
        <input type="hidden" name="winningBidId" id="winningBidId">

        <div class="gov-form-group">
            <label class="gov-form-label">Selected Supplier</label>
            <input type="text" id="selectedSupplier" class="gov-form-input" readonly>
        </div>

        <div class="gov-form-group">
            <label for="awardedValue" class="gov-form-label">
                Awarded Value (Maloti) <span class="gov-required">*</span>
            </label>
            <input type="number" id="awardedValue" name="awardedValue" 
                   class="gov-form-input" 
                   step="0.01" min="0.01" 
                   required>
        </div>

        <div class="gov-form-group">
            <label for="justification" class="gov-form-label">
                Award Justification <span class="gov-required">*</span>
            </label>
            <textarea id="justification" name="justification" 
                      class="gov-form-textarea" 
                      rows="4" 
                      placeholder="Provide justification for awarding this tender..." 
                      required></textarea>
        </div>

        <div class="gov-form-actions">
            <button type="submit" class="gov-btn gov-btn-primary">
                <span>🏆</span> Confirm Award
            </button>
            <button type="button" class="gov-btn gov-btn-secondary" onclick="hideAwardForm()">
                Cancel
            </button>
        </div>
    </form>
</div>

<!-- Winner Selection JavaScript -->
<script>
    function selectWinner(bidId, supplierName, bidAmount) {
        document.getElementById('winningBidId').value = bidId;
        document.getElementById('selectedSupplier').value = supplierName;
        document.getElementById('awardedValue').value = bidAmount;
        document.getElementById('award-form-section').style.display = 'block';
        document.getElementById('award-form-section').scrollIntoView({behavior: 'smooth'});
    }

    function hideAwardForm() {
        document.getElementById('award-form-section').style.display = 'none';
        document.getElementById('winningBidId').value = '';
        document.getElementById('selectedSupplier').value = '';
        document.getElementById('awardedValue').value = '';
        document.getElementById('justification').value = '';
    }
</script>

<!-- Hidden Developer Notes -->
<c:if test="${false}">
    DEVELOPER NOTES:
    - Only tenders in EVALUATED status can be awarded
    - Winning bid selection triggers award form display
    - Award justification is required
    - Upon award: tender status → AWARDED, bid outcomes updated, emails sent
</c:if>

<jsp:include page="/WEB-INF/views/common/footer.jsp" />