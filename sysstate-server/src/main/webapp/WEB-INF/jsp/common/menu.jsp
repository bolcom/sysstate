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
	<li><a href="${contextPath}/view/index.html"><b>Views</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--> <!--[if lte IE 6]><table><tr><td><![endif]-->
		<div class="select_sub show">
			<ul class="sub">
				<sc:authorize url="/view/create">
					<li><a href="${contextPath}/view/create.html"><img src="${contextPath}/images/create.png" /> Add</a></li>
				</sc:authorize>
			</ul>
		</div> <!--[if lte IE 6]></td></tr></table></a><![endif]--></li>
</ul>
<div class="nav-divider">&nbsp;</div>


<ul class="select">
	<li><a href="${contextPath}/filter/index.html"><b>Instances</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--> <!--[if lte IE 6]><table><tr><td><![endif]-->
		<div class="select_sub show">
			<ul class="sub">
				<li><a href="${contextPath}/instance/create.html">New</a></li>

				<li><a href="${contextPath}/filter/list.html">Instance
						Filters</a></li>
			</ul>
		</div> <!--[if lte IE 6]></td></tr></table></a><![endif]--></li>
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
    <li><a href="${contextPath}/scheduler/index.html"><b>Schedule</b>
        <!--[if IE 7]><!--></a>
    <!--<![endif]--> <!--[if lte IE 6]><table><tr><td><![endif]-->
        <div class="select_sub show">
            <ul class="sub">
                <sc:authorize url="/scheduler">
                    <li><a href="${contextPath}/scheduler/index.html">Active</a></li>
                </sc:authorize>
                <sc:authorize url="/scheduler/all">
                    <li><a href="${contextPath}/scheduler/all/index.html">All</a></li>
                </sc:authorize>
            </ul>
        </div> <!--[if lte IE 6]></td></tr></table></a><![endif]--></li>
</ul>
<div class="nav-divider">&nbsp;</div>
<ul class="select">
	<li><a href="${contextPath}/settings/index.html"><b>Administrate</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--> <!--[if lte IE 6]><table><tr><td><![endif]-->
		<div class="select_sub show">
			<ul class="sub">
				<sc:authorize url="/configuration">
					<li><a href="${contextPath}/configuration/index.html">Configuration</a></li>
				</sc:authorize>
				<sc:authorize url="/cache">
					<li><a href="${contextPath}/cache/index.html">Cache</a></li>
				</sc:authorize>
				<sc:authorize url="/template">
					<li><a href="${contextPath}/template/index.html">Templates</a></li>
				</sc:authorize>
				<sc:authorize url="/user">
					<li><a href="${contextPath}/user/index.html">Users</a></li>
				</sc:authorize>
				<sc:authorize url="/plugins">
					<li><a href="${contextPath}/plugins/index.html">Plugins</a></li>
				</sc:authorize>
				<sc:authorize url="/script">
					<li><a href="${contextPath}/script/index.html">Run Script</a></li>
				</sc:authorize>

				<sc:authorize url="/discovery">
				<!-- 
					<li><a href="${contextPath}/discovery/index.html">Discovery</a></li>
					 -->
				</sc:authorize>
				<sc:authorize url="/text">
					<li><a href="${contextPath}/text/index.html">Texts</a></li>
				</sc:authorize>
			</ul>
		</div> <!--[if lte IE 6]></td></tr></table></a><![endif]--></li>
</ul>
<div class="nav-divider">&nbsp;</div>

<ul class="select">
	<li><a href="${contextPath}/index.html" target="_BLANK""><b>Site</b>
		<!--[if IE 7]><!--></a>
	<!--<![endif]--></li>
</ul>
