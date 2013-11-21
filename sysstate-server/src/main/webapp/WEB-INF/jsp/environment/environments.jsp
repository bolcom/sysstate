<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table  id="project-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Order</a></th>
		<th class="table-header-repeat line-left"><a href="">Tags</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${environments}" var="environment">
		<tr>
			<td>${environment.name}</td>
			<td>${environment.order}</td>
			<td>${environment.tags}</td>
			<td>
				<a href="${environment.id}/update.html">Update</a> | 
				<a href="${environment.id}/delete.html">Delete</a> |
				<a href="${contextPath}/filter/environment/${environment.id}/index.html">Instances</a>
			</td>
		</tr>
	</c:forEach>
</table>
<a href="create.html">New</a>

