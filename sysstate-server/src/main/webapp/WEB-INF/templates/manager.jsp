<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="joda" uri="http://www.joda.org/joda/time/tags"%>
<%@ taglib prefix="sse" uri="http://www.unionsoft.nl/sse/"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="list" uri="http://www.unionsoft.nl/list/"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="tiles"%>
<%@taglib prefix="sc" uri="http://www.springframework.org/security/tags"%>
<html xmlns="http://www.w3.org/1999/xhtml">

<head>
	<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
	<c:set var="requestUrl" value="${requestScope['javax.servlet.forward.request_uri']}" scope="request"/>
	
	<title>SysState Administrator</title>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

	<link rel="stylesheet" href="${contextPath}/scripts/colorbox/colorbox.css" type="text/css" />
	<link rel="stylesheet" href="${contextPath}/css/manager.css" type="text/css"/>
	<link rel="stylesheet" href="${contextPath}/css/progressbar.css" type="text/css"/>
	<link rel="stylesheet" href="${contextPath}/css/screen.css" type="text/css" media="screen" title="default" />
	<!--[if IE]>
		<link rel="stylesheet" media="all" type="text/css" href="${contextPath}/css/pro_dropline_ie.css" />
	<![endif]-->

	<!--  jquery core -->
	
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
	<script src="${contextPath}/scripts/colorbox/jquery.colorbox.js"></script>
	

	<!--  checkbox styling script -->
	<script src="${contextPath}/js/jquery/ui.core.js" type="text/javascript"></script>
	<script src="${contextPath}/js/jquery/ui.checkbox.js" type="text/javascript"></script>
	<script src="${contextPath}/js/jquery/jquery.bind.js" type="text/javascript"></script>
	<script type="text/javascript">
		$(function(){
			$(".inline").colorbox({iframe:true, width:"75%", height:"75%"});
		});
	</script>  

<![if !IE 7]>

<!--  styled select box script version 1 -->
<script src="${contextPath}/js/jquery/jquery.selectbox-0.5.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('.styledselect').selectbox({ inputClass: "selectbox_styled" });
});
</script>
 

<![endif]>

<!--  styled select box script version 2 --> 
<script src="${contextPath}/js/jquery/jquery.selectbox-0.5_style_2.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('.styledselect_form_1').selectbox({ inputClass: "styledselect_form_1" });
	$('.styledselect_form_2').selectbox({ inputClass: "styledselect_form_2" });
});
</script>

<!--  styled select box script version 3 --> 
<script src="${contextPath}/js/jquery/jquery.selectbox-0.5_style_2.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function() {
	$('.styledselect_pages').selectbox({ inputClass: "styledselect_pages" });
});
</script>

<!--  styled file upload script --> 
<script src="${contextPath}/js/jquery/jquery.filestyle.js" type="text/javascript"></script>
<script type="text/javascript" charset="utf-8">
  $(function() {
      $("input.file_1").filestyle({ 
          image: "images/forms/choose-file.gif",
          imageheight : 21,
          imagewidth : 78,
          width : 310
      });
  });
</script>

<!-- Custom jquery scripts -->
<script src="${contextPath}/js/jquery/custom_jquery.js" type="text/javascript"></script>
 
<!-- Tooltips -->
<script src="${contextPath}/js/jquery/jquery.tooltip.js" type="text/javascript"></script>
<script src="${contextPath}/js/jquery/jquery.dimensions.js" type="text/javascript"></script>
<script type="text/javascript">
$(function() {
	$('a.info-tooltip ').tooltip({
		track: true,
		delay: 0,
		fixPNG: true, 
		showURL: false,
		showBody: " - ",
		top: -35,
		left: 5
	});
});
</script> 


<!--  date picker script -->
<link rel="stylesheet" href="${contextPath}/css/datePicker.css" type="text/css" />
<script src="${contextPath}/js/jquery/date.js" type="text/javascript"></script>
<script src="${contextPath}/js/jquery/jquery.datePicker.js" type="text/javascript"></script>
<script type="text/javascript" charset="utf-8">
        $(function()
{

// initialise the "Select date" link
$('#date-pick')
	.datePicker(
		// associate the link with a date picker
		{
			createButton:false,
			startDate:'01/01/2005',
			endDate:'31/12/2020'
		}
	).bind(
		// when the link is clicked display the date picker
		'click',
		function()
		{
			updateSelects($(this).dpGetSelected()[0]);
			$(this).dpDisplay();
			return false;
		}
	).bind(
		// when a date is selected update the SELECTs
		'dateSelected',
		function(e, selectedDate, $td, state)
		{
			updateSelects(selectedDate);
		}
	).bind(
		'dpClosed',
		function(e, selected)
		{
			updateSelects(selected[0]);
		}
	);
	
var updateSelects = function (selectedDate)
{
	var selectedDate = new Date(selectedDate);
	$('#d option[value=' + selectedDate.getDate() + ']').attr('selected', 'selected');
	$('#m option[value=' + (selectedDate.getMonth()+1) + ']').attr('selected', 'selected');
	$('#y option[value=' + (selectedDate.getFullYear()) + ']').attr('selected', 'selected');
}
// listen for when the selects are changed and update the picker
$('#d, #m, #y')
	.bind(
		'change',
		function()
		{
			var d = new Date(
						$('#y').val(),
						$('#m').val()-1,
						$('#d').val()
					);
			$('#date-pick').dpSetSelected(d.asString());
		}
	);

// default the position of the selects to today
var today = new Date();
updateSelects(today.getTime());

// and update the datePicker to reflect it...
$('#d').trigger('change');
});
</script>
<script type="text/javascript">
	$('#search-all-form-submit').click(function() {
		var form =$('#search-all-form');
		form.submit();
	});
