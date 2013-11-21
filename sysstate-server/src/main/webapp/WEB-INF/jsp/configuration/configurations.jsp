<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="propertyMetaListsForm" method="POST">
				<table id="id-form">
					<c:forEach var="propertyMetaList" items="${propertyMetaListsForm.propertyMetaLists}" varStatus="gccvStatus">
						<tr><th>${propertyMetaList.name}
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="propertyMetaLists[${gccvStatus.index}].id"/>
								<jsp:param name="type" value="hidden"/>
							</jsp:include>						
						</th></tr>
						
						<c:forEach var="propertyMetaValue" items="${propertyMetaList.propertyMetaValues}" varStatus="cvStatus">
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="propertyMetaLists[${gccvStatus.index}].propertyMetaValues[${cvStatus.index}].id"/>
								<jsp:param name="type" value="hidden"/>
							</jsp:include>						
							<c:set var="type" value=""/>
							<c:set var="items" value=""/>
							<c:if test="${propertyMetaValue.lov != null}">
								<c:set var="type" value="select"/>
								<c:set var="items" value="lovValues"/>
								<c:set var="lovValues" value="${propertyMetaValue.lov}" scope="request"/>
							</c:if>
								
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="propertyMetaLists[${gccvStatus.index}].propertyMetaValues[${cvStatus.index}].value"/>
								<jsp:param name="type" value="${type}"/>
								<jsp:param name="items" value="${items}"/>
								<jsp:param name="label" value="${propertyMetaValue.title}"/>
								<jsp:param name="allowEmpty" value="${propertyMetaValue.nullable}"/>
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
