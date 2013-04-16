<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table  id="plugin-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${filters}" var="filter">
		<tr>
			<td>${filter.name}</td>
			<td>
				<a href="${filter.id}/delete.html">Delete</a> |
				<a href="${contextPath}/filter/load/${filter.id}/index.html">Load</a>
			</td>
		</tr>
	</c:forEach>
</table>
