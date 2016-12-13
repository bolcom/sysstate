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
						<jsp:param name="disabled" value="true"/>						
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="firstName"/>
						<jsp:param name="label" value="First Name"/>
						<jsp:param name="disabled" value="true"/>						
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="lastName"/>
						<jsp:param name="label" value="Last Name"/>
						<jsp:param name="disabled" value="true"/>						
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="login"/>
						<jsp:param name="label" value="Login"/>
						<jsp:param name="disabled" value="true"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="password"/>
						<jsp:param name="label" value="Password"/>
						<jsp:param name="type" value="password"/>
						<jsp:param name="disabled" value="true"/>						
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="token"/>
						<jsp:param name="label" value="Token"/>
						<jsp:param name="disabled" value="true"/>
					</jsp:include>
				</table>
			</sf:form>
		</td>
	</tr>
</table>
	