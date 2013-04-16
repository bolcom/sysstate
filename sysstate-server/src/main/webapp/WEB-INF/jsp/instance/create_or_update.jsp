<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="instance" method="POST">
				<table id="id-form">
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="id"/>
						<jsp:param name="type" value="hidden"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="name"/>
						<jsp:param name="label" value="Name"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="homepageUrl"/>
						<jsp:param name="label" value="HomepageUrl"/>
						<jsp:param name="type" value="textarea"/>
						<jsp:param name="cols" value="80"/>
						<jsp:param name="rows" value="10"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="configuration"/>
						<jsp:param name="label" value="Configuration"/>
						<jsp:param name="type" value="textarea"/>
						<jsp:param name="cols" value="80"/>
						<jsp:param name="rows" value="10"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="enabled"/>
						<jsp:param name="label" value="Enabled"/>
						<jsp:param name="type" value="checkbox"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="tags"/>
						<jsp:param name="label" value="Tags"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="refreshTimeout"/>
						<jsp:param name="label" value="RefreshTimeout"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="pluginClass"/>
						<jsp:param name="itemLabel" value="simpleName"/>
						<jsp:param name="itemValue" value="name"/>
						<jsp:param name="label" value="Type"/>
						<jsp:param name="type" value="select"/>
						<jsp:param name="items" value="stateResolverNames"/>
					</jsp:include>
					<tr>
						<th valign="top"><c:out value="ProjectEnvironment"/>:</th>
						<td>
							
							<sf:select path="projectEnvironment.id">
								<c:forEach var="projectEnvironment" items="${projectEnvironments}">
									<sf:option value="${projectEnvironment.id}">${projectEnvironment.project.name} - ${projectEnvironment.environment.name}</sf:option>
								</c:forEach>
							</sf:select>
						</td>
						<td>
							<c:set var="error"><sf:errors path="projectEnvironment.id"/></c:set>
							<c:if test="${not empty error }">
								<div class="error-left"></div>
								<div class="error-inner"><c:out value="${error }"/></div>
							</c:if>
						</td>
					</tr>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>					
				</table>
			</sf:form>
		</td>
	</tr>
</table>
