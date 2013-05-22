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
				<th class="table-header-repeat line-left"><a href="">PreviousFireTime</a></th>
				<th class="table-header-repeat line-left"><a href="">NextFireTime</a></th>
				<th class="table-header-repeat line-left"><a href="">EndTime</a></th>
				<th class="table-header-repeat line-left"><a href="">RunTime</a></th>
			</tr>
		 	<c:forEach var="task" items="${tasks}" varStatus="vsTasks">
		 		<c:set var="triggers" value="${task.triggers }"/>
		 		<c:set var="jobExecutionContext" value="${task.jobExecutionContext }"/>
		 		<c:forEach var="trigger" items="${triggers}" varStatus="vsTriggers">
			 		<tr>
			 			<c:set var="jobDetail" value="${task.jobDetail}"/>
			 			<c:if test="${vsTriggers.first}">
				 			<td rowspan="${fn:length(triggers)}">${jobDetail.name}</td>
				 			<td rowspan="${fn:length(triggers)}">${jobDetail.group}</td>
			 			</c:if>
			 			<td>${trigger.previousFireTime }</td>
			 			<td>${trigger.nextFireTime }</td>
			 			<td>${trigger.endTime}</td>
			 			<td>${jobExecutionContext.jobRunTime}</td>
			 		</tr>
		 		
		 		</c:forEach>
		 	</c:forEach> 
		</table>
</div>
