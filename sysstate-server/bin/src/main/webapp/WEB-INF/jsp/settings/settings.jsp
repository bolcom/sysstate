<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<sc:authorize url="/property">
	<a href="${contextPath}/property/index.html"  class="icon-1 info-tooltip"></a>Properties<br/>
	<i>Update, create and delete properties.</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/queue">
	<a href="${contextPath}/queue/index.html" class="icon-1 info-tooltip"></a>Queue<br/>
	<i>Show Queue Statistics and active task list</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/cache">
	<a href="${contextPath}/cache/index.html" class="icon-1 info-tooltip"></a>Cache<br/>
	<i>Show Cache Statistics and purge caches</i>
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/plugin">
	<a href="${contextPath}/plugin/index.html" class="icon-1 info-tooltip"></a> Plugins<br/>
	<i>List all plugins and update their properties</i>
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

<sc:authorize url="/discovery">
	<a href="${contextPath}/discovery/index.html"  class="icon-1 info-tooltip"></a> Discovery<br/>
	<i>Import instances using DiscoveryPlugins</i>					
	<div class="clear"></div>
</sc:authorize>

<sc:authorize url="/text">
	<a href="${contextPath}/text/index.html"  class="icon-1 info-tooltip"></a> Texts<br/>
	<i>Create, update and delete Texts</i>					
	<div class="clear"></div>
</sc:authorize>

