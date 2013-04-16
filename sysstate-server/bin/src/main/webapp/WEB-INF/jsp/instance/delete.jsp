<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>

<h1>Delete Instance</h1>
name: ${instance.name} <br/>
homepageUrl: ${instance.homepageUrl} <br/>
refreshTimeout: ${instance.refreshTimeout} <br/>
pluginClass: ${instance.pluginClass} <br/>
projectEnvironment: ${instance.projectEnvironment.project.name} - ${instance.projectEnvironment.environment.name} <br/>
<form action="${contextPath}/instance/${instance.id}/delete/confirmed.html" method="post">
	<input type="submit" value="OK" />
</form>
