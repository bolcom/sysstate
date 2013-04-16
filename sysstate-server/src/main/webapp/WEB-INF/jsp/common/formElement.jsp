<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="s" uri="http://www.springframework.org/tags" %>
<%@taglib prefix="sf" uri="http://www.springframework.org/tags/form" %>
<c:set var="type" value="${param.type}"/>
<c:choose>
	<c:when test="${type == 'hidden' }">
		<c:set var="path" value="${param.path}"/>
		<sf:hidden path="${path}"/>
	</c:when>
	<c:otherwise>
		<tr>
			<c:choose>
				<c:when test="${type == 'submit' }">
					<th>&nbsp;</th>
					<td valign="top">
						<input type="submit" value="" class="form-submit" /> 
						<input type="reset" value="" class="form-reset" />
					</td>
					<td></td>
				</c:when>
				<c:otherwise>
					<c:set var="label" value="${param.label}"/>
					<c:set var="path" value="${param.path}"/>
					<c:set var="comments" value="${param.comments}"/>
					<th valign="top"><c:out  value="${label}"/>:</th>
					<td>
						<c:choose>
							<c:when test="${type == 'input' || type == 'null' || empty type}">
								<sf:input path="${path}" cssClass="inp-form"/>
							</c:when>
							<c:when test="${type == 'password'}">
								<sf:password  path="${path}" cssClass="inp-form"/>
							</c:when>
							<c:when test="${type == 'checkbox'}">
								<sf:checkbox  path="${path}" cssClass="inp-form"/>
							</c:when>
							<c:when test="${type == 'textarea'}">
								<c:set var="cols" value="${param.cols}"/>
								<c:set var="rows" value="${param.rows}"/>
								<sf:textarea path="${path}" cssClass="form-textarea" cols="${cols}" rows="${rows}"/>
							</c:when>		
							<c:when test="${type == 'select'}">
								<c:set var="multi" value="${param.multi}"/>
								<c:set var="size" value="${param.size}"/>
								
								<c:set var="itemLabel" value="${param.itemLabel}"/>
								<c:set var="itemValue" value="${param.itemValue}"/>																
								<c:set var="allowEmpty" value="${param.allowEmpty == 'true'}"/>
								<c:set var="list" value="${requestScope[param.items]}"/>
								<sf:select path="${path}" multiple="${multi == 'true'}" size="${size }" cssClass="${multi != 'true' ? 'styledselect_form_1' : ''}" >
									<c:if test="${allowEmpty }">
										<sf:option value="">-- None --</sf:option>
									</c:if>
									<c:choose>
										<c:when test="${not empty itemLabel && not empty itemValue}">
											<sf:options items="${list}" itemLabel="${itemLabel}" itemValue="${itemValue}"/>
										</c:when>
										<c:when test="${not empty itemLabel }">
											<sf:options items="${list}" itemLabel="${itemLabel}" />
										</c:when>
										<c:when test="${not empty itemValue}">
											<sf:options items="${list}" itemValue="${itemValue}" />
										</c:when>
										<c:otherwise>
											<sf:options items="${list}" />
										</c:otherwise>
									</c:choose>
							 	</sf:select>
						 	
							 	
							</c:when>
						</c:choose>
						<c:if test="${not empty comments}">
							<font style="font-style: italic;">${comments}</font>
						</c:if>
					</td>
					<td>
						<c:set var="error"><sf:errors path="${path}"/></c:set>
						<c:if test="${not empty error }">
							<div class="error-left"></div>
							<div class="error-inner">${error}</div>
						</c:if>
					</td>		
				</c:otherwise>	
			</c:choose>
</tr>
	
	</c:otherwise>
</c:choose>

