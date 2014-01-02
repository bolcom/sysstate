<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>

<sc:authorize url="/configuration">
	<a href="${contextPath}/configuration/index.html" class="icon-1 info-tooltip"></a>Configuration<br/>
	<i>Manage Global Configuration</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/cache">
	<a href="${contextPath}/cache/index.html" class="icon-1 info-tooltip"></a>Cache<br/>
	<i>Show Cache Statistics and purge caches</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/template">
	<a href="${contextPath}/template/index.html" class="icon-1 info-tooltip"></a> Templates<br/>
	<i>Create, update, delete and preview templates</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/user">
	<a href="${contextPath}/user/index.html" class="icon-1 info-tooltip"></a> Users<br/>
	<i>Create, update and delete Users</i>					
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/plugins">
	<a href="${contextPath}/plugins/index.html" class="icon-1 info-tooltip"></a> Plugins<br/>
	<i>List available plugins</i>					
	<div class="clear"></div>
</sc:authorize>


<sc:authorize url="/text">
	<a href="${contextPath}/text/index.html"  class="icon-1 info-tooltip"></a> Texts<br/>
	<i>Create, update and delete Texts</i>					
	<div class="clear"></div>
</sc:authorize>

