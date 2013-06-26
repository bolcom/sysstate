<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<h2>Choose the type of StateResolver you would like to create:</h2>
<c:forEach var="stateResolverName" items="${stateResolverNames}">
	<a href="${stateResolverName}/create.html">${stateResolverName}</a><br/>
</c:forEach>

