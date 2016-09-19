<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table  id="text-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Tags</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${texts}" var="text">
		<tr>
			<td>${text.name}</td>
			<td>${text.tags}</td>
			<td>
				<a href="${text.name}/update.html?rUrl=${contextPath}/text/index.html">Update</a> |
				<a href="${text.name}/delete.html?rUrl=${contextPath}/text/index.html">Delete</a> |
			</td>
		</tr>
	</c:forEach>
</table>
<a href="create.html?rUrl=${contextPath}/text/index.html">New</a>
