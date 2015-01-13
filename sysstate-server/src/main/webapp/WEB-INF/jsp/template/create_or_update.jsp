<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="template" method="POST">
				<table id="id-form">
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="id"/>
						<jsp:param name="label" value="ID"/>
						<jsp:param name="comments" value="Change the ID to create a copy!"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="css"/>
						<jsp:param name="label" value="Css"/>
						<jsp:param name="type" value="textarea"/>
						<jsp:param name="cols" value="80"/>
						<jsp:param name="rows" value="20"/>
					</jsp:include>					
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="renderHints"/>
						<jsp:param name="label" value="RenderHints"/>
						<jsp:param name="type" value="textarea"/>
						<jsp:param name="cols" value="80"/>
						<jsp:param name="rows" value="3"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="layout"/>
						<jsp:param name="label" value="Layout"/>
					</jsp:include>									
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="refresh"/>
						<jsp:param name="label" value="Refresh"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>						
				</table>
			</sf:form>
		</td>
	</tr>
</table>
