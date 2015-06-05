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
				<th class="table-header-repeat line-left"><a href="">Next Run</a></th>
				<th class="table-header-repeat line-left"><a href="">Last Run</a></th>
				<th class="table-header-repeat line-left"><a href="">RunTime</a></th>
				<th class="table-header-repeat line-left"><a href="">Options</a></th>
			</tr>
		 	<c:forEach var="task" items="${tasks}" varStatus="vsTasks">
		 		<tr>
		 			<td>
		 				<c:choose>
		 					<c:when test="${task.type == 'instance' }">
		 						<c:set var="instance" value="${task.instance}"/>
		 						<c:out value="[${instance.projectEnvironment.project.name}-${instance.projectEnvironment.environment.name}] ${instance.name}"/>
		 					</c:when>
		 					<c:otherwise>${task.name}</c:otherwise>
		 				</c:choose>
					</td>
		 			<td>${task.group}</td>
		 			<td>${task.nextRunTime}</td>
		 			<td>${task.lastRunTime}</td>
		 			<td>${task.runTime}</td>
					<td>
		 				<c:choose>
		 					<c:when test="${task.type == 'instance' }">
		 						<c:set var="instance" value="${task.instance}"/>
								<sc:authorize url="/instance/${instance.id}/toggle/enabled">
									<a href="${contextPath}/instance/${instance.id}/toggle/enabled.html?rUrl=${contextPath}/scheduler/index.html"  title="${instance.enabled ? 'Disable' : 'Enable'}"><img src="${contextPath}/images/power_${instance.enabled ? 'off' : 'on'}.png"  width="12" height="12"/></a>
								</sc:authorize>
								<sc:authorize url="/instance/${instance.id}/update">
									<a href="${contextPath}/instance/${instance.id}/update.html?rUrl=${contextPath}/scheduler/index.html" title="Edit Instance"><img src="${contextPath}/images/edit.gif" /></a>
								</sc:authorize>
								 <sc:authorize url="/instance/${instance.id}/refresh">
									<a href="${contextPath}/instance/${instance.id}/refresh.html?rUrl=${contextPath}/scheduler/index.html" title="Refresh Instance"><img src="${contextPath}/images/refresh.gif" /></a>
								</sc:authorize>
		 					</c:when>
		 				</c:choose>
					</td>		 			
				</tr>
		 	</c:forEach> 
		</table>
</div>
