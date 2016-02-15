<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>

<sc:authorize url="/configuration/index">
	<a href="${contextPath}/configuration/index.html" class="icon-1 info-tooltip"></a>Configuration<br/>
	<i>Manage Global Configuration</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/cache/index">
	<a href="${contextPath}/cache/index.html" class="icon-1 info-tooltip"></a>Cache<br/>
	<i>Show Cache Statistics and purge caches</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/template/index">
	<a href="${contextPath}/template/index.html" class="icon-1 info-tooltip"></a> Templates<br/>
	<i>Create, update, delete and preview templates</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/user/index">
	<a href="${contextPath}/user/index.html" class="icon-1 info-tooltip"></a> Users<br/>
	<i>Create, update and delete Users</i>					
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/plugins/index">
	<a href="${contextPath}/plugins/index.html" class="icon-1 info-tooltip"></a> Plugins<br/>
	<i>List available plugins</i>					
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/text/index">
	<a href="${contextPath}/text/index.html"  class="icon-1 info-tooltip"></a> Texts<br/>
	<i>Create, update and delete Texts</i>					
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/script/index">
	<a href="${contextPath}/script/index.html"  class="icon-1 info-tooltip"></a> Run Scripts<br/>
	<i>Run Scripts (for example: Groovy Scripts)</i>					
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/scheduler/index">
	<a href="${contextPath}/scheduler/index.html"  class="icon-1 info-tooltip"></a>Scheduling<br/>
	<i>View scheduling information</i>					
	<div class="clear"></div>
</sc:authorize>