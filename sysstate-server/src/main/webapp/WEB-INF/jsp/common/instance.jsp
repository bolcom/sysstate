<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@taglib prefix="sse" uri="http://www.unionsoft.nl/sse/"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="list" uri="http://www.unionsoft.nl/list/"%>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<c:set var="projectEnvironment" value="${instance.projectEnvironment}"/>
<c:set var="project" value="${projectEnvironment.project}"/>
<c:set var="environment" value="${projectEnvironment.environment}"/>
<c:set var="state" value="${instance.state}"/>

<tr class="${param.alternateRow ? '' : 'alternate-row' }">
	<c:if test="${param.id == 'checkBox'}">
		<td><input type="checkbox" name="instanceId" value="${instance.id}"/></td>
	</c:if>
	<c:if test="${param.state == true }">
		<td class="state state_${state.state}">
			&nbsp;
		</td>
	</c:if>
	<td>	
		<a name="${instance.id}"></a>
		<c:if test="${param.id == 'hidden'}">
			<input type="hidden" name="instanceId" value="${instance.id}"	/>
		</c:if>
		<c:choose>
			<c:when test="${param.options }">
				<a href="${contextPath}/filter/project/${project.id}/index.html"><c:out value="${project.name}"/></a>-<a href="${contextPath}/filter/environment/${environment.id}/index.html"><c:out value="${environment.name}"/></a>
				<sc:authorize url="/projectEnvironment/project/${project.id}/environment/${environment.id}/update">
					<a href="${contextPath}/projectEnvironment/project/${project.id}/environment/${environment.id}/update.html"><img src="${contextPath}/images/edit.gif" /></a>
				</sc:authorize>
			</c:when>
			<c:otherwise>
				<c:out value="${project.name}"/>-<c:out value="${environment.name}"/>
			</c:otherwise>
		</c:choose>
		
		<c:if test="${param.options}">
		</c:if>
	
		<c:if test="${not empty instance.name }">
			<br><c:out value="${instance.name}" escapeXml="true" />
		</c:if>
	</td>
	<td>
		<c:set var="maxLength" value="40"/>
		<c:set var="classLength" value="${fn:length(instance.pluginClass)}"/>
		
		
		type: <c:choose>
			<c:when test="${classLength > maxLength }">
				...<c:out value="${fn:substringAfter(fn:substring(instance.pluginClass, classLength - maxLength, classLength),'.')}" escapeXml="true" />
			</c:when>
			<c:otherwise>
				<c:out value="${instance.pluginClass}" escapeXml="true" />
			</c:otherwise>
		</c:choose></br>
		enabled: ${instance.enabled} |
		refresh: ${instance.refreshTimeout}ms <br/>
		<c:if test="${not empty instance.tags }">
			tags: '<c:out value="${instance.tags}" escapeXml="true" />'
		</c:if>
	</td>
	
	<c:if test="${param.state}">
		<td>
			<span class="row_prj_env_int_state state_${state.state}">
				<a href="${instance.homepageUrl}" target="_BLANK" title="">
					<img src="${contextPath}/images/transparant.gif" alt="ResponseTime: ${state.responseTime} ms" />
				</a>
			</span><a href="${instance.homepageUrl}" target="_BLANK" title=""> ${state.state}</a><br/>
			<a href="${instance.homepageUrl}" target="_BLANK">${state.description}</a><br/>
			
			Rating: 
			<jsp:include page="/WEB-INF/jsp/common/rating.jsp">
				<jsp:param name="rating" value="${instance.state.rating}"/>
			</jsp:include>
			<br/>
		</td>
	</c:if>
	<td>
		<c:if test="${param.state }">
			since: <joda:format value="${state.creationDate}" pattern="yyyy-MM-dd HH:mm:ss" /> <br />
			lastUpdate: <joda:format value="${state.lastUpdate}" pattern="yyyy-MM-dd HH:mm:ss" /> <br />
		</c:if> 
	</td>
	<c:if test="${param.options}">
		<td>
			<sc:authorize url="/instance/${instance.id}/configuration">
				<a class="inline" href="${contextPath}/instance/${instance.id}/configuration.html" title="Configuration"><img src="${contextPath}/images/gear.gif" /></a>
			</sc:authorize> 
			 <sc:authorize url="/instance/${instance.id}/refresh">
				<a href="${contextPath}/instance/${instance.id}/refresh.html#${instance.id}" title="Refresh Instance"><img src="${contextPath}/images/refresh.gif" /></a>
			</sc:authorize>
			<sc:authorize url="/instance/${instance.id}/update">
				<a href="${contextPath}/instance/${instance.id}/update.html" title="Edit Instance"><img src="${contextPath}/images/edit.gif" /></a>
			</sc:authorize>
			<sc:authorize url="/instance/${instance.id}/copy">
				<a href="${contextPath}/instance/${instance.id}/copy.html" title="Copy Instance"><img src="${contextPath}/images/copy.gif" /></a>
			</sc:authorize>
			 <sc:authorize url="/instance/${instance.id}/delete">
				 <a href="${contextPath}/instance/${instance.id}/delete.html?" title="Delete Instance"><img src="${contextPath}/images/delete.gif" /></a>
			</sc:authorize>
			<sc:authorize url="/instance/${instance.id}/toggle/enabled">
				<a href="${contextPath}/instance/${instance.id}/toggle/enabled.html"  title="${instance.enabled ? 'Disable' : 'Enable'}"><img src="${contextPath}/images/power_${instance.enabled ? 'off' : 'on'}.png"  width="12" height="12"/></a>
			</sc:authorize>
			
			<c:if test="${not empty state.message}">
				<a class="inline" href="${contextPath}/state/instance/${instance.id}/message.html" title="Message"><img src="${contextPath}/images/message.gif" /></a>
			</c:if>
			<sc:authorize url="/instance/${instance.id}/details">
				<a class="inline" href="${contextPath}/instance/${instance.id}/details.html" title="Details"><img src="${contextPath}/images/details.gif" /></a>
			</sc:authorize> 
			
		</td>
	</c:if>
</tr>
