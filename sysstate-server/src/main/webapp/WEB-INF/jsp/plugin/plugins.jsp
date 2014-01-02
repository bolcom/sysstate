<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="list" uri="http://www.unionsoft.nl/list/"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table class="basic-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Plugin</a></th>
		<th class="table-header-repeat line-left"><a href="">Version</a></th>
		
	</tr>
	<tbody>
		<c:forEach items="${plugins}" var="plugin" varStatus="varStatus">
			<tr class="${(varStatus.index)%2 eq 0 ? '' : 'alternate-row' }">
				<td><c:out value="${plugin.id}" escapeXml="true"/></td>
				<td><c:out value="${plugin.version}" escapeXml="true"/></td>			
			</tr>
		</c:forEach>
	</tbody>	
</table>


