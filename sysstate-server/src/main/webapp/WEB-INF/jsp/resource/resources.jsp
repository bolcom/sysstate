<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<table class="basic-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Manager</a></th>
		<th class="table-header-repeat line-left"><a href="">Configuration</a></th>
	</tr>
	<tbody>
		<c:forEach items="${resources}" var="resource" varStatus="varStatus">
			<tr class="${(varStatus.index)%2 eq 0 ? '' : 'alternate-row' }">
				<td><c:out value="${resource.name}" escapeXml="true"/></td>
				<td><c:out value="${resource.manager}" escapeXml="true"/></td>
				<td>
					<c:forEach items="${resource.configuration}" var="conf">
						<c:out value="${conf.key}" escapeXml="true"/>=<c:out value="${conf.value}" escapeXml="true"/><br/>
					</c:forEach>
				</td>
			</tr>
		</c:forEach>
	</tbody>	
</table>


