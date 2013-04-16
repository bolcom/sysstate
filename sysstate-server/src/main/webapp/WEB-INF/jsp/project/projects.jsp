<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table  id="project-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Order</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${projects}" var="project">
		<tr>
			<td>${project.name}</td>
			<td>${project.order}</td>
			<td>
				<a href="${project.id}/update.html?rUrl=${contextPath}/project/index.html">Update</a> |
				<a href="${project.id}/delete.html?rUrl=${contextPath}/project/index.html">Delete</a> |
				<a href="${contextPath}/filter/project/${project.id}/index.html">Instances</a>
			</td>
		</tr>
	</c:forEach>
</table>
<a href="create.html?rUrl=${contextPath}/project/index.html">New</a>
