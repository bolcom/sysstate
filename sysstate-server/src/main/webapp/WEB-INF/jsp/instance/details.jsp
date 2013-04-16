<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<h1>Instance</h1>
<h3>Details</h3>

<table id="instances-table">
	<tr>
		<th class="table-header-repeat line-left">&nbsp;</th>
		<th class="table-header-repeat line-left"><a href="">Env</a></th>
		<th class="table-header-repeat line-left"><a href="">Config</a></th>
		<th class="table-header-repeat line-left"><a href="">State</a></th>
		<th class="table-header-repeat line-left"><a href="">Scheduling</a></th>
	</tr>
	<c:set var="index" value="0"/>
	<jsp:include page="/WEB-INF/jsp/common/instance.jsp">
		<jsp:param name="alternateRow" value="false"/>
		<jsp:param name="id" value="hidden"/>
		<jsp:param name="options" value="false"/>
		<jsp:param name="state" value="true"/>
	</jsp:include>
</table>
<h3>Last States</h3>
<table id="states-table">
	<tr>
		<th class="table-header-repeat line-left">&nbsp;</th>
		<th class="table-header-repeat line-left"><a href="">Last</a></th>
		<th class="table-header-repeat line-left"><a href="">Description</a></th>
		<th class="table-header-repeat line-left"><a href="">responseTime</a></th>
		<th class="table-header-repeat line-left"><a href="">creationDate</a></th>
		<th class="table-header-repeat line-left"><a href="">rating</a></th>
	</tr>
	<c:set var="state" value="${instance.lastStable}" scope="request"/>
	<jsp:include page="/WEB-INF/jsp/common/state.jsp">
		<jsp:param name="alternateRow" value="false"/>
	</jsp:include>
	<c:set var="state" value="${instance.lastUnstable}" scope="request"/>
	<jsp:include page="/WEB-INF/jsp/common/state.jsp">
		<jsp:param name="alternateRow" value="false"/>
	</jsp:include>
	<c:set var="state" value="${instance.lastError}" scope="request"/>
	<jsp:include page="/WEB-INF/jsp/common/state.jsp">
		<jsp:param name="alternateRow" value="false"/>
	</jsp:include>
	<c:set var="state" value="${instance.lastPending}" scope="request"/>
	<jsp:include page="/WEB-INF/jsp/common/state.jsp">
		<jsp:param name="alternateRow" value="false"/>
	</jsp:include>

	<c:set var="state" value="${instance.lastDisabled}" scope="request"/>
	<jsp:include page="/WEB-INF/jsp/common/state.jsp">
		<jsp:param name="alternateRow" value="false"/>
	</jsp:include>





</table>


