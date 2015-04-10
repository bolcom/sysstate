<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<table id="template-table">

	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Content Type</a></th>
		<th class="table-header-repeat line-left"><a href="">Writer</a></th>
		<th class="table-header-repeat line-left"><a href="">Last Updated</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${templates}" var="template">
		<tr>
			<td>${template.name}</td>
			<td>${template.contentType}</td>
			<td>${template.writer}</td>
			<td>${template.lastUpdated}</td>
			<td>

				<sc:authorize url="/template/${template.name}/update">
					<a href="${template.name}/update.html">Update</a> | 
				</sc:authorize>
				<sc:authorize url="/template/${template.name}/delete">
					<a href="${template.name}/delete.html">Delete</a> | 
				</sc:authorize>	
				<sc:authorize url="/index">
					<a href="/template/render/${template.name}" target="_BLANK">Render</a>
				</sc:authorize>
			</td>
		</tr>
	</c:forEach>
</table>
<a href="create.html">New!</a>

