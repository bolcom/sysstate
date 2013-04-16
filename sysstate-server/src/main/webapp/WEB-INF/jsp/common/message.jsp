<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<c:set var="caption" value="${not empty param.caption ? param.caption : 'Message'}"/>
<h1><c:out value="${caption }" escapeXml="true"/></h1>
<hr>


<pre class="pre_wrap"><c:out value="${not empty message ? message : 'None'}" escapeXml="true"/></pre>