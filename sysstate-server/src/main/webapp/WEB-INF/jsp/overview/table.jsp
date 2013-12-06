<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="sse" uri="http://www.unionsoft.nl/sse/" %>

<table class="state_table" >
	<colgroup id="cg_prj"/>
	<c:forEach var="environment" items="${viewResults.environments}">
		<colgroup id="cg_env_${environment.id}" class="cg_env"/>
	</c:forEach>
	<thead class="head">
		<tr class="row_env">
			<c:if test="${properties['no_project_col'] != 'true'}">
				<th id="col_prj" class="top_left"><a href="${contextPath}/manager/index.html" >PRJ/ENV</a> </th>
			</c:if>	
			<c:forEach var="environment" items="${viewResults.environments}" varStatus="varStatus">
				<th id="col_env_${environment.id}" class="col_env ${varStatus.last ? 'top_right' :''}"><a href="${contextPath}/filter/environment/${environment.id}/index.html" >${environment.name}</a></th>
			</c:forEach>
		</tr>
	</thead>
	<tbody>
		<c:forEach var="project" items="${viewResults.projects}" varStatus="varStat">
				<tr id="row_prj_${project.id}" class="row_prj ${(varStat.index)%2 eq 0 ? 'row_prj_odd' : 'row_prj_even' }">
					<c:if test="${properties['no_project_col'] != 'true'}">
						<th class="row_prj_name">
							<a href="${contextPath}/filter/project/${project.id}/index.html" >
								${project.name}
							</a>
						</th>
					</c:if>
					<c:forEach var="environment" items="${viewResults.environments}" varStatus="varStatus">
						<sse:projectEnvironmentForProjectAndEnvironment  var="projectEnvironment" viewResult="${viewResults}" environment="${environment}" project="${project}"/>
						<c:set var="instances" value="${projectEnvironment.instances }"/>

						<td id="row_prj_${project.id}_env_${environment.id}" class="row_prj_env state_${projectEnvironment.state}">
					
							<div class="row_prj_env" style="position: relative;" >
								<!-- <div style="z-index: 10; position: absolute;top: 0;left: 0;">blaat</div> -->
								<!-- <div style="position: absolute;top: 0;left: 0;"> -->
								<div">
									<c:set var="target" value="${not empty projectEnvironment.homepageUrl ? '_BLANK' : '_SELF'}"/>
									<a class="inline" href="${contextPath}/projectEnvironment/project/${project.id}/environment/${environment.id}/details.html" title="Configuration">
										<span class="row_prj_env_int_state state_${projectEnvironment.state}"><img src="${contextPath}/images/transparant.gif"/></span>
										<c:set var="styleRating" value=""/>
										<c:if test="${properties['no_weather'] != 'true'}">
											<c:choose>
												<c:when test="${fn:length(instances) == 0 || projectEnvironment.rating  < 0 }">
													<c:set var="styleRating" value=""/>
												</c:when>
												<c:when test="${projectEnvironment.rating < 25 }">
													<c:set var="styleRating" value="rating_0"/>	
												</c:when>
												<c:when test="${projectEnvironment.rating < 50 }">
													<c:set var="styleRating" value="rating_25"/>	
												</c:when>
												<c:when test="${projectEnvironment.rating < 75 }">
													<c:set var="styleRating" value="rating_50"/>	
												</c:when>
												<c:when test="${projectEnvironment.rating < 100 }">
													<c:set var="styleRating" value="rating_75"/>	
												</c:when>
												<c:when test="${projectEnvironment.rating == 100 }">
													<c:set var="styleRating" value="rating_100"/>	
												</c:when>
											</c:choose>
											<span class="row_prj_env_int_rating ${styleRating}"><img src="${contextPath}/images/transparant.gif"/></span>
										</c:if>
										
										<span class="env_prj_name">${project.name}</span>
										
										<c:choose>
											<c:when test="${fn:length(instances) == 0 }">
												None
											</c:when>
											<c:when test="${not empty projectEnvironment.description}">
												${projectEnvironment.description}
											</c:when>
											<c:otherwise>
												(<font class="state_STABLE">${projectEnvironment.count.stable}</font>/<font class="state_UNSTABLE">${projectEnvironment.count.unstable}</font>/<font class="state_ERROR">${projectEnvironment.count.error}</font>/<font class="state_DISABLED">${projectEnvironment.count.disabled}</font>/<font class="state_PENDING">${projectEnvironment.count.pending}</font>)
											</c:otherwise>
										</c:choose>
									</a>
								</div>
							</div>
						</td>
					</c:forEach>
				</tr>
			
		</c:forEach>
	</tbody>
	<tfoot class="foot">
		<tr>
			<td class="bottom_left" colspan="${fn:length(viewResults.environments)}">
			<%--
			<c:if test="${!controls}">
				<a href="?&templateId=${template.id}&controls=enable&excludedProjects=${excludedProjects}&excludedEnvironments=${excludedEnvironments}"><img src="${contextPath}/images/gear.gif"/></a>
			</c:if>
			 --%>
			<td class="bottom_right">&nbsp;</td>
		</tr>
	</tfoot>		
</table>
<c:if test="${controls}">
	<a href="?templateId=${template.id}&controls=disable&excludedProjects=${excludedProjects}&excludedEnvironments=${excludedEnvironments}">Disable controls</a><br/>
			
	<c:if test="${not empty (excludedProjects)}">
		<a href="?controls=${controls ? 'enable' : 'disable'}&templateId=${template.id}&excludedProjects=&excludedEnvironments=${excludedEnvironments}">All Projects</a>
	</c:if>
	<c:if test="${not empty (excludedEnvironments)}">
		|| <a href="?controls=${controls ? 'enable' : 'disable'}&templateId=${template.id}&excludedProjects=${excludedProjects}&excludedEnvironments=">All Environments</a> 
	</c:if>

</c:if>
