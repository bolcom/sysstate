<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<h1>Instance</h1>
<h3>Details</h3>
<table id="instances-table">
	<tr>
		<th class="table-header-repeat line-left">&nbsp;</th>
		<th class="table-header-repeat line-left"><a href="">Env</a></th>
		<th class="table-header-repeat line-left"><a href="">Config</a></th>
		<th class="table-header-repeat line-left"><a href="">State</a></th>
		<th class="table-header-repeat line-left"><a href="">Scheduling</a></th>
	</tr>
	<c:set var="index" value="0"/>
	<jsp:include page="/WEB-INF/jsp/common/instance.jsp">
		<jsp:param name="alternateRow" value="false"/>
		<jsp:param name="id" value="hidden"/>
		<jsp:param name="options" value="false"/>
		<jsp:param name="state" value="true"/>
	</jsp:include>
</table>

<h3>Configuration</h3>
<table id="instances-configuration-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Property</a></th>
		<th class="table-header-repeat line-left"><a href="">Value</a></th>
	</tr>
	
	<c:forEach var="conf" items="${instance.configuration}">
		<tr class="${param.alternateRow ? '' : 'alternate-row' }">
			<td><c:out value="${conf.key}" escapeXml="true"/></td>
			<td><c:out value="${conf.value}" escapeXml="true"/></td>
		</tr>
	</c:forEach>
</table>
<h3>Last States Per Type</h3>
<table id="states-table">
	<tr>
		<th class="table-header-repeat line-left">&nbsp;</th>
		<th class="table-header-repeat line-left"><a href="">Last</a></th>
		<th class="table-header-repeat line-left"><a href="">Description</a></th>
		<th class="table-header-repeat line-left"><a href="">responseTime</a></th>
		<th class="table-header-repeat line-left"><a href="">creationDate</a></th>
		<th class="table-header-repeat line-left"><a href="">lastUpdate</a></th>
	</tr>
	<c:forEach var="statePerType" items="${statesPerType}" >
		<c:set var="state" value="${statePerType}" scope="request"/>
		<jsp:include page="/WEB-INF/jsp/common/state.jsp">
			<jsp:param name="alternateRow" value="false"/>
		</jsp:include>
	</c:forEach>
</table>

<h3>Instance Links</h3>
<table id="instancelink-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Direction</a></th>
		<th class="table-header-repeat line-left"><a href="">From</a></th>
		<th class="table-header-repeat line-left"><a href="">To</a></th>
	</tr>
	<c:forEach var="instanceLink" items="${instanceLinks}" >
		<tr>
			<td><c:out value="${instanceLink.name}" escapeXml="true"/></td>
			<td><c:out value="${instanceLink.direction}" escapeXml="true"/></td>
			<td><a href="${contextPath}/instance/${instanceLink.instanceFromId}/details.html">${instanceLink.instanceFromId}</a></td>
			<td><a href="${contextPath}/instance/${instanceLink.instanceToId}/details.html">${instanceLink.instanceToId}</a></td>
		</tr>	
	</c:forEach>
</table>
