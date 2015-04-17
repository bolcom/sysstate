<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<c:set var="contextPath" value="${pageContext.request.contextPath}" scope="request"/>
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
		<title>SysState Login</title>
		<link rel="stylesheet" href="${contextPath}/css/screen.css" type="text/css" media="screen" title="default" />
		<!--  jquery core -->
		<script src="${contextPath}/js/jquery/jquery-1.4.1.min.js" type="text/javascript"></script>
		
		<!-- Custom jquery scripts -->
		<script src="${contextPath}/js/jquery/custom_jquery.js" type="text/javascript"></script>
		
		<!-- MUST BE THE LAST SCRIPT IN <HEAD></HEAD></HEAD> png fix -->
		<script src="${contextPath}/js/jquery/jquery.pngFix.pack.js" type="text/javascript"></script>
		<script type="text/javascript">
			$(document).ready(function(){
			$(document).pngFix( );
			});
		</script>
	</head>
	<body id="login-bg"> 
	 
	<!-- Start: login-holder -->
	<div id="login-holder">
	
		<!-- start logo -->
		<div id="logo-login">
			<!-- <a href="index.html"><img src="${contextPath}/images/shared/logo.png" width="156" height="40" alt="" /></a> -->
		</div>
		<!-- end logo -->
		
		<div class="clear"></div>
		
		<!--  start loginbox ................................................................................. -->
		<div id="loginbox">
		<c:if test="${not empty error}">
			<div class="errorblock">
				Your login attempt was not successful, try again.<br /> Caused :
				${sessionScope["SPRING_SECURITY_LAST_EXCEPTION"].message}
			</div>
		</c:if>
		<!--  start login-inner -->
		<div id="login-inner">
			<form action="<c:url value='/j_spring_security_check' />" method="post">
				<table border="0" cellpadding="0" cellspacing="0">
				<tr>
					<th>Username</th>
					<td><input type="text" name="j_username" value=""  class="login-inp"/></td>
				</tr>
				<tr>
					<th>Password</th>
					<td><input type="password" name="j_password"  onfocus="this.value=''" class="login-inp" /></td>
				</tr>
				<tr>
					<th></th>
					<!-- 
					<td valign="top"><input type="checkbox" class="checkbox-size" id="login-check" /><label for="login-check">Remember me</label></td>
					 -->
				</tr>
				<tr>
					<th></th>
					<td><input type="submit" class="submit-login"  /></td>
				</tr>
				</table>
				<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			</form>
		</div>
	 	<!--  end login-inner -->
		<div class="clear"></div>
		<!-- 
			<a href="" class="forgot-pwd">Forgot Password?</a>
		 -->
	 </div>
	 <!--  end loginbox -->
	 
		<!--  start forgotbox ................................................................................... -->
		<div id="forgotbox">
			<div id="forgotbox-text">Please send us your email and we'll reset your password.</div>
			<!--  start forgot-inner -->
			<div id="forgot-inner">
			<table border="0" cellpadding="0" cellspacing="0">
			<tr>
				<th>Email address:</th>
				<td><input type="text" value=""   class="login-inp" /></td>
			</tr>
			<tr>
				<th> </th>
				<td><input type="button" class="submit-login"  /></td>
			</tr>
			</table>
			</div>
			<!--  end forgot-inner -->
			<div class="clear"></div>
			<a href="" class="back-login">Back to login</a>
		</div>
		<!--  end forgotbox -->
	
	</div>
	<!-- End: login-holder -->
	</body>
</html>