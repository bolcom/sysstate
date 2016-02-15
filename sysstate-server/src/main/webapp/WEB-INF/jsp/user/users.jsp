<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<table id="user-table">
	<tr>
		<th class="table-header-repeat line-left"><a href="">id</a></th>
		<th class="table-header-repeat line-left"><a href="">login</a></th>
		<th class="table-header-repeat line-left"><a href="">firstName</a></th>
		<th class="table-header-repeat line-left"><a href="">lastName</a></th>
		<th class="table-header-repeat line-left"><a href="">enabled</a></th>
		<th class="table-header-repeat line-left"><a href="">token</a></th>
		<th class="table-header-repeat line-left"><a href="">roles</a></th>
		<th class="table-header-repeat line-left"><a href="">options</a></th>
		
	</tr>
	<tbody>
		<c:forEach items="${users}" var="user" varStatus="varStatus">
			<tr class="${(varStatus.index)%2 eq 0 ? '' : 'alternate-row' }">
				<td>${user.id}</td>
				<td>${user.login}</td>
				<td>${user.firstName}</td>
				<td>${user.lastName}</td>
				<td>${user.enabled}</td>
				<td>${user.token}</td>				
				<td>
					<c:forEach items="${user.roles}" var="role">
						${role}<br/>
					</c:forEach>
				</td>
				<td>
					<sc:authorize url="/user/${user.id }/update">
						<a href="${user.id}/update.html">Update</a>
					</sc:authorize>
					<sc:authorize url="/user/${user.id }/delete">
						| <a href="${user.id}/delete.html">Delete</a>
					</sc:authorize>
					<sc:authorize url="/user/${user.id }/token">
						| <a href="${user.id}/token.html">Reset Token</a>
					</sc:authorize>					
				</td>
			</tr>
		</c:forEach>
	</tbody>
</table>
<a href="create.html">New!</a>
