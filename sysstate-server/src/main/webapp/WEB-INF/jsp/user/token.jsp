<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<h3>Reset token for the following user:</h3>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="user" method="POST">
				<table id="id-form">
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="id"/>
						<jsp:param name="type" value="hidden"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="firstName"/>
						<jsp:param name="label" value="First Name"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="lastName"/>
						<jsp:param name="label" value="Last Name"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>
				</table>
			</sf:form>
		</td>
	</tr>
</table>