<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<html>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
	<head>
		<link rel="stylesheet" href="${contextPath}/css/manager.css" type="text/css"/>
		<link rel="stylesheet" href="${contextPath}/css/screen.css" type="text/css" media="screen" title="default" />
	</head>
	<body>
		<div id="content" style="padding:0px;">
			<tiles:insertAttribute name="contents" />
		</div>
	</body>
</html>