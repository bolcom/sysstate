<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table id="property-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">key</a></th>
		<th class="table-header-repeat line-left"><a href="">value</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${properties}" var="property">
		<tr>
			<td>${property.key}</td>
			<td>${property.value}</td>
			<td>
				<a href="update.html?id=${property.key}">Update</a> | 
				<a href="delete.html?id=${property.key}">Delete</a>
			</td>
		</tr>
	</c:forEach>
</table>
<a href="create.html">New!</a>
