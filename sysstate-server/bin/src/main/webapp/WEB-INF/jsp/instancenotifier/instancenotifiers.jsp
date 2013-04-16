<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="list" uri="http://www.unionsoft.nl/list/"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table id="instance-worker-plugin-config-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">ID</a></th>
		<th class="table-header-repeat line-left"><a href="">Instance</a></th>
		<th class="table-header-repeat line-left"><a href="">Plugin</a></th>
		<th class="table-header-repeat line-left"><a href="">options</a></th>
		
	</tr>
	<tbody>
		<c:forEach items="${listResponse.results}" var="instanceNotifier" varStatus="varStatus">
			<tr class="${(varStatus.index)%2 eq 0 ? '' : 'alternate-row' }">
				<td>${instanceNotifier.id}</td>
				<td>${instanceNotifier.instance.name}</td>
				<td>${instanceNotifier.pluginClass}</td>
				<td>
					<a href="update.html?instanceNotifierId=${instanceNotifier.id}">Update</a>
					<a href="delete.html?instanceNotifierId=${instanceNotifier.id}">Delete</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>	
</table>
<a href="create.html">New!</a>

