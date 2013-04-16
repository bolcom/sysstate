<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
	<c:set var="count" value="${viewResults.instanceCount}" />
	<c:set var="displayTotal" value="${count.stable}" />
	<c:set var="displayType" value="stable" />
	
	<c:if test="${count.unstable > 0 || count.error > 0}">
		<c:set var="displayTotal" value="${count.unstable + count.error }" />
		<c:set var="displayType" value="unstableError" />
	</c:if>
	
	<c:set var="colCount" value="0"/>
	<c:set var="rowCount" value="0"/>
	<c:forEach var="instance" items="${viewResults.instances}" varStatus="varstat">
		<c:set var="state" value="${instance.state}"/>
		<c:set var="stateType" value="${state.state}"/>
	
		<c:if test="${(displayType == 'stable' && stateType == 'STABLE')  || (displayType == 'unstableError' && (stateType == 'ERROR' || stateType == 'UNSTABLE')) }">
			<div style="margin:10px;">
				<div class="print print_${stateType}">
					<div class="state ">
						<font class="prjEnvName">
							${instance.projectEnvironment.project.name} - ${instance.projectEnvironment.environment.name}
						</font><br />
						<c:out value="${state.description}" escapeXml="true"/><br/>
						<pre>${state.message}</pre>
					</div>
				</div>
			</div>
		</c:if>
		
	</c:forEach>