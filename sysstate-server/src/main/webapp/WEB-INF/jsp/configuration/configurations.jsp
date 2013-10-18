<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="propertyMetaListsForm" method="POST">
				<table id="id-form">
					<c:forEach var="propertyMetaList" items="${propertyMetaListsForm.propertyMetaLists}" varStatus="gccvStatus">
						<tr><th>${propertyMetaList.name}</th></tr>
						<c:forEach var="propertyMeta" items="${propertyMetaList.propertyMetas}" varStatus="cvStatus">
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="propertyMetaLists[${gccvStatus.index}].propertyMetas[${cvStatus.index}].id"/>
								<jsp:param name="type" value="hidden"/>
							</jsp:include>						
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="propertyMetaLists[${gccvStatus.index}].propertyMetas[${cvStatus.index}].value"/>
								<jsp:param name="label" value="${propertyMeta.title}"/>
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
