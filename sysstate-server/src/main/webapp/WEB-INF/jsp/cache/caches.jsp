<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table id="instance-worker-plugin-config-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Cache</a></th>
		<th class="table-header-repeat line-left"><a href="">options</a></th>
		
	</tr>
	<tbody>
		<c:forEach items="${cacheManagerNames}" var="cacheManagerName" varStatus="varStatus">
			<tr class="${(varStatus.index)%2 eq 0 ? '' : 'alternate-row' }">
				<td>${cacheManagerName}</td>
				<td>
					<a href="${cacheManagerName}/index.html">Statistics</a>
				</td>
			</tr>
		</c:forEach>
	</tbody>	
</table>


