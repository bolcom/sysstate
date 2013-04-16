<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sse" uri="http://www.unionsoft.nl/sse/" %>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<c:set var="project" value="${projectEnvironment.project }"/>
<c:set var="environment" value="${projectEnvironment.environment }"/>

<div class="nav-outer-repeat"> 
	<!--  start nav -->
	<div class="nav">
		<div class="table">
			<sc:authorize url="/filter/project/${project.id}/environment/${environment.id}/index">
				<ul class="select">
					<li><a href="${contextPath}/filter/project/${project.id}/environment/${environment.id}/index.html"  target="_TOP"><b>Show in Filter</b><!--[if IE 7]><!--></a><!--<![endif]-->
					</li>
				</ul>
				<div class="nav-divider">&nbsp;</div>
			</sc:authorize>
			<sc:authorize url="/instance/create">
				<ul class="select">
					<li><a href="${contextPath}/instance/create.html" target="_TOP"><b>Add Instance</b><!--[if IE 7]><!--></a><!--<![endif]-->
					</li>
				</ul>
				<div class="nav-divider">&nbsp;</div>
			</sc:authorize>
			<div class="clear"></div>
		</div>
		`<div class="clear"></div>
	</div>
	<!--  start nav -->
	
</div>
<div class="clear"></div>

<h1>Details</h1>
<hr>

<h3>ProjectEnvironment</h3>
<c:if test="${not empty projectEnvironment.homepageUrl}">
	<b>HomepageUrl:</b> <a href="${projectEnvironment.homepageUrl }">${projectEnvironment.homepageUrl }</a><br/>
</c:if>

<b>Project:</b> <c:out value="${project.name }" escapeXml="true"/><br/>


<b>Environment:</b> <c:out value="${environment.name }" escapeXml="true"/><br/>


<p>&nbsp;</p>

<h3>Instances</h3>
<table id="instances-table">
	<tr>
		<th class="table-header-repeat line-left">&nbsp;</th>
		<th class="table-header-repeat line-left"><a href="">Env</a></th>
		<th class="table-header-repeat line-left"><a href="">Config</a></th>
		<th class="table-header-repeat line-left"><a href="">State</a></th>
		<th class="table-header-repeat line-left"><a href="">Scheduling</a></th>
	</tr>
	<c:forEach var="instance" items="${instances}" varStatus="varStatus" >
		<c:set var="instance" value="${instance}" scope="request"/>
		<jsp:include page="/WEB-INF/jsp/common/instance.jsp">
			<jsp:param name="alternateRow" value="${(varStatus.index)%2 eq 0 }"/>
			<jsp:param name="id" value="hidden"/>
			<jsp:param name="options" value="false"/>
			<jsp:param name="state" value="true"/>
		</jsp:include>
		<c:set var="state" value="${instance.state}"/>
		<c:if test="${state.state != 'STABLE' || (state.rating < 100 && state.rating >= 0)}">
			<tr>
				<td colspan="5">
					<c:set var="message" value="${instance.configuration}" scope="request"/>
					<jsp:include page="/WEB-INF/jsp/common/message.jsp">
					<jsp:param value="Configuration" name="caption"/>
					</jsp:include>
				</td>
			</tr>
			<tr>
				<td colspan="5">
					<c:set var="message" value="${instance.state.message}" scope="request"/>
					<jsp:include page="/WEB-INF/jsp/common/message.jsp"/>
				</td>
			</tr>
		</c:if>
	</c:forEach>
</table>
