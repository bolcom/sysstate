<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<table>
	<tr valign="top">
		<td>
			<sf:form commandName="view" method="POST">
				<table id="id-form">
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="id"/>
						<jsp:param name="type" value="hidden"/>
					</jsp:include>				
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="name"/>
						<jsp:param name="label" value="Name"/>
					</jsp:include>
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="filter.id"/>
						<jsp:param name="label" value="Filter"/>
						<jsp:param name="type" value="select"/>
						<jsp:param name="items" value="filters"/>
						<jsp:param name="itemLabel" value="name"/>
						<jsp:param name="itemValue" value="id"/>
						<jsp:param name="allowEmpty" value="true"/>
												
					</jsp:include>		
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="template.name"/>
						<jsp:param name="label" value="Template"/>
						<jsp:param name="type" value="select"/>
						<jsp:param name="items" value="templates"/>
						<jsp:param name="itemLabel" value="name"/>
						<jsp:param name="itemValue" value="name"/>						
					</jsp:include>										
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="path" value="commonTags"/>
						<jsp:param name="label" value="commonTags"/>
					</jsp:include>	
					<jsp:include page="/WEB-INF/jsp/common/formElement.jsp">
						<jsp:param name="type" value="submit"/>
					</jsp:include>
				</table>
			</sf:form>
		</td>
	</tr>
</table>
