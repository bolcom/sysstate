<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="rating" value="${param.rating}"/>
<c:choose>
	<c:when test="${rating < 0 }">
		N/A
	</c:when>
	<c:otherwise>
		<c:choose>
			<c:when test="${rating < 0 }">
				<c:set var="styleRating" value=""/>	
			</c:when>
			<c:when test="${rating < 25 }">
				<c:set var="styleRating" value="rating_0"/>	
			</c:when>
			<c:when test="${rating < 50 }">
				<c:set var="styleRating" value="rating_25"/>	
			</c:when>
			<c:when test="${rating < 75 }">
				<c:set var="styleRating" value="rating_50"/>	
			</c:when>
			<c:when test="${rating < 100 }">
				<c:set var="styleRating" value="rating_75"/>	
			</c:when>
			<c:when test="${rating == 100 }">
				<c:set var="styleRating" value="rating_100"/>	
			</c:when>
		</c:choose>
		<span class="row_prj_env_int_rating ${styleRating}"><img src="${contextPath}/images/transparant.gif"/></span>
	</c:otherwise>
</c:choose>