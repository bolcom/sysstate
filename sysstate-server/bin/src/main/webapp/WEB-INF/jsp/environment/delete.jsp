<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
	<body>
		<h1>Delete Environment</h1>
		<sf:form commandName="environment" method="POST">
			<sf:hidden path="id"/>
			name: <sf:input path="name" disabled="true"/><sf:errors path="name"/><br/>
			order: <sf:input path="order"  disabled="true"/><sf:errors path="order"/><br/>
			<input type="submit" value="OK" />
		</sf:form>
	</body>
</html>