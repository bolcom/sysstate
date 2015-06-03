<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<div style="margin-top:30px;">

	<h1>TaskList</h1>
		<table id="queue-table">
			<tr>
				<th class="table-header-repeat line-left"><a href="">Name</a></th>
				<th class="table-header-repeat line-left"><a href="">Group</a></th>
				<th class="table-header-repeat line-left"><a href="">RunTime</a></th>
				<th class="table-header-repeat line-left"><a href="">Details</a></th>
				<th class="table-header-repeat line-left"><a href="">Options</a></th>
			</tr>
		 	<c:forEach var="task" items="${tasks}" varStatus="vsTasks">
		 		<tr>
		 			<td>${task.name}</td>
		 			<td>${task.group}</td>
		 			<td>${task.runTime}</td>
		 			<td>
		 				<c:choose>
		 					<c:when test="${task.type == 'instance' }">
		 						<c:set var="instance" value="${task.instance}"/>
		 						<p>Instance: ${instance.name}</p>
		 						<p>Project: [${instance.projectEnvironment.project.name}] </p>
		 						<p>Environment: [${instance.projectEnvironment.environment.name}] </p>
		 					</c:when>
		 				</c:choose>
		 			</td>
					<td>
		 				<c:choose>
		 					<c:when test="${task.type == 'instance' }">
								<sc:authorize url="/instance/${instance.id}/toggle/enabled">
									<a href="${contextPath}/instance/${instance.id}/toggle/enabled.html"  title="${instance.enabled ? 'Disable' : 'Enable'}"><img src="${contextPath}/images/power_${instance.enabled ? 'off' : 'on'}.png"  width="12" height="12"/></a>
								</sc:authorize>
								<sc:authorize url="/instance/${instance.id}/update">
									<a href="${contextPath}/instance/${instance.id}/update.html" title="Edit Instance"><img src="${contextPath}/images/edit.gif" /></a>
								</sc:authorize>
								
		 					</c:when>
		 				</c:choose>
					</td>		 			
				</tr>
		 	</c:forEach> 
		</table>
</div>
