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
	<c:set var="cols" value="1" />
	<c:choose>
		<c:when test="${displayTotal > 20}">
			<c:set var="cols" value="5" />
		</c:when>
		<c:when test="${displayTotal > 10}">
			<c:set var="cols" value="4" />
		</c:when>
		<c:when test="${displayTotal >= 5}">
			<c:set var="cols" value="3" />
		</c:when>
		<c:when test="${displayTotal >= 4}">
			<c:set var="cols" value="2" />
		</c:when>
		<c:when test="${displayTotal >= 3}">
			<c:set var="cols" value="1" />
		</c:when>
	</c:choose>
	<c:set var="rows">
		<fmt:formatNumber maxFractionDigits="0" value="${(displayTotal/cols)+0.5}" />
	</c:set>

	<c:set var="colCount" value="0"/>
	<c:set var="rowCount" value="0"/>
	<c:forEach var="instance" items="${viewResults.instances}" varStatus="varstat">
		<c:set var="state" value="${instance.state}"/>
		<c:set var="stateType" value="${state.state}"/>
	
		<c:if test="${(displayType == 'stable' && stateType == 'STABLE')  || (displayType == 'unstableError' && (stateType == 'ERROR' || stateType == 'UNSTABLE')) }">
			<div class="card" style="left: ${(colCount/cols)*100}%; top: ${(rowCount/rows)*100}%; width: ${100/cols}%; height: ${100/rows}%; margin: 0.2%;">
			
				<div class="print print_${stateType}">
				
					<div class="state">
						<font class="prjEnvName">
							${instance.projectEnvironment.project.name} - ${instance.projectEnvironment.environment.name}
						</font><br />
						<c:out value="${state.description}" escapeXml="true"/><br/>
						<pre><c:out value="${state.message}" escapeXml="true"/></pre>
					</div>
				</div>
			</div>
			<c:choose>
				<c:when test="${colCount >= cols-1 }">
					<c:set var="colCount" value="0"/>
					<c:set var="rowCount" value="${rowCount+1 }"/>
				</c:when>
				<c:otherwise>
					<c:set var="colCount" value="${colCount+1 }"/>
				</c:otherwise>
			</c:choose>
			
		</c:if>
		
	</c:forEach>

