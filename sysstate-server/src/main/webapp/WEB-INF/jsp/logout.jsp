<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<h3>Please confirm your logout.</h3>
<table>
	<tr valign="top">
		<td>
			<form action="<c:url value='/logout.html' />" method="post">
			<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				<table id="id-form">
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
						<jsp:param name="disabled" value="true"/>
					</jsp:include>
				</table>
			</form>
		</td>
	</tr>
</table>
