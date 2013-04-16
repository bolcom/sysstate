<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
	<body>
		<h1>Restore Template</h1>
		ID: ${template.id}
		<form action="restore.html" method="POST">
			<input type="submit" value="OK" />	
		</form>
	</body>
</html>