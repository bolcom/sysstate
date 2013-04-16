<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<html>
	<body>
		
		<h1>Delete Property</h1>
		<sf:form commandName="property" method="POST">
			<sf:hidden path="key"/>
			value: <sf:input path="value"  disabled="value"/><sf:errors path="value"/><br/>
			<input type="submit" value="OK" />
		</sf:form>

	</body>
</html>