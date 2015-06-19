\<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="sse" uri="http://www.unionsoft.nl/sse/"%>
<%@taglib prefix="list" uri="http://www.unionsoft.nl/list/"%>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags"%>


<c:set var="projectId" value="${navigation.projectId}" />
<c:set var="environmentId" value="${navigation.environmentId}" />
<c:set var="instanceId" value="${navigation.instanceId}" />
<c:set var="queryString"
	value="projectId=${projectId}&environmentId=${environmentId}&instanceId=${instanceId}&detailType=history" />
<ul class="select">
	<li><a href="${contextPath}/dashboard/index.html"><b>Home</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--></li>
</ul>
<div class="nav-divider">&nbsp;</div>

<ul class="select">
	<li><a href="${contextPath}/filter/index.html"><b>Instances</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--> <!--[if lte IE 6]><table><tr><td><![endif]-->
		</li>
</ul>
<div class="nav-divider">&nbsp;</div>

<sc:authorize url="/project/index">
	<ul class="select">
		<li><a href="${contextPath}/filter/list.html"><b>Filters</b>
			<!--[if IE 7]><!--></a>
		<!--<![endif]--></li>
	</ul>
	<div class="nav-divider">&nbsp;</div>
</sc:authorize>

<ul class="select">
	<li><a href="${contextPath}/view/index.html"><b>Views</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--> <!--[if lte IE 6]><table><tr><td><![endif]-->
		</li>
</ul>
<div class="nav-divider">&nbsp;</div>

<sc:authorize url="/project/index">
	<ul class="select">
		<li><a href="${contextPath}/project/index.html"><b>Projects</b>
			<!--[if IE 7]><!--></a>
		<!--<![endif]--></li>
	</ul>
	<div class="nav-divider">&nbsp;</div>
</sc:authorize>

<sc:authorize url="/environment/index">
	<ul class="select">
		<li><a href="${contextPath}/environment/index.html"><b>Environments</b>
			<!--[if IE 7]><!--></a>
		<!--<![endif]--></li>
	</ul>
	<div class="nav-divider">&nbsp;</div>
</sc:authorize>


<sc:authentication property="principal" var="user" />
<ul class="select">
	<li><a href="${contextPath}/settings/index.html"><b>Settings</b>
			<!--[if IE 7]><!--></a>
		<!--<![endif]--></li>
	</ul>
</ul>
<div class="nav-divider">&nbsp;</div>

<ul class="select">
	<li><a href="${contextPath}/index.html" target="_BLANK""><b>Site</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--></li>
</ul>
