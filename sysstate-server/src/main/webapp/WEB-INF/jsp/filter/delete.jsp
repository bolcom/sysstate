<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table>
	<tr valign="top">
		<td>
			<form action="${contextPath}/filter/${filter.id}/delete/confirmed.html" method="post">
				<table id="id-form">
					<tr>
						<th colspan="3"><h3>Confirm delete</h3></th>
					</tr>
					<tr>
						<th valign="top">FilterName:</th>
						<td><c:out value="${filter.name}" escapeXml="true"/></td>
					<tr>
					<tr>
						<th valign="top">Tags:</th>
						<td><c:out value="${filter.tags}" escapeXml="true"/></td>
					<tr>
					<tr>
						<th valign="top">Search:</th>
						<td><c:out value="${filter.search}" escapeXml="true"/></td>
					<tr>
					<tr>
						<th valign="top">Related Views:</th>
						<td>
							<c:forEach var="view" items="${filter.views}" varStatus="vs"><c:out value="${view.name}" escapeXml="true"/>${!vs.last ? ', ': ''}</c:forEach>
						</td>
					<tr>					
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>		
				</table>
			</form>
		</td>
	</tr>
</table>
