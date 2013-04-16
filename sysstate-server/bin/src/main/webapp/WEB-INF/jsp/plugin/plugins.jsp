<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table  id="plugin-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">Name</a></th>
		<th class="table-header-repeat line-left"><a href="">Options</a></th>
	</tr>
	<c:forEach items="${plugins}" var="plugin">
		<tr>
			<td>${plugin.pluginClass.simpleName}</td>
			<td>
				<a href="${plugin.pluginClass.name}/update.html">Configuration</a>
			</td>
		</tr>
	</c:forEach>
</table>
