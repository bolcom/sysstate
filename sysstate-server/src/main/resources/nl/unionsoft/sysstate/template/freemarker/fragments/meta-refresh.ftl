<html>
		
	<head>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
		<script src="${baseUrl}/scripts/colorbox/jquery.colorbox.js"></script>
		<link rel="stylesheet" type="text/css" href="${baseUrl}/template/render/${css}">	
		<link rel="stylesheet" type="text/css" href="${baseUrl}/scripts/colorbox/colorbox.css" />
		<script type="text/javascript" src="${baseUrl}/js/vis/vis.min.js"></script>
		<script type="text/javascript">
			var refresh = true;
			function Refresh() {
				if (refresh){
					location.reload();	
				}
			};
			<#if refresh??>
			$(document).ready(function() {
			 	setTimeout("Refresh()", ${refresh}000); 
			});
			</#if>
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
		<#include body>
	</body>
</html>