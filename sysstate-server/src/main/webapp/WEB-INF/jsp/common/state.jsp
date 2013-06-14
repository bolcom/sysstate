<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<c:if test="${state.id != null}">
	<tr class="${param.alternateRow ? '' : 'alternate-row' }">
		<td class="state state_${state.state}">
				&nbsp;
		</td>
		<td>
			${state.state}
		</td>
		<td>
			<c:out value="${state.description }" escapeXml="true"/>
		</td>
		<td>
			<c:out value="${state.responseTime }" escapeXml="true"/> ms
		</td>
		<td>
		 	<joda:format value="${state.creationDate}" pattern="yyyy-MM-dd HH:mm:ss" /> <br />
		</td>
		<td>
		 	<joda:format value="${state.lastUpdate}" pattern="yyyy-MM-dd HH:mm:ss" /> <br />
		</td>
		<td>
			<jsp:include page="/WEB-INF/jsp/common/rating.jsp">
				<jsp:param name="rating" value="${state.rating}"/>
			</jsp:include> ${state.rating }
		</td>
	</tr>
	<c:if test="${state.state == 'ERROR' or state.state == 'UNSTABLE'}">
		<tr>
			<td colspan="7">
				<pre><c:out value="${state.message}" escapeXml="true"/></pre>
			</td>
		</tr>
	</c:if>
</c:if>