</script>


<!-- MUST BE THE LAST SCRIPT IN <HEAD></HEAD></HEAD> png fix -->
<script src="${contextPath}/js/jquery/jquery.pngFix.pack.js" type="text/javascript"></script>
<script type="text/javascript">
$(document).ready(function(){
	$(document).pngFix( );
});
</script>
</head>
<body>
	
	 
	<!-- Start: page-top-outer -->
	
	<div id="page-top-outer">    
	
		<!-- Start: page-top -->
		<div id="page-top">
		 
			<!-- start logo -->
			<div id="logo">
				<!-- 
				<a href=""><img src="${contextPath}/images/shared/logo.png" width="156" height="40" alt="" /></a>
				 -->
			</div>
			 
			<!-- end logo -->
			
			<!--  start top-search--> 
			<div id="top-search">
				<form action="${contextPath}/manager/search.html" method="post" id="search-all-form">
					<table border="0" cellpadding="0" cellspacing="0">
						<tr>
							<td><input type="text" name="search" value="Search" onblur="if (this.value=='') { this.value='Search'; }" onfocus="if (this.value=='Search') { this.value=''; }" class="top-search-inp" /></td>
							<td>
								<select  class="styledselect" name="where">
									<option value="instances">Instances</option>
								</select> 
							</td>
							<td>
								<input type="image" src="${contextPath}/images/shared/top_search_btn.gif" id="search-all-form-submit" />
							</td>
						</tr>
					</table>
				</form>
			</div>
			
		 	<!--  end top-search -->
		 	<div class="clear"></div>
		
		</div>
	
	<!-- End: page-top -->
	 
	</div>
	 
	<!-- End: page-top-outer -->
		
	<div class="clear">&nbsp;</div>
	 
	<!--  start nav-outer-repeat................................................................................................. START -->
	<div class="nav-outer-repeat"> 
	<!--  start nav-outer -->
	<div class="nav-outer"> 
	
			<!-- start nav-right -->
			</div>
			
			<div id="nav-right">
				<sc:authentication property="principal" var="user"/>
				<div class="nav-divider">&nbsp;</div>
				<div class="showhide-account"><img src="${contextPath}/images/shared/nav/nav_myaccount.gif" width="93" height="14" alt="" /></div>
				<c:if test="${not empty user && user != 'anonymousUser'}">
					<div class="nav-divider">&nbsp;</div>
					<a href="${contextPath}/logout.html" id="logout"><img src="${contextPath}/images/shared/nav/nav_logout.gif" width="64" height="14" alt="" /></a>
				</c:if>
				<div class="clear">&nbsp;</div>
			
				<div class="account-content">
					<div class="account-drop-inner">
						<c:choose>
							<c:when test="${not empty user && user != 'anonymousUser'}">
								<a href="${contextPath}/logout.html" id="acc-settings">Logout</a>
								<!-- 
								<a href="" id="acc-settings">Settings</a>
								<div class="clear">&nbsp;</div>
								<div class="acc-line">&nbsp;</div>
								<a href="" id="acc-details">Personal details </a>
								<div class="clear">&nbsp;</div>
								<div class="acc-line">&nbsp;</div>
								<a href="" id="acc-project">Project details</a>
								<div class="clear">&nbsp;</div>
								<div class="acc-line">&nbsp;</div>
								<a href="" id="acc-inbox">Inbox</a>
								<div class="clear">&nbsp;</div>
								<div class="acc-line">&nbsp;</div>
								<a href="" id="acc-stats">Statistics</a>
								 --> 
							</c:when>
							<c:otherwise>
								<a href="${contextPath}/login.html" id="acc-settings">Login</a>
							</c:otherwise>
						</c:choose>
					
					</div>
				</div>
			</div>
			
			
			
			<!-- end nav-right -->
	
	
			<!--  start nav -->
			<div class="nav">
				<div class="table">
					<tiles:insertAttribute name="menu" ignore="false" />
					<div class="clear"></div>
				</div>
				`<div class="clear"></div>
			</div>
			<!--  start nav -->
	
	</div>
	<div class="clear"></div>
	<!--  start nav-outer -->
	
	<!--  start nav-outer-repeat................................................... END -->
	
	  <div class="clear"></div>
	 
	<!-- start content-outer ........................................................................................................................START -->
	<div id="content-outer">
		<!-- start content -->
		<div id="content">
		
			<!--  start page-heading -->
			<div id="page-heading">
				<h1><tiles:insertAttribute name="page-heading" ignore="false" /></h1>
			</div>
			<!-- end page-heading -->
			<c:set var="relatedActivities"><tiles:getAsString name="related-activities" /></c:set>
			<table border="0" width="100%" cellpadding="0" cellspacing="0" id="content-table">
				<tr>
					<th rowspan="3" class="sized"><img src="${contextPath}/images/shared/side_shadowleft.jpg" width="20" height="300" alt="" /></th>
					<th class="topleft"></th>
					<td id="tbl-border-top" colspan="${not empty relatedActivities ? '2' : '1'}">&nbsp;</td>
					<th class="topright"></th>
					<th rowspan="3" class="sized"><img src="${contextPath}/images/shared/side_shadowright.jpg" width="20" height="300" alt="" /></th>
				</tr>
				<tr>
					<td id="tbl-border-left"></td>
					<td>
						<!--  start content-table-inner ...................................................................... START -->
						<div id="content-table-inner">
							<div id="table-content">
								<c:forEach var="message" items="${messages}">
									<!--  start message-yellow -->
									<div id="message-${message.color}">
										<table border="0" width="100%" cellpadding="0" cellspacing="0">
											<tr>
												<td class="${message.color}-left">
													<c:out value="${message.text }" escapeXml="true"/>
												</td>
												<td class="${message.color}-right"><a class="close-${message.color}"><img src="${contextPath}/images/table/icon_close_${message.color}.gif"   alt="" /></a></td>
											</tr>
										</table>
									</div>
								</c:forEach>
							
								<!--  end message-green -->
								<tiles:insertAttribute name="contents" ignore="false" />
							</div>
							<!--  end table-content  -->
					
							<div class="clear"></div>
						 
						</div>
						<!--  end content-table-inner ............................................END  -->
					</td>
					
					<c:if test="${not empty relatedActivities}">
						<td>
						
							<!--  start related-activities -->
							<div id="related-activities">
								
								<!--  start related-act-top -->
								<div id="related-act-top">
									<img src="${contextPath}/images/forms/header_related_act.gif" width="271" height="43" alt="" />
								</div>
								<!-- end related-act-top -->
								
								<!--  start related-act-bottom -->
								<div id="related-act-bottom">
								
									<!--  start related-act-inner -->
									<div id="related-act-inner">
										<tiles:insertAttribute name="related-activities" ignore="true" />
										<div class="clear"></div>
										
									</div>
									<!-- end related-act-inner -->
									<div class="clear"></div>
								
								</div>
								<!-- end related-act-bottom -->
							
							</div>
							<!-- end related-activities -->
						
						</td>
					</c:if>
					<td id="tbl-border-right"></td>
				</tr>
				<tr>
					<th class="sized bottomleft"></th>
					<td id="tbl-border-bottom" colspan="${not empty relatedActivities ? '2' : '1'}">&nbsp;</td>
					<th class="sized bottomright"></th>
				</tr>
			</table>
			<div class="clear">&nbsp;</div>
		</div>
		<!--  end content -->
		<div class="clear">&nbsp;</div>
	</div>
	<!--  end content-outer........................................................END -->
	
	<div class="clear">&nbsp;</div>
	 
	<!-- start footer -->         
	<div id="footer">
		<div id="footer-pad">&nbsp;</div>
		<!--  start footer-left -->
		<div id="footer-text">
			<div id="footer-left">
			Admin Skin &copy; Copyright Internet Dreams Ltd. <a href="http://www.netdreams.co.uk" target="_BLANK">www.netdreams.co.uk</a>. All rights reserved.</div>
	        <div id="footer-right">
	        <a href="https://github.com/UnionSoft/sysstate">SysState</a> v${sysstateVersion} &copy; <a href="http://www.unionsoft.nl/" target="_blank">UnionSoft</a> - &copy; <a href="http://www.chriskramer.nl/" target="_blank"> Chris Kramer</a>. All rights reserved.</div>
        </div>

		<!--  end footer-left -->
		<div class="clear">&nbsp;</div>
	</div>
	<!-- end footer -->
	 
</body>
</html>