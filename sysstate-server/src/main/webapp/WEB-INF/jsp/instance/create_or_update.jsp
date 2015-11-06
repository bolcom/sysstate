<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table>
	<tr valign="top">
		<td>
				<!--  start step-holder -->
				<div id="step-holder">
					<div class="step-no">1</div>
					<div class="step-light-left">Choose Type</div>
					<div class="step-light-right">&nbsp;</div>
					<div class="step-no-off">2</div>
					<div class="step-dark-left">Edit Details</div>
					<div class="step-dark-round">&nbsp;</div>
					<div class="clear"></div>
				</div>
				<!--  end step-holder -->
		
			<sf:form commandName="instance" method="POST">
				<table id="id-form">
					<tr>
						<th colspan="3"><h3>General</h3></th>
					</tr>
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

					<tr>
						<th colspan="3"><h3>StateResolver</h3></th>
						
						<c:forEach var="propertyMeta" items="${propertyMetas}">
							<c:set var="type" value="textarea"/>

							<c:set var="items" value=""/>
							<c:if test="${propertyMeta.lov != null}">
								<c:set var="type" value="select"/>
								<c:set var="items" value="lovValues"/>
								<c:set var="lovValues" value="${propertyMeta.lov}" scope="request"/>
							</c:if>
						
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="configuration['${propertyMeta.id}']"/>
								<jsp:param name="label" value="${propertyMeta.title}"/>
								<jsp:param name="description" value="${propertyMeta.description}"/>								
								<jsp:param name="cols" value="80"/>
								<jsp:param name="rows" value="3"/>
								<jsp:param name="items" value="${items}"/>
								<jsp:param name="type" value="${type}"/>
							</jsp:include>
						</c:forEach>
					<tr>
						<th colspan="3"><h3>Options</h3></th>
					</tr>
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
						<jsp:param name="label" value="Type"/>
						<jsp:param name="type" value="hidden"/>
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
