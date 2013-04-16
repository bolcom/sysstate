<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<div>
	<h1>QueueStats:</h1>
	<p>
	Total Tasks: ${statistics.totalTasks} <br/>
	Executed Tasks: ${statistics.executedTasks} <br/>
	<hr>
	CorePoolSize: ${statistics.corePoolSize} <br/>
	KeepAliveTimeSecs: ${statistics.keepAliveTimeSecs} <br/>
	<hr>
	Remaining Tasks: ${statistics.remainingTasks} <br/>
	MaxPoolSize: ${statistics.maxPoolSize} <br/>
	CurrentLoad:  <fmt:formatNumber maxFractionDigits="2" value="${(statistics.remainingTasks > statistics.maxPoolSize ? 1 : statistics.remainingTasks/statistics.maxPoolSize) * 100}"/>% <br/>
	</p>
</div>
<div style="margin-top:30px;">

	<h1>TaskList</h1>
		<table id="queue-table">
			<tr>
				<th class="table-header-repeat line-left"><a href="">Reference</a></th>
				<th class="table-header-repeat line-left"><a href="">Name</a></th>
				<th class="table-header-repeat line-left"><a href="">CreationDate</a></th>
				<th class="table-header-repeat line-left"><a href="">State</a></th>
			</tr>
		 	<c:forEach var="task" items="${tasks}" varStatus="varStatus">
		 		<tr>
		 			<td>${task.reference}</td>
		 			<td>${task.creationDate}</td>
		 			<td>${task.state}</td>
		 			<td><a href="stacktrace/${task.reference}.html">stack</a></td>
		 		</tr>
		 	</c:forEach> 
		</table>
</div>
