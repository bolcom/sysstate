<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="text" method="POST">
				<table id="id-form">
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="name"/>
						<jsp:param name="label" value="Name"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="tags"/>
						<jsp:param name="label" value="Tags"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="text"/>
						<jsp:param name="label" value="Text"/>
						<jsp:param name="type" value="textarea"/>
						<jsp:param name="cols" value="80"/>
						<jsp:param name="rows" value="10"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>
				</table>
			</sf:form>
		</td>
	</tr>
</table>
