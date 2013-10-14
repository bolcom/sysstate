<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="groupConfigurationsForm" method="POST">
				<table id="id-form">
					<c:forEach var="groupConfigurationForm" items="${groupConfigurationsForm.groupConfigurationForms}" varStatus="gccvStatus">
						<tr><th>${groupConfigurationForm.groupName}</th></tr>
						<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="groupConfigurationForms[${gccvStatus.index}].groupClass"/>
								<jsp:param name="type" value="hidden"/>
						</jsp:include>						
						<c:forEach var="contextValue" items="${groupConfigurationForm.contextValues}" varStatus="cvStatus">
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="groupConfigurationForms[${gccvStatus.index}].contextValues[${cvStatus.index}].id"/>
								<jsp:param name="type" value="hidden"/>
							</jsp:include>						
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="groupConfigurationForms[${gccvStatus.index}].contextValues[${cvStatus.index}].value"/>
								<jsp:param name="label" value="${contextValue.title}"/>
							</jsp:include>						
						</c:forEach>
					</c:forEach>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>
				</table>
			</sf:form>
		</td>
	</tr>
</table>
