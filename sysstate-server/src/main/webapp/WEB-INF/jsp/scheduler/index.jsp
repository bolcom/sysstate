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
				<th class="table-header-repeat line-left"><a href="">RunTime Milliseconds</a></th>
			</tr>
		 	<c:forEach var="task" items="${tasks}" varStatus="vsTasks">
		 		<tr>
		 			<td>${task.name}</td>
		 			<td>${task.group}</td>
		 			<td>${task.runTime}</td>
		 			<td>${task.runTimeMillis}ms</td>
			 		</tr>
		 	</c:forEach> 
		</table>
</div>
