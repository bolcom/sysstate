<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table  id="plugin-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Last sync date</a></th>
		<th class="table-header-repeat line-left"><a href="">Last Query Date</a></th>
		<th class="table-header-repeat line-left"><a href="">Total times queried</a></th>
		<th class="table-header-repeat line-left"><a href="">Average query time</a></th>
		<th class="table-header-repeat line-left"><a href="">Last query time</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${filters}" var="filter">
		<tr>
			<td><c:out value="${filter.name}" escapeXml="true"/></td>
			<td>${filter.lastSyncDate}</td>
			<td>${filter.lastQueryDate}</td>
			<td>${filter.queryCount}</td>
			<td>${filter.averageQueryTime}ms</td>
			<td>${filter.lastQueryTime}ms</td>
			<td>
				<a href="${filter.id}/delete.html">Delete</a> |
				<a href="${contextPath}/filter/load/${filter.id}/index.html">Load</a>
			</td>
		</tr>
	</c:forEach>
</table>
