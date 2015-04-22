<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="template" method="POST">
				<table id="id-form">
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="name"/>
						<jsp:param name="label" value="Name"/>
						<jsp:param name="comments" value="Changing an existing name will copy the template."/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="writer"/>
						<jsp:param name="label" value="Writer"/>
						<jsp:param name="type" value="select"/>
						<jsp:param name="items" value="templateWriters"/>
						<jsp:param name="allowEmpty" value="false"/>
					</jsp:include>							
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="resource"/>
						<jsp:param name="label" value="Resource"/>
					</jsp:include>														
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="contentType"/>
						<jsp:param name="label" value="ContentType"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="includeViewResults"/>
						<jsp:param name="label" value="Include View Results"/>
						<jsp:param name="type" value="checkbox"/>
					</jsp:include>							

								
				</table>
			</sf:form>
		</td>
	</tr>
</table>
