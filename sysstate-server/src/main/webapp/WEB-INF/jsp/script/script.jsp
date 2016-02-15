<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<c:choose>
	<c:when test="${scriptEnabled}">
		<table>
			<tr valign="top">
				<td>
					<sf:form commandName="scriptExecutorForm" method="POST">
						<table id="id-form">
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="executorName"/>
								<jsp:param name="label" value="Executor Name"/>
								<jsp:param name="type" value="select"/>
								<jsp:param name="items" value="scriptExecutorNames"/>
							</jsp:include>	
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="path" value="contents"/>
								<jsp:param name="label" value="Script Contents"/>
								<jsp:param name="type" value="textarea"/>
								<jsp:param name="cols" value="80"/>
								<jsp:param name="rows" value="20"/>
							</jsp:include>
							<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
								<jsp:param name="type" value="submit"/>
							</jsp:include>					
						</table>
					</sf:form>
				</td>
			</tr>
		</table>
		<p>output: <pre><c:out value="${output}" escapeXml="true"/></pre></p>
		<p>executionResult:<pre><c:out value="${executionResult}" escapeXml="true"/></pre></p>
		<p>exception:<pre><c:out value="${exception}" escapeXml="true"/></pre></p>
	</c:when>
	<c:otherwise>
		<h3>Disabled feature</h3>
		<p>From a security perspective scripts are currently disabled. Set 'script.enabled=true' in your sysstate.properties to enable this feature.</p>
	</c:otherwise>
</c:choose>

