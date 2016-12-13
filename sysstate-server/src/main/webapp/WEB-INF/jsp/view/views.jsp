<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<table id="user-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">name</a></th>
		<th class="table-header-repeat line-left"><a href="">template</a></th>
		<th class="table-header-repeat line-left"><a href="">commonTags</a></th>
		<th class="table-header-repeat line-left"><a href="">filter</a></th>
		<th class="table-header-repeat line-left"><a href="">Request statistics</a></th>
		<th class="table-header-repeat line-left"><a href="">options</a></th>
		
		
	</tr>
	<tbody>
		<c:forEach items="${views}" var="view" varStatus="varStatus">
			<tr class="${(varStatus.index)%2 eq 0 ? '' : 'alternate-row' }">
				<td>${view.name}</td>
				<td><a href="/template/${view.template.name}/update.html">${view.template.name}</a></td>
				<td>${view.commonTags}</td>
				<td>
					<c:if test="${not empty view.filter.id}">
						<a href="${contextPath}/filter/load/${view.filter.id}/index.html">${view.filter.name}</a>
					</c:if>
				</td>
				<td>
					Last request date: ${view.lastRequestDate}<br/>
					Total times requested: ${view.requestCount}<br/>
					Average request time: ${view.averageRequestTime}ms<br/>
					Last request time: ${view.lastRequestTime}ms
				</td>
				<td>
					<sc:authorize url="/view/${view.name}/update">
						<a href="${view.name}/update.html">Update</a> |
					</sc:authorize>
					<sc:authorize url="/view/${view.name}/delete">
						<a href="${view.name}/delete.html">Delete</a> |
					</sc:authorize>
					<sc:authorize url="/view/${view.name}/index">
						<a href="${view.name}/index.html" title="Index">Index</a>
					</sc:authorize>
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<a href="create.html">New!</a>
