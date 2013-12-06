<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<html>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
	<head>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
		<c:if test="${not empty template}">
			<link href="${contextPath}/css/style.css?templateId=${template.id}" rel="stylesheet" type="text/css" />
		</c:if>
		<script src="${contextPath}/scripts/colorbox/jquery.colorbox.js"></script>
		<link rel="stylesheet" href="${contextPath}/scripts/colorbox/colorbox.css" type="text/css" />
		<script type="text/javascript">
			var refresh = true;
			function Refresh() {
				if (refresh){
					location.reload();	
				}
			};
			<c:if test="${template.refresh > 0}">
				$(document).ready(function() {
				 	setTimeout("Refresh()", ${template.refresh}000); 
				});
			</c:if>
		</script>
		<script type="text/javascript">
			$(function(){
				$(".inline").colorbox({iframe:true, width:"95%", height:"95%",
					opOpen: function(){
						refresh = false;
					}, onClosed: function(){
						location.reload();
					}
				});
			});
		</script>  
	</head>
	<body>
		<tiles:insertAttribute name="body" />
	</body>
</html>