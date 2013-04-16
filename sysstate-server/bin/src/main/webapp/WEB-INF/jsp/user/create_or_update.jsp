<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
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
						<jsp:param name="path" value="login"/>
						<jsp:param name="label" value="Login"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="password"/>
						<jsp:param name="label" value="Password"/>
						<jsp:param name="type" value="password"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="enabled"/>
						<jsp:param name="label" value="Enabled"/>
						<jsp:param name="type" value="checkbox"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="configuration"/>
						<jsp:param name="label" value="Configuration"/>
						<jsp:param name="type" value="textarea"/>
						<jsp:param name="cols" value="80"/>
						<jsp:param name="rows" value="10"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="roles"/>
						<jsp:param name="label" value="Roles"/>
						<jsp:param name="type" value="select"/>
						<jsp:param name="items" value="roles"/>
						<jsp:param name="size" value="10"/>
						<jsp:param name="multi" value="true"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>
				</table>
			</sf:form>
		</td>
	</tr>
</table>
	