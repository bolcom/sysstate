<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<table id="template-table">

	<tr>
		<th class="table-header-repeat line-left"><a href="">id</a></th>
		<th class="table-header-repeat line-left"><a href="">System Template</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${templates}" var="template">
		<tr>
			<td>${template.id}</td>
			<td>${template.systemTemplate}
				<c:if test="${template.systemTemplate}">
					<font style="font-style: italic;">(Cannot be deleted)</font>
				</c:if>
			</td>
			<td>

				<sc:authorize url="/template/${template.id}/update">
					<a href="${template.id}/update.html">Update</a> | 
				</sc:authorize>
				<c:choose>
					<c:when test="${template.systemTemplate}">
						<sc:authorize url="/template/${template.id}/restore">
							<a href="${template.id}/restore.html">Restore defaults</a> | 
						</sc:authorize>
					</c:when>
					<c:otherwise>
						<sc:authorize url="/template/${template.id}/delete">
							<a href="${template.id}/delete.html">Delete</a> | 
						</sc:authorize>	
					</c:otherwise>
				</c:choose>
				<sc:authorize url="/index">
					<a href="../index.html?templateId=${template.id}" target="_BLANK">Show</a>
				</sc:authorize>
			</td>
		</tr>
	</c:forEach>
</table>
<a href="create.html">New!</a>

