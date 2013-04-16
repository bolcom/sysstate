<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
	<body>
		
		<h1>Delete InstanceNotifier</h1>
		<sf:form commandName="instanceNotifier" method="POST">
			id: <sf:input path="id"/><sf:errors path="id"/><br/>
			<input type="submit" value="OK" />
		</sf:form>

	</body>
</html>