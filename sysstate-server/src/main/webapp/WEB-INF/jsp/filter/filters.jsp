<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table  id="plugin-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Query Statistics</a></th>
		<th class="table-header-repeat line-left"><a href="">Sync Statistics</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${filters}" var="filter">
		<tr>
			<td><c:out value="${filter.name}" escapeXml="true"/></td>
			<td>
				Last query date: ${filter.lastQueryDate}<br/>
				Total times queried: ${filter.queryCount}<br/>
				Average query time: ${filter.averageQueryTime}ms<br/>
				Last query time: ${filter.lastQueryTime}ms<br/>
			</td>
			<td>
				Last sync date: ${filter.lastSyncDate}
			</td>

			<td>
				<a href="${filter.id}/delete.html">Delete</a> |
				<a href="${contextPath}/filter/load/${filter.id}/index.html">Load</a>
			</td>
		</tr>
	</c:forEach>
</table>
